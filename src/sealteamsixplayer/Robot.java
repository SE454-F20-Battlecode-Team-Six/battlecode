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

    public Robot (RobotController rc)
    {
        this.rc = rc;
    }

    public void go()
    {
        // Put shared Robot turn code here.
        turnCount++;
    }

    private boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     */
    protected boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
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
