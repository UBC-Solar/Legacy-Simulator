package com.ubcsolar.database.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.database.CSVDatabase;

public class CSVDatabaseTest {
	
	CSVDatabase toTest;
	CSVDatabase toTestTwo;
	//======================SETTING UP THE TESTS=========================
	

	@Before
	public void setUp() throws IOException {
		for(int i = 0; i<15; i++){
			//force it to take at least one ms before creating next DB
			//to guarantee a new DB name
		}
		toTest = new CSVDatabase();
		
		for(int i = 0; i<15; i++){
			//force it to take at least one ms before creating next DB
			//to guarantee a new DB name
		}
	}

	@After
	public void tearDown() throws IOException{
		if(toTest != null && toTest.isConnected()){
				toTest.saveAndDisconnect();
		}
		if(toTestTwo != null && toTestTwo.isConnected()){
			toTestTwo.saveAndDisconnect(); //needed because we throw exceptions here
			toTestTwo = null;
		}
	}

	@BeforeClass
	public static void firstTimeSetup() throws Exception{
		//run only once at the very beginning.
		//used for things like logging into a Database
	}
	
	@AfterClass
	public static void afterAllTestTearDown() throws Exception{
		//runs after all the tests have run
		//used for things like closing connections to databases. 
		
		//TODO delete the .csv's created by this round of tests. 
	}

	//===================Constructor tests ==========================

	/*
	 * Straight forward test. Given a name, it should make a
	 * file with specified name
	 */
	@Test
	public void constructorWithStringShouldMakeName() throws IOException{
		CSVDatabase test = new CSVDatabase("hello");
		File theFile = new File("Output\\hello.csv");
		assertTrue(theFile.exists());
	}
	
	/*
	 * By default, Java will simply overwrite a file if told to make one with the
	 * same name. I don't want to be overwriting databases accidentally though,
	 * this program should throw an exception, even when the first has been saved. 
	 */
	@Test(expected = IOException.class)
	public void creatingADBWithTheSameNameAsAnExistingShouldThrowException() throws IOException{
		try{
			tearDown();
		}
		catch(IOException e){fail("Should not have gotten exception here");}
		CSVDatabase smallToTest = null;
		String name = ""+System.currentTimeMillis();
		try {
			smallToTest = new CSVDatabase(name);
		} catch (IOException e) {fail("Should not have gotten exception here");}
		
		smallToTest.saveAndDisconnect();
		
		CSVDatabase two = new CSVDatabase(name); //should throw exception here; trying to create one with same name. 
		
	}
	/*
	 * I thought it would error because it's open by another program.
	 * It didn't... but still should be able to overwrite an open DB
	 */
	@Test(expected = IOException.class)
	public void creatingDBWithOneWithSameNameOpenShouldThrowException() throws IOException{
		try{
			tearDown();
		}
		catch(IOException e){fail("Should not have gotten exception here");}
		CSVDatabase smallToTest = null;
		String name = ""+System.currentTimeMillis();
		try {
			smallToTest = new CSVDatabase(name);
		} catch (IOException e) {fail("Should not have gotten exception here");}
		
		CSVDatabase two = new CSVDatabase(name); //should throw exception here; trying to create one with same name. 
		
	}
	/*
	 * So apparently windows will allow a file simply name ".csv", but the program
	 * shouldn't allow it. 
	 */
	@Test(expected = IOException.class)
	public void creatingDBWithBlankNameShouldThrowException() throws IOException{
		try{
			tearDown();
		}catch(IOException e){fail("Should not have gotten exception here");}
		this.toTest = new CSVDatabase("");
	}
	
	@Test
	public void nullNameShouldThrowException() throws IOException{
		try{
			tearDown();
		}catch(IOException e){fail("Should not have gotten exception here");}
		
		try{this.toTest = new CSVDatabase(null);}
		catch(IOException e){
			throw e;
		}
		catch(NullPointerException e){
			fail("Should be able to handle null, just not create the file");
		}
	
	}
	
