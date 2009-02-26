/**
 * 
 */
package com.eclipse.featdiag.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import com.eclipse.featdiag.models.ConnectionModel;
import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.parser.Parser;

import junit.framework.TestCase;

/**
 * Test general parser operation through testing
 * the parsing of 4 demo files and comparing expected
 * to actual results.
 * @author Leo Kaliazine
 *
 */
public class ParserTest extends TestCase {

	Set<String> fields;
    Set<String> methods;
    List<ConnectionModel> connections;
    String slash = File.separator;
	
    private void setupParser(String className) throws CoreException {// throws Exception {
        
        Parser parser = new Parser(className);
        DiagramModel contentModel = new DiagramModel();
        parser.addMembersToDiagram(contentModel);
        fields = contentModel.getFields();
        methods = contentModel.getMethods();
        connections = contentModel.getConnectionModels();
    }
    
    /**
     * Empty test class meant to test that Parser will not
     * contain any fields or methods.
     * @throws CoreException 
     */
    public void testParserTestDemo1() throws CoreException{
    	
    	setupParser(System.getProperty("user.dir")+
    			slash + "bin" +
    			slash +	"featdiag" +
    			slash + "tests" +
    			slash +	"ParserTestDemo1.class");
    	
    	assertTrue(this.fields.size() == 0);
    	
    	assertTrue(this.methods.size() == 0);

    	assertTrue(connections.size() == 0);

    }
    
    /**
     * Test class containing various different
     * kind of fields, meant to check that Parser
     * will detect these fields, and nothing else. 
     * @throws CoreException 
     */
    public void testParserTestDemo2() throws CoreException{
    	
    	setupParser(System.getProperty("user.dir")+
    			slash + "bin" +
    			slash +	"featdiag" +
    			slash + "tests" +
    			slash +	"ParserTestDemo2.class");
    	
    	//check fields
    	List <String> fields = new ArrayList<String>();
    	fields.add("number");
    	fields.add("str");
    	fields.add("str1");
    	fields.add("obj");
    	assertTrue(this.fields.size() == fields.size());
    	assertTrue(this.fields.containsAll(fields));
    	
    	assertTrue(this.methods.size() == 0);
    	
    	assertTrue(connections.size() == 0);
    	
    }
    
    /**
     * Simple test class meant to check that the Parser will
     * find the basic fields and methods and their connections. 
     * @throws CoreException 
     */
    public void testParserTestDemo3() throws CoreException{
    	
    	setupParser(System.getProperty("user.dir")+
    			slash + "bin" +
    			slash +	"featdiag" +
    			slash + "tests" +
    			slash +	"ParserTestDemo3.class");
    	
    	//check fields
    	List <String> fields = new ArrayList<String>();
    	fields.add("number");
    	fields.add("str");
    	assertTrue(this.fields.size() == fields.size());
    	assertTrue(this.fields.containsAll(fields));
    	
    	
    	//check methods
    	List <String> methods = new ArrayList<String>();
    	methods.add("getNumber");
    	methods.add("getString");
    	methods.add("stringToNumber");
    	//same number of methods; covers the case where one of lists is empty
    	assertTrue(this.methods.size() == methods.size());
    	//makes sure the contained methods are the same
    	assertTrue(this.methods.containsAll(methods));
    	
    	//check connections
    	List <String> connectionsThatShouldExist = new ArrayList<String>();
    	List <String> connectionsThatExist = new ArrayList<String>();
    	connectionsThatShouldExist.add("getNumber number");
    	connectionsThatShouldExist.add("getString str");
    	connectionsThatShouldExist.add("stringToNumber str");
    	connectionsThatShouldExist.add("stringToNumber getNumber");
    	for(ConnectionModel cm : connections) {
    		connectionsThatExist.add(cm.getSource().getName()+" "+cm.getTarget().getName());
    	}
    	assertTrue(connectionsThatShouldExist.size() == connectionsThatExist.size());
    	assertTrue(connectionsThatShouldExist.containsAll(connectionsThatExist));
    }
    
    /**
     * Test class meant to check that the Parser will
     * find the fields and methods and their connections
     * even when some fields and methods are static and circular
     * connections are presents. 
     * @throws CoreException 
     */
    public void testParserTestDemo4() throws CoreException{
    	
    	setupParser(System.getProperty("user.dir")+
    			slash + "bin" +
    			slash +	"featdiag" +
    			slash + "tests" +
    			slash +	"ParserTestDemo4.class");
    	
    	//check fields
    	List <String> fields = new ArrayList<String>();
    	fields.add("number");
    	fields.add("str");
    	assertTrue(this.fields.size() == fields.size());
    	assertTrue(this.fields.containsAll(fields));
    	
    	
    	//check methods
    	List <String> methods = new ArrayList<String>();
    	methods.add("getNumber");
    	methods.add("getString");
    	methods.add("stringToNumber");
    	methods.add("giveNumber");
    	//same number of methods; covers the case where one of lists is empty
    	assertTrue(this.methods.size() == methods.size());
    	//makes sure the contained methods are the same
    	assertTrue(this.methods.containsAll(methods));
    	
    	//check connections
    	List <String> connectionsThatShouldExist = new ArrayList<String>();
    	List <String> connectionsThatExist = new ArrayList<String>();
    	connectionsThatShouldExist.add("getNumber number");
    	connectionsThatShouldExist.add("getNumber getString");
    	connectionsThatShouldExist.add("getString str");
    	connectionsThatShouldExist.add("getString stringToNumber");
    	connectionsThatShouldExist.add("stringToNumber str");
    	connectionsThatShouldExist.add("stringToNumber getNumber");
    	connectionsThatShouldExist.add("giveNumber number");
    	for(ConnectionModel cm : connections) {
    		connectionsThatExist.add(cm.getSource().getName()+" "+cm.getTarget().getName());
    	}
    	assertTrue(connectionsThatShouldExist.size() == connectionsThatExist.size());
    	assertTrue(connectionsThatShouldExist.containsAll(connectionsThatExist));
    }
}
