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
/**
 * 
 */
package org.lpe.common.util.stats;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.lpe.common.util.NumericPair;
import org.lpe.common.util.NumericPairList;

/**
 * The implementation of Inner Quartile Range outlier detector.
 * 
 * @author Roozbeh Farahbod
 * 
 */
public class IQROutlierDetector extends AbstractOutlierDetector {

	private static final int UPPER_QUARTILE = 75;

	private static final int LOWER_QUARTILE = 25;

	public static final double DEFAULT_IQR_FACTOR = 1.5;

	protected double iqrFactor = DEFAULT_IQR_FACTOR;

	/**
	 * Creates a new IQR outlier detector with the default value for the IQR
	 * factor (i.e., {@value #DEFAULT_IQR_FACTOR}).
	 * 
	 * @see #DEFAULT_IQR_FACTOR
	 */
	public IQROutlierDetector() {
	}

	/**
	 * Creates a new IQR outlier detector with the given value for the IQR
	 * factor.
	 * 
	 * @param iqrFactor
	 *            the IQR factor
	 */
	public IQROutlierDetector(double iqrFactor) {
		this.iqrFactor = iqrFactor;
	}

	@Override
	public List<Double> filterOutliers(double[] values) {
		DescriptiveStatistics ds = new DescriptiveStatistics(values);

		double firstQuartile = ds.getPercentile(LOWER_QUARTILE);
		double thirdQuartile = ds.getPercentile(UPPER_QUARTILE);
		double iqr = thirdQuartile - firstQuartile;
		double lowerRange = firstQuartile - iqrFactor * iqr;
		double higherRange = thirdQuartile + iqrFactor * iqr;

		List<Double> results = new ArrayList<Double>();

		for (Double value : values) {
			if (value <= higherRange && value >= lowerRange) {
				results.add(value);
			}
		}

		return results;
	}

	/**
	 * Filters a list of numeric pairs by the key of the pair.
	 * 
	 * @param list
	 *            list to filter
	 * @param <T>
	 *            type
	 * @return a list of numeric pairs without outliers in the keys
	 */
	public <T extends Number> NumericPairList<Double, T> filterOutliersInKeys(NumericPairList<Double, T> list) {
		DescriptiveStatistics ds = new DescriptiveStatistics(list.getValueArrayAsDouble());

		double firstQuartile = ds.getPercentile(LOWER_QUARTILE);
		double thirdQuartile = ds.getPercentile(UPPER_QUARTILE);
		double iqr = thirdQuartile - firstQuartile;
		double lowerRange = firstQuartile - iqrFactor * iqr;
		double higherRange = thirdQuartile + iqrFactor * iqr;

		NumericPairList<Double, T> result = new NumericPairList<Double, T>();

		for (NumericPair<Double, T> pair : list) {
			if (pair.getKey() <= higherRange && pair.getKey() >= lowerRange) {
				result.add(new NumericPair<Double, T>(pair.getKey(), pair.getValue()));
			}
		}

		return result;
	}

	/**
	 * Filters a list of numeric pairs by the value of the pair.
	 * 
	 * @param list
	 *            list to filter
	 * @param <T>
	 *            type
	 * @return a list of numeric pairs without outliers in the values
	 */
	public <T extends Number> NumericPairList<T, Double> filterOutliersInValues(NumericPairList<T, Double> list) {
		DescriptiveStatistics ds = new DescriptiveStatistics(list.getValueArrayAsDouble());

		double firstQuartile = ds.getPercentile(LOWER_QUARTILE);
		double thirdQuartile = ds.getPercentile(UPPER_QUARTILE);
		double iqr = thirdQuartile - firstQuartile;
		double lowerRange = firstQuartile - iqrFactor * iqr;
		double higherRange = thirdQuartile + iqrFactor * iqr;

		NumericPairList<T, Double> result = new NumericPairList<T, Double>();

		for (NumericPair<T, Double> pair : list) {
			if (pair.getValue() <= higherRange && pair.getValue() >= lowerRange) {
				result.add(new NumericPair<T, Double>(pair.getKey(), pair.getValue()));
			}
		}

		return result;
	}

}
