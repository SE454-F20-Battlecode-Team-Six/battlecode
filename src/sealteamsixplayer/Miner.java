package sealteamsixplayer;

import battlecode.common.*;

public class Miner extends Robot
{
    MapLocation refineryLocation;
    MapLocation designSchoolLocation;
    MapLocation fulfillCenterLocation;
    MapLocation[] soupLocations;

    public Miner (RobotController rc)
    {
        super(rc);
    }

    public void go()
    {
        super.go();
        // Priority order:
        //      If the miner is new (< 5 turns?) check blockchain for:
        //          refineryLocation, designSchoolLocation, soupLocations
        //      Build refinery if no refinery.
        //      Build design school if no design school
        //      Build Fulfillment center if no FC
        //      Mine soup if not full of soup
        //      Deposit soup in refinery if refinery near by
        //      if full, move towards refinery
        //      if not full sense for soup, and move towards soup

        try
        {
            if (turnCount < 5) // We are a youth, so it's time to learn.
                checkBlockchain();

            if (refineryLocation == null)
                tryBuildRefinery();

            if (designSchoolLocation == null)
                tryBuildDesignSchool();

            // TODO: Build fulfillment center for drones
            if(fulfillCenterLocation == null)
                tryBuildFR();

            if (!isFull())
            {
                for (Direction dir : directions)
                    tryMine(dir);
            }

            if (isNextToRefinery())
            {
                rc.depositSoup(to(refineryLocation), rc.getSoupCarrying());
                System.out.println("I'm depositing soup! " + rc.getLocation());
            }

            if (isNextToHq())
            {
                rc.depositSoup(to(refineryLocation), rc.getSoupCarrying());
                System.out.println("I'm depositing soup! " + rc.getLocation());
            }

            if (isFull() && refineryLocation != null)
            {
                tryMove(to(refineryLocation));
            }
            else if (isFull() && hqLocation != null)
            {
                tryMove(to(hqLocation));
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

    private boolean isNextToHq()
    {
        return hqLocation != null
            && hqLocation.isAdjacentTo(rc.getLocation())
            && rc.canDepositSoup(to(hqLocation));
    }

    private void checkBlockchain()
    {
        //hqLocation = comm.getHQ();

    }

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

    private void tryBuildDesignSchool() throws GameActionException
    {
        for (Direction dir : directions)
        {
            if (rc.canBuildRobot(RobotType.DESIGN_SCHOOL, dir))
            {
                rc.buildRobot(RobotType.DESIGN_SCHOOL, dir);
                designSchoolLocation = rc.getLocation().add(dir);
                System.out.println("I built a Design School! " + designSchoolLocation);
                if(comm.sendLocation(MessageType.DESIGN_SCHOOL_LOCATION, designSchoolLocation))
                    System.out.println("I sent the location of the Design School.");
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
                if(comm.sendLocation(MessageType.REFINERY_LOCATION, refineryLocation))
                    System.out.println("I sent the location of the Design School.");
            }
        }
    }

    private void tryBuildFR() throws GameActionException
    {
        for(Direction dir : directions) {
            if(rc.canBuildRobot(RobotType.FULFILLMENT_CENTER,dir)) {
                rc.buildRobot(RobotType.FULFILLMENT_CENTER, dir);
                fulfillCenterLocation = rc.getLocation().add(dir);
                System.out.println("I built a fulfillment center! " + fulfillCenterLocation);
                if(comm.sendLocation(MessageType.FR_LOCATION, fulfillCenterLocation))
                    System.out.println("I sent the location of the Fulfillment Center.");
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
