package sealteamsixplayer;

/**
 * Enum of various notable locations on the map, buildings, enemies, and soup included.
 */
public enum LocationType
{
    HQ_LOCATION,
    REFINERY_LOCATION,
    DESIGN_SCHOOL_LOCATION,
    FR_LOCATION,
    SOUP_LOCATION,
    ENEMY_HQ_LOCATION,
    EMPTIED_SOUP_LOCATION,
    NETGUN_LOCATION;

    /**
     * Converts an integer into the ordinal representation of a LocationType.
     *
     * Use LocationType.ordinal() to convert back to integer representation.
     */
    public static LocationType fromInteger(int x)
    {
        switch (x)
        {
            case 0: return HQ_LOCATION;
            case 1: return REFINERY_LOCATION;
            case 2: return DESIGN_SCHOOL_LOCATION;
            case 3: return FR_LOCATION;
            case 4: return SOUP_LOCATION;
            case 5: return ENEMY_HQ_LOCATION;
            case 6: return EMPTIED_SOUP_LOCATION;
            case 7: return NETGUN_LOCATION;
        }
        return null;
    }
}