	/*
	 * The class should be able to detect and ignore any extra csv suffix
	 */
	@Test(expected = IOException.class)
	public void csvSuffixShouldNotBeDuplicated(){
		try{
			tearDown();
		}catch(IOException e){fail("Should not have gotten exception here");}
		try {
			this.toTest = new CSVDatabase("test.csv");
		} catch(IOException e){fail("Should not have gotten exception here");}
		
		File temp = new File("test.csv.csv");
		assertFalse("File does exist, csv suffix duplicated", temp.exists());
		
	}

	
	@Test(expected = IOException.class)
	public void creatingDBWithInvalidNameShouldThrowException() throws IOException{
		try{
			tearDown();
		}catch(IOException e){fail("Should not have gotten exception here");}
		this.toTest = new CSVDatabase("something*.csv");
	}
		
	//================isConnected() tests ============================
	/*
	 * Test the 'isConnected' method, should
	 * be true if connected, and false if not. 
	 */
	@Test
	public void testIsConnected() throws IOException{
		CSVDatabase test = new CSVDatabase();
		//should build a .csv with the time as the name. 
		assertTrue(test.isConnected());
		test.flushAndSave();
		assertTrue(test.isConnected());
		test.saveAndDisconnect();
		assertFalse(test.isConnected());
	}
	
	//===============flushAndSave() tests ===================================
	
	/*
	 * Basic test
	 */
	@Test
	public void flushAndSaveShouldNotThrowException(){
		try {
			toTest.flushAndSave();
		} catch (IOException e) {
			fail("threw exception on a standard flush");
		}
	}
	
	/*
	 * Needs to throw an exception, currently should be IOException
	 */
	@Test(expected  = IOException.class)
	public void flushAndSaveWhenDCdShouldThrowException() throws IOException{
		try {
			toTest.saveAndDisconnect();
		} catch (IOException e) {fail("should not have had exception here");}
		toTest.flushAndSave();
	}
	
	//=================== saveAndDisconnect() tests =======================
	
	/*
	 * Basic test. Assumes isConected is working.
	 */
	@Test
	public void dbShouldBeDCdAfterSavdAndDisconnect() throws IOException{
		assertTrue(toTest.isConnected());
		this.toTest.saveAndDisconnect();
		assertFalse(toTest.isConnected());
	}
	
	/*
	 * If something accidentally says to disconnect and it's already disconnected,
	 * it shouldn't error (or have any other effect)
	 */
	@Test
	public void saveAndDisconnectTwiceShouldNotThrowException() throws IOException{
		try {
			toTest.saveAndDisconnect();
		} catch (IOException e) {
			//something weird has happened here, should work the first time.  
			throw e;
		}
		try{
		toTest.saveAndDisconnect();
		}
		catch(IOException e){
			fail("Threw exception on double disconnect. Possibly a file isue, but probably not");
		}
	}
	
	//==================== store() tests ==========================
	
	@Test
	public void storeShouldNotThrowException(){
		try {
			this.toTest.store(generateStandardTelemDataPacket());
		} catch (IOException e) {
			fail("threw exception, shouldn't have");
		}
	}
	
	@Test
	public void storingNullShouldNotThrowException(){
		try {
			this.toTest.store(null);
		} catch (IOException e) {
			fail("threw exception, shouldn't have");
		}
	}
	
	/*
	 * the DB should be robust enough to handle nulls given to it,
	 * or at least, throw an IllegalArgumentException rather than a 
	 * NullPointerException.
	 */
	@Test
	public void storingTelemPktWIthNullValuesShouldNotThrowException(){
		try {
			this.toTest.store(generateTerribleTelemDataPacket());
		} catch (IOException e) {
			fail("threw IOException, shouldn't have");
		}catch(NullPointerException e){
			fail("threw Null Pointer Exception, shouldn't have");
		}
	}
	
