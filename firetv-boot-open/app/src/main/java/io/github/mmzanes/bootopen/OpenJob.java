package io.github.mmzanes.bootopen;

import android.app.job.JobParameters;
import android.app.job.JobService;

/** Opens the target app once, then is done. */
public class OpenJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Opener.open(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
