/**
 * 
 */
package com.ubcsolar.common.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ubcsolar.common.TelemDataPacket;

/**
 * @author Noah
 *
 */
public class TelemDataPacketTest {

//============ Class/test values =====================
	private int defaultSpeed;
	private int defaultTotalVoltage;
	private HashMap<String, Integer> defaultTemperatures; 
	private HashMap<Integer,ArrayList<Float>> defaultCellVoltages;
	
//============ Setup/takedown methods =================
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		defaultSpeed = 5;
		defaultTotalVoltage = 6;
		defaultTemperatures = new HashMap<String, Integer>();		
		defaultTemperatures.put("bms", (35));
		defaultTemperatures.put("motor", (40));
		defaultTemperatures.put("pack0", (41));
		defaultTemperatures.put("pack1", (42));
		defaultTemperatures.put("pack2", (43));
		defaultTemperatures.put("pack3", (44));
		defaultCellVoltages = new HashMap<Integer,ArrayList<Float>>();
		for(int i = 0; i<4; i++){ //Current number of cells coming in pack is 4. Will probably have to adjust that.
			defaultCellVoltages.put(i, generatePackVoltages());
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

//====================CONSTRUCTOR TESTS======================
	@Test
	public void stndrdConstructorShouldNotThrowExcptns(){
		try{
			TelemDataPacket test = makeDefaultTelemDataPacket();
		}
		catch(Exception e){
			fail("Exception "  + e.getClass() + " thrown");
		}
		
	}
	
	@Test
	public void givenTimeConstructorShouldNotThrowExcptns(){
		TelemDataPacket test;
		long startTime = System.currentTimeMillis();
		try{
			test = this.makeTelemPacketWithTime(startTime);
		}
		catch(Exception e){
			fail("Exception "  + e.getClass() + " thrown");
			return;
		}
		
		assertTrue(Math.abs(test.getTimeCreated() - startTime)<0.0000000001);
		
	}
	
	/*
	 * Shouldn't allow data units to be made with null values. 
	 * If they try, should throw an exception. 
	 * Not sure if Null Pointer Exception is the right one (Illegal Argument Exception?)
	 */
	@Test(expected = NullPointerException.class)
	public void nullConstructorShouldThrowNullPointerExcptns(){
			TelemDataPacket test = new TelemDataPacket(-1,-1,null,null);
	}
	
	/*
	 *Up for debate, but I don't think we should enforce a 'no null' rule on
	 *the actual data. What if something happens and one value comes
	 *back null? Wouldn't want to toss the rest of the data.  
	 */
	@Test
	public void constructorMapsWithNullValuesShouldNotThrowNullPointerExcptns(){
		HashMap<String, Integer> tempWithNulls = new HashMap<String, Integer>();
		tempWithNulls.put("null value", null);
		tempWithNulls.put(null, 2);
		
		HashMap<Integer,ArrayList<Float>> cellVoltagesWithNulls = new HashMap<Integer,ArrayList<Float>>();
		cellVoltagesWithNulls.put(-1, new ArrayList<Float>());
		cellVoltagesWithNulls.put(null, null);
		cellVoltagesWithNulls.put(1, null);
		ArrayList<Float> temp = new ArrayList<Float>();
		temp.add(null);
		temp.add((float) 1.224);
		cellVoltagesWithNulls.put(2,temp);
		
		try{
			TelemDataPacket test = 
					new TelemDataPacket(defaultSpeed,defaultTotalVoltage,tempWithNulls, cellVoltagesWithNulls);
		}
		catch(Exception e){
			fail("Threw exception: " + e.getClass());
		}
	}
	
//================== Getter Tests ===================
	@Test
	public void getTimeCreatedShouldBeTimeCreated(){
		double timeNow = System.currentTimeMillis();
		TelemDataPacket test = this.makeDefaultTelemDataPacket();
		//Should be less than 3 ms between logging the time and creating the telemPacket
		//unless System is running very slow
		//Wasn't sure how else to test this. 
		assertTrue("Difference was: " + (test.getTimeCreated() - timeNow),(test.getTimeCreated() - timeNow)<3);
	}
	@Test
	public void getSpeedShouldGetSpeed(){
		TelemDataPacket test = this.makeDefaultTelemDataPacket();
		assertEquals("speeds", test.getSpeed(), this.defaultSpeed);
	}
	@Test
	public void getTotalVoltageShouldGetTotalVoltages(){
		TelemDataPacket test = this.makeDefaultTelemDataPacket();
		assertTrue("totalVoltage", (test.getTotalVoltage() - this.defaultTotalVoltage) < 0.000000001);
	}
	@Test
	public void getTemperaturesShouldGetTemperatures(){
		TelemDataPacket test = this.makeDefaultTelemDataPacket();
		assertEquals("temperatures", test.getTemperatures(), this.defaultTemperatures);
	}
	@Test
	public void getCellVoltagesShouldGetCellVoltages(){
		TelemDataPacket test = this.makeDefaultTelemDataPacket();
		assertEquals("cellVoltages", test.getCellVoltages(), this.defaultCellVoltages);
	}

// ================= toString test ==================
	@Test
	public void toStringShouldNotBeNullAndShouldBeLong(){
		TelemDataPacket test = this.makeDefaultTelemDataPacket();
		assertTrue(test.toString() != null);
		assertTrue(test.toString().length()>5); //Should be at least 5 characters long, probably more
	}
	
	
//==================  modification tests ==================
	
	@Test
	public void shouldntBeAbleToAddOrRemoveTemperatures(){
		TelemDataPacket test = this.makeDefaultTelemDataPacket();
		test.getTemperatures().put("test", -25);
		assertTrue(test.getTemperatures().get("test") == null);
		test.getTemperatures().remove("motor");
		assertFalse(test.getTemperatures().get("motor") == null);
	}
	@Test
	public void shouldntBeAbleToAddOrRemoveCellVoltages(){
		TelemDataPacket test = this.makeDefaultTelemDataPacket();
		test.getCellVoltages().put(35, null);
		assertTrue(test.getCellVoltages().get(35) == null);
		test.getCellVoltages().remove(1);
		assertFalse(test.getCellVoltages().get(1) == null);
	}
	
//================== equals() tests =======================
	
	@Test
	public void identicalPacketShouldBeEqual(){
		TelemDataPacket toTest = this.makeDefaultTelemDataPacket();
		
		assertTrue(toTest.equals(toTest));
	}
	
	@Test
	public void differentTimesShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(System.currentTimeMillis()+10);
		assertFalse(toTestOne.equals(toTestTwo));
		
	}
	
