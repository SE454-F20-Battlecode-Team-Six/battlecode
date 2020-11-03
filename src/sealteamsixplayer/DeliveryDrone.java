package sealteamsixplayer;

import battlecode.common.*;

public class DeliveryDrone extends Robot {
    DeliveryDrone(RobotController rc){
        super(rc);
    }

    Direction droppingPoint = null;

    @Override
    public void go() {

        //After the 100th turn
        try {
            if (turnCount <= 100){ //Using cows to pollute the other team
                if(!rc.isCurrentlyHoldingUnit()){
                    RobotInfo[] robots = rc.senseNearbyRobots(); //Planning to improve this?
                    for(RobotInfo r : robots){
                        if(r.getType() == RobotType.COW){
                            rc.pickUpUnit(r.getID());
                            //drop them near enemy
                        }
                    }
                }
            }
            if (turnCount > 100) { //After 100 turns, go after the other team robots
                Team enemy = rc.getTeam().opponent();
                if (!rc.isCurrentlyHoldingUnit()) {
                    RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);
                    if(robots.length > 0){
                        rc.pickUpUnit(robots[0].getID());

                        //More code here. Head straight to the ocean.
                        dropAtOcean();
                    }
                } else {
                    this.tryMove(randomDirection());
                }
            }
        } catch(GameActionException e){
            e.printStackTrace();
        }

    }

    //Not the very best way to move to the ocean but hey
    private boolean tryMoveToFlood(Direction dir) throws GameActionException{
        if(rc.senseFlooding(rc.adjacentLocation(dir))){
            this.droppingPoint = dir;
            rc.move(dir);
            return true;
        } else {
            rc.move(randomDirection()); //No flood so move somewhere else.
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
