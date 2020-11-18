package sealteamsixplayer;

import battlecode.common.*;

public class NetGun extends Robot {
    NetGun(RobotController rc){
        super(rc);
    }

    @Override
    public void go(){
        super.go();

        try {
            if(snipe() == 1) {
                System.out.println("Sniped a robot!");
            } else {
                System.out.println("Did not snipe any robot this round.");
            }
        } catch (GameActionException e) {
            e.printStackTrace();
        }

    }

    //360 snipe the nearest enemy
    int snipe() throws GameActionException {
        if(!rc.isReady())
            return -1; //Not ready so we bounce for now

        Team enemyTeam = rc.getTeam().opponent();
        RobotInfo tango = null;
        int distance = 0;
        MapLocation currLoc = rc.getLocation();
        RobotInfo[] nearbyTangos = rc.senseNearbyRobots(GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED, enemyTeam);

        //Finding the nearest enemy to snipe
        for(RobotInfo enemy: nearbyTangos){
            if(rc.canShootUnit(enemy.getID())){
                if(tango == null){
                    tango = enemy;
                    distance = currLoc.distanceSquaredTo(tango.location);
                }
                int nearestDist = currLoc.distanceSquaredTo(enemy.location);
                if(nearestDist < distance){
                    tango = enemy;
                    distance = nearestDist;
                }
            }
        }

        if(tango != null){
            rc.shootUnit(tango.ID);
            return 1;
        }
        return -1;
    }
}
