package com.ubcsolar.database.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ubcsolar.database.CSVDatabase;

public class CSVDatabaseTest {
	
	CSVDatabase toTest;
	CSVDatabase toTestTwo;
	
	
	
	@Test(expected = IOException.class)
	public void creatingDatabaseWithOneOpenShouldThrowException() throws IOException{
		try{
			tearDown();
		}
		catch(IOException e){fail("Should not have gotten exception here");}
		
		String name = ""+System.currentTimeMillis();
		try {
			CSVDatabase toTest = new CSVDatabase(name);
		} catch (IOException e) {fail("Should not have gotten exception here");}
		
		CSVDatabase two = new CSVDatabase(name); //should throw exception here; trying to create one with same name. 
		
	}
	
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
	@Test
	public void constructorWithStringShouldMakeName() throws IOException{
		CSVDatabase test = new CSVDatabase("hello");
		File theFile = new File("Output\\hello.csv");
		assertTrue(theFile.exists());
		
	}
	
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
	@Before
	public void setUp() throws IOException {
		for(int i = 0; i<15; i++){
			//force it to take at least one ms before creating next DB
			//to guarantee a new DB name
		}
		toTest = new CSVDatabase();
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

	@Test
	public void test() {
		assertTrue(true);
	}

}
