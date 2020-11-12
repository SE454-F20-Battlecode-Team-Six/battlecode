package sealteamsixplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DesignSchoolTests
{
    @Test
    public void canBuildWhenNeeded() throws GameActionException
    {
        RobotController rc = mock(RobotController.class);
        when(rc.isReady()).thenReturn(true);
        when(rc.canBuildRobot(RobotType.LANDSCAPER, Direction.NORTH)).thenReturn(true);

        DesignSchool r = new DesignSchool(rc);

        assertTrue(r.tryBuild(RobotType.LANDSCAPER, Direction.NORTH));
    }

    @Test
    public void buildsALandscaperWhenLessThanFourExist()
    {
        RobotController rc = mock(RobotController.class);
        when(rc.isReady()).thenReturn(true);
        when(rc.canBuildRobot(RobotType.LANDSCAPER, Direction.NORTH)).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(1);
        when(rc.getCooldownTurns()).thenReturn((float) 0.25);
        DesignSchool r = new DesignSchool(rc);

        assertEquals(r.landscaperCount, 0);

        r.go();

        assertEquals(r.landscaperCount, 1);
    }
}
