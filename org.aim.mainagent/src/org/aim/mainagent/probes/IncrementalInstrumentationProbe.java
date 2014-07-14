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
package org.aim.mainagent.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeIncrementalInstrumentation;
import org.aim.mainagent.TraceInstrumentor;

/**
 * 
 * 
 * @author Alexander Wert
 * 
 */
public class IncrementalInstrumentationProbe extends AbstractEnclosingProbe {

	public String __clazz = "__clazz";
	public static final String _CLAZZ = "__clazz";
	public static final String _INST_DESCRIPTION = "__instDescription";
	public Long __instDescription = 0L;

	/**
	 * Injection code for incremental instrumentation in full trace
	 * instrumentation.
	 */
	@ProbeIncrementalInstrumentation
	public void incrementalInstrumentation() {

		TraceInstrumentor.getInstance().instrumentIncrementally(__clazz.getClass().getName() + __methodSignature,
				__instDescription);
	}

}
