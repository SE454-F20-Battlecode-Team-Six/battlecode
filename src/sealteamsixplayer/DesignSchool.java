package sealteamsixplayer;

import battlecode.common.*;

public class DesignSchool extends Robot
{
    public DesignSchool(RobotController rc) { super(rc); }

    int landscaperCount = 0;
    final int landscaperMaxCount = 4;
    
    @Override
    public void go()
    {
        super.go();
        try
        {
            for (Direction dir : directions)
			{
				if(landscaperCount < landscaperMaxCount)
					if(tryBuild(RobotType.LANDSCAPER, dir))
						++landscaperCount;
			}
        }
        catch (GameActionException e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }
}
