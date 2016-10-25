/**
 * 
 */
package com.ubcsolar.sim.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.sim.SimEngine;

/**
 * @author dust
 *
 */
public class SimEngineTest {
	SimEngine mockSimEngine;
	GeoCoord testLocation1;
	GeoCoord testLocation2;
	
	public static final double ERROR = 0.0000001;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockSimEngine = new SimEngine();
		testLocation1 = new GeoCoord(49.27474888, -123.23827744, 74);
		testLocation2 = new GeoCoord(49.26914869, -123.22008133, 91);
	}

	/**
	 * Test method for
	 * {@link com.ubcsolar.sim.SimEngine#getInclinationAngle(com.ubcsolar.common.GeoCoord, com.ubcsolar.common.GeoCoord)}
	 * .
	 */
	@Test
	public final void testGetInclinationAngle() {
		// Positive angle
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation1, testLocation2) - 1.485144777554603) < ERROR);
		// Negative angle
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation2, testLocation1) + 1.485144777554603) < ERROR);
	}

	/**
	 * Test method for
	 * {@link com.ubcsolar.sim.SimEngine#getGradientResistanceForce(double)}.
	 */
	@Test
	public final void testGetGradientResistanceForce() {
		double expectedAngle = mockSimEngine.getInclinationAngle(testLocation1, testLocation2);
		double expectedForce = GlobalValues.CAR_MASS * 9.8 * Math.sin(expectedAngle);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(expectedAngle) - 9764.074650337221) < ERROR);
		
	}

	/**
	 * Test method for
	 * {@link com.ubcsolar.sim.SimEngine#getRollingResistanceForce(double, double, double)}
	 * .
	 */
	@Test
	public final void testGetRollingResistanceForce() {
		double expectedAngle = mockSimEngine.getInclinationAngle(testLocation1, testLocation2);
		double expectedTirePressure = 30;
		double expectedVelocity = 10;
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(expectedAngle, expectedTirePressure, expectedVelocity) - 4.473904107648733) < ERROR);
	}

}
