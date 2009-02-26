package com.eclipse.featdiag.editors.palette;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.eclipse.featdiag.editors.FeatDiagPlugin;
import com.eclipse.featdiag.models.BaseModel;
import com.eclipse.featdiag.models.FieldModel;
import com.eclipse.featdiag.models.MemberModel;
import com.eclipse.featdiag.models.PaletteModel;


/**
 * Creator of the the PaletteViewer
 */
public class PaletteViewer implements PropertyChangeListener {
	
	private static final ImageDescriptor fieldImage = AbstractUIPlugin.imageDescriptorFromPlugin(FeatDiagPlugin.PLUGIN_ID, "icons" + File.separator + "field.gif");
	private static final ImageDescriptor methodImage = AbstractUIPlugin.imageDescriptorFromPlugin(FeatDiagPlugin.PLUGIN_ID, "icons" + File.separator + "method.gif");
	
	private PaletteRoot paletteRoot;
	private PaletteDrawer drawer;
	
	public PaletteViewer() {
		drawer = new PaletteDrawer("New Class Members", AbstractUIPlugin
				.imageDescriptorFromPlugin(FeatDiagPlugin.PLUGIN_ID, "icons"
						+ File.separator + "members.gif"));
	}
	
	/**
	 * Creates the PaletteRoot for the
	 * Feature Diagram editor.
	 */
	private void createPaletteRoot()
	{
		// create root
		paletteRoot = new PaletteRoot();

		// a group of default control tools
		PaletteGroup controls = new PaletteGroup("Controls");
		paletteRoot.add(controls);

		// the selection tool
		ToolEntry selection = new SelectionToolEntry();
		controls.add(selection);

		// use selection tool as default entry
		paletteRoot.setDefaultEntry(selection);

		// the marquee selection tool
		controls.add(new MarqueeToolEntry());

		// a separator
		PaletteSeparator separator = new PaletteSeparator(FeatDiagPlugin.PLUGIN_ID + ".palette.seperator");
		separator.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		controls.add(separator);
	
		paletteRoot.add(drawer);
	}
	
	/**
	 * Handle either addition or removal of a field or method to the palette.
	 * @param evt
	 * @param image
	 */
	private void handleMemberChange(PropertyChangeEvent evt, ImageDescriptor image) {
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if (oldValue != null && newValue != null) {
			throw new IllegalStateException(
			"Exactly one of old or new values must be non-null for PALETTE_FIELD event");
		}
		
		// New Member Added to Palette
		if (oldValue == null) {
			addMember((MemberModel) newValue, image);
		}
		// New Member Removed from Palette
		else {
			removeMember((MemberModel) oldValue);
		}
	}
	
	/**
	 * Add the member to the palette with the given icon
	 * @param member
	 * @param image
	 */
	private void addMember(MemberModel member, ImageDescriptor image) {
		String memberName = member.getName();
		
		CombinedTemplateCreationEntry entry = new CombinedTemplateCreationEntry(memberName, "Add " + memberName + " to the Diagram",
				FieldModel.class, new MemberCreationFactory(member), image, image);
		entry.setId(member.toString());
		
		Display.getDefault().asyncExec(new AddToDrawer(entry));
	}
	
	/**
	 * Remove the member from the palette
	 * @param member
	 */
	@SuppressWarnings("unchecked")
	private void removeMember(MemberModel member) {
		String memberId = member.toString();
		PaletteEntry toRemove = null;
		
		List<PaletteEntry> children = drawer.getChildren();
		if (children == null) {
			return;
		}
		
		for (PaletteEntry entry : children) {
			if (memberId.equals(entry.getId())) {
				toRemove = entry;
				break;
			}
		}
		
		if (toRemove != null) {
			Display.getDefault().asyncExec(new RemoveFromDrawer(toRemove));
		}
	}
	
	/**
	 * Adds all fields and methods from the given
	 * PaletteModel immediately to this palette.
	 * @param model
	 */
	public void addContents(PaletteModel model) {
		for (MemberModel method : model.getMethods()) {
			addMember(method, methodImage);
		}
		
		for (MemberModel field : model.getFields()) {
			addMember(field, fieldImage);
		}
	}

	/**
	 * Returns a palette root.
	 * Creates a new palette root if one does not exist.	 * 
	 * @return
	 */
	public PaletteRoot getPaletteRoot()
	{
		if (paletteRoot == null) {
			createPaletteRoot();
		}
		return paletteRoot;
	}

	
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();

		if (BaseModel.PALETTE_FIELD.equals(property)) {
			handleMemberChange(evt, fieldImage);
		}
		else if (BaseModel.PALETTE_METHOD.equals(property)) {
			handleMemberChange(evt, methodImage);
		}
	}
	
	/**
	 * Runnable class that adds entries to the
	 * palette drawer.
	 * Must be run in UI-thread.
	 * @author nic
	 *
	 */
	private class AddToDrawer implements Runnable {
		private PaletteEntry entry;
		
		public AddToDrawer(PaletteEntry entry) {
			this.entry = entry;
		}
		
		
		public void run() {
			drawer.add(entry);
		}		
	}
	
	/**
	 * Runnable class that removes entries from the
	 * palette drawer.
	 * Must be run in UI-thread.
	 * @author nic
	 *
	 */
	private class RemoveFromDrawer implements Runnable {
		private PaletteEntry entry;
		
		public RemoveFromDrawer(PaletteEntry entry) {
			this.entry = entry;
		}
		
		
		public void run() {
			drawer.remove(entry);
		}		
	}
}