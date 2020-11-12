package sealteamsixplayer;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HQTests
{
    @Test
    public void runTheHq()
    {
        RobotInfo[] noRobots = {};
        RobotController rc = mock(RobotController.class);
        when(rc.senseNearbyRobots()).thenReturn(noRobots);
        when(rc.getRoundNum()).thenReturn(1);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        when(rc.canSubmitTransaction(any(), anyInt())).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(1);
        when(rc.getCooldownTurns()).thenReturn((float) 0.25);
        HQ r = new HQ(rc);

        r.go();

    }
}
