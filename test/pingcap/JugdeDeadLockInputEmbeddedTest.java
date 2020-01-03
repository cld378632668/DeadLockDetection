package pingcap;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mac on 2020/1/3.
 */
public class JugdeDeadLockInputEmbeddedTest {
    @Test
    public void jugdeDeadLockSingleResource() throws Exception {
        JugdeDeadLockInputEmbedded jugdeDeadLockInputEmbedded = new JugdeDeadLockInputEmbedded();
        jugdeDeadLockInputEmbedded.jugdeDeadLockSingleResource();

    }

}