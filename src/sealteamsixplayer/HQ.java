package sealteamsixplayer;

import battlecode.common.*;

import java.sql.SQLOutput;

public class HQ extends Robot
{
    private int numberOfMiners = 0;
    private NetGun netGun;

    public HQ(RobotController rc)
    {
        super(rc);
        netGun = new NetGun(rc);
    }

    @Override
    public void go()
    {
        super.go();

        try
        {
            // If this is the beginning of the game, communicate my location
            if (turnCount == 5)
                comm.sendLocation(LocationType.HQ_LOCATION, rc.getLocation());

            // We'll start by building 4 miners. Can adjust if this isn't enough soup production.
            if ((rc.getRoundNum() < 100 && numberOfMiners < 5) ||
                (rc.getRoundNum() >= 100 && numberOfMiners < 10))
            {
                for (Direction dir : directions)
                {
                    if (rc.canBuildRobot(RobotType.MINER, dir))
                    {
                        rc.buildRobot(RobotType.MINER, dir);
                        numberOfMiners++;
                        System.out.println("Built miner number " + numberOfMiners);
                    }
                }
            }

            //Now the HQ can snipe drone.  360 'em!!!!
            if(netGun.snipe() == 1) {
                System.out.println("The HQ took down an enemy!");
            } else {
                System.out.println("No enemy near HQ");
            }
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
        }
    }
}
