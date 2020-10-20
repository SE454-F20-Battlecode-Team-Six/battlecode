package sealteamsixplayer;
import battlecode.common.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public strictfp class RobotPlayer {
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) {
        // Create
        Robot robot = null;

        switch (rc.getType()) {
            case HQ:                    robot = new HQ(rc);           break;
            case MINER:                 robot = new Miner(rc);        break;
            case REFINERY:              robot = new Refinery(rc);     break;
            case DESIGN_SCHOOL:         robot = new DesignSchool(rc); break;
            case LANDSCAPER:            robot = new Landscaper(rc);   break;
            case VAPORATOR:
            case FULFILLMENT_CENTER:
            case DELIVERY_DRONE:
            case NET_GUN:               robot = new Robot(rc);        break;
            default:                    throw new NotImplementedException(); // This should never fire.
        }

        System.out.println("I'm a " + rc.getType() + " and I just got created!");

        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                robot.go();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runFulfillmentCenter() throws GameActionException {
//        for (Direction dir : directions)
//            tryBuild(RobotType.DELIVERY_DRONE, dir);
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
}
