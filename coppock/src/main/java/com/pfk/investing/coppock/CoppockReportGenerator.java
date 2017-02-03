package com.pfk.investing.coppock;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pfk.investing.quote.HistoricalQuoteFilter;
import com.pfk.investing.report.BuyReport;


/**
 * Hello world!
 *
 */
public class CoppockReportGenerator 
{
	private static final String CLASS_NAME = CoppockReportGenerator.class.getName();
	
	public static void main( String[] args )
    {
    	Logger logger = LogManager.getLogger(CLASS_NAME + ".main");
        try {
        	Calendar cal1 = new GregorianCalendar();
        	logger.debug(cal1.getTimeZone());
        	TimeZone.setDefault(TimeZone.getTimeZone(YahooFinance.TIMEZONE));
        	logger.debug(cal1.getTimeZone());
        	Calendar cal2 = new GregorianCalendar();
        	logger.debug("cal2 time zone: " + cal2.getTimeZone());
        	
        	Calendar fromDate = new GregorianCalendar(1996, 9, 1);
        	Calendar toDate = new GregorianCalendar();
        	
//            Stock stock = YahooFinance.get("EURUSD", false);
            Stock stock = YahooFinance.get("^GSPC", false);
            stock.print();
 
            List<HistoricalQuote> history = stock.getHistory(fromDate, toDate, Interval.DAILY );
            int count = 0;
            for (HistoricalQuote quote : history) {
            	count++;
            	logger.info("" + count + ") " + quote);
            }
             
            HistoricalQuoteFilter historicalQuoteFilter = new HistoricalQuoteFilter(history, 31);
            SortedMap<Calendar, HistoricalQuote> filteredQuotesByDate = historicalQuoteFilter.createMonthlyQuoteMap();
            logger.info("Filtered Historical Quotes...");
            Collection<HistoricalQuote> filteredQuotes = filteredQuotesByDate.values();
            for (HistoricalQuote quote : filteredQuotes) {
            	logger.info(quote.getDate().getTime() + "\t" + quote.getClose() + "\tall info: " + quote );
            }

            Coppock coppock = new Coppock();
            BuyReport buyReport = coppock.createBuyReport(filteredQuotesByDate);
            logger.info(buyReport);
        } catch (IOException e) {
        	e.printStackTrace();
        }

    }
    
    
    
}
