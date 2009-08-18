package com.eclipse.featdiag.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaCore;

// Wraps variables of type IMember so that they are serializable
public class MemberWrapper<T extends IMember> implements Serializable {
	private static final long serialVersionUID = -8744324261720009976L;
    public transient T member;
    private String iTypeHandleIdentifier;
	
	public MemberWrapper(T member){
		this.member = member;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		iTypeHandleIdentifier = member.getHandleIdentifier();
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		member = (T) JavaCore.create(iTypeHandleIdentifier);
	}
	
	public T getMember(){
		return member;
	}
}
