package sealteamsixplayer;

import battlecode.common.*;

public class Communication
{
    RobotController rc;
    static final int teamKey = 66554433;
    public static int maxSoupCost = 5;

    public Communication(RobotController rc)
    {
        this.rc = rc;
    }

    public boolean sendLocation(MessageType type, MapLocation location)
    {
        int[] message = {location.x, location.y};
        return sendMessage(type, message);
    }

    private boolean sendMessage(MessageType type, int[] message)
    {
        // Ensure message has space for key and type and that we can send message.
        if (message.length <= 5)
        {
            // Load up keyed message.
            int[] keyedMessage = new int[7];
            keyedMessage[0] = teamKey;
            keyedMessage[1] = type.ordinal();
            for (int i = 0; i < message.length; i++)
                keyedMessage[i+2] = message[i];

            // Find soup cost.
            int cost = 0;
            while (cost <= maxSoupCost)
            {
                if (rc.canSubmitTransaction(keyedMessage, cost))
                    break;
                cost++;
            }

            // We can't send the message because it's too expensive.
            if  (cost > maxSoupCost)
                return false;

            try // Submit message to blockchain.
            {
                rc.submitTransaction(keyedMessage, cost);
                return true;
            }
            catch (GameActionException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
	
    /** grabs locations for each building type (one slot each for now) from messages
    	 in blockchain.
	 [0] is HQ
	 [1] is refinery
	 [2] is design school
	 [3] is fulfilment center
		@param startBlock block to start looking for locations at
		@param stopBlock block to stop loop at
		@return MapLocation array of whatever was found
     */
	
	public MapLocation [] getLocations(int startBlock, int stopBlock) throws GameActionException
	{
		Transaction [] block = null;
		int count = startBlock;
		MapLocation [] locations = new MapLocation[4];
		while (count < rc.getRoundNum() && count < stopBlock)
		{
			block = rc.getBlock(count++);
			//parse block transactions for locations
			for (Transaction txn : block)
			{
				int[] message = txn.getMessage();
				//check for team key and ordinal to determine building type
				if (message[0] == Communication.teamKey && message[1] == 0)
					locations[0] = new MapLocation(message[2], message[3]);
				if (message[0] == Communication.teamKey && message[1] == 1)
					locations[1] = new MapLocation(message[2], message[3]);
				if (message[0] == Communication.teamKey && message[1] == 2)
					locations[2] = new MapLocation(message[2], message[3]);
				if (message[0] == Communication.teamKey && message[1] == 3)
					locations[3] = new MapLocation(message[2], message[3]);
			}
		}
		return locations;
	}
}
