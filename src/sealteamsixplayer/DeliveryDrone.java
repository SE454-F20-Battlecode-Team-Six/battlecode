package sealteamsixplayer;

import battlecode.common.*;

public class DeliveryDrone extends Mobile {
    DeliveryDrone(RobotController rc){
        super(rc);
    }

    Direction droppingPoint = null;


    @Override
    public void go() {

        //After the 100th turn
        try {
            if (turnCount <= 100){ //Using cows to pollute the other team

                while(!rc.isCurrentlyHoldingUnit()){
                    RobotInfo[] robots = rc.senseNearbyRobots();
                    for(RobotInfo r : robots){
                        if(r.getType() == RobotType.COW){
                            goTo(r.getLocation());               //Go to the cow position
                            if(rc.canPickUpUnit(r.getID())) {    //See if we can pick it up
                                checkBlockchain();               //Check the block chain
                                if(enemyHqLocation != null) {    //Check if we found enemy hq
                                    rc.pickUpUnit(r.getID());    //Drop bombs on them
                                    goTo(enemyHqLocation);
                                    rc.dropUnit(rc.getLocation().directionTo(enemyHqLocation));
                                }
                                //If enemy hq is not on blockchain, just wait there or move on to different cows
                            }
                        }
                    }
                    //After trying to move all the cow, move randomly
                    tryMove(randomDirection());
                }
                if(rc.isCurrentlyHoldingUnit())
                    rc.dropUnit(randomDirection());
            }

            if (turnCount > 100) { //After 100 turns, go after the other team robots
                Team enemy = rc.getTeam().opponent();
                while(!rc.isCurrentlyHoldingUnit()) {
                    RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

                    if(robots.length > 0){
                        if(goTo(robots[0].getLocation())) {
                            if (rc.canPickUpUnit(robots[0].getID())) { //Pick up the first robot found
                                rc.pickUpUnit(robots[0].getID());

                                //Head straight to the ocean.
                                dropAtOcean();
                            }
                        }
                    }
                }
                if(rc.isCurrentlyHoldingUnit()){ //Drop the thing it carrying, dunno if this is a good thing
                    rc.dropUnit(randomDirection());
                }
            }
        } catch(GameActionException e){
            e.printStackTrace();
        }

    }


    //Not the very best way to move to the ocean but hey it will stall the other team miner
    private boolean tryMoveToFlood(Direction dir) throws GameActionException{
        if(rc.senseFlooding(rc.adjacentLocation(dir))){
            this.droppingPoint = dir;
            rc.move(dir);
            return true;
        } else {
            rc.move(randomDirection()); //No flood so move somewhere else, not the best way right now.
            return false;
        }

    }

    //While the drone is carrying a robot, try to find a way to the ocean.
    private void dropAtOcean() throws GameActionException {
        while(rc.isCurrentlyHoldingUnit()){
            if(tryMoveToFlood(randomDirection())){
                rc.dropUnit(this.droppingPoint);
                this.droppingPoint = null; //reset the dropping point
            }
        }
    }
}
