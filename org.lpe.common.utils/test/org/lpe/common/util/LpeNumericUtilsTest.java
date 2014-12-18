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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.lpe.common.util.LpeNumericUtils.average;
import static org.lpe.common.util.LpeNumericUtils.dFormat;
import static org.lpe.common.util.LpeNumericUtils.filterOutliersUsingIQR;
import static org.lpe.common.util.LpeNumericUtils.formatTimeMillis;
import static org.lpe.common.util.LpeNumericUtils.getConfidenceIntervalWidth;
import static org.lpe.common.util.LpeNumericUtils.markOutliersUsingIQR;
import static org.lpe.common.util.LpeNumericUtils.stdDev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

/**
 * Tests {@link LpeNumericUtils}.
 * 
 * @author Henning Schulz
 * 
 */
public class LpeNumericUtilsTest {

	/**
	 * Tests the {@link LpeNumericUtils#dFormat(double, int) dFormat(double,
	 * int)}.
	 */
	@Test
	public void testDFormat() {
		assertEquals("1.2345", dFormat(1.2345, 4));
		assertEquals("1.2346", dFormat(1.23456789, 4));
		assertEquals("0.0", dFormat(0.0, 1));
		assertEquals("0.00000", dFormat(0.0, 5));
		assertEquals("-1.234", dFormat(-1.234, 3));
		assertEquals("0", dFormat(0.0, 0));
	}

	/**
	 * Tests the {@link LpeNumericUtils#formatTimeMillis(long)
	 * formatTimeMillis(long)}.
	 */
	@Test
	public void testFormatTimeMillis() {
		assertEquals("0 h - 0 min - 0 s - 42 ms", formatTimeMillis(42));
		assertEquals("5 h - 53 min - 20 s - 5 ms", formatTimeMillis(21200005));
		assertEquals("0 h - 0 min - 0 s - 0 ms", formatTimeMillis(0));
	}

	/**
	 * Tests the {@link LpeNumericUtils#average(java.util.Collection)
	 * average(Collection)} and {@link LpeNumericUtils#average(double[])
	 * average(double[])}.
	 */
	@Test
	public void testAverage() {
		List<Integer> ints = new ArrayList<Integer>();
		for (int i = 1; i <= 50; i++) {
			ints.add(i);
		}

		assertEquals(25.5, average(ints), 0.1);

		ints = new ArrayList<Integer>();
		for (int i = 1; i <= 50; i++) {
			ints.add(-i);
		}

		assertEquals(-25.5, average(ints), 0.1);

		assertEquals(-3.4285714286, average(new double[] { 5, 3, 7, -1, 4, -42, 0 }), 0.0000000001);

		assertEquals(0, average(new double[] {}), 0.1);

		boolean catched = false;

		try {
			average((double[]) null);
			average((List<Integer>) null);
		} catch (IllegalArgumentException e) {
			catched = true;
		}

		assertTrue("Calculating average on null objects should be prevented.", catched);
	}

	/**
	 * Tests the {@link LpeNumericUtils#stdDev(java.util.Collection)
	 * stdDev(Collection)}, {@link LpeNumericUtils#stdDev(Double[])
	 * stdDev(Double[])} and {@link LpeNumericUtils#stdDev(double[])
	 * stdDev(double[])}.
	 */
	@Test
	public void testStdDef() {
		assertEquals(1.41421, stdDev(new double[] { 1, 2, 3, 4, 5 }), 0.00001);
		assertEquals(1.41421, stdDev(new double[] { -1, -2, -3, -4, -5 }), 0.00001);
		assertEquals(3.16228, stdDev(new double[] { 1, -2, -3, 4, -5 }), 0.00001);
		assertEquals(0, stdDev(new double[] { 42 }), 0.00001);

		assertEquals(1.41421, stdDev(new Double[] { 1.0, 2.0, 3.0, 4.0, 5.0 }), 0.00001);
		assertEquals(1.41421, stdDev(new Double[] { -1.0, -2.0, -3.0, -4.0, -5.0 }), 0.00001);
		assertEquals(3.16228, stdDev(new Double[] { 1.0, -2.0, -3.0, 4.0, -5.0 }), 0.00001);
		assertEquals(0, stdDev(new Double[] { 42.0 }), 0.00001);

		List<Double> doubles = new ArrayList<Double>();
		doubles.add(1.0);
		doubles.add(-2.0);
		doubles.add(-3.0);
		doubles.add(4.0);
		doubles.add(-5.0);
		assertEquals(3.16228, stdDev(doubles), 0.00001);

		boolean catched = false;

		try {
			stdDev(new double[] {});
			stdDev(new Double[] {});
			stdDev(new ArrayList<Double>());
		} catch (IllegalArgumentException e) {
			catched = true;
		}

		assertTrue("Calculating standard deviation on an empty set should be prevented.", catched);

		catched = false;

		try {
			stdDev((double[]) null);
			stdDev((Double[]) null);
			stdDev((List<Double>) null);
		} catch (IllegalArgumentException e) {
			catched = true;
		}

		assertTrue("Calculating standard deviation on null objects should be prevented.", catched);
	}

