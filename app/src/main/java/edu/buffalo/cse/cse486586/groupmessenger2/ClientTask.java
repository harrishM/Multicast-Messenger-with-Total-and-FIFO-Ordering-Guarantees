package edu.buffalo.cse.cse486586.groupmessenger2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashSet;

class ClientTask extends AsyncTask<Payload, Void, Void> {
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
    protected Void doInBackground(Payload... payloads) {

        for (String node : toNodes) {
            try (Socket socket = new Socket(
                    InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                    Integer.parseInt(node));
                 OutputStream out = socket.getOutputStream()) {
//                Log.d(TAG, "ClientTask Sending: " + payloads[0] + " to " + node);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                objectOutputStream.writeObject(payloads[0]);
                objectOutputStream.flush();
            } catch (SocketException e) {
                Log.e(TAG, "ClientTask socket SocketException" + " while sending to " + node);
                Log.e(TAG, e.getMessage());
                messenger.ordering().markOffline(node);
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException" + " while sending to " + node);
                Log.e(TAG, e.getMessage());
            }
        }
        return null;
    }
}