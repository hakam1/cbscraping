package com.ibaseit.scraping.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {

	public static Connection getConnector() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			connection = DriverManager.getConnection(
					"jdbc:mysql://Ibasesqldb:3306/cb360automation", "root", "1Base1t");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return connection;
	}

}
