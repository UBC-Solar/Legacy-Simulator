/**
 * This class will encapsulate the underlying list so that this 
 * can be given out to other objects who will only be able to access the data; not
 * add or change. 
 * 
 * Need to be able to do it to give access to the list of all telem data packets 
 * so far, but also for the Controller to be able to add more as they come in.
 */

/*
 * USAGE: Create a list in your class, and then create a ProtectedList with your list in the 
 * constructor. Now you can pass out this one, while still modifying it as you
 * get new notifications. This means that every class can just store a refernce to this list rather 
 * than making a new list for each time you need it.
 */
package com.ubcsolar.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProtectedList<T>{
	//TODO make this whole class parameterized. 
	private final List<T> base;
	public <T> ProtectedList(List base) { //should be parametrized.
		this.base = base; 
	}
	
	
	//TODO lookup javadoc for List and copy out the getter methods. 
	
	public T get(int index){
		return base.get(index);
	}
	
	public boolean contains(Object arg0){
		return base.contains(arg0);
	}
	
	public Iterator<T> iterator(){
		return base.iterator();
	}
	
	public int size(){
		return base.size();
	}
	
	
	//TODO fix the description to be more professional and clear
	/**
	 * used if the object wants to create a copy they can modify
	 * THIS WILL BREAK THE LINK WITH DB AND WON'T BE UPDATED AS NEW RESULTS COME IN
	 * @return
	 */
	public ArrayList<T> getCopyOfList(){
		ArrayList<T> test = new ArrayList<T>();
		// COPY BASE INTO TEMP
		
		return test;
	}
	

}
