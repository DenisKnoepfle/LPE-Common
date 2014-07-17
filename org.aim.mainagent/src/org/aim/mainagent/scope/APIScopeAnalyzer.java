/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.mainagent.scope;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.AbstractScopeAnalyzer;
import org.aim.api.instrumentation.description.Restrictions;
import org.aim.api.instrumentation.description.internal.FlatScopeEntity;
import org.aim.mainagent.utils.MethodSignature;
import org.aim.mainagent.utils.Utils;

/**
 * Analyzes a certain API scope.
 * 
 * @author Alexander Wert
 * 
 */
public class APIScopeAnalyzer extends AbstractScopeAnalyzer {

	private Map<Class<?>, List<MethodSignature>> methodsToMatch;
	private Restrictions restrictions;

	/**
	 * Constructor.
	 * 
	 * @param apiScope
	 *            concrete instance of an AbstractInstAPIScope specifying a
	 *            concrete scope.
	 * @throws InstrumentationException
	 *             if an API class or interface could not be found.
	 */
	public APIScopeAnalyzer(AbstractInstAPIScope apiScope) throws InstrumentationException {
		methodsToMatch = new HashMap<>();
		for (String containerName : apiScope.getMethodsToMatch().keySet()) {
			try {
				Class<?> containerClass = Class.forName(containerName);
				List<MethodSignature> signatures = new ArrayList<>();
				for (String apiMethod : apiScope.getMethodsToMatch().get(containerName)) {
					String methodName = apiMethod.substring(0, apiMethod.indexOf('('));
					Class<?>[] paramTypes = Utils.getParameterTypes(apiMethod);
					signatures.add(new MethodSignature(methodName, paramTypes));
				}
				methodsToMatch.put(containerClass, signatures);
			} catch (ClassNotFoundException e) {
				throw new InstrumentationException("Failed determining scope " + apiScope.getClass().getName(), e);
			}
		}
	}

	@Override
	public void visitClass(Class<?> clazz, Set<FlatScopeEntity> scopeEntities) {
		if (clazz == null || !Utils.isNormalClass(clazz)) {
			return;
		}
		if (restrictions.hasModifierRestrictions() && !Modifier.isPublic(restrictions.getModifier())) {
			return;
		}
		if (restrictions.isExcluded(clazz.getName())) {
			return;
		}
		if (scopeEntities == null) {
			scopeEntities = new HashSet<>();
		}
		for (Class<?> apiClass : methodsToMatch.keySet()) {
			if (apiClass.isAssignableFrom(clazz)) {
				for (MethodSignature methodSignature : methodsToMatch.get(apiClass)) {
					try {
						Method targetMethod = clazz.getMethod(methodSignature.getMethodName(),
								methodSignature.getParameters());
						if (!Modifier.isAbstract(targetMethod.getModifiers())
								&& targetMethod.getDeclaringClass().equals(clazz)) {
							scopeEntities.add(new FlatScopeEntity(clazz, Utils.getMethodSignature(targetMethod, true)));
						}
					} catch (Exception e) {
						// method not found
						// --> class does not contain the desired method
						continue;
					}
				}
			}
		}
	}

	@Override
	public void setRestrictions(Restrictions restrictions) {
		this.restrictions = restrictions;

	}

}
