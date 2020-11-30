package sealteamsixplayer;

import battlecode.common.*;

public class Landscaper extends Mobile
{
	boolean wallIsBuilt = false;
	int elevationDiff = 6;

	public Landscaper(RobotController rc) { super(rc); }
	
	@Override
	public void go()
	{
		super.go();
		try
		{
			if(hqLocation == null)
			{
				//TODO do something if location isn't found. Probably try to sense it then random walk if can't find
			}
			else
			{
				//move towards HQ if not adjacent, otherwise try to build wall
				if(!checkForAdjHQ())
					goTo(hqLocation);
				else
				{
					wallIsBuilt = checkWall();
					if(wallIsBuilt)
					{
						goTo(patrolWall());
					}
					else
					{
						MapLocation diggingTile = rc.getLocation().add(to(hqLocation).opposite());
						if(Math.abs(rc.senseElevation(rc.getLocation()) - rc.senseElevation(diggingTile)) < elevationDiff)
						{
							System.out.println("Looks like it's time to dig!");
							buildWall();
						}
						else
							goTo(patrolWall());
					}
				}
			}
		}
		catch (GameActionException e)
		{
			e.printStackTrace();
		}
	}
	
	//checks if the HQ is adjacent to the landscaper
	public boolean checkForAdjHQ()
	{
		return hqLocation.isAdjacentTo(rc.getLocation()) && !rc.canMove(to(hqLocation));
	}
	
	//checks integrity of wall (if all adjacent tiles are 3+ elevation)
	//returns null if wall is fine or Direction.CENTER if on tile that needs dirt.
	// otherwise returns direction towards a weak spot in the wall
	public boolean checkWall() throws GameActionException
	{
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
		for (int i = 0; i< 8; ++i)
		{
			MapLocation diggingTile = rc.getLocation().add(to(hqLocation).opposite());
			if(Math.abs(rc.senseElevation(new MapLocation(wallCoords[i][0], wallCoords[i][1])) -
					rc.senseElevation(diggingTile)) <= elevationDiff)
			{
				return false;
			}
		}
		return true;
	}

	public Direction patrolWall() throws GameActionException
	{
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
			int rand = (int) Math.round(((Math.random() * 7)));
			MapLocation tileLoc = new MapLocation(wallCoords[rand][0], wallCoords[rand][1]);
			if (rc.senseRobotAtLocation((tileLoc)) != null)
				continue;
			return to(tileLoc);
		}
		return Direction.CENTER;
	}
	
	//atte
	public boolean buildWall() throws GameActionException
	{
		if(rc.canDigDirt(to(hqLocation).opposite()) && rc.getDirtCarrying() < 1)
		{
			rc.digDirt(to(hqLocation).opposite());
			return true;
		}
		else
		{
			if(rc.canDepositDirt(to(rc.getLocation())))
				rc.depositDirt(to(rc.getLocation()));
			return false;
		}
	}
}
