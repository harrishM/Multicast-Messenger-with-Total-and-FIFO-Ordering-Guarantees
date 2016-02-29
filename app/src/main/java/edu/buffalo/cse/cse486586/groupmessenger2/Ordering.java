package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Math.max;

public class Ordering {
    private static final String TAG = Ordering.class.getName();

    private final GroupMessenger messenger;
    private static final CopyOnWriteArraySet<String> OFFLINE_NODES = new CopyOnWriteArraySet<>();
    private static final PriorityBlockingQueue<BufferedMessage> queue = new PriorityBlockingQueue<>();

    private static final ConcurrentHashMap<UUID, Set<Proposal>> proposals = new ConcurrentHashMap<>();

    private static volatile int largestAgreedSequence = -1;
    private static volatile int largestProposedSequence = -1;

    public Ordering(GroupMessenger messenger) {
        this.messenger = messenger;
    }

    public void handle(Payload in) {
        switch (in.getType()) {
            case INITIAL_MESSAGE: {
//                Log.d(TAG, "INITIAL_MESSAGE: " + in);
                int proposedSequence = proposeSequence();
                Payload out = Payload.newProposal(in.getId(), proposedSequence, messenger.myPort());
                new ClientTask(messenger, toList(in.getFromNode())).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, out);

                queue.add(new BufferedMessage(in.getId(), proposedSequence, in.getMessage()));
                Log.d(TAG, "INITIAL_MESSAGE: Queue: " + queue);
                break;
            }
            case SEQUENCE_PROPOSAL: {
                int proposalCount = addToProposals(new Proposal(in.getId(), in.getFromNode(), in.getProposedSequence()));

//                Log.d(TAG, proposalCount + "'th SEQUENCE_PROPOSAL for: " + in.getId()
//                        + " from:" + in.getFromNode() + " sequence:" + in.getProposedSequence());
                if (proposalCount >= (GroupMessenger.nodes().size() - OFFLINE_NODES.size())) {
                    Log.d(TAG, "PROPOSALS: " + proposals);
                    Payload out = Payload.newAgreement(in.getId(), largestSequenceFor(in.getId()), messenger.myPort());
                    new ClientTask(messenger, GroupMessenger.nodes()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, out);
                }
//                Log.d(TAG, "SEQUENCE_PROPOSAL: Queue: " + queue);
                break;
            }
            case SEQUENCE_AGREEMENT: {
                largestAgreedSequence = Math.max(largestAgreedSequence, in.getAgreedSequence());
                Log.d(TAG, "largestAgreedSequence: " + largestAgreedSequence);

//                Log.d(TAG, "SEQUENCE_AGREEMENT for: " + in.getId()
//                        + " from:" + in.getFromNode() + " sequence:" + in.getAgreedSequence());
                Log.d(TAG, "SEQUENCE_AGREEMENT:B: Queue: " + queue);

                for (BufferedMessage m : queue) {
                    if (m.getId().equals(in.getId())) {
                        m.setSequence(in.getAgreedSequence());
                        m.markAgreed();
                    }

                }
                if (queue.peek() != null && queue.peek().isAgreed()) {
                    BufferedMessage message = queue.poll();
                    if (message != null) {
                        persist(message);
                    }
                }
                Log.d(TAG, "SEQUENCE_AGREEMENT:A: Queue: " + queue);
                break;
            }
        }
    }

    private List<String> toList(String fromNode) {
        List<String> l = new ArrayList<>(1);
        l.add(fromNode);
        return l;
    }

    private int addToProposals(Proposal proposal) {
        if (proposals.containsKey(proposal.id)) {
            Set<Proposal> proposalSet = Ordering.proposals.get(proposal.id);
            proposalSet.add(proposal);
            return proposalSet.size();
        } else {
            Set<Proposal> proposalSet = Collections.synchronizedSet(new HashSet<Proposal>());
            proposalSet.add(proposal);
            proposals.put(proposal.id, proposalSet);
            return 1;
        }
    }


    private void persist(BufferedMessage msg) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("key", String.valueOf(msg.getSequence()));
        contentValues.put("value", msg.getMessage());
        messenger.getContentResolver().insert(messenger.contentProviderUri(), contentValues);
    }

    private int largestSequenceFor(UUID id) {
        int max = Integer.MIN_VALUE;
        if (proposals.containsKey(id)) {
            for (Proposal p : proposals.get(id)) {
                max = max(max, p.proposedSequence);
            }
        }
        return max;
    }

    private int proposeSequence() {
        largestProposedSequence = max(largestAgreedSequence, largestProposedSequence) + 1;
        Log.d(TAG, "largestProposedSequence: " + largestProposedSequence);
        return largestProposedSequence;
    }

    public void markOffline(String node) {
        if (OFFLINE_NODES.add(node)) {
            Log.d(TAG, "Marked node :" + node + " is offline");
        }
    }

    private static class Proposal {
        private UUID id;
        private String fromNode;
        private int proposedSequence;

        public Proposal(UUID id, String fromNode, int proposedSequence) {
            this.id = id;
            this.fromNode = fromNode;
            this.proposedSequence = proposedSequence;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Proposal proposal = (Proposal) o;
            return Objects.equals(proposedSequence, proposal.proposedSequence) &&
                    Objects.equals(id, proposal.id) &&
                    Objects.equals(fromNode, proposal.fromNode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, fromNode, proposedSequence);
        }

        @Override
        public String toString() {
            return "Proposal{" +
                    "id=" + id +
                    ", fromNode='" + fromNode + '\'' +
                    ", proposedSequence=" + proposedSequence +
                    '}';
        }
    }
}
