package sealteamsixplayer;
import battlecode.common.*;

public class Refinery extends Robot {

    public Refinery(RobotController rc) {
        super(rc);
    }

    //TO-DO:
    //For now, report the location of the refinery and nearby robot
    //Since refinery cannot moved, it does not need to check direction
    @Override
    public void go() {
        super.go();

        announceSelfLocation();     //Announce refinery position

        //The plan is to use this in the BlockChain
        RobotInfo[] nearByRobots = sensor();

        //Alert if enemy team is near
        //Will try to integrate this into blockchain
        //For now it is for debug
        for(RobotInfo r : nearByRobots) {
            if (!r.getTeam().equals(rc.getTeam())) {  //If we're team A, then enemy is team B
                System.out.println("Enemy near refinery");
            }
        }
    }

    //Util function, sensor
    private RobotInfo[] sensor(){
        return this.rc.senseNearbyRobots();
    }

    //Not sure about this yet, probably will be in blockchain as well
    private void announceSelfLocation(){
        comm.sendLocation(MessageType.REFINERY_LOCATION,rc.getLocation());
    }
 }