package com.eclipse.featdiag.tests;

/**
 * Test class meant to check that the Parser will
 * find the fields and methods and their connections
 * even when some fields and methods are static and circular
 * connections are presents. 
 * @author Leo Kaliazine
 *
 */
public class ParserTestDemo4 {
	public volatile static int number=3;
	private String str;
	
	@SuppressWarnings("unused")
	private static int giveNumber() {
		return number;
	}
	
	private int getNumber() {
		getString();
		return number;
	}
	
	public String getString() {
		stringToNumber();
		return str;
	}
	
	public void stringToNumber() {
		str = String.valueOf(getNumber());
	}
}
