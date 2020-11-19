package sealteamsixplayer;

import static org.junit.Assert.*;

import battlecode.common.*;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MinerTests
{
    // Set up tests following 3-As strategy (goes by different names, this is just how I learned it.
    // First, Arrange. Set up pre-existing state such that the result of the system under test is expected.
    // Second, Act. Call the unit code and store the result.
    // Third, Assert. Inspect the result and assert that it matches the expected result.

    /**
     * Assert true when Miner is next to the HQ
     */
    @Test
    public void trueWhenMinerIsNextToHq()
    {
        // Arrange.
        // Mock the API of any dependencies. We only want to test the logic in our system under test.
        RobotController rc = mock(RobotController.class);
        when(rc.canDepositSoup(Direction.NORTH)).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        // Set up our system with the minimum necessities for the expected outcome.
        Miner r = new Miner(rc);
        r.hqLocation = new MapLocation(1, 2);

        // Act.
        boolean b = r.isNextToHq();

        // Assert.
        assertTrue(b);
    }

    /**
     * Assert false when Miner is not next to the HQ
     */
    @Test
    public void falseWhenMinerIsNotNextToHq()
    {
        // Arrange.
        // Mock the API of any dependencies. We only want to test the logic in our system under test.
        RobotController rc = mock(RobotController.class);
        when(rc.canDepositSoup(Direction.NORTH)).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        // Set up our system with the minimum necessities for the expected outcome.
        Miner r = new Miner(rc);
        r.hqLocation = new MapLocation(10, 20);

        // Act.
        boolean b = r.isNextToHq();

        // Assert.
        assertFalse(b);
    }

    /**
     * Assert true when Miner is next to the Refinery
     */
    @Test
    public void trueWhenMinerIsNextToRefinery()
    {
        // Arrange.
        // Mock the API of any dependencies. We only want to test the logic in our system under test.
        RobotController rc = mock(RobotController.class);
        when(rc.canDepositSoup(Direction.NORTH)).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        // Set up our system with the minimum necessities for the expected outcome.
        Miner r = new Miner(rc);
        r.refineryLocation = new MapLocation(1, 2);

        // Act.
        boolean b = r.isNextToRefinery();

        // Assert.
        assertTrue(b);
    }

    /**
     * Assert true when Miner is not next to the Refinery
     */
    @Test
    public void falseWhenMinerIsNotNextToRefinery()
    {
        // Arrange.
        // Mock the API of any dependencies. We only want to test the logic in our system under test.
        RobotController rc = mock(RobotController.class);
        when(rc.canDepositSoup(Direction.NORTH)).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        // Set up our system with the minimum necessities for the expected outcome.
        Miner r = new Miner(rc);
        r.refineryLocation = new MapLocation(10, 20);

        // Act.
        boolean b = r.isNextToRefinery();

        // Assert.
        assertFalse(b);
    }

    /**
     * Assert designSchoolLocation is equal to one block north of miner location after calling tryBuildDesignSchool.
     */
    @Test
    public void buildsDesignSchoolWhenLocationIsValid()
    {
        // Arrange.
        RobotType type = RobotType.DESIGN_SCHOOL;
        Direction dir = Direction.NORTH;
        RobotController rc = mock(RobotController.class);
        when(rc.canBuildRobot(type, dir)).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        Miner r = new Miner(rc);
        r.hqLocation = new MapLocation(10, 10);

        // Act.
        try
        {
            r.tryBuildDesignSchool();
        } catch (Exception e)
        {
            // Failing on exception is technically an assertion.
            fail("Test failed with exception " + e.getLocalizedMessage());
        }

        // Assert.
        assertEquals(r.designSchoolLocation, rc.getLocation().add(dir));
    }

    @Test
    public void closestSoupReturnsNullWhenSoupLocationsEmpty()
    {
        RobotController rc = mock(RobotController.class);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        Miner m = new Miner(rc);

        MapLocation soup = m.closestSoupLocation();

        assertNull(soup);
    }

    @Test
    public void closestSoupReturnsExpectedLocationFromSoupLocations()
    {
        RobotController rc = mock(RobotController.class);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        Miner m = new Miner(rc);
        MapLocation closeLocation = new MapLocation(2, 2);
        MapLocation farLocation = new MapLocation(10, 10);
        m.soupLocations.add(closeLocation);
        m.soupLocations.add(farLocation);

        MapLocation closest = m.closestSoupLocation();

        assertEquals(closest, closeLocation);
    }

    @Test
    public void minerIsFullWhenFull()
    {
        RobotController rc = mock(RobotController.class);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        when(rc.getSoupCarrying()).thenReturn(RobotType.MINER.soupLimit);
        Miner m = new Miner(rc);

        boolean full = m.isFull();

        assertTrue(full);
    }

    @Test
    public void minerIsNotFullWhenNotFull()
    {
        RobotController rc = mock(RobotController.class);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        when(rc.getSoupCarrying()).thenReturn(5);
        Miner m = new Miner(rc);

        boolean full = m.isFull();

        assertFalse(full);
    }

    @Test
    public void minerRunsWithoutException()
    {
        try
        {
            RobotController rc = setupRobotController();
            Miner m = new Miner(rc);
            m.go();
        } catch (GameActionException e)
        {
            fail("Failed with exception " + e.getLocalizedMessage());
        }
    }

    @Test
    public void fullMinerTriesToMoveToRefinery()
    {
        try
        {
            RobotController rc = setupRobotController();
            when(rc.getSoupCarrying()).thenReturn(RobotType.MINER.soupLimit);
            Miner m = new Miner(rc);
            m.hqLocation = new MapLocation(3,3);
            m.go();

        } catch (GameActionException e)
        {
            e.printStackTrace();
            fail("Failed with exception " + e.getLocalizedMessage());
        }
    }

    @Test
    public void builderMinerBuilds()
    {
        try
        {
            RobotController rc = setupRobotController();
            RobotInfo HQ = new RobotInfo(1,Team.B,RobotType.HQ,
                    0,false,0,0,0, new MapLocation(1,1));
            RobotInfo [] list = {HQ};
            when(rc.getTeamSoup()).thenReturn(750);
            when(rc.senseNearbyRobots()).thenReturn(list);
            Miner m = new Miner(rc);
            m.hqLocation = new MapLocation(1,1);
            m.builder = true;
            m.go();

        } catch (GameActionException e)
        {
            e.printStackTrace();
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
