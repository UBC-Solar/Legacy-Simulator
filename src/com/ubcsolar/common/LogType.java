/**
 * Made this so we could have different log entry types, to enable better filtering and formating
 * Java for example has a regular System.out.println(), but also has a seperate one just for errors. 
 * 
 * Not sure if it's worth implementing this way, but this sets out the types of logs that could be entered. 
 * No guarantees that the the log will handle them differently
 */

package com.ubcsolar.common;

public enum LogType {
NOTIFICATION, ERROR, SYSTEM_REPORT; 
}
