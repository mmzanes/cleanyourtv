package io.github.mmzanes.bootopen;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/** Opening the helper by hand opens the target app now: the first open after install also lets it run at boot. */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!Opener.open(this)) {
            Toast.makeText(this, getString(R.string.not_found, Opener.target(this)), Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
