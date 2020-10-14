package sealteamsixplayer;

import battlecode.common.*;

public class HQ extends Robot
{
    private int numberOfMiners = 0;

    public HQ(RobotController rc)
    {
        super(rc);
    }

    @Override
    public void go()
    {
        super.go();

        try
        {
            // We'll start by building 5 miners. Can adjust if this isn't enough soup production.
            if (numberOfMiners < 5)
            {
                for (Direction dir : directions)
                {
                    if (rc.canBuildRobot(RobotType.MINER, dir))
                    {
                        rc.buildRobot(RobotType.MINER, dir);
                        numberOfMiners++;
                    }
                }
            }
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
        }
    }
}
