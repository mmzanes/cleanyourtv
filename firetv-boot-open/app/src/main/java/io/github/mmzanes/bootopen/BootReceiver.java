package io.github.mmzanes.bootopen;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/** After boot, schedules OpenJob: it runs once a network is up, or after MAX_WAIT_MS without one. */
public class BootReceiver extends BroadcastReceiver {
    static final int JOB_ID = 1;

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        JobScheduler js = ctx.getSystemService(JobScheduler.class);
        if (js == null) return;
        js.schedule(new JobInfo.Builder(JOB_ID, new ComponentName(ctx, OpenJob.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(Opener.MIN_DELAY_MS)
                .setOverrideDeadline(Opener.MAX_WAIT_MS)
                .build());
    }
}
