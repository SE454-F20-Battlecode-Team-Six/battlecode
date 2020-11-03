package sealteamsixplayer;

import battlecode.common.MapLocation;


/***
 * Represents a MapLocation annotated with a LocationType
 */
public class TypedMapLocation
{
    private final LocationType t;
    private final MapLocation l;

    public TypedMapLocation(LocationType t, MapLocation l)
    {
        this.t = t;
        this.l = l;
    }

    public LocationType type() { return t; }
    public MapLocation location() { return l; }
}