	/*
	 * the DB should be robust enough to handle nulls given to it. 
	 * It's debatable whether it should throw an illegal argument exception,
	 * but definitely shouldn't throw a nullPointerException.
	 */
	@Test
	public void storingTelemPktWIthEmtpyArraysShouldNotThrowException(){
		try {
			this.toTest.store(generateEmptyMapsTelemDataPacket());
		} catch (IOException e) {
			fail("threw IOException, shouldn't have");
		}catch(NullPointerException e){
			fail("threw Null Pointer Exception, shouldn't have");
		}
	}
	
	//================ getLastTelemDataPacket() tests ===============
	
	private void testZeroAndNegativeInputsShouldGiveSizeZeroLists(CSVDatabase toCheck){
		assertTrue(toCheck.getLastTelemDataPacket(-1) != null);
		assertTrue(toCheck.getLastTelemDataPacket(-1).size() == 0);
		assertTrue(toCheck.getLastTelemDataPacket(0) != null);
		assertTrue(toCheck.getLastTelemDataPacket(0).size() == 0);
		assertTrue(toCheck.getLastTelemDataPacket(1) != null);
		// we can't know what this result will be, commented out.
		//assertTrue(toTest.getLastTelemDataPacket(0).size() == 0);
	}
	
	@Test
	public void shouldNotGetErrorIfGiveNegatives(){
		try{
			toTest.getLastTelemDataPacket(-1);
		}catch(Exception e){
			fail("Threw " + e.getClass());
		}
	}
	
	@Test
	public void emptyDataBaseShouldGiveEmptyListNoMatterArgs(){
		this.testZeroAndNegativeInputsShouldGiveSizeZeroLists(this.toTest);
		ArrayList<TelemDataPacket> returnedList;
		returnedList = this.toTest.getLastTelemDataPacket(25);
		assertTrue(returnedList != null && returnedList.size() == 0);
	}
	
	@Test
	public void sizeOneDataBaseShouldGiveMeListSizeOneNoMatterArgs() throws IOException{
		TelemDataPacket testingPacket = this.generateStandardTelemDataPacket();
		this.toTest.store(testingPacket);
		this.testZeroAndNegativeInputsShouldGiveSizeZeroLists(toTest);
		
		assertTrue(this.toTest.getLastTelemDataPacket(1).size() == 1);
		assertTrue(this.toTest.getLastTelemDataPacket(1).get(0).equals(testingPacket));
		//if asking for more, just return all
		assertTrue(this.toTest.getLastTelemDataPacket(2).size() == 1);
		assertTrue(this.toTest.getLastTelemDataPacket(2).get(0).equals(testingPacket));
	}
	
	@Test
	public void sizeTwoDataBaseShouldOnlyGiveWhatsAsked() throws IOException{
		TelemDataPacket testingPacket = this.generateStandardTelemDataPacket();
		TelemDataPacket secondTestingPacket = this.generateTerribleTelemDataPacket();
		this.toTest.store(testingPacket);
		this.toTest.store(secondTestingPacket);
		this.testZeroAndNegativeInputsShouldGiveSizeZeroLists(toTest);
		
		assertTrue(this.toTest.getLastTelemDataPacket(1).size() == 1);
		assertTrue(this.toTest.getLastTelemDataPacket(1).get(0).equals(secondTestingPacket));
		
		assertTrue(this.toTest.getLastTelemDataPacket(2).size() == 2);
		assertTrue(this.toTest.getLastTelemDataPacket(2).get(0).equals(secondTestingPacket));
		assertTrue(this.toTest.getLastTelemDataPacket(2).get(1).equals(testingPacket));
	}
	
