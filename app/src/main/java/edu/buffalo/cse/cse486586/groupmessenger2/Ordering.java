package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.max;

public class Ordering {
    private static final String TAG = Ordering.class.getName();

    private final GroupMessenger messenger;
    private static final CopyOnWriteArraySet<String> OFFLINE_NODES = new CopyOnWriteArraySet<>();
    private static final PriorityBlockingQueue<BufferedMessage> queue = new PriorityBlockingQueue<>();

    private static final ConcurrentHashMap<UUID, Set<Proposal>> proposals = new ConcurrentHashMap<>();

    private static final AtomicInteger sequence = new AtomicInteger(0);
    private static final AtomicInteger agreedSequence = new AtomicInteger(0);


    public Ordering(GroupMessenger messenger) {
        this.messenger = messenger;
    }

    public void handle(Payload in) {
        switch (in.getType()) {
            case INITIAL_MESSAGE: {
                String fromNode = in.getFromNode();
                float proposedSequence = proposeSequence();
                in.toProposal(proposedSequence, messenger.myPort());
                new ClientTask(messenger, fromNode).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, in);

                queue.offer(new BufferedMessage(proposedSequence, in.getId(), GroupMessenger.pid(fromNode), in.getMessage(), messenger.myPid()));
                Log.d(TAG, "INITIAL_MESSAGE: Queue: " + queue);
                break;
            }
            case SEQUENCE_PROPOSAL: {
                int proposalCount = addToProposals(in.getId(), new Proposal(in.getSequence(), in.getFromNode(), in.getProposedSequence()));
                Log.d(TAG, "P " + proposalCount);
                if (proposalCount >= (GroupMessenger.nodes().size() - OFFLINE_NODES.size())) {
                    Log.d(TAG, "PROPOSALS: " + proposals.get(in.getId()));
                    in.toAgreement(messenger.myPort(), largestSequenceFor(in.getId()));
                    new ClientTask(messenger, GroupMessenger.nodes()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, in);
                    proposals.remove(in.getId());
                }
                break;
            }
            case SEQUENCE_AGREEMENT: {
                Log.d(TAG, "SEQUENCE_AGREEMENT:B: Queue: " + queue);
//                sequence.set(Math.max(sequence.get(),(int) in.getAgreedSequence()));
//                updateSequence(in.getAgreedSequence());

                for (BufferedMessage m : queue) {
                    if (m.getId().equals(in.getId())) {
                        queue.remove(m);
                        m.setSequence(in.getAgreedSequence());
                        m.setStatus(BufferedMessage.Status.DELIVERABLE);
                        queue.offer(m);
                    }
                }
                while (queue.peek() != null && queue.peek().getStatus() == BufferedMessage.Status.DELIVERABLE) {
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


    private int addToProposals(UUID id, Proposal proposal) {
        if (proposals.containsKey(id)) {
            Set<Proposal> proposalSet = proposals.get(id);
            proposalSet.add(proposal);
            return proposalSet.size();
        } else {
            Set<Proposal> proposalSet = Collections.synchronizedSet(new HashSet<Proposal>());
            proposalSet.add(proposal);
            proposals.put(id, proposalSet);
            return 1;
        }
    }


    private void persist(BufferedMessage msg) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("key", String.valueOf(agreedSequence.getAndIncrement()));
        contentValues.put("value", msg.getMessage());
        messenger.getContentResolver().insert(messenger.contentProviderUri(), contentValues);
    }

    private float largestSequenceFor(UUID id) {
        float max = Float.MIN_VALUE;
        if (proposals.containsKey(id)) {
            for (Proposal p : proposals.get(id)) {
                max = max(max, p.getProposedSequence());
            }
        }

        return max;
    }

    private float proposeSequence() {
        return nextSequence() + (messenger.myPid() / 10.0F);
    }

    public void markOffline(String node) {
        if (OFFLINE_NODES.add(node)) {
            Log.d(TAG, "Marked node :" + node + " is offline");
        }
    }

    public int nextSequence() {
        return sequence.incrementAndGet();
    }

    public void updateSequence(float val) {
        int i = sequence.get();
        if (i < val) {
            sequence.compareAndSet(i, (int) Math.ceil(val) + 1);
        }
    }
}
