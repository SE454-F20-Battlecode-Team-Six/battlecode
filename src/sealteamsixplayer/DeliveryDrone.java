package sealteamsixplayer;

import battlecode.common.*;

public class DeliveryDrone extends Mobile {
    DeliveryDrone(RobotController rc){
        super(rc);
    }

    MapLocation floodedTile;
    MapLocation exploreDest;


    @Override
    public void go() {
        super.go();
        try {
            if(failedMoveCount > 2)
            {
                failedMoveCount = 0;
                goTo(randomDirection(), true);
            }
            if(exploreDest == null)
                exploreDest = explore();
            if(turnCount % 50 == 0)
                updateExploreDest();
            if (rc.getRoundNum() <= 300){ //Using cows to pollute the other team
                if(!rc.isCurrentlyHoldingUnit()){
                    RobotInfo[] robots = rc.senseNearbyRobots();
                    float smallestDist = Integer.MAX_VALUE;
                    RobotInfo closestRobot = null;
                    for(RobotInfo r : robots){
                        if(r.getType() != RobotType.COW)
                            continue;
                        if(rc.getLocation().isAdjacentTo(r.getLocation())){
                            if(rc.canPickUpUnit(r.ID)){
                                rc.pickUpUnit(r.ID);
                                System.out.println("I picked up a fat cow!");
                                dropAtOcean(); //For now drop em in ocean
                                return;
                            }
                        } else {
                            float currDist = rc.getLocation().distanceSquaredTo(r.getLocation());
                            if(currDist < smallestDist){
                                smallestDist = currDist;
                                closestRobot = r;
                            }
                        }
                    }
                    tryFly(closestRobot);
                } else {
                    dropAtOcean();
                    //rc.dropUnit(randomDirection());
                }
            }
            else { //After 100 turns, go after the other team robots
                Team enemy = rc.getTeam().opponent();
                if(!rc.isCurrentlyHoldingUnit()) {
                    RobotInfo[] robots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), enemy);
                    float smallestDist = Integer.MAX_VALUE;
                    RobotInfo closestRobot = null;
                    //Pick up robot if it's next to drone
                    //if no enemies are adjacent, move to closest robot
                    for (RobotInfo robot : robots)
                    {
                        //if the robot isn't a landscaper or miner, skip
                        if(robot.getType() != RobotType.LANDSCAPER && robot.getType() != RobotType.MINER)
                            continue;
                        if(rc.getLocation().isAdjacentTo(robot.getLocation()))
                        {
                            if (rc.canPickUpUnit(robot.getID())) {
                                rc.pickUpUnit(robot.getID());
                                System.out.println("I picked something up!");
                                dropAtOcean();  //Head straight to the ocean.
                                return;
                            }
                        }
                        //see if robot is closer to drone than current closest
                        else
                        {
                            float currentDist = rc.getLocation().distanceSquaredTo(robot.getLocation());
                            if (currentDist < smallestDist)
                            {
                                smallestDist = currentDist;
                                closestRobot = robot;
                            }
                        }
                    }
                    tryFly(closestRobot);
                }
                else
                    dropAtOcean();

            }
        } catch(GameActionException e){
            e.printStackTrace();
        }

    }

    private void tryFly(RobotInfo closestRobot) throws GameActionException
    {
        if(closestRobot != null){
            if(!goTo(to(closestRobot.getLocation()), true))
                ++failedMoveCount;
            else
                failedMoveCount = 0;
        }
        else if(enemyHqLocation != null)
        {
            if(!goTo(to(enemyHqLocation), true))
                ++failedMoveCount;
            else
                failedMoveCount = 0;
        }
        else
        {
            if(!goTo(to(exploreDest), true))
                ++failedMoveCount;
            else
                failedMoveCount = 0;
        }
    }


    //Drop drone in the ocean if we're next to it
    private void dropAtOcean() throws GameActionException {
        detectFlood(); //update nearest floodedTile
        if (floodedTile != null)
            if(rc.getLocation().isAdjacentTo(floodedTile))
                if(rc.canDropUnit(to(floodedTile)))
                    rc.dropUnit(to(floodedTile));
                else
                    Clock.yield();//wait until floodedTile is no longer obstructed
            else
                goTo(to(floodedTile), true);
        else{
            if(enemyHqLocation != null)
                goTo(to(enemyHqLocation), true);
            else
                goTo(to(exploreDest), true);
        }
    }

    //looks for a place to drop a unit. Have to detect flood for every tile in sensor radius
    //sets floodedTile to nearest flooded tile
    public void detectFlood() throws GameActionException
    {
        MapLocation[] nearbyFloodedTiles = new MapLocation[69]; //max sensor radius for drone can see 69 tiles
        for(int x = 0; x < 8; ++x)
        {
            for(int y = 0; y < 8; ++y)
            {
                MapLocation currentTile = new MapLocation(rc.getLocation().x+x-4, rc.getLocation().y+y-4);
                if(rc.canSenseLocation(currentTile) && rc.senseFlooding((currentTile)))
                    nearbyFloodedTiles[x * 8 + y] = currentTile;
            }
        }
        float smallestDist = Integer.MAX_VALUE;
        MapLocation closestTile = null;
        for(MapLocation tile : nearbyFloodedTiles)
        {
            if(tile == null) //some tiles will be null
                continue;
            if(rc.getLocation().distanceSquaredTo(tile) < smallestDist)
            {
                smallestDist = rc.getLocation().distanceSquaredTo(tile);
                closestTile = tile;
            }
        }
        if(closestTile != null)
            floodedTile = closestTile;
    }

    public void updateExploreDest()
    {
        MapLocation newDest = explore();
        int count = 0;
        while(newDest == exploreDest && count++ < 5)
        {
            newDest = explore();
        }
        exploreDest = newDest;
    }

}
