package com.ibaseit.scraping.dao;

import java.sql.SQLException;
import java.util.List;

public interface DbOperations {

	public void insert(List<LogSheetData> logSheetData) throws SQLException;

	public void insertMongo(List<LogSheetData> logSheetData) throws SQLException;

}
