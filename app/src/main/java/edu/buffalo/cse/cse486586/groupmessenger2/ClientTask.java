package edu.buffalo.cse.cse486586.groupmessenger2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class ClientTask extends AsyncTask<Payload, Void, Set<ClientTask.Result>> {
    private static final String TAG = ClientTask.class.getName();
    private final GroupMessenger messenger;
    private final Collection<String> toNodes;

    public ClientTask(GroupMessenger messenger, Collection<String> toNodes) {
        this.messenger = messenger;
        this.toNodes = toNodes;
    }

    public ClientTask(GroupMessenger messenger, String toNode) {
        this.messenger = messenger;
        this.toNodes = new HashSet<>(1);
        this.toNodes.add(toNode);
    }


    @Override
    protected Set<ClientTask.Result> doInBackground(Payload... payloads) {
        Set<Result> results = new HashSet<>();
        for (String node : toNodes) {
            try (Socket socket = new Socket(
                    InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                    Integer.parseInt(node));
                 OutputStream out = socket.getOutputStream()) {
//                Log.d(TAG, "ClientTask Sending: " + payloads[0] + " to " + node);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                objectOutputStream.writeObject(payloads[0]);
                objectOutputStream.flush();
                results.add(new Result(true, node));
            } catch (Exception e) {
                results.add(new Result(false, node));
                Log.e(TAG, "ClientTask socket Exception" + " while sending to " + node);
//                Log.e(TAG, e.getMessage());
            }
        }
        return results;
    }

    @Override
    protected void onPostExecute(Set<ClientTask.Result> results) {
        for (Result r : results) {
            if (!r.success) {
                Nodes.markOffline(r.node);
            }
        }
    }

    public static class Result {
        private static final Result EMPTY_RESULT = new Result();
        private boolean success;
        private String node;

        public Result(boolean success, String node) {
            this.success = success;
            this.node = node;
        }

        public Result() {
        }
    }
}