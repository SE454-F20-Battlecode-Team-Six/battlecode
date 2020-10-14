package sealteamsixplayer;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) {

        // Create
        Robot rob = null;
        switch (rc.getType()) {
            case HQ:                 rob = new HQ(rc);      break;
            case MINER:              rob = new Miner(rc);   break;
            case REFINERY:
            case VAPORATOR:
            case DESIGN_SCHOOL:
            case FULFILLMENT_CENTER:
            case LANDSCAPER:
            case DELIVERY_DRONE:
            case NET_GUN:            rob = new Robot(rc);   break;
        }

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                rob.go();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }

    static void runVaporator() throws GameActionException {

    }

    static void runDesignSchool() throws GameActionException {

    }

    static void runFulfillmentCenter() throws GameActionException {
//        for (Direction dir : directions)
//            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException {

    }

    static void runDeliveryDrone() throws GameActionException {
//        Team enemy = rc.getTeam().opponent();
//        if (!rc.isCurrentlyHoldingUnit()) {
//            // See if there are any enemy robots within capturing range
//            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);
//
//            if (robots.length > 0) {
//                // Pick up a first robot within range
//                rc.pickUpUnit(robots[0].getID());
//                System.out.println("I picked up " + robots[0].getID() + "!");
//            }
//        } else {
//            // No close robots, so search for robots within sight radius
//            tryMove(randomDirection());
//        }
    }

    static void runNetGun() throws GameActionException {

    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

//    static void tryBlockchain() throws GameActionException {
//        if (turnCount < 3) {
//            int[] message = new int[7];
//            for (int i = 0; i < 7; i++) {
//                message[i] = 123;
//            }
//            if (rc.canSubmitTransaction(message, 10))
//                rc.submitTransaction(message, 10);
//        }
//        // System.out.println(rc.getRoundMessages(turnCount-1));
//    }
}
