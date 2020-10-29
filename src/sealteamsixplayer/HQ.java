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
            // If this is the beginning of the game, communicate my location
            if (turnCount < 3)
                comm.sendLocation(MessageType.HQ_LOCATION, rc.getLocation());

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

            //Maybe try to send the location to the blockchain here
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
        }
    }
}
