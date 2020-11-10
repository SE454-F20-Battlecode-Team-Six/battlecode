package sealteamsixplayer;

import battlecode.common.*;

import java.util.ArrayList;

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
    int turnCount = 0, round = 0;
    float cooldown = 0;
    Communication comm;

    public Robot (RobotController rc)
    {
        this.rc = rc;
        comm = new Communication(rc);
    }

    public void go()
    {
        // Put shared Robot turn code here.
        turnCount++;
        round = rc.getRoundNum();
        cooldown = rc.getCooldownTurns();
    }

    /**
     * Returns the direction from the robot to the location given.
     *
     * Shorthand for <code>rc.getLocation().directionTo(location)</code>
     */
    protected Direction to(MapLocation location) {
        return rc.getLocation().directionTo(location);
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
