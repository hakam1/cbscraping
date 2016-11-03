package com.ibaseit.scraping.dto;

import org.jsoup.select.Elements;

public class HumboldtNMAHtmlData {
    
    private String inputDate;
    private String dueDate;
    private String caseNumber;
    private String ReferenceNumber;
    private String amount;
    private String reason;
    private String transactionDate;
    private String firstSixAndLastFour;

    public HumboldtNMAHtmlData(Elements noOfrows) {
	inputDate=noOfrows.get(10).text();
	dueDate=noOfrows.get(9).text();
	caseNumber=noOfrows.get(4).text();
	ReferenceNumber=noOfrows.get(5).text();
	amount=noOfrows.get(8).text();
	reason=noOfrows.get(3).text();
	transactionDate=noOfrows.get(7).text();
	firstSixAndLastFour=noOfrows.get(6).text();
	
    }
    public String getInputDate() {
        return inputDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public String getReferenceNumber() {
        return ReferenceNumber;
    }

    public String getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getFirstSixAndLastFour() {
        return firstSixAndLastFour;
    }


}
