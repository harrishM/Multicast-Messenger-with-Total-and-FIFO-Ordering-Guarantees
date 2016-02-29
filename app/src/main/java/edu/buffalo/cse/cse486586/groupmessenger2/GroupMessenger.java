package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

public class GroupMessenger extends Application {
    private static final String TAG = GroupMessenger.class.getName();

    private Ordering ordering;


    private static final int SERVER_PORT = 10000;
    private static final List<String> NODES;
    private String myPort;

    static {
        NODES = new ArrayList<>(5);
        NODES.add("11108");
        NODES.add("11112");
        NODES.add("11116");
        NODES.add("11120");
        NODES.add("11124");
    }

    private Uri uri;


    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * Calculate the port number that this AVD listens on.
         * It is just a hack that I came up with to get around the networking limitations of AVDs.
         * The explanation is provided in the PA1 spec.
         */
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        this.myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        ordering = new Ordering(this);

        this.uri = new Uri.Builder()
                .scheme("content")
                .authority("edu.buffalo.cse.cse486586.groupmessenger2.provider")
                .build();
    }

    public String myPort() {
        return myPort;
    }

    public static int serverPort() {
        return SERVER_PORT;
    }

    public static List<String> nodes() {
        return NODES;
    }

    public Ordering ordering() {
        return ordering;
    }

    public Uri contentProviderUri() {
        return uri;
    }
}
