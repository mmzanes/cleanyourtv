package io.github.mmzanes.bootopen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/** Opens the target app: its TV entry point, or its phone one. */
final class Opener {
    /** Seconds after boot before the app opens, so Amazon's Home has finished coming up underneath. */
    static final long MIN_DELAY_MS = 5_000;
    /** Longest wait for a network; the app opens then even without one. */
    static final long MAX_WAIT_MS = 120_000;

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
        Intent i = intent(ctx.getPackageManager(), target(ctx));
        if (i == null) return false;
        try {
            ctx.startActivity(i);
            return true;
        } catch (RuntimeException e) {   // the app went away between the lookup and the start
            return false;
        }
    }
}
