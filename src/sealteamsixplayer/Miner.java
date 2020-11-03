package sealteamsixplayer;

import battlecode.common.*;

public class Miner extends Mobile
{
    public static final int BUILDING_BUFFER = 8;
    MapLocation soupTarget = null;
    MapLocation closestRefinery = null;

    public Miner (RobotController rc)
    {
        super(rc);
    }

    public void go()
    {
        super.go();

        try
        {
            // If soupTarget is no longer in the list of locations, it must have been emptied.
            if(soupTarget != null && !soupLocations.contains(soupTarget))
                soupTarget = null;

            // if we don't have a soupTarget, pick the closest one in our list.
            if (soupTarget == null)
                soupTarget = closestSoupLocation();

            // I just need a little rest, that's all.
            if (!rc.isReady()) return;

            // If the miner isn't full of soup, keep mining.
            if (!isFull())
            {
                // First try and mine.
                System.out.println("Trying to mine.");
                boolean mined = false;
                for(Direction dir : directions)
                    if(tryMine(dir))
                        mined = true;

                // Otherwise, head towards our soup target.
                if (!mined)
                {
                    if (soupTarget != null && !atLocation(soupTarget))
                    {
                        System.out.println("Moving to soup location: " + soupTarget);
                        goTo(soupTarget);
                    }
                    // If all else failed, flail around like a loon.
                    else
                    {
                        System.out.println("Moving randomly.");
                        goTo(this.randomDirection());
                    }
                }
            }

//            if (isNextToRefinery())
//            {
//                rc.depositSoup(to(refineryLocation), rc.getSoupCarrying());
//                System.out.println("I'm depositing soup! " + rc.getLocation());
//            }

            else if (isNextToHq())
            {
                rc.depositSoup(to(hqLocation), rc.getSoupCarrying());
                System.out.println("I'm depositing soup! " + rc.getLocation());
            }

            // TODO: designate a single builder Miner that will build needed buildings.
            //  If not builder, skip these three.
            //if (refineryLocation == null)
            //tryBuildRefinery();

            if (designSchoolLocation == null && rc.getTeamSoup() > 150)
                tryBuildDesignSchool();

            if (fulfillCenterLocation == null && rc.getTeamSoup() > 150)
                tryBuildFR();

            if (isFull())
            {
                if (closestRefinery == null)
                    closestRefinery = findClosestRefinery();
                System.out.println("I'm full, moving to " + closestRefinery);
                tryMove(to(closestRefinery));
            }
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Returns the closer of hqLocation or refineryLocation.
     */
    public MapLocation findClosestRefinery()
    {
        if (hqLocation == null) return null;
        if (refineryLocation == null) return hqLocation;
        return rc.getLocation().distanceSquaredTo(refineryLocation) <
            rc.getLocation().distanceSquaredTo(hqLocation) ? refineryLocation : hqLocation;
    }

    /**
     * Returns the soup MapLocation that's closest to the robot, or null if there are no soup locations known.
     */
    public MapLocation closestSoupLocation()
    {
        MapLocation me = rc.getLocation();
        MapLocation closestSoup = null;
        int closestDistance = Integer.MAX_VALUE;

        // Loop through the soupLocations we know about and keep track of the closest one we've seen.
        for (MapLocation soup : soupLocations)
        {
            int dist = me.distanceSquaredTo(soup);
            if (dist < closestDistance)
            {
                closestDistance = dist;
                closestSoup = soup;
            }
        }
        return closestSoup;
    }

    /**
     * Returns true if the robot is next to the HQ.
     */
    private boolean isNextToHq()
    {
        return hqLocation != null
            && hqLocation.isAdjacentTo(rc.getLocation())
            && rc.canDepositSoup(to(hqLocation));
    }

    /**
     * Returns true if the robot is next to the refinery.
     */
    private boolean isNextToRefinery()
    {
        return refineryLocation != null
            && refineryLocation.isAdjacentTo(rc.getLocation())
            && rc.canDepositSoup(to(refineryLocation));
    }

    private boolean isFull()
    {
        return rc.getSoupCarrying() >= RobotType.MINER.soupLimit - GameConstants.SOUP_MINING_RATE;
    }

    /**
     * Try to build a robot at least <code>BUILDING_BUFFER</code> distance away from HQ.
     * @param type RobotType to build.
     * @return Direction that the building was built in.
     * @throws GameActionException
     */
    public Direction tryBuild(RobotType type) throws GameActionException
    {
        if (hqLocation != null && rc.getLocation().distanceSquaredTo(hqLocation) > BUILDING_BUFFER)
        {
            for (Direction dir : directions)
            {
                if (rc.canBuildRobot(type, dir))
                {
                    rc.buildRobot(type, dir);
                    return dir;
                }
            }
        }
        // We failed to build, lets try to move away from the HQ so we can try again
        if (hqLocation != null)
        {
            tryMove(rc.getLocation().directionTo(hqLocation).opposite());
            System.out.println("Failed to build " + type + ". Moving away from the HQ.");
        }
        return null;
    }

    private void tryBuildDesignSchool() throws GameActionException
    {
        Direction built = tryBuild(RobotType.DESIGN_SCHOOL);
        if (built != null)
        {
            designSchoolLocation = rc.getLocation().add(built);
            comm.sendLocation(LocationType.DESIGN_SCHOOL_LOCATION, designSchoolLocation);
            System.out.println("I built a Design School! " + designSchoolLocation);
        }
    }

    private void tryBuildRefinery() throws GameActionException
    {
        Direction built = tryBuild(RobotType.REFINERY);
        if (built != null)
        {
            refineryLocation = rc.getLocation().add(built);
            comm.sendLocation(LocationType.REFINERY_LOCATION, refineryLocation);
            System.out.println("I built a refinery! " + refineryLocation);
        }
    }

    private void tryBuildFR() throws GameActionException
    {
        Direction built = tryBuild(RobotType.FULFILLMENT_CENTER);
        if (built != null)
        {
            fulfillCenterLocation = rc.getLocation().add(built);
            comm.sendLocation(LocationType.FR_LOCATION, fulfillCenterLocation);
            System.out.println("I built a fulfillment center! " + fulfillCenterLocation);
        }
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     */
    private boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            System.out.println("I mined soup at " + rc.getLocation().add(dir));
            return true;
        } else return false;
    }
}
