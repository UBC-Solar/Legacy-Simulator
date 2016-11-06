/**
 * 
 */
package com.ubcsolar.sim.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.sim.DefaultCarModel;
import com.ubcsolar.sim.SimEngine;
import com.ubcsolar.weather.FIODataPointFactory;
import com.ubcsolar.weather.ForecastIOFactory;

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
	GeoCoord testLocation5;
	GeoCoord testLocation6;
	
	ForecastIO testForecast;
	FIODataPoint point1FIO;
	FIODataPoint point2FIO;
	FIODataPoint point3FIO;

	double expectedTirePressure = 30;
	double expectedVelocity = 10;
	
	public static final double ERROR = 0.00001;

	
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
		testLocation5 = new GeoCoord(0, 0, 32);
		testLocation6 = new GeoCoord(45, 45, 234);
		
		FIODataPointFactory datapointFactory = new FIODataPointFactory();
		
		JsonObject point1 = datapointFactory.time(0).windBearing(90).windSpeed(20).cloudCover(0).build();
		JsonObject point2 = datapointFactory.time(3600).windBearing(200).windSpeed(30).cloudCover(1).build();
		JsonObject point3 = datapointFactory.time(7200).windBearing(45).windSpeed(30).cloudCover(0.43).build();
		List<JsonObject> datapoints = new ArrayList<JsonObject>();
		datapoints.add(point1);
		datapoints.add(point2);
		datapoints.add(point3);
		
		ForecastIOFactory.addDatapoints(datapoints);
		ForecastIOFactory.changeLocation(testLocation2);
		testForecast = ForecastIOFactory.build();
		
		point1FIO = new FIODataPoint(point1);
		point2FIO = new FIODataPoint(point2);
		point3FIO = new FIODataPoint(point3);
		point1FIO.setTimezone("PST");
		point2FIO.setTimezone("PST");
		point3FIO.setTimezone("PST");

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
	
	
	@Test
	public final void testChooseReport(){
		assertEquals(mockSimEngine.chooseReport(testForecast, 0).time(), point1FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 3600).time(), point2FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 1000).time(), point1FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, -100).time(), point1FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 1801).time(), point2FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 1800).time(), point1FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 10000000).time(), point3FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 3601).time(), point2FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 5400).time(), point2FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 5401).time(), point3FIO.time());
		assertEquals(mockSimEngine.chooseReport(testForecast, 7199).time(), point3FIO.time());
	}
	
	@Test
	public final void testCalculateDrag(){
		double testDrag1 = mockSimEngine.calculateDrag(testLocation2, testLocation1, 20, point2FIO);
		double calculatedDrag1 = -0.5*GlobalValues.CAR_CROSS_SECTIONAL_AREA*GlobalValues.DRAG_COEFF*
				(0.5134923613) * (0.5134923613);//velocities calculated by hand
		assertTrue(Math.abs(testDrag1-calculatedDrag1) <= ERROR);
		
		double testDrag2 = mockSimEngine.calculateDrag(testLocation1, testLocation2, 20, point2FIO);
		double calculatedDrag2 = 0.5*GlobalValues.CAR_CROSS_SECTIONAL_AREA*GlobalValues.DRAG_COEFF*
				(0.5976187498) * (0.5976187498);
		assertTrue(Math.abs(testDrag2-calculatedDrag2) <= ERROR);
		
		double testDrag3 = mockSimEngine.calculateDrag(testLocation6, testLocation5, 30, point3FIO);
		double calculatedDrag3 = 0.5*GlobalValues.CAR_CROSS_SECTIONAL_AREA*GlobalValues.DRAG_COEFF*
				(1.6666666667) * (1.666666667);
		assertTrue(Math.abs(testDrag3-calculatedDrag3) <= ERROR);
		
		double testDrag4 = mockSimEngine.calculateDrag(testLocation5, testLocation6, 30, point3FIO);
		assertTrue(testDrag4 == 0);
		
		FIODataPointFactory datapointFactory = new FIODataPointFactory();
		JsonObject point4 = datapointFactory.time(2345).windSpeed(0).build();
		FIODataPoint point4FIO = new FIODataPoint(point4);
		
		double testDrag5 = mockSimEngine.calculateDrag(testLocation5, testLocation6, 30, point4FIO);
		double calculatedDrag5 = 0.5*GlobalValues.CAR_CROSS_SECTIONAL_AREA*GlobalValues.DRAG_COEFF*
				(0.833333333) * (0.83333333);
		assertTrue(Math.abs(testDrag5-calculatedDrag5) <= ERROR);
	}
	
	@Test
	public final void testCalculateSunPower(){
		double carArea = (new DefaultCarModel()).getSolarPanelArea();

		double testPower1 = mockSimEngine.calculateSunPower(point1FIO);
		double calculatedPower1 = GlobalValues.PANEL_EFFICIENCY * carArea * 990.0;
		assertTrue(Math.abs(testPower1-calculatedPower1) <= ERROR);
		
		double testPower2 = mockSimEngine.calculateSunPower(point2FIO);
		double calculatedPower2 = GlobalValues.PANEL_EFFICIENCY * carArea * 247.5;
		assertTrue(Math.abs(testPower2-calculatedPower2) <= ERROR);
		
		double testPower3 = mockSimEngine.calculateSunPower(point3FIO);
		double calculatedPower3 = GlobalValues.PANEL_EFFICIENCY * carArea * 930.9660525;
		assertTrue(Math.abs(testPower3-calculatedPower3) <= ERROR);
		
	}

}
