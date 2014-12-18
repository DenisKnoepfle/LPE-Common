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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * List of numeric pairs.
 * 
 * @author Alexander Wert
 * 
 * @param <T>
 *            key type
 * @param <S>
 *            value type
 */
public class NumericPairList<T extends Number, S extends Number> implements Iterable<NumericPair<T, S>> {
	private final List<NumericPair<T, S>> pairs;

	/**
	 * Construcotr.
	 */
	public NumericPairList() {
		pairs = new ArrayList<NumericPair<T, S>>();
	}

	/**
	 * Adds a pair.
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            value
	 */
	public void add(T key, S value) {
		getPairs().add(new NumericPair<T, S>(key, value));
	}

	/**
	 * 
	 * @param pair
	 *            pair to add
	 */
	public void add(NumericPair<T, S> pair) {
		getPairs().add(pair);
	}

	/**
	 * 
	 * @return list of keys
	 */
	public List<T> getKeyList() {
		List<T> keys = new ArrayList<T>();
		for (NumericPair<T, S> np : getPairs()) {
			keys.add(np.getKey());
		}
		return keys;
	}

	/**
	 * 
	 * @return list of values
	 */
	public List<S> getValueList() {
		List<S> values = new ArrayList<S>();
		for (NumericPair<T, S> np : getPairs()) {
			values.add(np.getValue());
		}
		return values;
	}

	/**
	 * 
	 * @return list of keys as numbers
	 */
	public List<Number> getKeyListAsNumbers() {
		List<Number> keys = new ArrayList<Number>();
		for (NumericPair<T, S> np : getPairs()) {
			keys.add(np.getKey());
		}
		return keys;
	}

	/**
	 * 
	 * @return list of values as numbers
	 */
	public List<Number> getValueListAsNumbers() {
		List<Number> values = new ArrayList<Number>();
		for (NumericPair<T, S> np : getPairs()) {
			values.add(np.getValue());
		}
		return values;
	}

	/**
	 * @param scaleFactor
	 *            scale factor
	 * @return list of keys as number multiplied with the scale factor
	 */
	public List<Number> getKeyListAsNumbers(double scaleFactor) {
		List<Number> keys = new ArrayList<Number>();
		for (NumericPair<T, S> np : getPairs()) {
			keys.add(np.getKey().doubleValue() * scaleFactor);
		}
		return keys;
	}

	/**
	 * @param scaleFactor
	 *            scale factor
	 * @return list of values as number multiplied with the scale factor
	 */
	public List<Number> getValueListAsNumbers(double scaleFactor) {
		List<Number> values = new ArrayList<Number>();
		for (NumericPair<T, S> np : getPairs()) {
			values.add(np.getValue().doubleValue() * scaleFactor);
		}
		return values;
	}

	/**
	 * 
	 * @return list of keys as double
	 */
	public List<Double> getKeyListAsDouble() {
		List<Double> keys = new ArrayList<Double>();
		for (NumericPair<T, S> np : getPairs()) {
			keys.add(np.getKey().doubleValue());
		}
		return keys;
	}

	/**
	 * 
	 * @return list of values as double
	 */
	public List<Double> getValueListAsDouble() {
		List<Double> values = new ArrayList<Double>();
		for (NumericPair<T, S> np : getPairs()) {
			values.add(np.getValue().doubleValue());
		}
		return values;
	}

	/**
	 * 
	 * @return array of keys as double
	 */
	public double[] getKeyArrayAsDouble() {
		double[] keys = new double[getPairs().size()];
		int i = 0;
		for (NumericPair<T, S> np : getPairs()) {
			keys[i] = np.getKey().doubleValue();
			i++;
		}
		return keys;
	}

	/**
	 * 
	 * @return array of values as double
	 */
	public double[] getValueArrayAsDouble() {
		double[] values = new double[getPairs().size()];
		int i = 0;
		for (NumericPair<T, S> np : getPairs()) {
			values[i] = np.getValue().doubleValue();
			i++;
		}
		return values;
	}

