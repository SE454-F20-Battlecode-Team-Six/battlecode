package sealteamsixplayer;

import static org.junit.Assert.*;

import battlecode.common.*;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class FulfillmentCenterTests
{
  // Set up tests following 3-As strategy (goes by different names, this is just how I learned it.
  // First, Arrange. Set up pre-existing state such that the result of the system under test is expected.
  // Second, Act. Call the unit code and store the result.
  // Third, Assert. Inspect the result and assert that it matches the expected result.

  /**
   * Assert true drone built successfully
   */
  @Test
  public void trueWhenTryBuildDroneSucceeds() throws GameActionException
  {
    // Arrange.
    // Mock the API of any dependencies. We only want to test the logic in our system under test.
    RobotController rc = mock(RobotController.class);

    // Set up our system with the minimum necessities for the expected outcome.
    FulfillmentCenter r = new FulfillmentCenter(rc);
    for(Direction dir : r.directions)
    {
      when(rc.canBuildRobot(RobotType.DELIVERY_DRONE, dir)).thenReturn(true);
    }

    // Act.
    boolean b = r.tryBuildDrone();

    // Assert.
    assertTrue(b);
  }

  /**
   * Assert false drone not built successfully
   */
  @Test
  public void falseWhenTryBuildDroneFails() throws GameActionException
  {
    // Arrange.
    // Mock the API of any dependencies. We only want to test the logic in our system under test.
    RobotController rc = mock(RobotController.class);

    // Set up our system with the minimum necessities for the expected outcome.
    FulfillmentCenter r = new FulfillmentCenter(rc);
    for(Direction dir : r.directions)
    {
      when(rc.canBuildRobot(RobotType.DELIVERY_DRONE, dir)).thenReturn(false);
    }

    // Act.
    boolean b = r.tryBuildDrone();

    // Assert.
    assertFalse(b);
  }
}