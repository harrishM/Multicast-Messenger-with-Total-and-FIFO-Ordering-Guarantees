package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

class ServerTask extends AsyncTask<ServerSocket, String, Void> {
    private static final String TAG = ServerTask.class.getName();
    private final GroupMessenger messenger;
    private final TextView tv;
    private static int sequence = 0;

    public ServerTask(TextView tv, GroupMessenger messenger) {
        this.tv = tv;
        this.messenger = messenger;
    }

    @Override
    protected Void doInBackground(ServerSocket... sockets) {
        ServerSocket serverSocket = sockets[0];
        while (true) {
            try (Socket socket = serverSocket.accept();
                 InputStream in = socket.getInputStream()) {
//                Log.d(TAG, "ServerTask created socket");
                ObjectInputStream objectInputStream = new ObjectInputStream(in);
                Payload payload = (Payload) objectInputStream.readObject();
                messenger.ordering().handle(payload);
                if (payload.getMessage() != null) {
                    publishProgress(payload.getMessage());
//                    Log.d(TAG, "ServerTask Received message: " + msg);
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "ServerTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ServerTask socket IOException");
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "ServerTask socket ClassNotFoundException");
            }
//            Log.d(TAG, "ServerTask background finished");
        }
    }



    protected void onProgressUpdate(String... strings) {
        String strReceived = strings[0].trim();
        tv.append(strReceived + "\t\n");
    }
}