	/*
	 * Because of their time, the DB should be able to sort them, at the very 
	 * least when they're returned to the program. 
	 * Not sure what the database looks like though.
	 */
	@Test
	public void packetsStoredOutOfOrderShouldReturnInOrder() throws IOException{
		ArrayList<TelemDataPacket> packets = new ArrayList<TelemDataPacket>();
		for(int i = 0; i<2; i++){
			packets.add(this.generateStandardTelemDataPacket());
		}
		for(int i = 0; i<2; i++){
			packets.add(this.generateTerribleTelemDataPacket());
		}
		for(int i = 0; i<2; i++){
			packets.add(this.generateEmptyMapsTelemDataPacket());
		}
		
		this.toTest.store(packets.get(3));
		this.toTest.store(packets.get(0));
		this.toTest.store(packets.get(5));
		this.testZeroAndNegativeInputsShouldGiveSizeZeroLists(toTest);
		
		ArrayList<TelemDataPacket> returned = toTest.getLastTelemDataPacket(3);
		
		assertTrue(returned.size() == 3);
		assertTrue(returned.get(0).equals(packets.get(0)));
		assertTrue(returned.get(1).equals(packets.get(3)));
		assertTrue(returned.get(2).equals(packets.get(5)));
		
		
	}
	
	//========= getAllTelemDataPacketsSince(Double) tests===========
	
	
	private void testFutureTimeShouldGiveSizeZeroLists(CSVDatabase toCheck){
		double futureTime = System.currentTimeMillis() + 1000;
		assertTrue(toCheck.getAllTelemDataPacketsSince(futureTime) != null);
		assertTrue(toCheck.getAllTelemDataPacketsSince(futureTime).size() == 0);
	}

	
	/*
	 * Not sure exactly what I should do for illegal values.
	 *
	 */ //TODO check simpleDateFormat for examples on what to do if given
	//an invalid double instead of time. 
	@Test(expected  = IllegalArgumentException.class)
	public void negativeValuesShouldThrowException(){
		toTest.getAllTelemDataPacketsSince(-1);
	}
	
	@Test
	public void emptyDBShouldGiveNothing(){
		this.testFutureTimeShouldGiveSizeZeroLists(toTest);
		assertTrue(
				this.toTest.getAllTelemDataPacketsSince(System.currentTimeMillis()).size() == 0);
	}
	
	@Test
	public void dbSizeOneShouldGiveListSizeOne() throws IOException{
		double startTime = System.currentTimeMillis();
		TelemDataPacket test = this.generateStandardTelemDataPacket();
		toTest.store(test);
		this.testFutureTimeShouldGiveSizeZeroLists(toTest);
		ArrayList<TelemDataPacket> theList = toTest.getAllTelemDataPacketsSince(startTime);
		assertTrue(theList.size() == 1);
		assertTrue(theList.get(0).equals(test));
	}
	
	@Test
	public void dbSizeTwoShouldGiveOnlyWhatIsSpecified() throws IOException{
		TelemDataPacket testOne = this.generateStandardTelemDataPacket();
		TelemDataPacket testTwo = this.generateTerribleTelemDataPacket();
		TelemDataPacket testThree = this.generateEmptyMapsTelemDataPacket();
		toTest.store(testOne);
		toTest.store(testTwo);
		toTest.store(testThree);
		this.testFutureTimeShouldGiveSizeZeroLists(toTest);
		ArrayList<TelemDataPacket> theList = toTest.getAllTelemDataPacketsSince(testThree.getTimeCreated()-10);
		assertTrue(theList.size() == 1);
		assertTrue(theList.get(0).equals(testThree));
		
		theList = toTest.getAllTelemDataPacketsSince(testTwo.getTimeCreated()-10);
		assertTrue(theList.size() == 2);
		assertTrue(theList.get(0).equals(testTwo));
		assertTrue(theList.get(1).equals(testThree));
		
		theList = toTest.getAllTelemDataPacketsSince(testOne.getTimeCreated()-10);
		assertTrue(theList.size() == 3);
		assertTrue(theList.get(0).equals(testOne));
		assertTrue(theList.get(1).equals(testTwo));
		assertTrue(theList.get(2).equals(testThree));
	}
	
