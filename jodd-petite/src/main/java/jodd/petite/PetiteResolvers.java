// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.petite;

import jodd.petite.resolver.CtorResolver;
import jodd.petite.resolver.DestroyMethodResolver;
import jodd.petite.resolver.InitMethodResolver;
import jodd.petite.resolver.MethodResolver;
import jodd.petite.resolver.PropertyResolver;
import jodd.petite.resolver.ProviderResolver;
import jodd.petite.resolver.SetResolver;

/**
 * Holds all resolvers instances and offers delegate methods.
 */
public class PetiteResolvers {

	protected CtorResolver ctorResolver;
	protected PropertyResolver propertyResolver;
	protected MethodResolver methodResolver;
	protected SetResolver setResolver;
	protected InitMethodResolver initMethodResolver;
	protected DestroyMethodResolver destroyMethodResolver;
	protected ProviderResolver providerResolver;

	public PetiteResolvers(final InjectionPointFactory injectionPointFactory) {
		this.ctorResolver = new CtorResolver(injectionPointFactory);
		this.propertyResolver = new PropertyResolver(injectionPointFactory);
		this.methodResolver = new MethodResolver(injectionPointFactory);
		this.setResolver = new SetResolver(injectionPointFactory);
		this.initMethodResolver = new InitMethodResolver();
		this.destroyMethodResolver = new DestroyMethodResolver();
		this.providerResolver = new ProviderResolver();
	}

	// ---------------------------------------------------------------- delegates

	/**
	 * Resolves constructor injection point.
	 */
	public CtorInjectionPoint resolveCtorInjectionPoint(final Class<?> type) {
		return this.ctorResolver.resolve(type, true);
	}

	/**
	 * Resolves property injection points.
	 */
	public PropertyInjectionPoint[] resolvePropertyInjectionPoint(final Class<?> type, final boolean autowire) {
		return this.propertyResolver.resolve(type, autowire);
	}

	/**
	 * Resolves method injection points.
	 */
	public MethodInjectionPoint[] resolveMethodInjectionPoint(final Class<?> type) {
		return this.methodResolver.resolve(type);
	}

	/**
	 * Resolves set injection points.
	 */
	public SetInjectionPoint<?>[] resolveSetInjectionPoint(final Class<?> type, final boolean autowire) {
		return this.setResolver.resolve(type, autowire);
	}

	/**
	 * Resolves init method points.
	 */
	public InitMethodPoint[] resolveInitMethodPoint(final Object bean) {
		return this.initMethodResolver.resolve(bean);
	}

	/**
	 * Resolves destroy method points.
	 */
	public DestroyMethodPoint[] resolveDestroyMethodPoint(final Object bean) {
		return this.destroyMethodResolver.resolve(bean);
	}

	/**
	 * Resolves provider definition defined in a bean.
	 */
	public ProviderDefinition[] resolveProviderDefinitions(final BeanDefinition beanDefinition) {
		return this.providerResolver.resolve(beanDefinition);
	}

}