package com.eclipse.featdiag.utils;

import java.util.List;

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
    	}
    	
    	if (indexBase < base.size()) {
    		// The rest of the elements have been removed
    		while (indexBase < base.size()) {
    			removed.add(base.get(indexBase));
    		}
    	}
    	else if (indexMod < modified.size()) {
    		// The rest of the elements have been added
    		while (indexMod < modified.size()) {
    			added.add(modified.get(indexMod));
    		}
    	}
    }
}
