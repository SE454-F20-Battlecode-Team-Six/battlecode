package sealteamsixplayer;

import battlecode.common.*;
import java.util.ArrayList;
import java.util.Iterator;

import static sealteamsixplayer.LocationType.*;

/***
 * Extend Robot class with code specific to mobile robots (e.g. Miner, Landscaper, Drone)
 */
public class Mobile extends Robot
{
    MapLocation hqLocation;
    MapLocation refineryLocation;
    MapLocation designSchoolLocation;
    MapLocation fulfillCenterLocation;
    MapLocation enemyHqLocation;
    ArrayList<MapLocation> soupLocations = new ArrayList<>();
    int failedMoveCount; //help with getting unstuck
    boolean isDrone = false;
    java.util.Random randVal = new java.util.Random();

    public Mobile(RobotController rc)
    {
        super(rc);
    }

    public void go()
    {
        super.go();

        try
        {
            senseLocations();
            // Check the blockchain every 5 turns.
            if (turnCount % 5 == 0)
                checkBlockchain();
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Convenience method for checking if we are "at location". That is to say, on top of or next to.
     */
    public boolean atLocation(MapLocation target)
    {
        return rc.getLocation().equals(target) || rc.getLocation().isAdjacentTo(target);
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return
     */
    protected boolean tryMove(Direction dir, boolean isDrone) throws GameActionException
    {
        if (rc.isReady() && rc.canMove(dir))
        {
            if(isDrone)
            {
                System.out.println(rc.getType() + " moving " + dir + "--" + rc.getLocation());
                rc.move(dir);
                return true;
            }
            else
            {
                if (!rc.senseFlooding(rc.adjacentLocation(dir)))
                {
                    System.out.println(rc.getType() + " moving " + dir + "--" + rc.getLocation());
                    rc.move(dir);
                    return true;
                }
                return false;
            }
        }
        else
            return false;
    }

    /**
     * Walk in the "general" direction given by <code>dir</code>. Calculates forward as any of:
     *      forward, left-forward, left, right-forward, right
     * @returns true if the robot was able to move, false otherwise.
     */
    public boolean goTo(Direction dir) throws GameActionException
    {
        Direction[] forward = {
            dir,
            dir.rotateLeft(),
            dir.rotateRight(),
            dir.rotateLeft().rotateLeft(),
            dir.rotateRight().rotateRight()
        };

        // Try to move forward.
        for (Direction d : forward)
        {
            if (tryMove(d, isDrone))
                return true;
        }
        System.err.println("Failed to move " + dir + "!");
        System.err.println("ready:" + rc.isReady() +
            " can move:" + rc.canMove(dir) +
            " can sense:" + rc.canSenseLocation(rc.adjacentLocation(dir)) +
            " robot:" + rc.senseRobotAtLocation(rc.adjacentLocation(dir)) +
            " flooding:" + rc.senseFlooding(rc.adjacentLocation(dir)));
        return false;
    }

    /**
     * Walk in the "general" direction given by <code>dir</code>. Calculates forward as any of:
     *      forward, left-forward, left, right-forward, right
     * @returns true if the robot was able to move, false otherwise.
     */
    public boolean goTo(Direction dir, boolean isDrone) throws GameActionException
    {
        Direction[] forward = {
          dir,
          dir.rotateLeft(),
          dir.rotateRight(),
          dir.rotateLeft().rotateLeft(),
          dir.rotateRight().rotateRight()
        };

        // Try to move forward.
        for (Direction d : forward)
        {
            if (tryMove(d, isDrone))
                return true;
        }
        System.err.println("Failed to move " + dir + "!");
        System.err.println("ready:" + rc.isReady() +
          " can move:" + rc.canMove(dir) +
          " can sense:" + rc.canSenseLocation(rc.adjacentLocation(dir)) +
          " robot:" + rc.senseRobotAtLocation(rc.adjacentLocation(dir)) +
          " flooding:" + rc.senseFlooding(rc.adjacentLocation(dir)));
        return false;
    }

    /**
     * Convenience wrapper for goTo(MapLocation).
     */
    public boolean goTo(MapLocation loc) throws GameActionException
    {
        return goTo(to(loc));
    }

    /**
     * Pick a map quadrant to walk to randomly. Quadrants are defined by flipping robot location
     * horizontally, vertically, or diagonally.
     */
    public MapLocation explore()
    {
        float h = rc.getMapHeight() - 1;
        float w = rc.getMapWidth() - 1;
        MapLocation[] mapExplorePoints = new MapLocation[9];
        mapExplorePoints[0] = new MapLocation((int)w/6, (int)(.666 * h  + h*.166));//top left
        mapExplorePoints[1] = new MapLocation((int)(.333 * w + w*.166), (int)(.666 * h + h*.166));//top mid
        mapExplorePoints[2] = new MapLocation((int)(.666 * w + w*.166), (int)(.666 * h + h*.166));//top right
        mapExplorePoints[3] = new MapLocation((int)(1*.166), (int)(.333 * h + h*.166));//mid left
        mapExplorePoints[4] = new MapLocation((int)(.333 * w + w*.166), (int)(.333 * h + h*.166));//middle
        mapExplorePoints[5] = new MapLocation((int)(.666 * w + w*.166), (int)(.333 * h + h*.166));//mid right
        mapExplorePoints[6] = new MapLocation((int)(w*.166), (int)(h*.166));//bot left
        mapExplorePoints[7] = new MapLocation((int)(.333 * w + w*.166), (int)(h*.166));//bot mid
        mapExplorePoints[8] = new MapLocation((int)(.666 * w + w*.166), (int)(h*.166));//bot right
        //double r = Math.random(); //this feeds the same value to every unit
        int r = (int)(randVal.nextDouble() * 9); //this seems to give actual randomness...not sure what the difference is.
        System.out.println("My random value for exploring is: " + r);
        rc.setIndicatorLine(rc.getLocation(), mapExplorePoints[r], 200, 0, 0);
        return mapExplorePoints[r];
    }

    /**
     * Senses locations in within the current sensor radius around the robot.
     *
     * Currently:
     * 1) Tries to sense enemy HQ.
     * 2) Removes soup spots that are soupLocations but are empty.
     * 3) Adds new soup locations.
     *
     * @throws GameActionException
     */
    public void senseLocations() throws GameActionException
    {
        int radius = rc.getCurrentSensorRadiusSquared();
        int myHeight = rc.senseElevation(rc.getLocation());

        // Try to sense enemy HQ
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, rc.getTeam().opponent());
        for (RobotInfo enemy : enemies)
        {
            if (enemy.type == RobotType.HQ)
            {
                enemyHqLocation = enemy.location;
                comm.sendLocation(ENEMY_HQ_LOCATION, enemyHqLocation);
            }
        }

        // Remove old empty soup spots. Need to manually iterate over list
        // to avoid a ConcurrentModificationException.
        Iterator<MapLocation> i = soupLocations.iterator();
        while (i.hasNext())
        {
            MapLocation soup = i.next();
            if (rc.canSenseLocation(soup) && rc.senseSoup(soup) == 0)
            {
                i.remove();
                comm.sendLocation(EMPTIED_SOUP_LOCATION, soup);
            }
        }

        // Add new soup locations
        MapLocation[] soups = rc.senseNearbySoup();
        for (MapLocation soup : soups)
        {
            // Avoid soup that's too hard to walk to. Probably a better way of doing this, but this is quick.
            int elevationChange = Math.abs(myHeight - rc.senseElevation(soup));
            if (elevationChange <= 3 && !soupLocations.contains(soup))
            {
                soupLocations.add(soup);
                comm.sendLocation(SOUP_LOCATION, soup);
            }
        }
    }

    /**
     * Translate a call to comm.getLocations into actionable locations by parsing the
     * given list of <code>TypedMapLocation</code>.
     */
    protected void checkBlockchain() throws GameActionException
    {
        ArrayList<TypedMapLocation> locations = comm.getLocations();

        for (TypedMapLocation typedMapLocation : locations)
        {
            switch (typedMapLocation.type())
            {
                case HQ_LOCATION:
                    if (hqLocation == null)
                        hqLocation = typedMapLocation.location();
                    break;
                case DESIGN_SCHOOL_LOCATION:
                    if (designSchoolLocation == null)
                        designSchoolLocation = typedMapLocation.location();
                    break;
                case REFINERY_LOCATION:
                    if (refineryLocation == null)
                        refineryLocation = typedMapLocation.location();
                    break;
                case FR_LOCATION:
                    if (fulfillCenterLocation == null)
                        fulfillCenterLocation = typedMapLocation.location();
                    break;
                case ENEMY_HQ_LOCATION:
                    if (enemyHqLocation == null)
                        enemyHqLocation = typedMapLocation.location();
                    break;
                case SOUP_LOCATION:
                    MapLocation newSoupLoc = typedMapLocation.location();
                    if (!soupLocations.contains(newSoupLoc))
                        soupLocations.add(newSoupLoc);
                    break;
                case EMPTIED_SOUP_LOCATION:
                    soupLocations.remove(typedMapLocation.location());
                    break;
                default:
                    throw new GameActionException(
                        GameActionExceptionType.INTERNAL_ERROR,
                        "Got an invalid location type when parsing the block chain.");
            }
        }
    }
}
