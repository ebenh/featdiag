package com.eclipse.featdiag.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.eclipse.featdiag.models.ConnectionModel;
import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.models.FieldModel;
import com.eclipse.featdiag.models.MethodModel;
import com.eclipse.featdiag.utils.MemberDiff;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.FieldOrMethod;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.PUTSTATIC;


/**
 * This class parses a java .class file pulling out methods and fields and their
 * interactions (connections)
 *
 * @author Leo, Nic
 *
 */
public class Parser {
    private JavaClass clazz;

    private List<MethodModel> methods;
    private List<FieldModel> fields;
    private List<Edge> dependencies = new ArrayList<Edge>();

    private ConstantPoolGen constantPool;

    public Parser(String className) {
        try {
            clazz = new ClassParser(className).parse();
        } catch (ClassFormatError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (clazz == null) {
            throw new InvalidClassName();
        }
       
        constantPool = new ConstantPoolGen(clazz.getConstantPool());
        buildMethods();
        buildFields();
        buildDependencies();
    }
   
    /**
     * Adds all the members and connections to the given diagram
     * from the .class file.
     * @param diagram
     */
    public void addMembersToDiagram(DiagramModel diagram) {
        for (MethodModel method : methods) {
            diagram.addMethodModel(method);
        }
       
        for (FieldModel field : fields) {
            diagram.addFieldModel(field);
        }
       
        for (Edge edge : dependencies) {
            if (edge.targetIsMethod()) {
                diagram.addMethodToMethodConnection(edge.getSource(),
                        edge.getTarget());
            } else {
                diagram.addMethodToFieldConnection(edge.getSource(),
                        edge.getTarget());
            }
        }
    }
   
    /**
     * Updates members in the given diagram based on the .class file.
     * Adds/Removes members to reflect the .class file.
     * @param diagram
     */
    public void updateMembers(DiagramModel diagram) {
       
        List<MethodModel> diagramMethods = diagram.getMethodModels();
        diagramMethods.addAll(diagram.getPaletteModel().getMethods());
        Collections.sort(diagramMethods);
        Collections.sort(this.methods);
       
        List<MethodModel> addedMethods = new Vector<MethodModel>();
        List<MethodModel> removedMethods = new Vector<MethodModel>();
        new MemberDiff<MethodModel>().diff(diagramMethods, this.methods, addedMethods, removedMethods);
       
       
        List<FieldModel> diagramFields = diagram.getFieldModels();  
        diagramFields.addAll(diagram.getPaletteModel().getFields());
        Collections.sort(diagramFields);
        Collections.sort(this.fields);
       
        List<FieldModel> addedFields = new Vector<FieldModel>();
        List<FieldModel> removedFields = new Vector<FieldModel>();
        new MemberDiff<FieldModel>().diff(diagramFields, this.fields, addedFields, removedFields);
       
        List<Edge> diagramdependencies = buildDependencies (diagram.getConnectionModels());
        diagramdependencies.addAll(diagram.getPaletteModel().getEdges());
        Collections.sort(diagramdependencies);
        Collections.sort(this.dependencies);
       
        List<Edge> addedEdges = new Vector<Edge>();
        List<Edge> removedEdges = new Vector<Edge>();
        new MemberDiff<Edge>().diff(diagramdependencies, this.dependencies, addedEdges, removedEdges);
       
        addMembersToPalette(diagram, addedMethods, addedFields, addedEdges);
        removeMembersFromDiagram(diagram, removedMethods, removedFields, removedEdges);
    }
    
    /**
     * Adds the given methods, fields and connections to the flyout
     * palette.
     * @param diagram
     * @param methods
     * @param fields
     * @param edges
     */
    private void addMembersToPalette(DiagramModel diagram, List<MethodModel> methods, List<FieldModel> fields, List<Edge> edges) {
        for (MethodModel method : methods) {
            diagram.addPaletteMethod(method);
        }
       
        for (FieldModel field : fields) {
            diagram.addPaletteField(field);
        }
       
        for (Edge edge : edges) {
            if (edge.targetIsMethod()) {
                diagram.addMethodToMethodConnection(edge.getSource(), edge.getTarget());
            }
            else {
                diagram.addMethodToFieldConnection(edge.getSource(), edge.getTarget());
            }
        }
    }
   
    /**
     * Removes the given methods, fields and edges from the diagram.
     * @param diagram
     * @param methods
     * @param fields
     * @param edges
     */
    private void removeMembersFromDiagram(DiagramModel diagram, List<MethodModel> methods, List<FieldModel> fields, List<Edge> edges) {
        for (Edge edge : edges) {
            if (edge.targetIsMethod()) {
                diagram.removeMethodToMethodConnection(edge.getSource(), edge.getTarget());
            }
            else {
                diagram.removeMethodToFieldConnection(edge.getSource(), edge.getTarget());
            }
        }
       
        for (MethodModel method : methods) {
            diagram.removeMethodModel(method);
        }
       
        for (FieldModel field : fields) {
            diagram.removeFieldModel(field);
        }
    }
   
    /**
     * Creates and returns a list of edges corresponding to the given
     * connection models.
     * @param models
     * @return
     */
    private List<Edge> buildDependencies(List<ConnectionModel> models) {
        List<Edge> retval = new Vector<Edge>();
        for (ConnectionModel connection : models) {
            String source = connection.getSource().getName();
            String target = connection.getTarget().getName();
            boolean destIsMethod = connection.targetIsMethod();
            retval.add(new Edge(source, target, destIsMethod));
        }
       
        return retval;
    }
   
    /**
     * Creates and stores a list of methodModels based on
     * the .class file.
     */
    private void buildMethods() {
        this.methods = new Vector<MethodModel>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (!method.getName().contains("<")){
                int modifier = method.getAccessFlags();               
                this.methods.add(new MethodModel(method.getName(), modifier));
            }
        }
    }
   
