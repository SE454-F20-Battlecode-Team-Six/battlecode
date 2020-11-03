package sealteamsixplayer;

import battlecode.common.*;

import java.util.ArrayList;

public class Communication
{
    RobotController rc;
    private static final int TEAM_KEY = 66554433;
    public static final int MAX_SOUP_COST = 7;

    int latestReadBlock;

    public Communication(RobotController rc)
    {
        this.rc = rc;
    }

    /**
     * Sends a message to the block chain saying that the given location is the given type of location.
     */
    public boolean sendLocation(LocationType type, MapLocation location)
    {
        int[] message = {location.x, location.y};
        return sendMessage(type, message);
    }

    /**
     * Sends a keyed message to the block chain containing the given location type and message.
     */
    private boolean sendMessage(LocationType type, int[] message)
    {
        // Ensure message has space for key and type and that we can send message.
        if (message.length <= 5)
        {
            // Load up keyed message.
            int[] keyedMessage = new int[7];
            keyedMessage[0] = TEAM_KEY;
            keyedMessage[1] = type.ordinal();
            System.arraycopy(message, 0, keyedMessage, 2, message.length);

            // Soup cost is 1/100th of the round num rounded up, or MAX_SOUP_COST, whichever is less.
            int cost = Math.min(rc.getRoundNum() / 100 + 1, MAX_SOUP_COST);

            if (rc.canSubmitTransaction(keyedMessage, cost))
            {
                try // Submit message to blockchain.
                {
                    rc.submitTransaction(keyedMessage, cost);
                    return true;
                } catch (GameActionException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /***
     * Start reading at the block after the <code>latestReadBlock</code>, up to the block just before the current round.
     * Updates state of latestReadBlock, and returns an ArrayList of TypedMapLocations read from the block chain.
     *      note: TypedMapLocations are paired MessageType/MapLocations
     * @return List of TypedMapLocations (MapLocations annotated with the type of message that was sent).
     */
    public ArrayList<TypedMapLocation> getLocations() throws GameActionException
    {
        // Starting at the latest read block, loop over blocks until we've read up to the current round.
        int round = rc.getRoundNum();
        ArrayList<TypedMapLocation> result = new ArrayList<>();
        for (int i = latestReadBlock + 1; i < round; i++)
        {
            ArrayList<int[]> messages = getMessages(i);

            // Foreach message, if message is ours, add it to the result list of TypedMapLocations.
            for (int[] message : messages)
                if (message[0] == TEAM_KEY)
                    result.add(new TypedMapLocation(
                        LocationType.fromInteger(message[1]),
                        new MapLocation(message[2], message[3])));
        }
        latestReadBlock = round - 1;
        return result;
    }

    /***
     * Unpack messages from transactions in the given block (identified by blockNum).
     * @return List of transactions that took place on the block.
     */
    public ArrayList<int[]> getMessages(int blockNum) throws GameActionException
    {
        Transaction[] ts = rc.getBlock(blockNum);
        ArrayList<int[]> messages = new ArrayList<>();

        for (Transaction txn : ts)
            messages.add(txn.getMessage());
        return messages;
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
				if (message[0] == Communication.TEAM_KEY && message[1] == 0)
					locations[0] = new MapLocation(message[2], message[3]);
				if (message[0] == Communication.TEAM_KEY && message[1] == 1)
					locations[1] = new MapLocation(message[2], message[3]);
				if (message[0] == Communication.TEAM_KEY && message[1] == 2)
					locations[2] = new MapLocation(message[2], message[3]);
				if (message[0] == Communication.TEAM_KEY && message[1] == 3)
					locations[3] = new MapLocation(message[2], message[3]);
			}
		}
		return locations;
	}
}
