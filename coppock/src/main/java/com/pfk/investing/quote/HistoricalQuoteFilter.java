/**
 * 
 */
package com.pfk.investing.quote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Provides filtering for List<HistoricalQuote>.
 * @author PFK
 *
 */
public class HistoricalQuoteFilter {
	
	private List<HistoricalQuote> quotes;
	private Integer dayOfMonth;
	private SortedMap<Calendar, HistoricalQuote> filteredQuotes = new TreeMap<Calendar, HistoricalQuote>();
	private TreeMap<Calendar, HistoricalQuote> quotesByDate = new TreeMap<Calendar, HistoricalQuote>();	
	private Calendar currentDate;
	private static final String CLASS_NAME = HistoricalQuoteFilter.class.getName();
	
	/**
	 * @param quotes
	 * @param dayOfMonth
	 */
	public HistoricalQuoteFilter(List<HistoricalQuote> quotes, Integer dayOfMonth) {
		this.quotes = quotes;
		this.dayOfMonth = dayOfMonth;
	}



	/**
	 * Return subset of <td>quotes</td>, returning the <td>HistoricalQuote</td>
	 * on the <td>dayOfMonth</td>.
	 * If there is no quote on the provided dayOfMonth, include the 
	 * <i>nearest</i> Historical Quote that is before the dayOfMonth.
	 * @param quotes
	 * @param dayOfMonth
	 * @return
	 */
	public SortedMap<Calendar, HistoricalQuote> createMonthlyQuoteMap() {
		Logger logger = LogManager.getLogger(CLASS_NAME +  ".createMonthlyQuoteMap");
		for (HistoricalQuote historicalQuote : quotes ) {
			quotesByDate.put(historicalQuote.getDate(), historicalQuote);
		}
		getFirstDate();
		Calendar mostRecentDate = quotesByDate.lastKey();
		SimpleDateFormat df = new SimpleDateFormat("MM/DD/YYYY");
		
		logger.info("Starting filter with currentTime: " + df.format(currentDate.getTime()) + ", mostRecentTime: " + mostRecentDate.getTime());
		while (currentDate.before(mostRecentDate)) {
			int attempts = 0;
			while (!quotesByDate.containsKey(currentDate) && attempts < 10) {
				logger.info("Nothing found for: " + df.format(currentDate.getTime()));
				adjustCurrentDate();
				attempts++;
			}
			addQuoteForCurrentDate();
			calculateNextDate();
		}
		return filteredQuotes;
	}



	private void adjustCurrentDate() {
		Logger logger = LogManager.getLogger(CLASS_NAME + ".adjustCurrentDate");
		SimpleDateFormat df = new SimpleDateFormat("MM/DD/YYYY");
		logger.debug("entered with: " + df.format(currentDate.getTime()));
		int currentDayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);
		if (currentDayOfMonth > dayOfMonth) {
			currentDate.set(Calendar.DAY_OF_MONTH, currentDayOfMonth+1);
		} else if (currentDayOfMonth == 1){
			currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth+1);
		} else {
			currentDate.set(Calendar.DAY_OF_MONTH, currentDayOfMonth-1);			
		}
		logger.debug("exiting with: " + df.format(currentDate.getTime()));
	}



	private void getFirstDate() {
		Calendar firstDate = quotesByDate.firstKey();
		if (firstDate.get(Calendar.DAY_OF_MONTH) < dayOfMonth){
			firstDate.add(Calendar.MONTH, -1); 
		}
		currentDate = firstDate;
		calculateNextDate();
	}
	
	private void calculateNextDate() {
		currentDate.setLenient(false);
		currentDate.add(Calendar.MONTH, 1);

		if (setDayOfMonth(0)) {
			
		} else if(setDayOfMonth(-1)) {
			
		} else if (setDayOfMonth(-2)) {
			
		} else {
			setDayOfMonth(-3);
		}
	}

	private boolean setDayOfMonth(int offset) {
		Logger logger = LogManager.getLogger(CLASS_NAME + ".setDayOfMonth");
		try {
			currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth + offset);
			currentDate.getTime();
			logger.debug("currentDate: " + currentDate.getTime());
			return true;
		} catch (IllegalArgumentException e) {
						
			return false;
		}
		
	}

	private void addQuoteForCurrentDate() {
		Logger logger = LogManager.getLogger(CLASS_NAME + ".addQuoteForCurrentDate");
		HistoricalQuote historicalQuoteForCurrentDate = quotesByDate.get(currentDate);
		SimpleDateFormat df = new SimpleDateFormat("MM/DD/YYYY");
		if (historicalQuoteForCurrentDate != null) {
			logger.info("adding: " + df.format(historicalQuoteForCurrentDate.getDate().getTime()) + "\t" + historicalQuoteForCurrentDate.getClose());
			filteredQuotes.put(historicalQuoteForCurrentDate.getDate(), historicalQuoteForCurrentDate);
		} else {
			logger.info("Did not add quote for: " + df.format(currentDate.getTime()));
		}
		
	}

}
