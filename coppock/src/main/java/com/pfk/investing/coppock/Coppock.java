package com.pfk.investing.coppock;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pfk.investing.report.BuyReport;
import com.pfk.investing.report.CoppockBuyReport;

import yahoofinance.histquotes.HistoricalQuote;

public class Coppock {
	
	private TreeMap<Calendar, BigDecimal > trailing14MonthROC = new TreeMap<Calendar, BigDecimal>();
	private TreeMap<Calendar, BigDecimal> trailing11MonthROC = new TreeMap<Calendar, BigDecimal>();
	private TreeMap<Calendar, BigDecimal> weightedMovingAverages = new TreeMap<Calendar, BigDecimal>();
	
	private static final String CLASS_NAME = Coppock.class.getName();
	
	private static final BigDecimal FIFTY_FIVE = new BigDecimal(55);
	private static final BigDecimal TEN = new BigDecimal(10);
	private static final BigDecimal NINE = new BigDecimal(9);
	private static final BigDecimal EIGHT = new BigDecimal(8);
	private static final BigDecimal SEVEN = new BigDecimal(7);
	private static final BigDecimal SIX = new BigDecimal(6);
	private static final BigDecimal FIVE = new BigDecimal(5);
	private static final BigDecimal FOUR = new BigDecimal(4);
	private static final BigDecimal THREE = new BigDecimal(3);
	private static final BigDecimal TWO = new BigDecimal(2);
	private static final BigDecimal ONE = new BigDecimal(1);
	private static final BigDecimal ZERO = new BigDecimal(0);
	
	private static final MathContext mathContext_9 = new MathContext(9, RoundingMode.HALF_DOWN);
	private static final MathContext mathContext_6 = new MathContext(6, RoundingMode.HALF_DOWN);
	
	public Coppock(){
		
	}
	
	public BuyReport createBuyReport(SortedMap<Calendar, HistoricalQuote> monthlyQuotes) {
		Logger logger = LogManager.getLogger(CLASS_NAME + ".createBuyReport");
		logger.entry();
		validateMonthlyQuotes(monthlyQuotes);
		logger.info("monthlyQuotes are valid");
		
		computeRateOfChanges(monthlyQuotes.values());
		computeWeightedMovingAverage();
		
		logger.info("Weighted Moving Averages have been computed");
		Calendar[] weightedMovingAverageDates = weightedMovingAverages.keySet().toArray(new Calendar[weightedMovingAverages.size()]) ;
		Set<Entry<Calendar, BigDecimal>> weightedAverages = weightedMovingAverages.entrySet();
		BigDecimal enterExitMarketLevel = ZERO;
		CoppockBuyReport buyReport = new CoppockBuyReport();
		TreeMap<Calendar, HistoricalQuote> buyDatesAndQuotes = new TreeMap<Calendar, HistoricalQuote>();
		boolean outOfTheMarket = false;
		SimpleDateFormat df = new SimpleDateFormat("MM/DD/YYYY");
		for (Entry<Calendar, BigDecimal> entry : weightedAverages) {
			Calendar weightedMovingAverageDate = entry.getKey();
			BigDecimal weightedMovingAverageAmount = entry.getValue();
			if (logger.isInfoEnabled()) {
				logger.info("processing " + df.format(weightedMovingAverageDate.getTime()) + ", weighted average: " + weightedMovingAverageAmount 
						+ ", enterExitMarketLevel: " + enterExitMarketLevel + ", outOfTheMarket: " + outOfTheMarket + 
						", close: " + monthlyQuotes.get(weightedMovingAverageDate).getClose());
			}
			if (outOfTheMarket) {
				if (weightedMovingAverageAmount.compareTo(enterExitMarketLevel) > 0) {
					enterExitMarketLevel = weightedMovingAverageAmount;
					logger.info("reentering market on " + weightedMovingAverageDate.getTime() + " enterExitMarketLevel: " + enterExitMarketLevel + "\t" + entry );
					buyDatesAndQuotes.put(weightedMovingAverageDate, monthlyQuotes.get(weightedMovingAverageDate));
					outOfTheMarket = false;
				} else {
					enterExitMarketLevel = weightedMovingAverageAmount;
					logger.info("new enterExitMarketLevel: " + enterExitMarketLevel + " on :" + weightedMovingAverageDate.getTime());
				}
			} else if (weightedMovingAverageAmount.compareTo(enterExitMarketLevel) <0){
				enterExitMarketLevel = entry.getValue();
				logger.info("exiting market on " + weightedMovingAverageDate.getTime() + ", reenterMarketLevel: " + enterExitMarketLevel);
				outOfTheMarket = true;
			} else if (weightedMovingAverageAmount.compareTo(ZERO) < 0) {
				enterExitMarketLevel = weightedMovingAverageAmount;
			} else {
				enterExitMarketLevel = ZERO;
			}
		}
		buyReport.setBuyDatesAndQuotes(buyDatesAndQuotes);
		return buyReport;
	}

