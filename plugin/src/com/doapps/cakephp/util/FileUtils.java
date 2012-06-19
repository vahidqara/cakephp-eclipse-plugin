/**
 * 
 */
package com.doapps.cakephp.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.doapps.cakephp.Activator;
import com.doapps.cakephp.preferences.PreferenceConstants;

/**
 * @author ryan
 *
 */
public class FileUtils {

	public static String getSelectedText()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (null == page)
		{
			return null;
		}
		String text = null;
		ISelection selection = page.getSelection();
		if (selection instanceof ITextSelection)
		{
			text = ((ITextSelection) selection).getText();
		}
		else if (selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IMethod)
			{
				String fullyQualifiedName = ((IMethod) element).getFullyQualifiedName();
				String[] split = fullyQualifiedName.split("\\$");
				if (split.length > 1)
				{
					return split[1];
				}
			}
			return null;
		}
		return text;
	}

	static public IProject getProjectForSelection()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		if (selection != null)
		{
			if (selection instanceof ITextSelection)
			{
				IEditorInput editorInput = page.getActiveEditor().getEditorInput();
				if (editorInput instanceof IFileEditorInput)
				{
					return ((IFileEditorInput) editorInput).getFile().getProject();
				}
			}
			else if (selection instanceof IStructuredSelection && !selection.isEmpty())
			{
				Object element = ((IStructuredSelection) selection).getFirstElement();
				if (element instanceof IResource)
				{
					return ((IResource) element).getProject();
				}
			}
			// TODO: are there other types of selections
		}

		IFile file = getSelectedFile();
		if (file != null)
		{
			return file.getProject();
		}
		return null;
	}

	/**
	 * Get the full path of the current project
	 * 
	 * @return full path string or "" if not found
	 */
	static public IPath getCurrentProjectPath() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		//First check: editor is 'active'  
		IWorkbenchPart workbenchPart = window.getActivePage().getActivePart(); 
		IFile file = (IFile) workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);		
		IPath projectPath = file.getProject().getLocation();

		//Second check: editor not active, ctrl+alt (right click prop) on project
		if(null == projectPath) {
			if (window != null)
			{
				IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof IAdaptable)
				{
					IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
					projectPath = project.getLocation();	            
				}
			}
		}

		return projectPath;
	}

	static public Boolean isProjectSpecificSettingsEnabled(IProject project) {
		Boolean projectSpecificSettingsEnabled = false;
		try {
			//First check if they have a proj specific app dir
			String enableProjSpecific = FileUtils.getProjectProperty(project, PreferenceConstants.P_ENABLE_PROJECT_SPECIFIC_SETTINGS);
			if(null != enableProjSpecific) {
				projectSpecificSettingsEnabled = Boolean.parseBoolean(enableProjSpecific);			
			}		
		} catch (CoreException e) {
			//Property not found, so not using project specific settings
		}
		return projectSpecificSettingsEnabled;
	}

	/**
	 * Get project specific property (if enabled) fall back to workspace pref
	 * 
	 * @param key they key must be the same in the properties store and the preference store
	 * @return null|String
	 */
	static public String getProjectPropertyOrWorkspacePref(IProject project, String key) {
		if(!isProjectSpecificSettingsEnabled(project)) return Activator.getDefault().getPreferenceStore().getString(key);
		else {
			try {
				return getProjectProperty(project, key);
			} catch (CoreException e) {
				return null;
			}
		}
	}

	/**
	 * Get a property from the persistent property store
	 * 
	 * @param key
	 * @return null|String value
	 * @throws CoreException
	 */
	static public String getProjectProperty(IProject project, String key) throws CoreException {
		// shouldn't this property have a name?
		return project.getPersistentProperty(new QualifiedName("", key));
		//		String value = null;
		//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		//	    if (window != null)
		//	    {
		//	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		//	        Object firstElement = selection.getFirstElement();
		//	        if (firstElement instanceof IResource)
		//	        {
		//	        	value = ((IResource) firstElement).getPersistentProperty(new QualifiedName("", key));
		//	        }
		//	    }
		//	    return value;
	}

	public static IFile getSelectedFile()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page == null)
		{
			return null;
		}
		IFile file = null;
		ISelection selection = page.getSelection();

		if (selection instanceof ITextSelection)
		{
			IEditorInput editorInput = page.getActiveEditor().getEditorInput();
			if (editorInput instanceof IFileEditorInput)
			{
				file = ((IFileEditorInput) editorInput).getFile();
			}
		}
		else if (selection instanceof IStructuredSelection && !selection.isEmpty())
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IFile)
			{
				file = (IFile) element;
			}
			else if (element instanceof IMethod)
			{
				IResource r = ((IMethod) element).getResource();
				if (r != null)
				{
					file = (IFile) r.getAdapter(IFile.class);
				}
			}
		}
		return file;
	}
}
