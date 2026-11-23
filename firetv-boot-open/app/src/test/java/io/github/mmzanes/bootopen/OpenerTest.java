package io.github.mmzanes.bootopen;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OpenerTest {
    @Test public void aPackageNameIsValid() {
        assertTrue(Opener.valid("com.tid.enjoy"));
        assertTrue(Opener.valid("com.netflix.ninja"));
    }

    @Test public void emptyOrMalformedOpensNothing() {
        assertFalse(Opener.valid(null));
        assertFalse(Opener.valid(""));
        assertFalse(Opener.valid("enjoy"));
        assertFalse(Opener.valid("com..tid"));
        assertFalse(Opener.valid("com.tid.enjoy "));
    }

    @Test public void theWaitEndsAfterTheMinimumDelay() {
        assertTrue(Opener.MAX_WAIT_MS > Opener.MIN_DELAY_MS);
    }
}
