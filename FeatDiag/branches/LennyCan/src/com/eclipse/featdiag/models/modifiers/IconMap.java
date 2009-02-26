package com.eclipse.featdiag.models.modifiers;

import com.sun.org.apache.bcel.internal.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that maps modifier masks to icon file names.
 * @author nic
 *
 */
public class IconMap {
	
	private static Map<Integer, String> fieldIcons;
	private static Map<Integer, String> methodIcons;
	
	static {
		//TODO get the rest of the icons and insert them into maps
		fieldIcons = new HashMap<Integer, String>();
		fieldIcons.put(new Integer(0), "field_default.gif");
		fieldIcons.put(new Integer(Constants.ACC_PRIVATE), "field_private.gif");
		fieldIcons.put(new Integer(Constants.ACC_PROTECTED), "field_protected.gif");
		fieldIcons.put(new Integer(Constants.ACC_PUBLIC), "field_public.gif");
		
		
		methodIcons = new HashMap<Integer, String>();
		methodIcons.put(new Integer(0), "meth_default.gif");
		methodIcons.put(new Integer(Constants.ACC_PRIVATE), "meth_private.gif");
		methodIcons.put(new Integer(Constants.ACC_PROTECTED), "meth_protected.gif");
		methodIcons.put(new Integer(Constants.ACC_PUBLIC), "meth_public.gif");
	}
	
	/**
	 * Returns the name of the image file for the icon
	 * for a field with the given modifiers.
	 * @param modifiers
	 * @return
	 */
	public static String getFieldIconName(int modifiers) {
		return getIconName(modifiers, fieldIcons);
	}
	
	/**
	 * Returns the name of the image file for the icon
	 * for a method with the given modifiers.
	 * @param modifiers
	 * @return
	 */
	public static String getMethodIconName(int modifiers) {
		return getIconName(modifiers, methodIcons);
	}
	
	private static String getIconName(int modifiers, Map<Integer, String> icons) {
		String imageName;
		//we only care about the first 3 bits: (public, private, protected)
		//7 == 0x111
		if (icons.containsKey(new Integer(modifiers & 7))) {
			imageName = icons.get(new Integer(modifiers & 7));
		}
		else {
			imageName = icons.get(new Integer(0));
		}
		
		return imageName;
	}
}
