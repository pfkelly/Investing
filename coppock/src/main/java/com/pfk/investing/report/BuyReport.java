package com.pfk.investing.report;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeMap;

import yahoofinance.histquotes.HistoricalQuote;

public interface BuyReport {
	public TreeMap<Calendar, HistoricalQuote> getBuyDatesAndQuotes();
}
