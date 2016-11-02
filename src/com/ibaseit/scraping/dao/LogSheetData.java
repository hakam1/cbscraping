package com.ibaseit.scraping.dao;

import java.sql.Date;

public class LogSheetData {

	private Date input_date;
	private Date due_date;
	private double amount;
	private Date transaction_date;
	private String reason_description;
	private String family_id;
	private String reference_number;
	private String cc_first_6;
	private String cc_last_4;
	private String cc_type;
	private String case_number;

	public Date getInput_date() {
		return input_date;
	}

	public void setInput_date(Date input_date) {
		this.input_date = input_date;
	}

	public Date getDue_date() {
		return due_date;
	}

	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(Date transaction_date) {
		this.transaction_date = transaction_date;
	}

	public String getReason_description() {
		return reason_description;
	}

	public void setReason_description(String reason_description) {
		this.reason_description = reason_description;
	}

	public String getFamily_id() {
		return family_id;
	}

	public void setFamily_id(String family_id) {
		this.family_id = family_id;
	}

	public String getReference_number() {
		return reference_number;
	}

	public void setReference_number(String reference_number) {
		this.reference_number = reference_number;
	}

	public String getCc_first_6() {
		return cc_first_6;
	}

	public void setCc_first_6(String cc_first_6) {
		this.cc_first_6 = cc_first_6;
	}

	public String getCc_last_4() {
		return cc_last_4;
	}

	public void setCc_last_4(String cc_last_4) {
		this.cc_last_4 = cc_last_4;
	}

	public String getCc_type() {
		return cc_type;
	}

	public void setCc_type(String cc_type) {
		this.cc_type = cc_type;
	}

	public String getCase_number() {
		return case_number;
	}

	public void setCase_number(String case_number) {
		this.case_number = case_number;
	}
}