    /**
     * Creates and stores a list of fieldModels based on
     * the .class file.
     */
    private void buildFields() {
        this.fields = new Vector<FieldModel>();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            int modifier = field.getAccessFlags();
            String type = field.getType().toString();
            this.fields.add(new FieldModel(field.getName(), modifier, type));
        }
    }
   
    /**
     * Creates and stores a list of edges based on the
     * .class file.
     */
//    private void buildDependencies() {
//        Method[] methods = clazz.getMethods();
//        for (Method method : methods) {
//            if (!(method.isAbstract() || method.isNative())) {
//                buildMethodEdges(method);
//            }
//        }
//    }
      
    private void buildDependencies() {
        for (Method method : clazz.getMethods()) {
            if (method.isAbstract() || method.isNative())
                continue;
           
            MethodGen methodGen = new MethodGen(method, clazz.getClassName(), constantPool);
           
            for (Instruction instruction : methodGen.getInstructionList().getInstructions()) {
                Edge edge = null;
                if (isMethodInvocation(instruction)) {
                    edge = new Edge(method.getName(), ((FieldOrMethod)instruction).getName(constantPool), true);
                }
                if(isFieldAccess(instruction)){
                    edge = new Edge(method.getName(), ((FieldOrMethod)instruction).getName(constantPool), false);
                }
                if (edge != null && !edge.containsSubstring("<init>") && ! edge.containsSubstring("$")) {
                    dependencies.add(edge);
                }
            } // end for
        }// end for
    }
    /**
     * Builds edges from a given method to other methods and fields
     *
     * @param method
     *            which will be the source for all edges built
     */
//    private void buildMethodEdges(Method method) {
//        Set<String> uses = getUsesForMethod(method);
//        for (String use : uses) {
//            // check if use is a method, starts with "()"
//            boolean dest_isMethod = use.startsWith("()");
//            use = use.replaceFirst("[(][)]", "");
//            Edge edge = new Edge(method.getName(), use.replaceFirst("[(][)]", ""), dest_isMethod);
//            if (!edge.containsSubstring("<init>") && ! edge.containsSubstring("$")) {
//                dependencies.add(edge);
//            }
//        }
//    }

