package sealteamsixplayer;

import battlecode.common.*;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MobileTests
{
    @Test
    public void exploreProducesExpectedMapLocation()
    {
        RobotController rc = mock(RobotController.class);
        when(rc.getMapWidth()).thenReturn(12);
        when(rc.getMapHeight()).thenReturn(12);
        Mobile m = new Mobile(rc);

        // exploreLocation should return with width and height in { 12/6, 12/2, 12/6*5 }
        List<Integer> values = Arrays.asList(12/6, 12/2, 12/6*5);

        // Act.
        MapLocation exploreLocation = m.explore();

        // Assert.
        assertTrue(values.contains(exploreLocation.x));
        assertTrue(values.contains(exploreLocation.y));
    }

    @Test
    public void atLocationTrueWhenAtLocation()
    {
        try
        {
            RobotController rc = setupRobotController();
            Mobile m = new Mobile(rc);

            assertTrue(m.atLocation(new MapLocation(2,2)));
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
            fail("Test failed with exception " + e.getLocalizedMessage());
        }
    }

    @Test
    public void mobileChecksBlockchainEveryFiveRounds()
    {
        try
        {
            RobotController rc = setupRobotController();
            int teamKey = 66554433;
            Transaction[] txns = {
                new Transaction(1, new int[]{teamKey, LocationType.HQ_LOCATION.ordinal(), 3, 3}, 123),
                new Transaction(1, new int[]{teamKey, LocationType.DESIGN_SCHOOL_LOCATION.ordinal(), 4, 4}, 123),
                new Transaction(1, new int[]{teamKey, LocationType.REFINERY_LOCATION.ordinal(), 5, 5}, 123),
                new Transaction(1, new int[]{teamKey, LocationType.FR_LOCATION.ordinal(), 6, 6}, 123),
                new Transaction(1, new int[]{teamKey, LocationType.ENEMY_HQ_LOCATION.ordinal(), 7, 7}, 123),
                new Transaction(1, new int[]{teamKey, LocationType.SOUP_LOCATION.ordinal(), 8, 8}, 123),
                new Transaction(1, new int[]{teamKey, LocationType.EMPTIED_SOUP_LOCATION.ordinal(), 8, 8}, 123),
            };
            when(rc.getBlock(anyInt())).thenReturn(txns);

            Mobile m = new Mobile(rc);

            // set turnCount to x-1 where x % 5 == 0
            m.turnCount = 24;

            m.go();
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
            fail("Test failed with exception " + e.getLocalizedMessage());
        }
    }

    public RobotController setupRobotController() throws GameActionException
    {
        MapLocation myLoc = new MapLocation(1, 1);
        RobotInfo nmy = new RobotInfo(
            2,
            Team.B,
            RobotType.MINER,
            0,
            false,
            0,
            7,
            0,
            new MapLocation(4, 4));
        RobotInfo[] nmyBots = {nmy};
        MapLocation[] aSoup = {new MapLocation(2,2)};

        RobotController rc = mock(RobotController.class);

        when(rc.canMineSoup(Direction.NORTH)).thenReturn(true);
        when(rc.canMove(any())).thenReturn(true);
        when(rc.canSenseLocation(any())).thenReturn(true);
        when(rc.getCurrentSensorRadiusSquared()).thenReturn(100);
        when(rc.getLocation()).thenReturn(myLoc);
        when(rc.getRoundNum()).thenReturn(50);
        when(rc.getSoupCarrying()).thenReturn(5);
        when(rc.getTeam()).thenReturn(Team.A);
        when(rc.isReady()).thenReturn(true);
        when(rc.senseElevation(any())).thenReturn(3);
        when(rc.senseNearbyRobots(anyInt(), any(Team.class)))
            .thenReturn(nmyBots);
        when(rc.senseNearbySoup()).thenReturn(aSoup);
        when(rc.senseSoup(any())).thenReturn(0);

        return rc;
    }
}
