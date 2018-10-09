/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ide.server;

import com.google.inject.ImplementedBy;

/**
 * @author dietrich - Initial contribution and API
 * 
 * @since 2.16
 */
@ImplementedBy(ILanguageServerExitHandler.DefaultImpl.class)
public interface ILanguageServerExitHandler {

	/**
	 * Callback that is called when Language Servers <code>exit</code> is called.
	 */
	void exit(int status);
	
	/**
	 * Implementation for {@link ILanguageServerExitHandler} that does nothing.
	 */
	public static class NullImpl implements ILanguageServerExitHandler {

		@Override
		public void exit(int status) {
			// do nothing
		}
		
	}
	
	/**
	 * Default Implementation for {@link ILanguageServerExitHandler}. Calls <code>System.exit</code>.
	 */
	public static class DefaultImpl implements ILanguageServerExitHandler {

		@Override
		public void exit(int status) {
			System.exit(status);
		}
		
	}
	
}
