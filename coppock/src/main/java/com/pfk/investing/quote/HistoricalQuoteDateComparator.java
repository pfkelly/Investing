package com.pfk.investing.quote;

import java.util.Calendar;
import java.util.Comparator;

import yahoofinance.histquotes.HistoricalQuote;

public class HistoricalQuoteDateComparator implements Comparator<HistoricalQuote> {

	public int compare(HistoricalQuote quote1, HistoricalQuote quote2) {
		return quote1.getDate().compareTo(quote2.getDate());
	}

}
