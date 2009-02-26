package com.eclipse.featdiag.tests;

/**
 * Simple test class meant to check that the Parser will
 * find the basic fields and methods and their connections. 
 * @author Leo Kaliazine
 *
 */
public class ParserTestDemo3 {
	public int number;
	private String str;
	
	private int getNumber() {
		return number;
	}
	
	public String getString() {
		return str;
	}
	
	public void stringToNumber() {
		str = String.valueOf(getNumber());
	}
}
