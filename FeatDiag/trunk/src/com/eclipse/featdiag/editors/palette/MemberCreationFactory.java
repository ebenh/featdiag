package com.eclipse.featdiag.editors.palette;

import org.eclipse.gef.requests.CreationFactory;

import com.eclipse.featdiag.models.MemberModel;


/**
 * Factory for creating instances of node objects from the
 * palette.
 */
public class MemberCreationFactory implements CreationFactory
{
	private MemberModel member; 

	/**
	 * Creates a new Member Creation Factory to hold the
	 * given MemberModel until it is ready to be moved to
	 * the diagram.
	 * @param member
	 */
	public MemberCreationFactory(MemberModel member)
	{
		this.member = member; 
	}
	
	
	public MemberModel getNewObject()
	{
		return member;
	}

	
	public Class<?> getObjectType()
	{
		return member.getClass();
	}

}