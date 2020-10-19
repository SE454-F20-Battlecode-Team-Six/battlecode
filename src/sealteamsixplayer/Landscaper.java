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
			int count = (int) (Math.random() * 3);
			System.out.println("The landscaper count is: " + count);
			for (Direction dir : directions)
			{
				if (count % 3 == 0)
					rc.digDirt(dir);
				else if (count % 2 == 0)
					tryMove(dir);
				else
					rc.depositDirt(dir);
				++count;
				Clock.yield();
			}
		}
		catch (GameActionException e)
		{
			e.printStackTrace();
		}
	}
}
