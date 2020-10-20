package sealteamsixplayer;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Communication
{
    RobotController rc;
    static final int teamKey = 66554433;
    public static int maxSoupCost = 5;

    public enum MessageType
    {
        HQ_LOCATION,
        REFINERY_LOCATION,
        DESIGN_SCHOOL_LOCATION,
        SOUP_LOCATION,
    }

    public Communication(RobotController rc)
    {
        this.rc = rc;
    }

    public boolean sendLocation(MessageType type, int x, int y)
    {
        int[] message = {x, y};
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
}
