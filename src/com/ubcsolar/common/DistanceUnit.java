/**
 * Classes should support at least these units. 
 * NOTE: not sure how to implement units in a program
 * One way: use only one unit and then have the UI convert, but if we're dealing with mm and km in the same 
 * program, then might get rounding errors. 
 * 
 * Current plan: have the program recognize and support various units within, possibly taking them as arguments
 * in method calls.  Much more complicated (every method must support every unit), with
 * many conversion calls... not sure if it's worth it
 */

package com.ubcsolar.common;

public enum DistanceUnit {
	MILES, KILOMETERS, FEET
}
