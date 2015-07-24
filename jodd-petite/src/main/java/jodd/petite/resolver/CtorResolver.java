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

package jodd.petite.resolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.CtorDescriptor;
import jodd.petite.CtorInjectionPoint;
import jodd.petite.InjectionPointFactory;
import jodd.petite.PetiteException;
import jodd.petite.PetiteUtil;
import jodd.petite.meta.PetiteInject;
import jodd.util.StringUtil;

/**
 * Resolver for constructor injection points.
 */
public class CtorResolver {

	protected final InjectionPointFactory injectionPointFactory;

	public CtorResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	/**
	 * Resolves constructor injection point from type. Looks for single annotated constructor.
	 * If no annotated constructors found, the total number of constructors will be checked.
	 * If there is only one constructor, that one will be used as injection point. If more
	 * constructors exist, the default one will be used as injection point. Otherwise, exception
	 * is thrown.
	 */
	public CtorInjectionPoint resolve(Class type, boolean useAnnotation) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		CtorDescriptor[] allCtors = cd.getAllCtorDescriptors();
		Constructor foundedCtor = null;
		Constructor defaultCtor = null;
		String refValues = null;

		for (CtorDescriptor ctorDescriptor : allCtors) {
			Constructor<?> ctor = ctorDescriptor.getConstructor();

			Class<?>[] paramTypes = ctor.getParameterTypes();
			if (paramTypes.length == 0) {
				defaultCtor = ctor;	// detects default ctors
			}
			if (useAnnotation == false) {
				continue;
			}
			PetiteInject ref = ctor.getAnnotation(PetiteInject.class);
			Annotation[][] annotations = ctor.getParameterAnnotations();
			if (ref == null && !containsAnnotation(annotations)) {
				continue;
			}
			if (foundedCtor != null) {
				throw new PetiteException("Two or more constructors are annotated as injection points in bean: " + type.getName());
			}
			foundedCtor = ctor;
			if (ref != null) {
				refValues = ref.value().trim();
			} else {
				//TODO Convert the Annotation[][] to a String or directly to String[][]
				refValues = convertAnnotationsToString(annotations);
			}
		}
		if (foundedCtor == null) {
			if (allCtors.length == 1) {
				foundedCtor = allCtors[0].getConstructor();
			} else {
				foundedCtor = defaultCtor;
			}
		}
		if (foundedCtor == null) {
			throw new PetiteException("No constructor (annotated, single or default) founded as injection point for: " + type.getName());
		}

		String[][] references = PetiteUtil.convertAnnValueToReferences(refValues);

		return injectionPointFactory.createCtorInjectionPoint(foundedCtor, references);
	}
	
	private boolean containsAnnotation(Annotation[][] annotations) {
		for (Annotation[] parameterAnnotations : annotations) {
			for (Annotation annotation : parameterAnnotations) {
				if (annotation.annotationType().equals(PetiteInject.class)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private String[][] convertAnnotationsToStringArray(Annotation[][] annotations) {
		String[][] result = new String[annotations.length][];
		
		for (int i = 0; i < annotations.length; i++) {
			Annotation[] parameterAnnotations = annotations[i];
			for (Annotation annotation : parameterAnnotations) {
				if (annotation.annotationType().equals(PetiteInject.class)) {
					result[i] = new String[] { ((PetiteInject) annotation).value() };
				}
			}
		}
		
		return result;
	}
	
	private String convertAnnotationsToString(Annotation[][] annotations) {
		List<String> annotationValues = new LinkedList<>();
		
		for (int i = 0; i < annotations.length; i++) {
			Annotation[] parameterAnnotations = annotations[i];
			for (Annotation annotation : parameterAnnotations) {
				if (annotation.annotationType().equals(PetiteInject.class)) {
					annotationValues.add(((PetiteInject) annotation).value());
				}
			}
		}
		
		return StringUtil.join(annotationValues.toArray(new String[annotationValues.size()]), ",");
	}
}