	private void computeWeightedMovingAverage() {
		Logger logger = LogManager.getLogger(CLASS_NAME + ".computeWeightedMovingAverage");
		logger.entry();
		// TODO Auto-generated method stub
		Set<Calendar> trailing14MonthKeys = trailing14MonthROC.keySet();
		Calendar[] trailing14MonthDates = trailing14MonthKeys.toArray(new Calendar[]{});
		for (int i = 9; i < trailing14MonthDates.length; i++) {
			logger.info("computing values for: " + trailing14MonthDates[i].get(Calendar.YEAR) + trailing14MonthDates[i].getTime().getTime());
			BigDecimal combinedROC0 = trailing11MonthROC.get(trailing14MonthDates[i]).add(trailing14MonthROC.get(trailing14MonthDates[i]));
			BigDecimal combinedROC1 = trailing11MonthROC.get(trailing14MonthDates[i-1]).add(trailing14MonthROC.get(trailing14MonthDates[i-1]));
			BigDecimal combinedROC2 = trailing11MonthROC.get(trailing14MonthDates[i-2]).add(trailing14MonthROC.get(trailing14MonthDates[i-2]));
			BigDecimal combinedROC3 = trailing11MonthROC.get(trailing14MonthDates[i-3]).add(trailing14MonthROC.get(trailing14MonthDates[i-3]));
			BigDecimal combinedROC4 = trailing11MonthROC.get(trailing14MonthDates[i-4]).add(trailing14MonthROC.get(trailing14MonthDates[i-4]));
			BigDecimal combinedROC5 = trailing11MonthROC.get(trailing14MonthDates[i-5]).add(trailing14MonthROC.get(trailing14MonthDates[i-5]));
			BigDecimal combinedROC6 = trailing11MonthROC.get(trailing14MonthDates[i-6]).add(trailing14MonthROC.get(trailing14MonthDates[i-6]));
			BigDecimal combinedROC7 = trailing11MonthROC.get(trailing14MonthDates[i-7]).add(trailing14MonthROC.get(trailing14MonthDates[i-7]));
			BigDecimal combinedROC8 = trailing11MonthROC.get(trailing14MonthDates[i-8]).add(trailing14MonthROC.get(trailing14MonthDates[i-8]));
			BigDecimal combinedROC9 = trailing11MonthROC.get(trailing14MonthDates[i-9]).add(trailing14MonthROC.get(trailing14MonthDates[i-9]));
			
			// Weight the ROC - 10 * to most recent, 9 * to last month, etc.
			BigDecimal weightedROC0 = combinedROC0.multiply(TEN);
			BigDecimal weightedROC1 = combinedROC1.multiply(NINE);
			BigDecimal weightedROC2 = combinedROC2.multiply(EIGHT);
			BigDecimal weightedROC3 = combinedROC3.multiply(SEVEN);
			BigDecimal weightedROC4 = combinedROC4.multiply(SIX);
			BigDecimal weightedROC5 = combinedROC5.multiply(FIVE);
			BigDecimal weightedROC6 = combinedROC6.multiply(FOUR);
			BigDecimal weightedROC7 = combinedROC7.multiply(THREE);
			BigDecimal weightedROC8 = combinedROC8.multiply(TWO);
			
			// add them up and save on the current date
			BigDecimal weightedMovingSum = weightedROC0.add(weightedROC1).add(weightedROC2).add(weightedROC3).add(weightedROC4).
					add(weightedROC5).add(weightedROC6).add(weightedROC7).add(weightedROC8).add(combinedROC9);
			BigDecimal weightedMovingAverage = weightedMovingSum.divide(FIFTY_FIVE, mathContext_9);
			
			logger.info("adding key: " + trailing14MonthDates[i].getTime() + " value: " + weightedMovingAverage.toString());
			weightedMovingAverages.put(trailing14MonthDates[i], weightedMovingAverage);
		}
	}

