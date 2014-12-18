/**
 * Copyright 2014 SAP AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.lpe.common.jmeter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.lpe.common.jmeter.IO.DynamicPipedInputStream;
import org.lpe.common.jmeter.IO.FilePoller;
import org.lpe.common.jmeter.config.JMeterWorkloadConfig;
import org.lpe.common.util.LpeStreamUtils;
import org.lpe.common.util.system.LpeSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class which handles the connection to JMeter console process.
 * 
 * The JMeterWrapper instance is capable of running several loadtests, the log of all gets combined.
 * 
 * It should be possible to let several JMeter instances run their tests in parallel
 * 
 * @author Jonas Kunz
 */
public final class JMeterWrapper {

	/**
	 * location of the JMeter bin folder
	 */
	private Process jmeterProcess;

	private DynamicPipedInputStream logStream;

	/**
	 * Singleton instance.
	 */
	private static JMeterWrapper instance;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JMeterWrapper.class);

	private JMeterWrapper() {
		// create the stream to provide log from JMETER
		logStream = new DynamicPipedInputStream();

	}

	private static int getUniqueLogID() {
		return (int) System.currentTimeMillis();
	}

	/**
	 * @return singleton instance.
	 */
	public static synchronized JMeterWrapper getInstance() {
		if (instance == null) {
			instance = new JMeterWrapper();
		}
		return instance;
	}

	/**
	 * Starts a load test and then returns immediately. To wait for the test to finish use {@link waitForLoadTestFinish}
	 * or poll {@link isLoadTestRunning}
	 * 
	 * @param config The test configuration
	 * @throws IOException if starting load fails
	 */
	public synchronized void startLoadTest(final JMeterWorkloadConfig config) throws IOException {

		// check whether a loadTest is already running
		if (jmeterProcess != null) {
			throw new RuntimeException("An Jmeter Process is already running, can only run one process per wrapperinstance");
		}

		// create log file
		File logFile = createLogFile(config);

		List<String> cmd = buildCmdLine(config,
										logFile);

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.directory(new File(config.getPathToJMeterRootFolder()));

		// the error stream must be piped, otherwise none takes the messages and JMeter waits to infinity
		// till someone receives the messages!
		pb.redirectErrorStream(true);
		jmeterProcess = pb.start();
		// output needs to be redirected
		final InputStream is = jmeterProcess.getInputStream();
		File outputFile = new File(config.getPathToJMeterRootFolder().concat("\\" + config.getDefaultOutputFile()));
		final FileOutputStream fos = new FileOutputStream(outputFile);
		LpeSystemUtils.submitTask(new Runnable() {
			
			@Override
			public void run() {
				try {
					LpeStreamUtils.pipe(is, fos);
				} catch (IOException e) {
					LOGGER.debug("Error while piping console output to file", e);
				}
			}
		});

		// poll the log file
		final FilePoller poll = new FilePoller(logFile, logStream, true);

		if (config.getCreateLogFlag()) {
			poll.startPolling();
		}

		final JMeterWrapper thisWrapper = this;

		// add a Thread that waits for the Process to terminate who then
		// notifies all other waiting Threads
		LpeSystemUtils.submitTask(new Runnable() {

			public void run() {
				try {
					jmeterProcess.waitFor();

					// notify the log-thread that the process has ended and wait
					// for
					// polling to finish
					if (config.getCreateLogFlag()) {
						poll.endPolling();
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				synchronized (thisWrapper) {
					jmeterProcess = null;
					thisWrapper.notifyAll();
				}
			}
		});
	}

	/**
	 * Creates a new log file based on the passed configuration.
	 * 
	 * @param config the {@link JMeterWorkloadConfig}, only the logfile flag is requested
	 * @return the log file, <code>null</code> possible
	 * @throws IOException on file creation fail
	 */
	private File createLogFile(JMeterWorkloadConfig config) throws IOException {
		File logFile = null;

		if (config.getCreateLogFlag()) {
			int logID = getUniqueLogID();
			String logFilename = config.getPathToJMeterRootFolder() + "\\" + config.getLogFilePrefix() + logID;
			logFile = new File(logFilename);
			if (logFile.exists()) {
				logFile.delete();
			}
			logFile.createNewFile();
		}

		return logFile;
	}

	/**
	 * Builds the command line to execute a Apache JMeter load script with the passed configuration. If the lof flag has
	 * been set, the output will be redirected to the passed log file.<br />
	 * See more about JMeter command line configuration: http://jmeter.apache.org/usermanual/get-started.html.
	 * 
	 * @param config the configuration for JMeter
	 * @param logFile {@link File} which will get the JMeter output, if the configuration has the
	 * @return List of the translated configuration into command line arguments
	 */
	private List<String> buildCmdLine(final JMeterWorkloadConfig config, File logFile) {
		List<String> cmd = new ArrayList<String>();

		cmd.add("java");
		cmd.add("-jar");
		cmd.add("bin\\ApacheJMeter.jar");
		cmd.add("-n"); // JMeter in non-gui mode
		cmd.add("-t"); // load script fiel path
		cmd.add("\"" + config.getPathToScript() + "\"");

		if (config.getCreateLogFlag()) {
			cmd.add("-j");
			cmd.add(logFile.getAbsolutePath());
		}

		// now add all the JMeter variables
		cmd.add("-Jp_durationSeconds=" + config.getExperimentDuration());
		cmd.add("-Jp_numUsers=" + config.getNumUsers());
		cmd.add("-Jp_thinkTimeMinMS=" + config.getThinkTimeMinimum());
		cmd.add("-Jp_thinkTimeMaxMS=" + config.getThinkTimeMaximum());

		double rampUpSecondsPerUser = config.getRampUpInterval() / config.getRampUpNumUsersPerInterval();
		double coolDownSecondsPerUser = config.getCoolDownInterval() / config.getCoolDownNumUsersPerInterval();
		cmd.add("-Jp_rampUpSecondsPerUser=" + rampUpSecondsPerUser);
		cmd.add("-Jp_rampDownSecondsPerUser=" + coolDownSecondsPerUser);

		if (config.getSamplingFileFlag()) {
			cmd.add("-Jp_resultFile=" + config.getPathToSamplingFile());
		}
		
		// add custom properties
		Properties additionalProps = config.getAdditionalProperties();
		for (Entry<Object, Object> property : additionalProps.entrySet()) {
			cmd.add("-J" + property.getKey() + "=" + property.getValue());
		}

		return cmd;
	}

	/**
	 * Returns the stream instance which belongs to this wrapper. It contains the log of the loadtests ran
	 * 
	 * @return the stream instance - must not be closed
	 */
	public InputStream getLogStream() {
		return logStream;
	}

	/**
	 * Checks whether a load test is running at the moment.
	 * 
	 * @return <tt>true</tt> if running, <tt>false</tt> if not
	 */
	public boolean isLoadTestRunning() {
		return (jmeterProcess != null);
	}

	/**
	 * Waits for the current loadtest to finish. If no loadtest is running, the method returns immediately.
	 * 
	 * @throws InterruptedException if the Thread is interrupted
	 */
	public synchronized void waitForLoadTestFinish() throws InterruptedException {

		while (jmeterProcess != null) {
			this.wait();
		}
	}
}
