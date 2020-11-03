package sealteamsixplayer;

import battlecode.common.*;
import java.util.ArrayList;
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
    protected boolean tryMove(Direction dir) throws GameActionException
    {

        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.adjacentLocation(dir)))
        {
            System.out.println(rc.getType() + " moving " + dir + "--" + rc.getLocation());
            rc.move(dir);
            return true;
        }
        else
            return false;
    }

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
            if (tryMove(d))
                return true;
        }
        System.out.println("Failed to move " + dir + "!");
        return false;
    }

    public boolean goTo(MapLocation loc) throws GameActionException
    {
        return goTo(to(loc));
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

        // Try to sense enemy HQ
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, enemyTeam);
        for (RobotInfo enemy : enemies)
        {
            if (enemy.type == RobotType.HQ)
            {
                enemyHqLocation = enemy.location;
                comm.sendLocation(ENEMY_HQ_LOCATION, enemyHqLocation);
            }
        }

        // Remove old empty soup spots
        for (MapLocation soup : soupLocations)
        {
            if (rc.canSenseLocation(soup) && rc.senseSoup(soup) == 0)
            {
                soupLocations.remove(soup);
                comm.sendLocation(EMPTIED_SOUP_LOCATION, soup);
            }
        }

        // Add new soup locations
        MapLocation[] soups = rc.senseNearbySoup();
        for (MapLocation soup : soups)
        {
            if (!soupLocations.contains(soup))
            {
                soupLocations.add(soup);
                comm.sendLocation(SOUP_LOCATION, soup);
            }
        }
    }

    private void checkBlockchain() throws GameActionException
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
