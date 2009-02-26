package com.eclipse.featdiag.tests;

/**
 * Test class containing various different
 * kind of fields, meant to check that Parser
 * will detect these fields, and nothing else. 
 * @author Leo Kaliazine
 *
 */
public class ParserTestDemo2 {
	public int number;
	@SuppressWarnings("unused")
	private String str;
	@SuppressWarnings("unused")
	private static final String str1="Hello";
	Object obj;
}
