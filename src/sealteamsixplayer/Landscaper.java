package sealteamsixplayer;

import battlecode.common.*;

public class Landscaper extends Robot
{
	public Landscaper(RobotController rc) { super(rc); }
	
	@Override
	public void go()
	{
		super.go();
		try
		{
			if(!getHQLocation())
			{
				//TODO do something if location isn't found
			}
			else
			{
				//move towards HQ if not adjacent, otherwise try to build wall
				if(!checkForAdjHQ())
					moveToHQ();
				else
				{
					Direction nextMove = checkWall();
					if(nextMove == Direction.CENTER) //build wall if standing on weak wall tile
						buildWall();
					else if(nextMove == null) //do nothing if can't move to weak tile and not on weak tile
						Clock.yield();
					else //else move to weak tile if possible. if not, then just wait a turn.
						tryMove(nextMove);
				}
			}
		}
		catch (GameActionException e)
		{
			e.printStackTrace();
		}
	}
	
	//checks if hqLocation already known. if not, finds
	// HQ location in blocks from first three rounds
	private boolean getHQLocation() throws GameActionException
	{
		if(hqLocation != null)
			return true;
		MapLocation [] locations = comm.getLocations(1,4);
		hqLocation = locations[0];
		if(hqLocation != null)
		{
			System.out.println("The HQ Location is: " + hqLocation);
			return true;
		}
		return false;
	}
	
	//checks if the HQ is adjacent to the landscaper
	private boolean checkForAdjHQ()
	{
		RobotInfo [] robots = rc.senseNearbyRobots(2, rc.getTeam());
		for (RobotInfo robot : robots)
		{
			if(robot.type == RobotType.HQ && robot.team == rc.getTeam())
			{
				return true;
			}
		}
		return false;
	}
	
	//attempt to move towards the HQ
	private void moveToHQ() throws GameActionException
	{
		//if landscaper can move directly towards HQ, do that
		tryMove(to(hqLocation));
		for(Direction dir : directions)
			tryMove(dir);
	}
	
	//checks integrity of wall (if all adjacent tiles are 3+ elevation)
	//returns null if wall is fine or Direction.CENTER if on tile that needs dirt.
	// otherwise returns direction towards a weak spot in the wall. order of tile check is clockwise
	// starting at 12 (top middle)
	private Direction checkWall() throws GameActionException
	{
		MapLocation diggingTile = rc.getLocation().add(to(hqLocation).opposite());
		if(Math.abs(rc.senseElevation(rc.getLocation()) - rc.senseElevation(diggingTile)) < 6)
		{
			System.out.println("Looks like it's time to dig!");
			return Direction.CENTER;
		}
		int [][] wallCoords = new int[8][2];
		wallCoords[0][0] = hqLocation.x;
		wallCoords[0][1] = hqLocation.y+1;
		wallCoords[1][0] = hqLocation.x+1;
		wallCoords[1][1] = hqLocation.y+1;
		wallCoords[2][0] = hqLocation.x+1;
		wallCoords[2][1] = hqLocation.y;
		wallCoords[3][0] = hqLocation.x+1;
		wallCoords[3][1] = hqLocation.y-1;
		wallCoords[4][0] = hqLocation.x;
		wallCoords[4][1] = hqLocation.y-1;
		wallCoords[5][0] = hqLocation.x-1;
		wallCoords[5][1] = hqLocation.y-1;
		wallCoords[6][0] = hqLocation.x-1;
		wallCoords[6][1] = hqLocation.y;
		wallCoords[7][0] = hqLocation.x-1;
		wallCoords[7][1] = hqLocation.y+1;
		for(int i = 0; i < 50; ++i)
		{
			int rand = (int) (Math.random() * 8);
			MapLocation tileLoc = new MapLocation(wallCoords[rand][0], wallCoords[rand][1]);
			if (rc.senseRobotAtLocation((tileLoc)) != null)
				continue;
			return to(tileLoc);
		}
		return null;
	}
	
	//atte
	private void buildWall() throws GameActionException
	{
		if(rc.canDigDirt(to(hqLocation).opposite()) && rc.getDirtCarrying() < 1)
			rc.digDirt(to(hqLocation).opposite());
		else
		{
			if(rc.canDepositDirt(to(rc.getLocation())))
				rc.depositDirt(to(rc.getLocation()));
		}
	}
}
