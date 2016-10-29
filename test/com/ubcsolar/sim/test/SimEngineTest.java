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
	GeoCoord testLocation3;
	GeoCoord testLocation4;
	double expectedTirePressure = 30;
	double expectedVelocity = 10;
	
	public static final double ERROR = 0.000001;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockSimEngine = new SimEngine();
		testLocation1 = new GeoCoord(49.274748, -123.238277, 74);
		testLocation2 = new GeoCoord(49.269148, -123.220081, 91);
		testLocation3 = new GeoCoord(23.123453, 45.591276, 45);
		testLocation4 = new GeoCoord(-12.485890, -94.382364, 10);
	}

	/**
	 * Test method for
	 * {@link com.ubcsolar.sim.SimEngine#getInclinationAngle(com.ubcsolar.common.GeoCoord, com.ubcsolar.common.GeoCoord)}
	 * .
	 */
	@Test
	public final void testGetInclinationAngle() {
		
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation1, testLocation2) - 1.485145724) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation1, testLocation3) + 0.00243923742) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation1, testLocation4) + 0.008617019243) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation2, testLocation3) + 0.003869004564) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation2, testLocation4) + 0.01090753139) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation3, testLocation4) +0.002239073317) < ERROR);
	
		//magnitudes of the angles should be the same, only signs change
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation1, testLocation2) + mockSimEngine.getInclinationAngle(testLocation2, testLocation1)) == 0);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation1, testLocation3) + mockSimEngine.getInclinationAngle(testLocation3, testLocation1)) == 0);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation1, testLocation4) + mockSimEngine.getInclinationAngle(testLocation4, testLocation1)) == 0);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation2, testLocation3) + mockSimEngine.getInclinationAngle(testLocation3, testLocation2)) == 0);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation2, testLocation4) + mockSimEngine.getInclinationAngle(testLocation4, testLocation2)) == 0);
		assertTrue(Math.abs(mockSimEngine.getInclinationAngle(testLocation3, testLocation4) + mockSimEngine.getInclinationAngle(testLocation4, testLocation3)) == 0);
	}

	/**
	 * Test method for
	 * {@link com.ubcsolar.sim.SimEngine#getGradientResistanceForce(double)}.
	 */
	@Test
	public final void testGetGradientResistanceForce() {
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation2)) - 9764.075444) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation3)) - (-23.90450301)) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation4)) - (-84.44574351)) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation2, testLocation3)) - (-37.91615013)) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation2, testLocation4)) - (-106.891688)) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation3, testLocation4)) - (-21.94290018)) < ERROR);
	
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation2, testLocation1)) - (-9764.075444)) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation3, testLocation1)) - 23.90450301) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation4, testLocation1)) - 84.44574351) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation3, testLocation2)) - 37.91615013) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation4, testLocation2)) - 106.891688) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getGradientResistanceForce(mockSimEngine.getInclinationAngle(testLocation4, testLocation3)) - 21.94290018) < ERROR); 
		
	}

	/**
	 * Test method for
	 * {@link com.ubcsolar.sim.SimEngine#getRollingResistanceForce(double, double, double)}
	 * .
	 */
	@Test
	public final void testGetRollingResistanceForce() {
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation2), expectedTirePressure, expectedVelocity) - 4.47385478) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation3), expectedTirePressure, expectedVelocity) - 52.29754442) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation4), expectedTirePressure, expectedVelocity) - 52.29575838) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation2, testLocation3), expectedTirePressure, expectedVelocity) - 52.29730857) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation2, testLocation4), expectedTirePressure, expectedVelocity) - 52.29458899) < ERROR);
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation3, testLocation4), expectedTirePressure, expectedVelocity) - 52.2975689) < ERROR);
	
		//Friction should always be positive, regardless of direction (uphill vs downhill)
		//Friction on a slope should be the same, regardless of direction (uphill vs downhill)
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation2), expectedTirePressure, expectedVelocity)) 
					== 
				   Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation2, testLocation1), expectedTirePressure, expectedVelocity)));
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation3), expectedTirePressure, expectedVelocity)) 
				   == 
			       Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation3, testLocation1), expectedTirePressure, expectedVelocity)));
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation1, testLocation4), expectedTirePressure, expectedVelocity)) 
				   == 
			       Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation4, testLocation1), expectedTirePressure, expectedVelocity)));
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation2, testLocation3), expectedTirePressure, expectedVelocity)) 
				   == 
			       Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation3, testLocation2), expectedTirePressure, expectedVelocity)));
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation2, testLocation4), expectedTirePressure, expectedVelocity)) 
				   == 
			       Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation4, testLocation2), expectedTirePressure, expectedVelocity)));
		assertTrue(Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation3, testLocation4), expectedTirePressure, expectedVelocity)) 
				   == 
			       Math.abs(mockSimEngine.getRollingResistanceForce(mockSimEngine.getInclinationAngle(testLocation4, testLocation3), expectedTirePressure, expectedVelocity)));
	}

}
