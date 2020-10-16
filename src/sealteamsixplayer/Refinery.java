package sealteamsixplayer;

import battlecode.common.*;

 public class Refinery extends Robots {

    public Refinery(RobotController rc) {
        super(rc);
    }

    @Override
    public void go() {
        super.go();
        
        //TO-DO:
        //
        try {
            if(rc.getTeamSoup() >= 1){

            }
        } catch (GameActionException e) {
            e.printStackTrace();
        }
    }

 }