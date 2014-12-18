package org.lpe.common.loadgenerator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Measurement data of the load generator.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
public class LGMeasurementData {
	private Map<String, List<TimeSpan>> transactionTimes;

	/**
	 * @return the transactionTimes
	 */
	public Map<String, List<TimeSpan>> getTransactionTimes() {
		if (transactionTimes == null) {
			transactionTimes = new HashMap<String, List<TimeSpan>>();
		}
		return transactionTimes;
	}

	/**
	 * @param transactionTimes
	 *            the transactionTimes to set
	 */
	public void setTransactionTimes(Map<String, List<TimeSpan>> transactionTimes) {
		this.transactionTimes = transactionTimes;
	}

	/**
	 * Adds a time information of a transaction.
	 * 
	 * @param transactionName
	 *            name of the transaction for which the timing information
	 *            should be added
	 * @param startTimestamp
	 *            timestamp of the transaction begin
	 * @param stopTimestamp
	 *            timestamp of the transaction end
	 */
	@JsonIgnore
	public void addTransactionTime(String transactionName, long startTimestamp, long stopTimestamp) {
		List<TimeSpan> timesList = null;
		if (getTransactionTimes().containsKey(transactionName)) {
			timesList = getTransactionTimes().get(transactionName);
		} else {
			timesList = new ArrayList<TimeSpan>();
			getTransactionTimes().put(transactionName, timesList);
		}
		timesList.add(new TimeSpan(startTimestamp, stopTimestamp));
	}

	/**
	 * Returns a list of time spans for the given transaction.
	 * 
	 * @param transactionName
	 *            name of the transaction for which the time spans shell be
	 *            returned.
	 * @return a list of time spans
	 */
	@JsonIgnore
	public List<TimeSpan> getTimesForTransaction(String transactionName) {
		if (getTransactionTimes().containsKey(transactionName)) {
			return getTransactionTimes().get(transactionName);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return a set of transaction names
	 */
	@JsonIgnore
	public Set<String> getTransactionNames() {
		return getTransactionTimes().keySet();
	}
}
