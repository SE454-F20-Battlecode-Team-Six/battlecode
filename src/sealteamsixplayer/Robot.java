package sealteamsixplayer;

import battlecode.common.*;

/**
 * Base class for all Robots. Contains shared code including the go method.
 */
public class Robot
{
    static Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST
    };

    protected RobotController rc;
    int turnCount = 0;
    Communication comm;

    MapLocation hqLocation;

    public Robot (RobotController rc)
    {
        this.rc = rc;
        comm = new Communication(rc);
    }

    public void go()
    {
        // Put shared Robot turn code here.
        turnCount++;
    }

    protected Direction to(MapLocation location) {
        return rc.getLocation().directionTo(location);
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     */
    protected boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.adjacentLocation(dir))) {
            rc.move(dir);
            return true;
        } else return false;
    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    protected Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }
}
