package sealteamsixplayer;

import static org.junit.Assert.*;

import battlecode.common.*;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LandscaperTests {
    // Set up tests following 3-As strategy (goes by different names, this is just how I learned it.
    // First, Arrange. Set up pre-existing state such that the result of the system under test is expected.
    // Second, Act. Call the unit code and store the result.
    // Third, Assert. Inspect the result and assert that it matches the expected result.

    /**
     * Assert true when landscaper is next to HQ
     */
    @Test
    public void trueWhenLandscaperIsNextToHQ()
    {
        // Arrange.
        // Mock the API of any dependencies. We only want to test the logic in our system under test.
        RobotController rc = mock(RobotController.class);

        when(rc.canMove(Direction.NORTH)).thenReturn(false);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        // Set up our system with the minimum necessities for the expected outcome.
        Landscaper r = new Landscaper(rc);
        r.hqLocation = new MapLocation(1, 2);

        // Act.
        boolean b = r.checkForAdjHQ();

        // Assert.
        assertTrue(b);
    }

    /**
     * Assert false when landscaper is not next to HQ
     */
    @Test
    public void falseWhenLandscaperIsNotNextToHQ()
    {
        // Arrange.
        // Mock the API of any dependencies. We only want to test the logic in our system under test.
        RobotController rc = mock(RobotController.class);

        when(rc.canMove(Direction.NORTH)).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        // Set up our system with the minimum necessities for the expected outcome.
        Landscaper r = new Landscaper(rc);
        r.hqLocation = new MapLocation(1, 3);

        // Act.
        boolean b = r.checkForAdjHQ();

        // Assert.
        assertFalse(b);
    }

    /**
     * Assert true when the wall is fully built around the hq.
     * @throws GameActionException
     */
    @Test
    public void trueWhenWallIsFullyBuilt() throws GameActionException
    {
        RobotController rc = mock(RobotController.class);
        Landscaper r = new Landscaper(rc);

        r.hqLocation = new MapLocation(3,3);
        int [][] wallCoords = new int[8][2];
        wallCoords[0][0] = r.hqLocation.x;
        wallCoords[0][1] = r.hqLocation.y+1;
        wallCoords[1][0] = r.hqLocation.x+1;
        wallCoords[1][1] = r.hqLocation.y+1;
        wallCoords[2][0] = r.hqLocation.x+1;
        wallCoords[2][1] = r.hqLocation.y;
        wallCoords[3][0] = r.hqLocation.x+1;
        wallCoords[3][1] = r.hqLocation.y-1;
        wallCoords[4][0] = r.hqLocation.x;
        wallCoords[4][1] = r.hqLocation.y-1;
        wallCoords[5][0] = r.hqLocation.x-1;
        wallCoords[5][1] = r.hqLocation.y-1;
        wallCoords[6][0] = r.hqLocation.x-1;
        wallCoords[6][1] = r.hqLocation.y;
        wallCoords[7][0] = r.hqLocation.x-1;
        wallCoords[7][1] = r.hqLocation.y+1;
        for (int i = 0; i< 8; ++i)
        {
            when(rc.getLocation()).thenReturn(new MapLocation(wallCoords[i][0],wallCoords[i][1]));
            MapLocation diggingTile = rc.getLocation().add(r.to(r.hqLocation).opposite());
            when(rc.senseElevation(new MapLocation(wallCoords[i][0], wallCoords[i][1]))).thenReturn(11);
            when(rc.senseElevation(diggingTile)).thenReturn(0);
        }
        boolean b = r.checkWall();

        assertTrue(b);
    }

    /**
     * Assert false when the wall is not fully built around the hq.
     * @throws GameActionException
     */
    @Test
    public void falseWhenWallIsNotFullyBuilt() throws GameActionException
    {
        RobotController rc = mock(RobotController.class);
        Landscaper r = new Landscaper(rc);

        r.hqLocation = new MapLocation(3,3);
        int [][] wallCoords = new int[8][2];
        wallCoords[0][0] = r.hqLocation.x;
        wallCoords[0][1] = r.hqLocation.y+1;
        wallCoords[1][0] = r.hqLocation.x+1;
        wallCoords[1][1] = r.hqLocation.y+1;
        wallCoords[2][0] = r.hqLocation.x+1;
        wallCoords[2][1] = r.hqLocation.y;
        wallCoords[3][0] = r.hqLocation.x+1;
        wallCoords[3][1] = r.hqLocation.y-1;
        wallCoords[4][0] = r.hqLocation.x;
        wallCoords[4][1] = r.hqLocation.y-1;
        wallCoords[5][0] = r.hqLocation.x-1;
        wallCoords[5][1] = r.hqLocation.y-1;
        wallCoords[6][0] = r.hqLocation.x-1;
        wallCoords[6][1] = r.hqLocation.y;
        wallCoords[7][0] = r.hqLocation.x-1;
        wallCoords[7][1] = r.hqLocation.y+1;
        for (int i = 0; i< 8; ++i)
        {
            when(rc.getLocation()).thenReturn(new MapLocation(wallCoords[i][0],wallCoords[i][1]));
            MapLocation diggingTile = rc.getLocation().add(r.to(r.hqLocation).opposite());
            when(rc.senseElevation(new MapLocation(wallCoords[i][0], wallCoords[i][1]))).thenReturn(11);
            when(rc.senseElevation(diggingTile)).thenReturn(5);
        }
        boolean b = r.checkWall();

        assertFalse(b);
    }

    /**
     * Assert false when the wall is not fully built around the hq.
     * @throws GameActionException
     */
    @Test
    public void notEqualsWhenPatrolTileIsNotCurrentTile() throws GameActionException
    {
        RobotController rc = mock(RobotController.class);
        Landscaper r = new Landscaper(rc);
        when(rc.getLocation()).thenReturn(new MapLocation(2,2));
        r.hqLocation = new MapLocation(3,3);

        Direction d = r.patrolWall();
        assertNotEquals(d, Direction.CENTER);
    }

}
