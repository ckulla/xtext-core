/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.scoping.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.namespaces.AbstractGlobalScopeProvider;
import org.eclipse.xtext.util.OnChangeEvictingCacheAdapter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
public class ImportUriGlobalScopeProvider extends AbstractGlobalScopeProvider {

	@Inject
	private ImportUriResolver importResolver;
	
	@Inject
	private Provider<LoadOnDemandResourceDescriptions> loadOnDemandDescriptions;
	
	public ImportUriResolver getImportUriResolver() {
		return importResolver;
	}
	
	public void setImportResolver(ImportUriResolver importResolver) {
		this.importResolver = importResolver;
	}
	
	public IResourceDescriptions getResourceDescriptions(EObject ctx, Collection<URI> importUris) {
		IResourceDescriptions result = getResourceDescriptions(ctx);
		LoadOnDemandResourceDescriptions demandResourceDescriptions = loadOnDemandDescriptions.get();
		demandResourceDescriptions.initialize(result, importUris, ctx.eResource());
		return demandResourceDescriptions;
	}

	public IScope getScope(EObject context, EReference reference) {
		final LinkedHashSet<URI> uniqueImportURIs = getImportedUris(context);
		IResourceDescriptions descriptions = getResourceDescriptions(context, uniqueImportURIs);
		ArrayList<URI> newArrayList = Lists.newArrayList(uniqueImportURIs);
		Collections.reverse(newArrayList);
		IScope scope = IScope.NULLSCOPE;
		for (URI u : newArrayList) {
			scope = createLazyResourceScope(scope, u, descriptions, reference);
		}
		return scope;
	}

	private LinkedHashSet<URI> getImportedUris(EObject context) {
		OnChangeEvictingCacheAdapter cache = OnChangeEvictingCacheAdapter.getOrCreate(context.eResource());
		if (cache.get(getClass().getName()) == null) {
			TreeIterator<EObject> iterator = context.eResource().getAllContents();
			final LinkedHashSet<URI> uniqueImportURIs = new LinkedHashSet<URI>(10);
			while (iterator.hasNext()) {
				EObject object = iterator.next();
				String uri = importResolver.apply(object);
				if (uri != null) {
					URI importUri = URI.createURI(uri);
					uniqueImportURIs.add(importUri);
				}
			}
			Iterator<URI> uriIter = uniqueImportURIs.iterator();
			while(uriIter.hasNext()) {
				if (!EcoreUtil2.isValidUri(context, uriIter.next()))
					uriIter.remove();
			}
			cache.set(getClass().getName(), uniqueImportURIs);
		}
		return cache.get(getClass().getName());
	}

	private SimpleScope createLazyResourceScope(IScope parent, final URI uri, final IResourceDescriptions descriptions,
			final EReference reference) {
		return new SimpleScope(parent, null) {
			@Override
			public Iterable<IEObjectDescription> internalGetContents() {
				IResourceDescription description = descriptions.getResourceDescription(uri);
				if (description == null)
					return Iterables.emptyIterable();
				Iterable<IEObjectDescription> exportedObjects = description.getExportedObjects(reference.getEReferenceType());
				return exportedObjects;
			}
		};
	}

	public void setLoadOnDemandDescriptions(Provider<LoadOnDemandResourceDescriptions> loadOnDemandDescriptions) {
		this.loadOnDemandDescriptions = loadOnDemandDescriptions;
	}

	public Provider<LoadOnDemandResourceDescriptions> getLoadOnDemandDescriptions() {
		return loadOnDemandDescriptions;
	}

}
