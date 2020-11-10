package sealteamsixplayer;

import battlecode.common.RobotController;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class VaporatorTests
{
    /**
     * This just tests the Vaporator constructor.
     */
    @Test
    public void canCreateVaporator()
    {
        RobotController rc = mock(RobotController.class);
        Robot r = new Vaporator(rc);
        assertNotNull(r);
    }
}
