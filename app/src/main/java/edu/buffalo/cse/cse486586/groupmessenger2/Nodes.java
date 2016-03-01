package edu.buffalo.cse.cse486586.groupmessenger2;

import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Nodes {
    private static final String TAG = Nodes.class.getName();

    private static final int SERVER = 10000;
    private static final Map<String, Integer> NODES; // <port,  pid>
    private static final CopyOnWriteArraySet<String> OFFLINE_NODES = new CopyOnWriteArraySet<>();

    static {
        NODES = new LinkedHashMap<>(5);
        NODES.put("11108", 1);
        NODES.put("11112", 2);
        NODES.put("11116", 3);
        NODES.put("11120", 4);
        NODES.put("11124", 5);
    }

    public static int server() {
        return SERVER;
    }

    public static Set<String> all() {
        return NODES.keySet();
    }

    public static Set<String> liveNodes() {
        Set<String> live = new HashSet<>(NODES.keySet());
        live.removeAll(OFFLINE_NODES);
        return live;
    }

    public static int pidFor(String node) {
        return NODES.get(node);
    }

    public static int liveNodeCount() {
        return NODES.size() - OFFLINE_NODES.size();
    }

    public static boolean isOffline(String node) {
        return OFFLINE_NODES.contains(node);
    }

    public static void markOffline(String node) {
        if (OFFLINE_NODES.add(node)) {
            Log.d(TAG, "Marked node :" + node + " is offline");
        }
    }
}
