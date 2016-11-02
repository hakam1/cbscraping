package com.ibaseit.scraping.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.ibaseit.scraping.dbutils.DBConnector;

public class ChargeBackDAO implements DbOperations {

	@Override
	public void insert(List<LogSheetData> logSheetData) {

		String query = " insert into chargeback_automation (input_date,due_date, amount, transaction_date, reason_description,family_id,reference_number, cc_first_6,cc_last_4,cc_type,case_number)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement preparedStmt = null;

		try {
			con = DBConnector.getConnector();
			preparedStmt = con.prepareStatement(query);
			for (int i = 0; i < logSheetData.size(); i++) {
				LogSheetData logObj = logSheetData.get(i);
				preparedStmt.setDate(1, logObj.getInput_date());
				preparedStmt.setDate(2, logObj.getDue_date());
				preparedStmt.setDouble(3, logObj.getAmount());
				preparedStmt.setDate(4, logObj.getTransaction_date());
				preparedStmt.setString(5, logObj.getReason_description());
				preparedStmt.setString(6, logObj.getFamily_id());
				preparedStmt.setString(7, logObj.getReference_number());
				preparedStmt.setString(8, logObj.getReference_number());
				preparedStmt.setString(9, logObj.getReference_number());
				preparedStmt.setString(10, logObj.getReference_number());
				preparedStmt.setString(11, logObj.getReference_number());

				boolean stat = preparedStmt.execute();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStmt != null)
					preparedStmt.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException se1) {
				se1.printStackTrace();
			}

		}

		System.out.println("DB insertion is done...");
	}
}
