package support;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import log.rules.LogRules;

public class ImportExport {

	private static String DB_NAME = "LOG_ANALYZER_DB";

	public static ArrayList<LogRules> getAllTheTableData(String filePath, String TABLE_NAME) {
		ArrayList<LogRules> rules = new ArrayList<>();
		try {
			String sql = String.format("SELECT * FROM %S", TABLE_NAME);
			Connection conn = getDatabaseConnection(new File(filePath));
			PreparedStatement pStatement = conn.prepareStatement(sql);
			ResultSet rs = pStatement.executeQuery();
			while (rs.next()) {
				LogRules logRules = new LogRules();
				logRules.setId(rs.getInt(1));
				logRules.setRuleText(rs.getString(2));
				logRules.setPrintLast(rs.getString(3));
				logRules.setLineCount(rs.getInt(4));
				logRules.setEnable(rs.getBoolean(5));
				rules.add(logRules);
			}
			rs.close();
			pStatement.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rules;
	}

	public static ArrayList<String> getTablesName(File selectedFile) {

		ArrayList<String> tableNames = new ArrayList<>();
		try {
			Connection conn = getDatabaseConnection(selectedFile);
			DatabaseMetaData dbmd = conn.getMetaData();
			String[] types = { "TABLE" };
			ResultSet rs = dbmd.getTables(null, null, "%", types);
			while (rs.next()) {
				String ss = rs.getString("TABLE_NAME");
				if (ss != null && ss.length() != 0) {
					tableNames.add(ss);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return tableNames;

	}

	public static Connection getDatabaseConnection(File file) {

		try {
			System.out.println(file.exists());
			System.out.println(file.getParent());
			String dbUrl = "jdbc:h2:" + file.getParent() + File.separator + DB_NAME;
			System.out.println(file.getParent());
			String dbUserName = "";
			String dbPassword = "";
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
