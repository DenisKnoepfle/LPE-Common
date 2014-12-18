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
package org.lpe.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.lpe.common.util.dummy.DummyService;
import org.lpe.common.util.system.LpeSystemUtils;
import org.lpe.common.util.web.LpeWebUtils;
import org.lpe.common.util.web.WebServer;

public class LpeWebUtilsTest {

	static AtomicInteger done = new AtomicInteger();

	public static void startServer(int poolsize) {
		List<String> packages = new ArrayList<String>();
		packages.add("org.lpe.common.util.dummy");
		WebServer.getInstance().start(8124, "", packages, poolsize, poolsize);
	}

	@Test
	public void testSetWorkers() {
		DummyService.maxQueue.set(0);
		DummyService.queue.set(0);
		done.set(0);
		startServer(5);

		for (int i = 0; i < 50; i++) {
			LpeSystemUtils.submitTask(new Runnable() {

				@Override
				public void run() {
					LpeWebUtils.getWebClient().resource("http://localhost:8124/dummy/service")
							.accept(MediaType.APPLICATION_JSON).get(String.class);
					done.incrementAndGet();
				}
			});
		}

		while (done.get() < 50) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		System.out.println(DummyService.maxQueue);
		Assert.assertTrue(DummyService.maxQueue.get() <= 5);

		DummyService.maxQueue.set(0);
		DummyService.queue.set(0);
		done.set(0);
		startServer(10);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			throw new RuntimeException(e1);
		}

		for (int i = 0; i < 50; i++) {
			LpeSystemUtils.submitTask(new Runnable() {

				@Override
				public void run() {
					LpeWebUtils.getWebClient().resource("http://localhost:8124/dummy/service")
							.accept(MediaType.APPLICATION_JSON).get(String.class);
					done.incrementAndGet();
				}
			});
		}

		while (done.get() < 50) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		System.out.println(DummyService.maxQueue);
		Assert.assertTrue(DummyService.maxQueue.get() >= 5);
		Assert.assertTrue(DummyService.maxQueue.get() <= 10);

	}

	@AfterClass
	public static void stopServer() {
		WebServer.getInstance().shutdown();
	}
}