	/**
	 * @param scaleFactor
	 *            scale factor
	 * @return array of keys as double multiplied with the scale factor
	 */
	public double[] getKeyArrayAsDouble(double scaleFactor) {
		double[] keys = new double[getPairs().size()];
		int i = 0;
		for (NumericPair<T, S> np : getPairs()) {
			keys[i] = np.getKey().doubleValue() * scaleFactor;
			i++;
		}
		return keys;
	}

	/**
	 * @param scaleFactor
	 *            scale factor
	 * @return array of values as double multiplied with the scale factor
	 */
	public double[] getValueArrayAsDouble(double scaleFactor) {
		double[] values = new double[getPairs().size()];
		int i = 0;
		for (NumericPair<T, S> np : getPairs()) {
			values[i] = np.getValue().doubleValue() * scaleFactor;
			i++;
		}
		return values;
	}

	/**
	 * @param scaleFactor
	 *            scale factor
	 * @return list of keys as double multiplied with the scale factor
	 */
	public List<Double> getKeyListAsDouble(double scaleFactor) {
		List<Double> keys = new ArrayList<Double>();
		for (NumericPair<T, S> np : getPairs()) {
			keys.add(np.getKey().doubleValue() * scaleFactor);
		}
		return keys;
	}

	/**
	 * @param scaleFactor
	 *            scale factor
	 * @return list of values as double multiplied with the scale factor
	 */
	public List<Double> getValueListAsDouble(double scaleFactor) {
		List<Double> values = new ArrayList<Double>();
		for (NumericPair<T, S> np : getPairs()) {
			values.add(np.getValue().doubleValue() * scaleFactor);
		}
		return values;
	}

	/**
	 * 
	 * @return min of the keys
	 */
	public T getKeyMin() {
		T min = null;
		boolean first = true;
		for (NumericPair<T, S> np : getPairs()) {
			if (first) {
				min = np.getKey();
				first = false;
			} else if (np.getKey().doubleValue() < min.doubleValue()) {
				min = np.getKey();
			}
		}
		return min;
	}

	/**
	 * 
	 * @return max of the keys
	 */
	public T getKeyMax() {
		T max = null;
		boolean first = true;
		for (NumericPair<T, S> np : getPairs()) {
			if (first) {
				max = np.getKey();
				first = false;
			} else if (np.getKey().doubleValue() > max.doubleValue()) {
				max = np.getKey();
			}
		}
		return max;
	}

	/**
	 * 
	 * @return min of the values
	 */
	public S getValueMin() {
		S min = null;
		boolean first = true;
		for (NumericPair<T, S> np : getPairs()) {
			if (first) {
				min = np.getValue();
				first = false;
			} else if (np.getValue().doubleValue() < min.doubleValue()) {
				min = np.getValue();
			}
		}
		return min;
	}

	/**
	 * 
	 * @return max of the values
	 */
	public S getValueMax() {
		S max = null;
		boolean first = true;
		for (NumericPair<T, S> np : getPairs()) {
			if (first) {
				max = np.getValue();
				first = false;
			} else if (np.getValue().doubleValue() > max.doubleValue()) {
				max = np.getValue();
			}
		}
		return max;
	}

	@Override
	public Iterator<NumericPair<T, S>> iterator() {
		return getPairs().iterator();
	}

	/**
	 * 
	 * @return size of the list
	 */
	public int size() {
		return getPairs().size();
	}

	/**
	 * 
	 * @param index
	 *            index to get
	 * @return element at that index
	 */
	public NumericPair<T, S> get(int index) {
		return getPairs().get(index);
	}

	/**
	 * sorts the list.
	 * 
	 * @return sorted list
	 */
	public List<NumericPair<T, S>> sort() {
		Collections.sort(getPairs());
		return getPairs();
	}

	/**
	 * sorts the list.
	 * 
	 * @return sorted list
	 */
	public List<NumericPair<T, S>> sortByValue() {
		Collections.sort(getPairs(), new Comparator<NumericPair<? extends Number, ? extends Number>>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public int compare(NumericPair<? extends Number, ? extends Number> o1,
					NumericPair<? extends Number, ? extends Number> o2) {

				return ((Comparable) o1.getValue()).compareTo(o2.getValue());
			}
		});
		return getPairs();
	}

	/**
	 * @return the pairs
	 */
	public List<NumericPair<T, S>> getPairs() {
		return pairs;
	}

}
