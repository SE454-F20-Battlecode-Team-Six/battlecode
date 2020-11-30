package sealteamsixplayer;

import battlecode.common.*;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeliveryDroneTests
{
    @Test
    public void droneRuns() {
        try
        {
            RobotController rc = setupRobotController();
            DeliveryDrone d = new DeliveryDrone(rc);
            d.go();
        } catch (GameActionException e)
        {
            fail("Failed with exception " + e.getLocalizedMessage());
        }
    }

    @Test
    public void droneRunsAttackAfterTurn400() {
        try
        {
            RobotController rc = setupRobotController();
            when(rc.getRoundNum()).thenReturn(400);
            DeliveryDrone d = new DeliveryDrone(rc);
            d.go();
        } catch (GameActionException e)
        {
            fail("Failed with exception " + e.getLocalizedMessage());
        }
    }

    @Test
    public void droneDropsInOceanIfHolding() {
        try
        {
            RobotController rc = setupRobotController();
            when(rc.isCurrentlyHoldingUnit()).thenReturn(true);
            DeliveryDrone d = new DeliveryDrone(rc);
            d.go();
        } catch (GameActionException e)
        {
            fail("Failed with exception " + e.getLocalizedMessage());
        }
    }

    @Test
    public void droneUpdatesExploreLocation() {
        try
        {
            RobotController rc = setupRobotController();
            when(rc.getBlock(anyInt())).thenReturn(
                new Transaction[] {
                    new Transaction(3, new int[] {1, 2, 3, 4, 5, 6, 7}, 1)
                });
            DeliveryDrone d = new DeliveryDrone(rc);
            d.turnCount = 49;
            d.go();
        } catch (GameActionException e)
        {
            fail("Failed with exception " + e.getLocalizedMessage());
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
        when(rc.senseNearbyRobots()).thenReturn(nmyBots);
        when(rc.senseNearbyRobots(anyInt(), any(Team.class)))
            .thenReturn(nmyBots);
        when(rc.senseNearbySoup()).thenReturn(aSoup);
        when(rc.senseSoup(any())).thenReturn(0);

        return rc;
    }
}
