package sealteamsixplayer;

import battlecode.common.*;

public class FulfillmentCenter extends Robot {
    private int droneCount = 0;
    FulfillmentCenter(RobotController rc){
        super(rc);
    }
    //Probably gonna use the tryBuild function in here

    //Will add more strategy
    public void go() {
        try {
            while(rc.getRoundNum() < 500){
                if(rc.getTeamSoup() > 200 && droneCount < 7){  //Attempting a fast strategy when early in game
                    if (tryBuildDrone())
                        ++droneCount;
                }
            }

            while(rc.getRoundNum() > 500 && rc.getRoundNum() < 1000){ //More drone later in game
                if(rc.getTeamSoup() > 100 && droneCount < 14){
                    if (tryBuildDrone())
                        ++droneCount;
                }
            }

        } catch(GameActionException e){
            e.printStackTrace();
        }

    }

    //Just a getter
    public int droneAmount(){
        return this.droneCount;
    }

    private boolean tryBuildDrone() throws GameActionException {
        boolean status = false;
        for(Direction dir : directions){
            if(rc.canBuildRobot(RobotType.DELIVERY_DRONE,dir)){
                rc.buildRobot(RobotType.DELIVERY_DRONE,dir);
                status  = true;
            }
        }
        return status;
    }
}
