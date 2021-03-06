/**
 *
 * CakePHP Eclipse Plugin
 * Copyright 2012, DoApp, Inc. (http://www.doapps.com)
 *
 * Licensed under The MIT License
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright 2012, DoApp, Inc. (http://www.doapps.com)
 * @link          https://github.com/doapp/cakephp-eclipse-plugin
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package com.doapps.cakephp.files.impl;

import java.util.regex.Pattern;

import com.doapps.cakephp.files.CakeVersion;
import com.doapps.cakephp.files.IElement;

/**
 * @author ryan
 *
 */
public class CakeVersion2 extends CakeVersion
{
	private static CakeVersion2 instance = null;
	
	public static String appDirName = "app";
	private static final Pattern CONTROLLER_NAME_PATTERN = Pattern.compile("(.*)Controller\\..*");
	
	// so only this package com implement it
	private CakeVersion2()
	{
		super("2.X");
	}

	public static CakeVersion2 getInstance()
	{
		if (instance == null)
		{
			instance = new CakeVersion2();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see com.doapps.cakephp.files.ICakeVersion#getDefaultAppDir()
	 */
	public String getDefaultAppDirName() {
		return CakeVersion2.appDirName;
	}	

	/* (non-Javadoc)
	 * @see com.doapps.cakephp.files.ICakeVersion#getModelDirName()
	 */
	public String getModelDirName() {
		return "Model";
	}

	public String constructModelName(String name) {
		//TODO: make sure 1st char is capital: http://stackoverflow.com/questions/1149855/how-to-upper-case-every-first-letter-of-word-in-a-string
		//How do we handle something like CoolUser
	    return name + ".php";
	}
	
	/* (non-Javadoc)
	 * @see com.doapps.cakephp.files.ICakeVersion#getViewDirName()
	 */
	public String getViewDirName() {
		return "View";
	}
	
	public String getElementDirName() {
		return "Elements";
	}
  
  @Override
  public String getJSfolderNameForElement(IElement element)
  {
    return "Elements";
  }
	
	/* (non-Javadoc)
	 * @see com.doapps.cakephp.files.ICakeVersion#getControllerDirName()
	 */
	public String getControllerDirName() {
		return "Controller";
	}
	
	public String getControllerFileNameSuffix() {
		return "Controller.php";
	}
	
	public String constructControllerName(String name) {
	    return name + getControllerFileNameSuffix();
	}
	
	public String constructViewName(String controllerName, String action)
	{
	  return controllerName + "/" + action + ".ctp";
	}
	
	public Pattern getControllerNamePattern() {
		return CakeVersion2.CONTROLLER_NAME_PATTERN;
	}
}