	private void computeRateOfChanges(Collection<HistoricalQuote> monthlyQuotes) {
		Logger logger = LogManager.getLogger(CLASS_NAME + ".computeRateOfChanges");
		HistoricalQuote[] quotes = monthlyQuotes.toArray(new HistoricalQuote[monthlyQuotes.size()] );
		SimpleDateFormat df = new SimpleDateFormat("MM/DD/YYYY");
		for (int i = 14; i < quotes.length; i++) {
			HistoricalQuote currentQuote = quotes[i];
			HistoricalQuote quoteFrom14MonthsAgo = quotes[i-14];
			HistoricalQuote quoteFrom11MonthsAgo = quotes[i-11];
			BigDecimal closeAmountFromCurrent = currentQuote.getClose().round(mathContext_6); 
			BigDecimal closeAmountFrom14MonthsAgo = quoteFrom14MonthsAgo.getClose().round(mathContext_6);
			BigDecimal closeAmountFrom11MonthsAgo = quoteFrom11MonthsAgo.getClose().round(mathContext_6);
			BigDecimal rateOfChangeLast14Months = closeAmountFromCurrent.divide(closeAmountFrom14MonthsAgo, mathContext_9).subtract(ONE);
			BigDecimal rateOfChangeLast11Months = closeAmountFromCurrent.divide(closeAmountFrom11MonthsAgo, mathContext_9).subtract(ONE);
			trailing14MonthROC.put(currentQuote.getDate(), rateOfChangeLast14Months );
			trailing11MonthROC.put(currentQuote.getDate(), rateOfChangeLast11Months );
			logger.info("currentQuote: " + df.format(currentQuote.getDate().getTime()) + ", close: " + closeAmountFromCurrent);
			logger.info("quoteFrom11MonthsAgo: " + df.format(quoteFrom11MonthsAgo.getDate().getTime()) + ", close: " + closeAmountFrom11MonthsAgo);
			logger.info("quoteFrom14MonthsAgo: " + df.format(quoteFrom14MonthsAgo.getDate().getTime()) + ", close: " + closeAmountFrom14MonthsAgo);
			logger.info(df.format(currentQuote.getDate().getTime()) + "\t11 Month ROC: " + rateOfChangeLast11Months + "\t14 Month ROC: " + rateOfChangeLast14Months);
		}
		
	}

	private void validateMonthlyQuotes(SortedMap<Calendar, HistoricalQuote> monthlyQuotes) throws IllegalArgumentException {
		
		if (monthlyQuotes.size() < 25) {
			throw new IllegalArgumentException("Require at least 25 months of data");
		}
		Iterator<HistoricalQuote> quoteIter = monthlyQuotes.values().iterator();
		HistoricalQuote quoteA = quoteIter.next();
		while(quoteIter.hasNext()) {
			HistoricalQuote quoteB = quoteIter.next();
			int quoteAMonth = quoteA.getDate().get(Calendar.MONTH);
			int quoteBMonth = quoteB.getDate().get(Calendar.MONTH);
			if (quoteAMonth == 11) {
				if (quoteBMonth != 0) {
					throw new IllegalArgumentException("Expected quoteB to be for January " + quoteB + " followed " + quoteA);
				}
			} else {
				if (quoteAMonth+1 != quoteBMonth) {
					throw new IllegalArgumentException("Unexpected quote " + quoteB + " followed " + quoteA);
				}
			}
			quoteA = quoteB;
		}
	}

}
