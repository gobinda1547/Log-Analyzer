package db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.h2.tools.DeleteDbFiles;

import log.rules.LogRules;
import support.ImportExport;

public class Database {
	private static String DB_NAME = "LOG_ANALYZER_DB";

	private static String TF1_RULE_ID = "RULE_ID";
	private static String TF2_RULE_TEXT = "RULE_TEXT";
	private static String TF3_PRINT_LAST = "PRINT_LAST";
	private static String TF4_LINE_COUNT = "LINE_COUNT";
	private static String TF5_IS_ENABLE = "IS_ENABLE";

	public static Connection getDatabaseConnection() {

		try {
			String dbUrl = String.format("jdbc:h2:./%s", DB_NAME);
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

	public static void deleteDatabaseFile() {
		try {
			DeleteDbFiles.execute(".", DB_NAME, true);
			DeleteDbFiles.execute("~", DB_NAME, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean createTable(String tableName) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(String.format("CREATE TABLE %S (", tableName));
			sql.append(String.format("%S SERIAL PRIMARY KEY, ", TF1_RULE_ID));
			sql.append(String.format("%S VARCHAR(255), ", TF2_RULE_TEXT));
			sql.append(String.format("%S VARCHAR(255), ", TF3_PRINT_LAST));
			sql.append(String.format("%S NUMBER, ", TF4_LINE_COUNT));
			sql.append(String.format("%S BOOLEAN );", TF5_IS_ENABLE));

			Connection conn = getDatabaseConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql.toString());

			stmt.close();
			conn.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String copyTable(String filePath, String tableName) {

		ArrayList<String> strings = selectAllTheTableName();
		for (int i = 0; i < strings.size(); i++) {
			if (tableName.equals(strings.get(i))) {
				return "This Rule Already Exist..!";
			}
		}

		if (createTable(tableName) == false) {
			return "Can not Create Rule Table. Try Again!";
		}

		ArrayList<LogRules> rules = ImportExport.getAllTheTableData(filePath, tableName);
		if (rules.size() == 0) {
			return "Rule Table Created but There is NO rules.";
		}

		for (int i = 0; i < rules.size(); i++) {
			insertTableData(tableName, rules.get(i));
		}
		return "Rules Copied Successfully";
	}

	public static ArrayList<String> selectAllTheTableName() {
		ArrayList<String> tableNames = new ArrayList<>();
		try {
			Connection conn = getDatabaseConnection();
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
		}
		return tableNames;
	}

	public static ArrayList<LogRules> selectAllTheTableData(String TABLE_NAME) {
		ArrayList<LogRules> rules = new ArrayList<>();
		try {
			String sql = String.format("SELECT * FROM %S", TABLE_NAME);
			Connection conn = getDatabaseConnection();
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

	public static LogRules selectOnlyOneTableData(String tableName, int ruleID) {
		LogRules logRules = null;
		try {
			String sql = String.format("SELECT * FROM %S WHERE %S=?", tableName, TF1_RULE_ID);
			Connection conn = getDatabaseConnection();
			PreparedStatement pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, ruleID);

			ResultSet rs = pStatement.executeQuery();
			if (rs.next()) {
				logRules = new LogRules();
				logRules.setId(rs.getInt(1));
				logRules.setRuleText(rs.getString(2));
				logRules.setPrintLast(rs.getString(3));
				logRules.setLineCount(rs.getInt(4));
				logRules.setEnable(rs.getBoolean(5));
			}
			rs.close();
			pStatement.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logRules;
	}

	public static ArrayList<LogRules> selectAllTheTableData(String tableName, boolean isEnable) {
		ArrayList<LogRules> rules = new ArrayList<>();
		try {
			Connection conn = getDatabaseConnection();
			String sql = String.format("SELECT * FROM %S WHERE %S=?", tableName, TF5_IS_ENABLE);
			PreparedStatement pStatement = conn.prepareStatement(sql);
			pStatement.setBoolean(1, isEnable);
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

	public static boolean insertTableData(String tableName, LogRules logRules) {

		boolean ans = false;

		if (logRules.getRuleText().length() == 0) {
			return false;
		}

		try {
			Connection connection = getDatabaseConnection();
			StringBuilder sb = new StringBuilder("");
			sb.append(String.format("INSERT INTO %S", tableName));
			sb.append(String.format("(%S,%S,%S,%S)", TF2_RULE_TEXT, TF3_PRINT_LAST, TF4_LINE_COUNT, TF5_IS_ENABLE));
			sb.append(String.format(" VALUES (?,?,?,?);"));

			PreparedStatement insertPreparedStatement = connection.prepareStatement(sb.toString());
			insertPreparedStatement.setString(1, logRules.getRuleText());
			insertPreparedStatement.setString(2, logRules.getPrintLast());
			insertPreparedStatement.setInt(3, logRules.getLineCount());
			insertPreparedStatement.setBoolean(4, logRules.isEnable());

			ans = (insertPreparedStatement.executeUpdate() != 0);
			insertPreparedStatement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return ans;
	}

	public static boolean deleteTable(String tableName) {

		if (tableName == null || tableName.length() == 0) {
			return false;
		}

		try {
			Connection connection = getDatabaseConnection();
			String deleteQuery = String.format("DROP TABLE %S;", tableName);
			PreparedStatement insertPreparedStatement = connection.prepareStatement(deleteQuery);
			insertPreparedStatement.execute();
			insertPreparedStatement.close();
			connection.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean deleteTableData(String tableName, int ruleID) {
		boolean ans = false;

		try {
			Connection connection = getDatabaseConnection();
			String DeleteQuery = String.format("DELETE FROM %S WHERE %S=?", tableName, TF1_RULE_ID);

			PreparedStatement insertPreparedStatement = connection.prepareStatement(DeleteQuery);
			insertPreparedStatement.setInt(1, ruleID);

			ans = (insertPreparedStatement.executeUpdate() != 0);
			insertPreparedStatement.close();

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return ans;
	}

	public static boolean updateTableData(String tableName, LogRules RULE_INFORMATION) {

		boolean ans = true;

		try {
			StringBuilder sb = new StringBuilder("");
			sb.append(String.format("UPDATE %S SET ", tableName));
			sb.append(String.format("%S=?, ", TF2_RULE_TEXT));
			sb.append(String.format("%S=?, ", TF3_PRINT_LAST));
			sb.append(String.format("%S=?, ", TF4_LINE_COUNT));
			sb.append(String.format("%S=? ", TF5_IS_ENABLE));
			sb.append(String.format("WHERE %S=?", TF1_RULE_ID));

			Connection conn = getDatabaseConnection();
			PreparedStatement preparedStatement = conn.prepareStatement(sb.toString());
			preparedStatement.setString(1, RULE_INFORMATION.getRuleText());
			preparedStatement.setString(2, RULE_INFORMATION.getPrintLast());
			preparedStatement.setInt(3, RULE_INFORMATION.getLineCount());
			preparedStatement.setBoolean(4, RULE_INFORMATION.isEnable());
			preparedStatement.setInt(5, RULE_INFORMATION.getId());

			ans = (preparedStatement.executeUpdate() != 0);

			preparedStatement.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ans;
	}

}
