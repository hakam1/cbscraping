package com.ibaseit.scraping.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.ibaseit.scraping.dbutils.DBConnector;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

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
				preparedStmt.setString(8, logObj.getCc_first_6());
				preparedStmt.setString(9, logObj.getCc_last_4());
				preparedStmt.setString(10, logObj.getCc_type());
				preparedStmt.setString(11, logObj.getCase_number());

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
		System.out.println("sqldb");
		System.out.println("SQLDB total insertion is done...");
	}

	public void insertMongo(List<LogSheetData> logSheetDataMongo) {
		DB db = DBConnector.getMongoConnection();
		DBCollection coll = db.getCollection("chargeback_automation");
		System.out
				.println("Collection chargeback_automation selected successfully");

		DBObject dbObject = null;
		System.out.println("size=" + logSheetDataMongo.size());
		for (int i = 0; i < logSheetDataMongo.size(); i++) {
			LogSheetData logObj = logSheetDataMongo.get(i);
			String s = "{'input_date': '" + logObj.getInput_date()
					+ "', 'due_date': '" + logObj.getDue_date() + "', 'amount': '"
					+ logObj.getAmount() + "', 'transaction_date': '"
					+ logObj.getTransaction_date() + "', 'reason_description': '"
					+ logObj.getReason_description() + "', 'family_id': '"
					+ logObj.getFamily_id() + "', 'reference_number': '"
					+ logObj.getReference_number() + "', 'cc_first_6': '"
					+ logObj.getCc_first_6() + "', 'cc_last_4': '"
					+ logObj.getCc_last_4() + "', 'cc_type': '" + logObj.getCc_type()
					+ "', 'case_number': '" + logObj.getCase_number() + "'}";

			dbObject = (DBObject) JSON.parse(s);
			coll.insert(dbObject);
		}

		System.out.println("mongodb");
		System.out.println("MONGODB total insertion is done...");
	}
}
