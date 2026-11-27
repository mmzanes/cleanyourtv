package io.github.mmzanes.bootopen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/** Opens the target app: its TV entry point, or its phone one. */
final class Opener {
    /** adb logcat -s BootOpen */
    static final String TAG = "BootOpen";
    /** Seconds after boot before the app opens, so Amazon's Home has finished coming up underneath. */
    static final long MIN_DELAY_MS = 5_000;
    /** Longest wait for a network, from the boot notice; the app opens then even without one. Under the 60 s a
     *  background broadcast receiver may run. */
    static final long MAX_WAIT_MS = 45_000;

    private Opener() {
    }

    static String target(Context ctx) {
        return ctx.getString(R.string.target_package).trim();
    }

    /** null when the package is empty, missing or disabled: nothing opens. */
    static Intent intent(PackageManager pm, String pkg) {
        if (!valid(pkg)) return null;
        Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
        if (i == null) i = pm.getLaunchIntentForPackage(pkg);
        if (i != null) i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return i;
    }

    static boolean valid(String pkg) {
        return pkg != null && pkg.matches("[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+");
    }

    /** True when the app was opened. */
    static boolean open(Context ctx) {
        String pkg = target(ctx);
        Intent i = intent(ctx.getPackageManager(), pkg);
        if (i == null) {
            Log.w(TAG, "not found or disabled: " + pkg);
            return false;
        }
        try {
            ctx.startActivity(i);
            Log.i(TAG, "opened " + pkg);
            return true;
        } catch (RuntimeException e) {   // the app went away between the lookup and the start
            Log.w(TAG, "could not open " + pkg, e);
            return false;
        }
    }
}
