package com.ubcsolar.test;

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
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void test1GetMaxRechargeTime(){
		assertTrue(myTestBattery.getMaxRechargeTime(10.0)== 39600.0);
	}
	
	@Test
	public void test2GetMaxRechargeTime(){
		assertTrue(myTestBattery.getMaxRechargeTime(30.0)== -1.0);
	}
	
	@Test
	public void test1GetCurrent(){
		assertTrue(myTestBattery.getCurrent(5.0)== 79200.0);
	}
	
	@Test
	public void testStoreEnergy(){
		assertTrue(myTestBattery.storeEnergy(5.0,10.0)== 4416.5);
	}
	
	@Test
	public void testDrawEnergy(){
		assertTrue(myTestBattery.drawEnergy(5.0,10.0)== 4416.5);
				
	}
	
}
