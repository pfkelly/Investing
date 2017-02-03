package com.pfk.investing.report;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import yahoofinance.histquotes.HistoricalQuote;

public class CoppockBuyReport implements BuyReport {

	private TreeMap<Calendar, HistoricalQuote> buyDatesAndQuotes;

	public TreeMap<Calendar, HistoricalQuote> getBuyDatesAndQuotes() {
		return buyDatesAndQuotes;
	}

	public void setBuyDatesAndQuotes(TreeMap<Calendar, HistoricalQuote> buyDatesAndQuotes) {
		this.buyDatesAndQuotes = buyDatesAndQuotes;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("CoppockBuyReport\n[\n" );
		if (buyDatesAndQuotes == null) {
			sb.append("NO BUY DATES");
		} else {
			Collection<HistoricalQuote> quotes = buyDatesAndQuotes.values();
			for(HistoricalQuote quote : quotes) {
				sb.append(quote).append("\n");
			}
			sb.append("]");
		}
		return sb.toString();
	}

	
}
