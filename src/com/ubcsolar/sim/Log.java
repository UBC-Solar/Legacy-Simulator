package com.ubcsolar.sim;

import java.util.*;
public class Log{
private static Log logInstance = null;
private ArrayList<String> noteBook = new ArrayList<String>();

private Log(String message){
System.out.println(message);
}
 
 
private static synchronized Log getInstance() {
        if (logInstance == null) {
           logInstance = new Log("Log created itself");
           }
                return logInstance;
        }




public static void write(String message){
getInstance().noteBook.add(message);
System.out.println("Logged: \"" + message + "\".");

}

public static void printOut(){
//TODO make this print to a file. May need to format
}


}
