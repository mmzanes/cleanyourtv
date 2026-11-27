package io.github.mmzanes.bootopen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.SystemClock;
import android.util.Log;

/**
 * After boot (or the TEST action), waits MIN_DELAY_MS, then up to MAX_WAIT_MS for a network, and opens the target.
 * Runs inside the broadcast (goAsync), well within the 60 s a background receiver has, so nothing is left to a
 * job that Fire OS may postpone for a rarely opened app.
 */
public class BootReceiver extends BroadcastReceiver {
    static final String TEST = "io.github.mmzanes.bootopen.TEST";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        String action = intent.getAction();
        Log.i(Opener.TAG, "received " + action);
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action) && !TEST.equals(action)) return;
        Context app = ctx.getApplicationContext();
        PendingResult result = goAsync();
        new Thread(() -> {
            try {
                SystemClock.sleep(Opener.MIN_DELAY_MS);
                long end = SystemClock.elapsedRealtime() + Opener.MAX_WAIT_MS - Opener.MIN_DELAY_MS;
                while (!online(app) && SystemClock.elapsedRealtime() < end) SystemClock.sleep(1_000);
                Log.i(Opener.TAG, "network " + (online(app) ? "up" : "still down, opening anyway"));
                Opener.open(app);
            } finally {
                result.finish();
            }
        }, "bootopen").start();
    }

    static boolean online(Context ctx) {
        ConnectivityManager cm = ctx.getSystemService(ConnectivityManager.class);
        if (cm == null) return false;
        Network n = cm.getActiveNetwork();
        NetworkCapabilities caps = n == null ? null : cm.getNetworkCapabilities(n);
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
