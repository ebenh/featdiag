package com.eclipse.featdiag.utils;

import java.util.List;

import com.eclipse.featdiag.models.MemberModel;
import com.eclipse.featdiag.models.MethodModel;

/**
 * Class to find differences between 2 lists
 * 
 * @author nic
 *
 * @param <Type>
 */
public class MemberDiff<Type extends Comparable<? super Type>> {

	/**
	 * Fills arrays added and removed with Objects that have been
	 * added or removed from the base list to obtain the modified list.
	 * @param base
	 * @param modified
	 * @param added
	 * @param removed
	 */
	public void diff(List<Type> base, List<Type> modified, List<Type> added, List<Type> removed) {
    	int indexBase = 0;
    	int indexMod = 0;
    	while (indexBase < base.size() && indexMod < modified.size()) {
    		Type memberBase = base.get(indexBase);
    		Type memberMod = modified.get(indexMod);
    		int comp = memberBase.compareTo(memberMod);
    		if (comp == 0) {
    			// Member has not been changed
    			indexBase++;
    			indexMod++;
    		}
    		else if (comp < 0) {
    			// Member has been removed
    			removed.add(memberBase);
    			indexBase++;
    		}
    		else { //comp > 0
    			// Member has been added
    			added.add(memberMod);
    			indexMod++;
    		}
    		// if the member names are the same but the modifiers are different,
    		// remove the base member from the diagram and add the modified member
    		// to the palette.
    		if (memberBase instanceof MemberModel &&
    			((MemberModel) memberBase).getName().equals(
    					((MemberModel) memberMod).getName()) &&
    			((MemberModel) memberBase).getModifiers() != 
    				((MemberModel) memberMod).getModifiers()) {
    			removed.add(memberBase);
    			added.add(memberMod);
    		}
    		
    		if (memberBase instanceof MethodModel &&
        			((MemberModel) memberBase).getName().equals(
        					((MemberModel) memberMod).getName())) {
//    			String[] baseArgTypes = ((MethodModel) memberBase).getArgTypeNames();
//    			String[] modArgTypes = ((MethodModel) memberMod).getArgTypeNames();
//    			if (baseArgTypes.length != modArgTypes.length) {
//    				removed.add(memberBase);
//        			added.add(memberMod);
//        			
//    			} else {
//	    			for (int i = 0; i < baseArgTypes.length; i ++){
//	    				if (! baseArgTypes[i].equals(modArgTypes[i])) {
//		    				removed.add(memberBase);
//		        			added.add(memberMod);
//		        			break;
//	    				}
//	    				
//	    			}
//    			}
        			
        	}
    	}
    	
    	// if base list is longer than modified list, it means something is
    	// removed from the diagram.
    	if (indexBase < base.size()) {
    		// The rest of the elements have been removed
    		while (indexBase < base.size()) {
    			removed.add(base.get(indexBase));
    			indexBase++;
    		}
    	}
    	// if base list is shorter than modified list, it means something is
    	// added to the diagram.
    	else if (indexMod < modified.size()) {
    		// The rest of the elements have been added
    		while (indexMod < modified.size()) {
    			added.add(modified.get(indexMod));
    			indexMod++;
    		}
    	}
    }
}
