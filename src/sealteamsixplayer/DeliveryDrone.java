package sealteamsixplayer;

import battlecode.common.*;

public class DeliveryDrone extends Mobile {
    DeliveryDrone(RobotController rc){
        super(rc);
    }

    Direction droppingPoint;
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
            if(turnCount++ == 0)
                exploreDest = explore();
            if(turnCount % 50 == 0)
                updateExploreDest();
            if (rc.getRoundNum() <= 100){ //Using cows to pollute the other team

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
                    goTo(randomDirection(), true);
                }
                if(rc.isCurrentlyHoldingUnit())
                    rc.dropUnit(randomDirection());
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
                    if(closestRobot != null)
                    {
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
                        {
                            ++failedMoveCount;
                        }
                        else failedMoveCount = 0;
                    }
                }
                else {
                    dropAtOcean();
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
    private void detectFlood() throws GameActionException
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

    private void updateExploreDest()
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
