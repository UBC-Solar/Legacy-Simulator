package com.ubcsolar.sim.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ubcsolar.sim.*;

public class BatteryTest {

	Battery myTestBattery;
	
	@Before
	public void init(){
	myTestBattery = new Battery();
}
	
	@Test
	public void init2(){
		myTestBattery = new Battery("myfile.txt");
	}
	
	public void test2(){
		init2();
		test();
		init2();
		testGetBatteryVoltage();
		init2();
		testGetStateOfCharge();
		init2();
		testGetMaxRechargeTime();
		init2();
		testGetCurrent();
		init2();/*
		testStoreEnergy();
		init2();
		testDrawEnergy();*/
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetBatteryVoltage(){
		assertTrue(myTestBattery.getBatteryVoltage()== 40.15);
	}
	
	
	
	@Test
	public void testGetStateOfCharge(){
		assertNotNull(myTestBattery.getStateOfCharge());
		assertTrue(myTestBattery.getStateOfCharge()== (int)((220*40.15/2)/(220*40.15))*100);
	}
	
	@Test
	public void testGetMaxRechargeTime(){
		assertTrue(myTestBattery.getMaxRechargeTime(10.0)== 39600.0);
		assertTrue(myTestBattery.getMaxRechargeTime(230.0)== -1.0);
	}
	
	@Test
	public void testGetCurrent(){
		assertTrue(myTestBattery.getCurrent(5.0)== 79200.0);
	}
	/*
	@Test
	public void testStoreEnergy(){
		assertTrue(myTestBattery.storeEnergy(5.0,10.0)> 4416.5);
	}
	
	@Test
	public void testDrawEnergy(){
		assertTrue(myTestBattery.drawEnergy(5.0,10.0)< 4416.5);
				
	}*/
	
}
