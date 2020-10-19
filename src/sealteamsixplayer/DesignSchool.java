package sealteamsixplayer;

import battlecode.common.*;

public class DesignSchool extends Robot
{
    public DesignSchool(RobotController rc) { super(rc); }

    @Override
    public void go()
    {
        super.go();
        try
        {
            for (Direction dir : directions)
                tryBuild(RobotType.LANDSCAPER, dir);
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
        }
    }
    
    private boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }
}