	@Test
	public void differentSpeedsShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		this.defaultSpeed++;
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		this.defaultSpeed--;
		
		assertFalse(toTestOne.equals(toTestTwo));	
	}
	
	@Test
	public void differentTotalVoltageShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		this.defaultTotalVoltage++;
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		this.defaultTotalVoltage--;
		
		assertFalse(toTestOne.equals(toTestTwo));	
	}
	
	@Test
	public void temperaturesShortAValueShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		String keyToRemove = defaultTemperatures.keySet().iterator().next();
		int valueToRemove = defaultTemperatures.get(keyToRemove);
		this.defaultTemperatures.remove(keyToRemove);
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		this.defaultTemperatures.put(keyToRemove, valueToRemove);
		
		assertFalse(toTestOne.equals(toTestTwo));	
	}
	
	@Test
	public void temperaturesWithExtraValueShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		System.out.println(this.defaultTemperatures.size());
		this.defaultTemperatures.put("TEST", 42);
		System.out.println(this.defaultTemperatures.size());
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		System.out.println(toTestOne.getTemperatures().size());
		System.out.println(toTestTwo.getTemperatures().size());
		
		assertFalse(toTestOne.equals(toTestTwo));	
	}
	
	@Test
	public void temperatureWithOneValueChangedShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		String keyToRemove = defaultTemperatures.keySet().iterator().next();
		int valueToRemove = defaultTemperatures.get(keyToRemove);
		defaultTemperatures.remove(keyToRemove);
		defaultTemperatures.put(keyToRemove, valueToRemove + 1);
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		assertFalse(toTestOne.equals(toTestTwo));
	}
	
	@Test
	public void seperateButSameTemperatureMapsShouldBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		HashMap<String, Integer> newTempMap = new HashMap<String, Integer>();
		for(String key : this.defaultTemperatures.keySet()){
			newTempMap.put(key, defaultTemperatures.get(key));
		}
		
		TelemDataPacket toTestTwo = new TelemDataPacket(defaultSpeed, defaultTotalVoltage,newTempMap,defaultCellVoltages,toTestOne.getTimeCreated());
		
		assertTrue(toTestOne.equals(toTestTwo));	
	}
	
	
	@Test
	public void cellVoltagesShortAValueShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		int keyToRemove = defaultCellVoltages.keySet().iterator().next();
		ArrayList<Float> valueToRemove = defaultCellVoltages.get(keyToRemove);
		this.defaultCellVoltages.remove(keyToRemove);
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		this.defaultCellVoltages.put(keyToRemove, valueToRemove);
		
		assertFalse(toTestOne.equals(toTestTwo));	
	}
	
	@Test
	public void cellVoltagesWithExtraValueShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		int randomKey = 27;
		this.defaultCellVoltages.put(randomKey, new ArrayList<Float>());
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		this.defaultCellVoltages.remove(randomKey);
		
		assertFalse(toTestOne.equals(toTestTwo));	
	}
	
	
	@Test
	public void cellVoltagesWithOneDifferentArrayShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		int keyToRemove = defaultCellVoltages.keySet().iterator().next();
		ArrayList<Float> valueToRemove = defaultCellVoltages.get(keyToRemove);
		this.defaultCellVoltages.remove(keyToRemove);
		ArrayList<Float> definitelyDifferent = new ArrayList<Float>();
		for(float f : valueToRemove){
			definitelyDifferent.add(f + 1 );
		}
		this.defaultCellVoltages.put(keyToRemove, definitelyDifferent);
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		
		assertFalse(toTestOne.equals(toTestTwo));	 
	}
	
	@Test
	public void seperateButSameCellVoltageMapsShouldBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		HashMap<Integer, ArrayList<Float>> newCellVMap = new HashMap<Integer, ArrayList<Float>>();
		for(int key : this.defaultCellVoltages.keySet()){
			newCellVMap.put(key, defaultCellVoltages.get(key));
		}
		
		TelemDataPacket toTestTwo = new TelemDataPacket(defaultSpeed, defaultTotalVoltage,defaultTemperatures,newCellVMap,toTestOne.getTimeCreated());
		
		assertTrue(toTestOne.equals(toTestTwo));	
	}
	
	@Test
	public void cellVoltageArrayListWithSameValuesShouldBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		int keyToRemove = defaultCellVoltages.keySet().iterator().next();
		ArrayList<Float> valueToRemove = defaultCellVoltages.get(keyToRemove);
		this.defaultCellVoltages.remove(keyToRemove);
		ArrayList<Float> seperateButSameValues = new ArrayList<Float>();
		for(float f : valueToRemove){
			seperateButSameValues.add(f);
		}
		this.defaultCellVoltages.put(keyToRemove, seperateButSameValues);
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		
		assertTrue(toTestOne.equals(toTestTwo));
	}
	
	
	@Test
	public void cellVoltageArrayListWithExtraValueShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		int arbitraryKey = defaultCellVoltages.keySet().iterator().next();
		ArrayList<Float> existingCellVoltageList = defaultCellVoltages.get(arbitraryKey);
		existingCellVoltageList.add((float) 2.35); //add in one extra value that wasn't there before
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
		assertFalse(toTestOne.equals(toTestTwo));
	}
	
	@Test
	public void cellVoltageArrayListOneValueShortShouldNotBeEqual(){
		TelemDataPacket toTestOne = this.makeDefaultTelemDataPacket();
		int arbitraryKey = defaultCellVoltages.keySet().iterator().next();
		ArrayList<Float> oneCellVoltageList = defaultCellVoltages.get(arbitraryKey);
		//remove one value that was there before
		//dynamic selection; arbitrary value and now guaranteed to not have index out of range. 
		oneCellVoltageList.remove(oneCellVoltageList.size() -1 ); 
		TelemDataPacket toTestTwo = this.makeTelemPacketWithTime(toTestOne.getTimeCreated());
	}
	
	
	
// ================= Helper Functions ===============
	
	
	//because I need to make a bunch of these and this is easier
	private TelemDataPacket makeTelemPacketWithTime(double milliSecondTime){
		return new TelemDataPacket(defaultSpeed, defaultTotalVoltage,defaultTemperatures,defaultCellVoltages,milliSecondTime);
	}
	//because I need to make a bunch of these and this is easier
	private TelemDataPacket makeDefaultTelemDataPacket(){
		return new TelemDataPacket(defaultSpeed, defaultTotalVoltage,defaultTemperatures,defaultCellVoltages);
	}
	
	//Generates a default pack voltage array, because it's too hard to type manually. 
	private ArrayList<Float> generatePackVoltages(){
		ArrayList<Float> cell1 = new ArrayList<Float>();
		Random rng = new Random();
		
		for(int i = 0; i<10; i++){
			cell1.add(rng.nextFloat() * 10);
		}
		return cell1;
	}
	
	
	
}