	/**
	 * Tests the
	 * {@link LpeNumericUtils#getConfidenceIntervalWidth(SummaryStatistics, double)
	 * getConfidenceIntervalWidth(SummaryStatistics, double)}.
	 */
	@Test
	public void testConfidenceIntervalWidth() {
		assertEquals(357.6787 * 2, getConfidenceIntervalWidth(10, 500, 0.05), 0.01);
	}

	/**
	 * Tests the {@link LpeNumericUtils#filterOutliersUsingIQR(List)
	 * filterOutliersUsingIQR(List)} and
	 * {@link LpeNumericUtils#markOutliersUsingIQR(List)
	 * markOutliersUsingIQR(List)}.
	 */
	@Test
	public void testOutliers() {
		List<Double> doubles = new ArrayList<Double>();
		doubles.add(1.0);
		doubles.add(3.0);
		doubles.add(4.0);
		doubles.add(2.0);
		doubles.add(1.0);
		doubles.add(3.0);
		doubles.add(4.0);
		doubles.add(2.0);
		doubles.add(1.0);
		doubles.add(3.0);
		doubles.add(4.0);
		doubles.add(2.0);
		doubles.add(1.0);
		doubles.add(3.0);
		doubles.add(4.0);
		doubles.add(2.0);
		doubles.add(1000.0);

		List<Double> ref = new ArrayList<Double>();
		ref.add(1.0);
		ref.add(3.0);
		ref.add(4.0);
		ref.add(2.0);
		ref.add(1.0);
		ref.add(3.0);
		ref.add(4.0);
		ref.add(2.0);
		ref.add(1.0);
		ref.add(3.0);
		ref.add(4.0);
		ref.add(2.0);
		ref.add(1.0);
		ref.add(3.0);
		ref.add(4.0);
		ref.add(2.0);

		assertEquals(ref, filterOutliersUsingIQR(doubles));

		Map<Double, Boolean> outlierMap = new HashMap<Double, Boolean>();
		outlierMap.put(1.0, false);
		outlierMap.put(3.0, false);
		outlierMap.put(4.0, false);
		outlierMap.put(2.0, false);
		outlierMap.put(1000.0, true);

		assertEquals(outlierMap, markOutliersUsingIQR(doubles));
	}

	@Test
	public void testListConversion() {

		List<Float> fList = new ArrayList<Float>();
		fList.add(1.0f);
		fList.add(2.2f);
		fList.add(3.4f);
		fList.add(4.6f);
		fList.add(5.0f);
		
		List<Float> fListExpected = new ArrayList<Float>();
		fListExpected.add(1.0f);
		fListExpected.add(2.0f);
		fListExpected.add(3.0f);
		fListExpected.add(4.0f);
		fListExpected.add(5.0f);

		List<Double> dList = new ArrayList<Double>();
		dList.add(1.0);
		dList.add(2.2);
		dList.add(3.4);
		dList.add(4.6);
		dList.add(5.0);
		
		List<Double> dListExpected = new ArrayList<Double>();
		dListExpected.add(1.0);
		dListExpected.add(2.0);
		dListExpected.add(3.0);
		dListExpected.add(4.0);
		dListExpected.add(5.0);

		List<Integer> iList = new ArrayList<Integer>();
		iList.add(1);
		iList.add(2);
		iList.add(3);
		iList.add(4);
		iList.add(5);

		List<Long> lList = new ArrayList<Long>();
		lList.add(1L);
		lList.add(2L);
		lList.add(3L);
		lList.add(4L);
		lList.add(5L);
		
		List<Short> sList = new ArrayList<Short>();
		sList.add((short)1);
		sList.add((short)2);
		sList.add((short)3);
		sList.add((short)4);
		sList.add((short)5);
		
		Assert.assertEquals(iList, LpeNumericUtils.convertList(fList, Integer.class));
		Assert.assertEquals(lList, LpeNumericUtils.convertList(dList, Long.class));
		Assert.assertEquals(dListExpected, LpeNumericUtils.convertList(sList, Double.class));
		Assert.assertEquals(sList, LpeNumericUtils.convertList(iList, Short.class));
		Assert.assertEquals(fListExpected, LpeNumericUtils.convertList(iList, Float.class));

	}

}