	@Test
	public void storingOutOfOrderShouldComeBackInOrder() throws IOException{
		TelemDataPacket testOne = this.generateStandardTelemDataPacket();
		TelemDataPacket testTwo = this.generateTerribleTelemDataPacket();
		TelemDataPacket testThree = this.generateEmptyMapsTelemDataPacket();
		toTest.store(testTwo);
		toTest.store(testThree);
		toTest.store(testOne);
		
		this.testFutureTimeShouldGiveSizeZeroLists(toTest);
		ArrayList<TelemDataPacket> theList = toTest.getAllTelemDataPacketsSince(testThree.getTimeCreated()-10);
		assertTrue(theList.size() == 1);
		assertTrue(theList.get(0).equals(testThree));
		
		theList = toTest.getAllTelemDataPacketsSince(testTwo.getTimeCreated()-10);
		assertTrue(theList.size() == 2);
		assertTrue(theList.get(0).equals(testTwo));
		assertTrue(theList.get(1).equals(testThree));
		
		theList = toTest.getAllTelemDataPacketsSince(testOne.getTimeCreated()-10);
		assertTrue(theList.size() == 3);
		assertTrue(theList.get(0).equals(testOne));
		assertTrue(theList.get(1).equals(testTwo));
		assertTrue(theList.get(2).equals(testThree));
	}
	
	//==================getAllTelemDataPacket()====================
	
	@Test
	public void emptyDBShouldGiveEmptyList(){
		assertTrue(toTest.getAllTelemDataPacket() != null);
		assertTrue(toTest.getAllTelemDataPacket().size() == 0);
	}
	
	@Test
	public void dbWithOneShouldGIveListSizeOne() throws IOException{
		TelemDataPacket testPacket = this.generateStandardTelemDataPacket();
		toTest.store(testPacket);
		assertTrue(toTest.getAllTelemDataPacket() != null);
		assertTrue(toTest.getAllTelemDataPacket().size() == 1);
		assertTrue(toTest.getAllTelemDataPacket().get(0).equals(testPacket));
	}
	
	@Test
	public void dbWithLotsShouldGiveBigList() throws IOException{
		int number = 1000;
		for(int i= 0; i<number; i++){
			toTest.store(this.generateStandardTelemDataPacket());
		}
		
		assertTrue(toTest.getAllTelemDataPacket().size() == number);
		
		
	}
	
	//=================== Helper Functions ========================
	
	//public TelemDataPacket(int newSpeed, int newTotalVoltage,
	//HashMap<String,Integer> newTemperatures, HashMap<Integer,ArrayList<Float>> newCellVoltages){
	private TelemDataPacket generateStandardTelemDataPacket(){
		int speed = 5;
		int totalVoltage = 6;
		HashMap<String, Integer> temperatures = new HashMap<String, Integer>();		
		temperatures.put("bms", (35));
		temperatures.put("motor", (40));
		temperatures.put("pack0", (41));
		temperatures.put("pack1", (42));
		temperatures.put("pack2", (43));
		temperatures.put("pack3", (44));
		HashMap<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
		for(int i = 0; i<4; i++){ //Current number of cells coming in pack is 4. Will probably have to adjust that.
			cellVoltages.put(i, generatePackVoltages());
		}
		
		return new TelemDataPacket(speed, totalVoltage, temperatures, cellVoltages);
		
	}
	
	private ArrayList<Float> generatePackVoltages(){
		ArrayList<Float> cell1 = new ArrayList<Float>();
		Random rng = new Random();
		
		for(int i = 0; i<10; i++){
			cell1.add(rng.nextFloat() * 10);
		}
		return cell1;
	}
	
	private TelemDataPacket generateTerribleTelemDataPacket(){
		int speed = -1;
		int totalVoltage = -2;
		//NullTelemDataPacket will store the empty maps as 'null's
		return new NullTelemDataPacket(speed, totalVoltage, new HashMap<String, Integer>(), new HashMap<Integer, ArrayList<Float>>());
	}
	
	private TelemDataPacket generateEmptyMapsTelemDataPacket(){
		int speed = -1;
		int totalVoltage = -2;
		HashMap<String, Integer> temperatures = new HashMap<String, Integer>();	
		HashMap<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
		return new TelemDataPacket(speed, totalVoltage, temperatures, cellVoltages);
	}



	
}
