/*******************************************************************************
 * Copyright (c) 2014 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.formatting2;

import com.google.common.base.Function;

/**
 * <p>
 * A TextReplacer is responsible for a {@link ITextSegment text region} inside a {@link IFormattableDocument} and
 * produces {@link ITextReplacement replacements} for that region.
 * </p>
 * 
 * <p>
 * The {@link IFormattableDocument} ensures that replacers are executed in the correct order, i.e. from the beginning of
 * the document to the end of the document.
 * </p>
 * 
 * @author Moritz Eysholdt - Initial contribution and API
 * @since 2.8
 */
public interface ITextReplacer {

	/**
	 * A function that maps a given replacer to the region that it replaces.
	 */
	public final static Function<ITextReplacer, ITextSegment> GET_REGION = new Function<ITextReplacer, ITextSegment>() {
		@Override
		public ITextSegment apply(ITextReplacer input) {
			return input.getRegion();
		}
	};

	/**
	 * The replaced region.
	 */
	ITextSegment getRegion();

	/**
	 * Creates the real replacements for this region. The given context
	 * may be altered and returned afterwards.
	 */
	ITextReplacerContext createReplacements(ITextReplacerContext context);
}