//    private void buildMethodEdges(Method method) {
//        MethodGen methodGen = new MethodGen(method, clazz.getClassName(), constantPool);
//       
//        for (Instruction instruction : methodGen.getInstructionList().getInstructions()) {
//            Edge edge = null;
//            if (isMethodInvocation(instruction)) {
//                edge = new Edge(method.getName(), ((FieldOrMethod)instruction).getName(constantPool), true);
//            } else if(isFieldAccess(instruction)){
//                edge = new Edge(method.getName(), ((FieldOrMethod)instruction).getName(constantPool), false);
//            }
//            if (edge != null && !edge.containsSubstring("<init>") && ! edge.containsSubstring("$")) {
//                dependencies.add(edge);
//            }
//        }
//    }
   
//    private InstructionList getInstructionListForMethod(Method method) {
//        MethodGen methodGen = new MethodGen(method, clazz.getClassName(),
//                constantPool);
//        return methodGen.getInstructionList();
//    }

//    @SuppressWarnings("unchecked")
//    private Set<String> getUsesForMethod(Method method) {
//        HashSet<String> uses = new HashSet<String>();
//        InstructionList instructions = getInstructionListForMethod(method);
//        for (Iterator it = instructions.iterator(); it.hasNext();) {
//            InstructionHandle handle = (InstructionHandle) it.next();
//            Instruction instruction = handle.getInstruction();
//            if (!isIncludedAccessInstruction(instruction))
//                continue;
//            FieldOrMethod accessInstruction = (FieldOrMethod) instruction;
//            if (!isInstructionForThisClass(accessInstruction))
//                continue;
//            // differentiate methods from fields
//            String sig = accessInstruction.getSignature(constantPool);
//            if (sig.matches("\\(.*\\).*")) {
//                uses.add("()" + accessInstruction.getName(constantPool));
//            } else {
//                uses.add(accessInstruction.getName(constantPool));
//            }
//
//        }
//        return uses;
//    }

//    @SuppressWarnings("unchecked")
//    private Set<String> getUsesForMethod(Method method) {
//        HashSet<String> uses = new HashSet<String>();
//        HashSet<FieldOrMethod> uses2 = new HashSet<FieldOrMethod>();
//       
//        MethodGen methodGen = new MethodGen(method, clazz.getClassName(), constantPool);
//       
//        for (Instruction instruction : methodGen.getInstructionList().getInstructions()) {
//            if (isMethodInvocation(instruction)) {
//                //if (isInstructionForThisClass((FieldOrMethod)instruction))
//                uses.add("()" + ((FieldOrMethod)instruction).getName(constantPool));
//                uses2.add((FieldOrMethod)instruction);
//            } else if(isFieldAccess(instruction)){
//                //if (isInstructionForThisClass((FieldOrMethod)instruction))
//                uses.add(((FieldOrMethod)instruction).getName(constantPool));
//                uses2.add((FieldOrMethod)instruction);
//            }
//        }
//        return uses;
//    }
   
//    private boolean isInstructionForThisClass(FieldOrMethod accessInstruction) {
//        return clazz.getClassName().equals(
//                accessInstruction.getClassName(constantPool));
//    }

//    private boolean isIncludedAccessInstruction(Instruction instruction) {
//        return instruction instanceof GETFIELD
//                || instruction instanceof PUTFIELD
//                || instruction instanceof INVOKESPECIAL
//                || instruction instanceof INVOKEVIRTUAL
//                || instruction instanceof GETSTATIC
//                || instruction instanceof PUTSTATIC;
//    }
   
    // note eben
    private static boolean isMethodInvocation(final Instruction instruction)
    {   
        return instruction instanceof INVOKESPECIAL
            || instruction instanceof INVOKEVIRTUAL
            || instruction instanceof INVOKESTATIC
            || instruction instanceof INVOKEINTERFACE;
    }
   
    // note eben
    private static boolean isFieldAccess(final Instruction instruction)
    {   
        return instruction instanceof GETFIELD
            || instruction instanceof PUTFIELD
            || instruction instanceof GETSTATIC
            || instruction instanceof PUTSTATIC;
    }
}