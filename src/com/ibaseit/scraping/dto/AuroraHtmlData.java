package com.ibaseit.scraping.dto;

import org.jsoup.select.Elements;

public class AuroraHtmlData {

	private String inputDate;
	private String caseNumber;
	private String amount;
	private String transdate;
	private String reason;
	private String firstSixAndLastFour;
	private String comment;

	public AuroraHtmlData(Elements rec) {

		this.inputDate = rec.get(1).text();
		this.caseNumber = rec.get(2).text();
		this.amount = rec.get(6).text();
		this.transdate = rec.get(7).text();
		this.reason = rec.get(8).text();
		this.firstSixAndLastFour = rec.get(4).text();
		this.comment = rec.get(9).text();
	}

	public String getInputDate() {
		return inputDate;
	}

	public String getCaseNumber() {
		return caseNumber;
	}

	public String getAmount() {
		return amount;
	}

	public String getTransdate() {
		return transdate;
	}

	public String getReason() {
		return reason;
	}

	public String getFirstSixAndLastFour() {
		return firstSixAndLastFour;
	}

	public String getComment() {
		return comment;
	}

}
