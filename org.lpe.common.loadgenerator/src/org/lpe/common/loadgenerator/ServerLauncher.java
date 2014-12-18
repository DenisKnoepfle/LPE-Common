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
package org.lpe.common.loadgenerator;

import java.util.ArrayList;
import java.util.List;

import org.lpe.common.util.web.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server Launcher for the load generator Services.
 * 
 * @author Alexander Wert
 * 
 */
public final class ServerLauncher {
	private static final int DEFAULT_PORT = 8080;
	private static final String PORT_KEY = "port=";
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerLauncher.class);

	private static Integer port = DEFAULT_PORT;

	/**
	 * Private constructor due to singleton class.
	 */
	private ServerLauncher() {

	}

	/**
	 * Opens up a server on the localhost IP address and the port 8090 of the
	 * underlying system.
	 * 
	 * @param args
	 *            should contain at least one parameter indicating whether to
	 *            start or stop
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			LOGGER.error("Load generator Service Launcher requires exactly two arguments:");
			LOGGER.error("1st argument: start / shutdown");
			System.exit(0);
		}

		parseArgs(args);

		if (args[0].equalsIgnoreCase("start")) {
			List<String> servicePackages = new ArrayList<String>();
			servicePackages.add("org.lpe.common.loadgenerator.service");
			WebServer.getInstance().start(port, "", servicePackages);
		} else if (args[0].equalsIgnoreCase("shutdown")) {
			WebServer.triggerServerShutdown(port, "");
		} else {
			LOGGER.error("Invalid value for 1st argument! Valid values are: start / shutdown");
		}

	}

	/**
	 * Parses the agent arguments.
	 * 
	 * @param agentArgs
	 *            arguments as string
	 */
	private static void parseArgs(String[] agentArgs) {
		if (agentArgs == null) {
			return;
		}
		for (String arg : agentArgs) {
			if (arg.startsWith(PORT_KEY)) {
				port = Integer.parseInt(arg.substring(PORT_KEY.length()));
			}
		}
	}

}
