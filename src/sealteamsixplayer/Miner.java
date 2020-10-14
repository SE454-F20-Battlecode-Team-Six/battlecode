package sealteamsixplayer;

import battlecode.common.*;

public class Miner extends Robot
{
    MapLocation refineryLocation;
    MapLocation designSchoolLocation;

    public Miner (RobotController rc)
    {
        super(rc);
    }

    public void go()
    {
        super.go();
        // Priority order:
        //      Build refinery if no refinery.
        //      Build design school if no design school
        //      Build Fulfillment center if no FC
        //      Mine soup if not full of soup
        //      Deposit soup in refinery if refinery near by
        //      if full, move towards refinery
        //      if not full sense for soup, and move towards soup

        // tryBlockchain();
        try
        {
            if (refineryLocation == null)
                tryBuildRefinery();

            if (designSchoolLocation == null)
                tryBuildDesignSchool();

            // TODO: Build fulfillment center for drones

            if (!isFull())
            {
                for (Direction dir : directions)
                    tryMine(dir);
            }

            if (isNextToRefinery())
            {
                rc.depositSoup(rc.getLocation().directionTo(refineryLocation), rc.getSoupCarrying());
                System.out.println("I'm depositing soup! " + rc.getLocation());
            }

            if (isFull())
            {
                tryMove(rc.getLocation().directionTo(refineryLocation));
            }

            tryMove(this.randomDirection());
            // TODO: update this to move to soup locations
            // maybe including a list of sensed soup locations? see rc.senseNearbySoup()
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
        }
    }

    private boolean isNextToRefinery()
    {
        return refineryLocation != null
            && refineryLocation.isAdjacentTo(rc.getLocation())
            && rc.canDepositSoup(rc.getLocation().directionTo(refineryLocation));
    }

    private boolean isFull()
    {
        return rc.getSoupCarrying() >= RobotType.MINER.soupLimit - GameConstants.SOUP_MINING_RATE;
    }

    private void tryBuildDesignSchool() throws GameActionException
    {
        for (Direction dir : directions)
        {
            if (rc.canBuildRobot(RobotType.DESIGN_SCHOOL, dir))
            {
                rc.buildRobot(RobotType.DESIGN_SCHOOL, dir);
                designSchoolLocation = rc.getLocation().add(dir);
                System.out.println("I built a Design School! " + designSchoolLocation);
            }
        }
    }

    private void tryBuildRefinery() throws GameActionException
    {
        for (Direction dir : directions)
        {
            if (rc.canBuildRobot(RobotType.REFINERY, dir))
            {
                rc.buildRobot(RobotType.REFINERY, dir);
                refineryLocation = rc.getLocation().add(dir);
                System.out.println("I built a refinery! " + refineryLocation);
            }
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
