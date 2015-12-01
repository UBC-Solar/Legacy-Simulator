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
	
	//NOTE: THESE TESTS ASSUME AN EMPTY 'OUTPUT' FOLDER. 
	//Some of them test specific names; if they're there from previous
	//tests, it will fail. 
	
	//Also, the NULL telemdatapackets might fail if they get loaded back with empty maps instead 
	//of null. Will have to update the tests when that happens. 
	CSVDatabase toTest;
	CSVDatabase toTestTwo;
	//======================SETTING UP THE TESTS=========================
	

	@Before
	public void setUp() throws IOException {
		for(int i = 0; i<10000; i++){
			//force it to take at least one ms before creating next DB
			//to guarantee a new DB name
		}
		toTest = new CSVDatabase();
		
		for(int i = 0; i<10000; i++){
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
	
	/*
	 * If pasing a null value, the user/program is not expecting a 
	 * 'null.csv' file to be made, but it should be able to handle it
	 * anyway. 
	 *  
	 */
	@Test
	public void nullNameShouldNotThrowException() throws IOException{
		try{
			tearDown();
		}catch(IOException e){fail("Should not have gotten exception here");}
		File testFile = new File("null.csv");
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

	/*
	 * It won't be able to make th file and needs to tell the system that; 
	 * it won't be able to proceed. 
	 */
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
	
	/*
	 * the basic vanilla test. If this fails; check file write permission settings
	 * on the Output folder. 
	 */
	@Test
	public void storeShouldNotThrowException(){
		try {
			this.toTest.store(generateStandardTelemDataPacket());
		} catch (IOException e) {
			fail("threw exception, shouldn't have");
		}
	}
	
	/*
	 * Should take as wide a range as possible without complaining. 
	 */
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
	
	/*
	 * Should check this with an empty DB and a db of different sizes. 
	 * Want to support as wide range of values as possible.
	 * 
	 *  Not set up as a unique test because every test in this section should
	 *  call it after they load in their test data.  
	 */
	private void testZeroAndNegativeInputsShouldGiveSizeZeroLists(CSVDatabase toCheck){
		assertTrue(toCheck.getLastTelemDataPacket(-1) != null);
		assertTrue(toCheck.getLastTelemDataPacket(-1).size() == 0);
		assertTrue(toCheck.getLastTelemDataPacket(0) != null);
		assertTrue(toCheck.getLastTelemDataPacket(0).size() == 0);
		assertTrue(toCheck.getLastTelemDataPacket(1) != null);
		// we can't know what this result will be, commented out.
		//assertTrue(toTest.getLastTelemDataPacket(0).size() == 0);
	}
	
	/*
	 * If it asks for -1 values, just give back an empty list. No need to crash the program.
	 */
	@Test
	public void shouldNotGetErrorIfGiveNegatives(){
		try{
			toTest.getLastTelemDataPacket(-1);
		}catch(Exception e){
			fail("Threw " + e.getClass());
		}
	}
	
	/*
	 * if Database is empty, should not return any data.  
	 */
	@Test
	public void emptyDataBaseShouldGiveEmptyListNoMatterArgs(){
		this.testZeroAndNegativeInputsShouldGiveSizeZeroLists(this.toTest);
		ArrayList<TelemDataPacket> returnedList;
		returnedList = this.toTest.getLastTelemDataPacket(25);
		assertTrue(returnedList != null && returnedList.size() == 0);
	}
	
	/*
	 * If this list has one thing, it should return it if 
	 * it's asked for x things, if x>=1, and empty lists is x<=0. Should
	 * definitely not error.  
	 */
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
	
	/*
	 * First non-trivial test. If amount of data is 2, should give 
	 * the right number according to the argument 
	 * (Empty list if x<=0, one if x = 1, two if x>=2). 
	 * Should not throw errors for negative or overlarge inputs. 
	 */
	@Test
	public void sizeTwoDataBaseShouldOnlyGiveWhatsAsked() throws IOException{
		TelemDataPacket testingPacket = this.generateStandardTelemDataPacket();
		TelemDataPacket secondTestingPacket = this.generateTerribleTelemDataPacket(testingPacket.getTimeCreated() + 3);
		this.toTest.store(testingPacket);
		this.toTest.store(secondTestingPacket);
		this.testZeroAndNegativeInputsShouldGiveSizeZeroLists(toTest);
		
		assertTrue(this.toTest.getLastTelemDataPacket(1).size() == 1);
		assertTrue(this.toTest.getLastTelemDataPacket(1).get(0).equals(secondTestingPacket));
		
		assertTrue(this.toTest.getLastTelemDataPacket(2).size() == 2);
		assertTrue(this.toTest.getLastTelemDataPacket(2).get(0).equals(testingPacket));
		assertTrue(this.toTest.getLastTelemDataPacket(2).get(1).equals(secondTestingPacket));
		
		assertTrue(this.toTest.getLastTelemDataPacket(3).size() == 2);
		assertTrue(this.toTest.getLastTelemDataPacket(3).get(0).equals(testingPacket));
		assertTrue(this.toTest.getLastTelemDataPacket(3).get(1).equals(secondTestingPacket));
	}
	
	/*
	 * Because of their time, the DB should be able to sort them, at the very 
	 * least when they're returned to the program. 
	 * Not sure what the database looks like though (could be stored out of order
	 * and just the result is sorted)
	 */
	@Test
	public void packetsStoredOutOfOrderShouldReturnInOrder() throws IOException{
		ArrayList<TelemDataPacket> packets = new ArrayList<TelemDataPacket>();
		double startTime =System.currentTimeMillis() - 15;
		for(int i = 0; i<2; i++){
			packets.add(this.generateStandardTelemDataPacket(startTime + (3*i)));
		}
		startTime += 6;
		
		for(int i = 0; i<2; i++){
			packets.add(this.generateTerribleTelemDataPacket(startTime + (3*i)));
		}
		startTime += 6;
		for(int i = 0; i<2; i++){
			packets.add(this.generateEmptyMapsTelemDataPacket(startTime + (3*i)));
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
	
	/*
	 * Should not throw exceptions storing and returning telemDataPackets
	 * that have nulls or are weird.
	 * 
	 */ //TODO: consider having DB just silently drop null telem packets. 
	//We'd be losing data, but they're improper anyway? 
	@Test
	public void retrievingBadInputsWithGetLastShouldNotThrowException() throws IOException{
		ArrayList<TelemDataPacket> testPkts = this.loadGoodBadEmptyTelemPackets();
		testZeroAndNegativeInputsShouldGiveSizeZeroLists(toTest);
		
		try{
			ArrayList<TelemDataPacket> returned = toTest.getLastTelemDataPacket(3);
		}catch(Exception e){
			fail("threw exception pulling out weird-value telemPackets");
		}
	}
	
	//========= getAllTelemDataPacketsSince(Double) tests===========
	
	/*
	 * Should never throw an error if given a time that's in the future. 
	 * This method is called by every other test in this section to ensure
	 * Compliance with any sort of inputs/stored data
	 */
	private void testFutureTimeShouldGiveSizeZeroLists(CSVDatabase toCheck){
		double futureTime = System.currentTimeMillis() + 1000;
		assertTrue(toCheck.getAllTelemDataPacketsSince(futureTime) != null);
		assertTrue(toCheck.getAllTelemDataPacketsSince(futureTime).size() == 0);
	}

	
	/*
	 * Not sure exactly what I should do for illegal values.
	 * Following Date's practices on exceptions. Negative values
	 * are just dates before Jan 1 1970. 
	 */
	@Test
	public void getAllSinceNegativeValuesShouldNotThrowException(){
		try{
		toTest.getAllTelemDataPacketsSince(-1);
		}
		catch(Exception e){
			fail("threw an exception");
		}
	}
	
	/*
	 * If it has nothing, it shouldn't return any values. 
	 */
	@Test
	public void getAllSinceEmptyDBShouldGiveNothing(){
		this.testFutureTimeShouldGiveSizeZeroLists(toTest);
		assertTrue(
				this.toTest.getAllTelemDataPacketsSince(System.currentTimeMillis()).size() == 0);
	}
	
	/*
	 * If it only has one value, it should return it when asked. 
	 */
	@Test
	public void getAllSinceDBSizeOneShouldGiveListSizeOne() throws IOException{
		double startTime = System.currentTimeMillis();
		TelemDataPacket test = this.generateStandardTelemDataPacket();
		toTest.store(test);
		this.testFutureTimeShouldGiveSizeZeroLists(toTest);
		ArrayList<TelemDataPacket> theList = toTest.getAllTelemDataPacketsSince(startTime-3);
		assertTrue(theList.size() == 1);
		assertTrue(theList.get(0).equals(test));
		
		ArrayList<TelemDataPacket> theOtherList = toTest.getAllTelemDataPacketsSince(System.currentTimeMillis());
		assertTrue(theOtherList.size() == 0);
	}
	
	/*
	 * Should only return values created later than the specified time. 
	 */
	@Test
	public void getAllSinceDBSizeTwoShouldGiveOnlyWhatIsSpecified() throws IOException{
		TelemDataPacket testOne = this.generateStandardTelemDataPacket();
		TelemDataPacket testTwo = this.generateTerribleTelemDataPacket(testOne.getTimeCreated() + 3);
		TelemDataPacket testThree = this.generateEmptyMapsTelemDataPacket(testTwo.getTimeCreated() + 5);
		toTest.store(testOne);
		toTest.store(testTwo);
		toTest.store(testThree);
		this.testFutureTimeShouldGiveSizeZeroLists(toTest);
		ArrayList<TelemDataPacket> theList = toTest.getAllTelemDataPacketsSince(testThree.getTimeCreated()-1);
		assertTrue("Should be size 1, is" + theList.size(),theList.size() == 1);
		assertTrue(theList.get(0).equals(testThree));
		
		theList = toTest.getAllTelemDataPacketsSince(testTwo.getTimeCreated()-1);
		assertTrue(theList.size() == 2);
		assertTrue(theList.get(0).equals(testTwo));
		assertTrue(theList.get(1).equals(testThree));
		
		theList = toTest.getAllTelemDataPacketsSince(testOne.getTimeCreated()-1);
		assertTrue(theList.size() == 3);
		assertTrue(theList.get(0).equals(testOne));
		assertTrue(theList.get(1).equals(testTwo));
		assertTrue(theList.get(2).equals(testThree));
	}
	
	/*
	 * The DB should return the as sorted, and shouldn't miss any 
	 * if they were stored out-of-order. 
	 * Doesn't really matter if the DB stores them in order or not, or if it just 
	 * sorts on return. (But probably should sort on 'store'). 
	 */
	@Test
	public void getAllSinceStoringOutOfOrderShouldComeBackInOrder() throws IOException{
		TelemDataPacket testOne = this.generateStandardTelemDataPacket();
		TelemDataPacket testTwo = this.generateTerribleTelemDataPacket(testOne.getTimeCreated() + 3);
		TelemDataPacket testThree = this.generateEmptyMapsTelemDataPacket(testTwo.getTimeCreated() + 5);
		toTest.store(testTwo);
		toTest.store(testThree);
		toTest.store(testOne);
		
		this.testFutureTimeShouldGiveSizeZeroLists(toTest);
		ArrayList<TelemDataPacket> theList = toTest.getAllTelemDataPacketsSince(testThree.getTimeCreated()-1);
		assertTrue(theList.size() == 1);
		assertTrue(theList.get(0).equals(testThree));
		
		theList = toTest.getAllTelemDataPacketsSince(testTwo.getTimeCreated()-1);
		assertTrue(theList.size() == 2);
		assertTrue(theList.get(0).equals(testTwo));
		assertTrue(theList.get(1).equals(testThree));
		
		theList = toTest.getAllTelemDataPacketsSince(testOne.getTimeCreated()-1);
		assertTrue(theList.size() == 3);
		assertTrue(theList.get(0).equals(testOne));
		assertTrue(theList.get(1).equals(testTwo));
		assertTrue(theList.get(2).equals(testThree));
	}
	/*
	 * It shouldn't throw an exception (should be able to handle nulls)
	 * but it would be ok if they weren't quite the same on return 
	 * (i.e the one's with null values should return as just empty lists). 
	 */
	@Test
	public void getAllSinceRetrievingBadInputsWithGetSinceShouldNotThrowException() throws IOException{
		double startTime = System.currentTimeMillis();
		ArrayList<TelemDataPacket> testPkts = this.loadGoodBadEmptyTelemPackets();
		testZeroAndNegativeInputsShouldGiveSizeZeroLists(toTest);
		
		try{
			ArrayList<TelemDataPacket> returned = toTest.getAllTelemDataPacketsSince(startTime);
		}catch(Exception e){
			fail("threw exception pulling out weird-value telemPackets");
		}
	}
	
	//================== getAllTelemDataPacket() tests ====================
	
	/*
	 * Straight forward. If nothing saved, don't return anything. 
	 */
	@Test
	public void emptyDBShouldGiveEmptyList(){
		assertTrue(toTest.getAllTelemDataPacket() != null);
		assertTrue(toTest.getAllTelemDataPacket().size() == 0);
	}
	
	/*
	 * If there has been one item stored, 'get all' should return a list
	 * of size one. 
	 */
	@Test
	public void dbWithOneShouldGIveListSizeOne() throws IOException{
		TelemDataPacket testPacket = this.generateStandardTelemDataPacket();
		toTest.store(testPacket);
		assertTrue(toTest.getAllTelemDataPacket() != null);
		assertTrue(toTest.getAllTelemDataPacket().size() == 1);
		assertTrue(toTest.getAllTelemDataPacket().get(0).equals(testPacket));
	}
	
	/*
	 * More of a stress test; didn't bog down the system like I thought it would. 
	 */
	@Test
	public void dbWithLotsShouldGiveBigList() throws IOException{
		int number = 1000;
		for(int i= 0; i<number; i++){
			toTest.store(this.generateStandardTelemDataPacket());
		}
		
		assertTrue(toTest.getAllTelemDataPacket().size() == number);
		
		
	}
	
	/*
	 * Shouldn't throw any exceptions (should handle nulls), but
	 * the null ones may not be exactly the same (empty maps instead of nulls on return). 
	 */
	@Test
	public void retrievingBadInputsWithGetAllShouldNotThrowException() throws IOException{
		double startTime = System.currentTimeMillis();
		ArrayList<TelemDataPacket> testPkts = this.loadGoodBadEmptyTelemPackets();
		testZeroAndNegativeInputsShouldGiveSizeZeroLists(toTest);
		
		try{
			ArrayList<TelemDataPacket> returned = toTest.getAllTelemDataPacket();
		}catch(Exception e){
			fail("threw exception pulling out weird-value telemPackets");
		}
	}
	
	
	/*
	 * If stored out of order, should be returned in order and without missing any. 
	 */
	@Test
	public void getAllOutOfOrderItemsShouldBeReturnedInOrder() throws IOException{
		ArrayList<TelemDataPacket> testValues = new ArrayList<TelemDataPacket>();
		double startTime = System.currentTimeMillis();
		testValues.add(this.generateStandardTelemDataPacket(startTime - 7));
		testValues.add(this.generateStandardTelemDataPacket(startTime - 5));
		testValues.add(this.generateEmptyMapsTelemDataPacket(startTime - 3));
		testValues.add(this.generateEmptyMapsTelemDataPacket(startTime - 1));
		testValues.add(this.generateTerribleTelemDataPacket(startTime + 1));
		testValues.add(this.generateTerribleTelemDataPacket(startTime + 3));
		//A better approach would just be to specify times but you'd have to redo the
		// generateTerrible and Empty pckts.
		toTest.store(testValues.get(4));
		toTest.store(testValues.get(2));
		toTest.store(testValues.get(3));
		toTest.store(testValues.get(1));
		
		
		ArrayList<TelemDataPacket> returnedValues = toTest.getAllTelemDataPacket();
		assertTrue(returnedValues.size() == 4);
		assertTrue(returnedValues.get(0).equals(testValues.get(1)));
		assertTrue(returnedValues.get(1).equals(testValues.get(2)));
		assertTrue(returnedValues.get(2).equals(testValues.get(3)));
		assertTrue(returnedValues.get(3).equals(testValues.get(4)));
	}
	
	private void wasteAMillisecond(){
		int v = 0;
		for(int first=0; first<100000; first++){
			for(int scnd = 0; scnd<100000; scnd++){
				for(int third = 0; third<50; third++){
					v++;
					v--;
				}
			}
		}
	}
	
	//=================== get(double) tests =======================
	
	@Test
	public void givingCreatedTimeShouldGetBackProperPacket() throws IOException{
		TelemDataPacket toStore = this.generateStandardTelemDataPacket();
		toTest.store(toStore);
		TelemDataPacket gotBack = (TelemDataPacket) toTest.getTelemDataPacket("" + toStore.getTimeCreated());
		assertTrue(gotBack.equals(toStore));
	}
	
	@Test
	public void givingNonexistentKeyShouldReturnNull(){
		assertTrue(toTest.getTelemDataPacket("" + System.currentTimeMillis()) == null);
	}
	
	@Test
	public void givingEmptyKeyShouldReturnNull(){
		assertTrue(toTest.getTelemDataPacket("") == null);
	}
	@Test
	public void givingBadlyFormattedKeyShouldReturnNull(){
		try{
		assertTrue(toTest.getTelemDataPacket("4dfNotADouble") == null);
		}catch(NumberFormatException e){
			fail("Threw a casting exception");
		}
	}
	
	//=================== Helper Functions ========================
	
	private ArrayList<TelemDataPacket> loadGoodBadEmptyTelemPackets() throws IOException{
		ArrayList<TelemDataPacket> toReturn = new ArrayList<TelemDataPacket>();
		toReturn.add(this.generateStandardTelemDataPacket());
		toReturn.add(this.generateTerribleTelemDataPacket());
		toReturn.add(this.generateEmptyMapsTelemDataPacket());
		
		for(TelemDataPacket t : toReturn){
			toTest.store(t);
		}
		
		return toReturn;		
	}
	
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
	
	private TelemDataPacket generateStandardTelemDataPacket(double timeInMillis){
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
		
		return new TelemDataPacket(speed, totalVoltage, temperatures, cellVoltages, timeInMillis);
		
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
	
	private TelemDataPacket generateTerribleTelemDataPacket(double timeCreatedInMillis){
		int speed = -1;
		int totalVoltage = -2;
		//NullTelemDataPacket will store the empty maps as 'null's
		return new NullTelemDataPacket(speed, totalVoltage, new HashMap<String, Integer>(), new HashMap<Integer, ArrayList<Float>>(), timeCreatedInMillis);
	}
	
	private TelemDataPacket generateEmptyMapsTelemDataPacket(){
		int speed = -3;
		int totalVoltage = -2;
		HashMap<String, Integer> temperatures = new HashMap<String, Integer>();	
		HashMap<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
		return new TelemDataPacket(speed, totalVoltage, temperatures, cellVoltages);
	}
	
	private TelemDataPacket generateEmptyMapsTelemDataPacket(double timeInMillis){
		int speed = -3;
		int totalVoltage = -2;
		HashMap<String, Integer> temperatures = new HashMap<String, Integer>();	
		HashMap<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
		return new TelemDataPacket(speed, totalVoltage, temperatures, cellVoltages, timeInMillis);
	}
	
	
	


	
}
