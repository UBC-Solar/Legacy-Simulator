package com.ubcsolar.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ubcsolar.sim.*;

public class PanelsTest {

	Panels myTestPanels;
	
	@Before
	public void init(){
		myTestPanels = new Panels(0, null, 0, 0, null);
}
	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void test1CurrentFromPanel(){
		assertTrue(myTestPanels.current_from_panel()== 1400.4 / 38.06 );
	}
	
	@Test
	public void testPower(){
		assertTrue(myTestPanels.power_from_panel()== 1400.4);
	}
	
	@Test
	public void testTemp(){
		assertTrue(myTestPanels.temp_from_panel()== 0);
	}
	
	@Test
	public void testVoltage(){
		assertTrue(myTestPanels.voltage_from_panel()== 38.06);
	}
	
	
				
	}
	

