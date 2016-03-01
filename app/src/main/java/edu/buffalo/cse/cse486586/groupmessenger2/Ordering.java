package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.buffalo.cse.cse486586.groupmessenger2.Nodes.pidFor;
import static java.lang.Math.max;

public class Ordering {
    private static final String TAG = Ordering.class.getName();

    private final GroupMessenger messenger;
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
                new ClientTask(messenger, Nodes.liveNodes()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, in);

                queue.offer(new BufferedMessage(proposedSequence, in.getId(), fromNode, in.getMessage(), pidFor(messenger.myPort())));
                Log.d(TAG, "INITIAL_MESSAGE: Queue: " + queue);
                break;
            }
            case SEQUENCE_PROPOSAL: {
                addToProposals(in.getId(), new Proposal(in.getSequence(), in.getFromNode(), in.getProposedSequence()));

                for (UUID key : proposals.keySet()) {
                    Set<Proposal> proposalSet = proposals.get(key);
                    if (proposalSet.size() == Nodes.liveNodeCount()) {
                        Log.d(TAG, "PROPOSALS: " + proposalSet);
                        Payload agreement = Payload.newAgreement(key, messenger.myPort(), largestSequenceFor(key));
                        new ClientTask(messenger, Nodes.liveNodes()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, agreement);
                        proposals.remove(key);
                    }
                }
                break;
            }
            case SEQUENCE_AGREEMENT: {
                Log.d(TAG, "SEQUENCE_AGREEMENT:B: Queue: " + queue);

                for (BufferedMessage m : queue) {
                    if (!Nodes.isOffline(m.getFromNode())) {
                        if (m.getId().equals(in.getId())) {
                            queue.remove(m);
                            m.setAgreedPid(pidFor(in.getFromNode()));
                            m.setSequence(in.getAgreedSequence());
                            m.setStatus(BufferedMessage.Status.DELIVERABLE);
                            queue.offer(m);
                        }
                    } else {
                        Log.d(TAG, "Removing failed node " + m.getFromNode() + " data");
                        queue.remove(m);
                    }
                }
                while (queue.peek() != null && queue.peek().getStatus() == BufferedMessage.Status.DELIVERABLE) {
                    BufferedMessage message = queue.poll();
                    if (message != null) {
                        persist(message);
                    }
                }
                break;
            }
        }
    }


    private void addToProposals(UUID id, Proposal proposal) {
        if (proposals.containsKey(id)) {
            Set<Proposal> proposalSet = proposals.get(id);
            proposalSet.add(proposal);
        } else {
            Set<Proposal> proposalSet = Collections.synchronizedSet(new HashSet<Proposal>());
            proposalSet.add(proposal);
            proposals.put(id, proposalSet);
        }
    }


    private void persist(BufferedMessage msg) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("key", String.valueOf(agreedSequence.getAndIncrement()));
        contentValues.put("value", msg.getMessage());
        messenger.getContentResolver().insert(messenger.contentProviderUri(), contentValues);
    }

    private float proposeSequence() {
        return nextSequence() + (pidFor(messenger.myPort()) / 10.0F);
    }


    public int nextSequence() {
        return sequence.incrementAndGet();
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
}
