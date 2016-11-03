package com.ibaseit.scraping.dbutils;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

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
	public static DB getMongoConnection(){
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("192.168.203.116", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB db = mongo.getDB("cb360automation");
		System.out.println("Connect to database successfully");
		boolean auth = db.authenticate("sa", "1B1tsqlsa".toCharArray());
		System.out.println("Authentication: " + auth);
		DBCollection coll = db.getCollection("chargeback_automation");
		System.out.println("Collection chargeback_automation selected successfully");
		return db;
	}
}
