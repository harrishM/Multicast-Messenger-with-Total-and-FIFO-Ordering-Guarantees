package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class GroupMessenger extends Application {
    private static final String TAG = GroupMessenger.class.getName();

    private Ordering ordering;
    private String myPort;
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

        this.uri = new Uri.Builder()
                .scheme("content")
                .authority("edu.buffalo.cse.cse486586.groupmessenger2.provider")
                .build();
        ordering = new Ordering(this);
    }

    public String myPort() {
        return myPort;
    }
    
    public Ordering ordering() {
        return ordering;
    }

    public Uri contentProviderUri() {
        return uri;
    }
}
