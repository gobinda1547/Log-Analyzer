package log.analyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import db.Database;
import log.rules.LogRules;

public class LogManager {

	private static LogManager logManager;

	private ArrayList<String> allLogs;
	private boolean scannerIsRunning;

	public LogManager() {
		allLogs = new ArrayList<>();
		scannerIsRunning = false;
	}

	public static void addLogFile(File[] inputFiles) {
		logManager = new LogManager();

		for (int i = 0; i < inputFiles.length; i++) {
			for (int j = 0; j < inputFiles.length; j++) {
				if (inputFiles[i].getName().compareTo(inputFiles[j].getName()) < 0) {
					File temp = inputFiles[i];
					inputFiles[i] = inputFiles[j];
					inputFiles[j] = temp;
				}
			}
		}

		Thread differentScanningThread = new Thread(new MyFileScanner(inputFiles));
		differentScanningThread.start();
	}

	public static void applyRules(String ruleType) {

		if (logManager == null) {
			LogAnalyser.showMessageInConsolePanel("upload log files first..", false);
			return;
		}
		if (logManager.scannerIsRunning) {
			LogAnalyser.showMessageInConsolePanel("Scanner is already Running.. \nPlease Try Again...", false);
			return;
		}

		Thread thread = new Thread(new LogRuleAplicator(ruleType));
		thread.start();
	}

	static class LogRuleAplicator implements Runnable {

		private String ruleType;
		private String NEW_LINE = "\n";

		public LogRuleAplicator(String ruleType) {
			this.ruleType = ruleType;
		}

		@Override
		public void run() {

			ArrayList<LogRules> logRules = Database.selectAllTheTableData(ruleType, true);

			LogAnalyser.showMessageInConsolePanel(String.format("Enabled Log Rules Total: %d", logRules.size()), false);

			StringBuilder sb = new StringBuilder();

			String currentLogLine = null;
			LogRules currentLogRule = null;

			for (int i = 0; i < logManager.allLogs.size(); i++) {
				currentLogLine = logManager.allLogs.get(i);

				for (int j = 0; j < logRules.size(); j++) {
					currentLogRule = logRules.get(j);

					if (currentLogLine.contains(currentLogRule.getRuleText())) {
						sb.append(currentLogLine + NEW_LINE);
						for (int k = 1; k < currentLogRule.getLineCount(); k++) {
							sb.append(logManager.allLogs.get(i + k) + NEW_LINE);
							++i;
						}

						if (currentLogRule.getPrintLast().equals("n")) {
							sb.append(NEW_LINE);
						} else {
							sb.append(currentLogRule.getPrintLast());
						}
						break;
					}

				}

			}
			LogAnalyser.showMessageInConsolePanel("Rules Applying Completed!\n", false);
			LogAnalyser.showMessageInMainPanel(sb.toString(), true);
		}

	}

	static class MyFileScanner implements Runnable {

		private File[] files;

		public MyFileScanner(File[] files) {
			this.files = files;
		}

		@Override
		public void run() {
			logManager.scannerIsRunning = true;
			LogAnalyser.showMessageInConsolePanel("Scanning process started!", true);

			String scannedString = null;
			for (int currentNumber = 0; currentNumber < files.length; currentNumber++) {
				try {
					MyScanner myScanner = new MyScanner(files[currentNumber]);
					while ((scannedString = myScanner.nextLine()) != null) {
						logManager.allLogs.add(scannedString);
					}
					myScanner.close();

					scannedString = "Scanning Done For File:" + files[currentNumber].getName();
					LogAnalyser.showMessageInConsolePanel(scannedString, true);

				} catch (Exception e) {
					scannedString = "Problem while scanning File:" + files[currentNumber].getName();
					LogAnalyser.showMessageInConsolePanel(scannedString, true);
				}
			}

			LogAnalyser.showMessageInConsolePanel("Scanning process end successfully!", false);
			LogAnalyser.showMessageInConsolePanel("Total: " + String.valueOf(logManager.allLogs.size()), true);
			logManager.scannerIsRunning = false;

		}

		class MyScanner {

			private BufferedReader br;

			public MyScanner(File file) throws FileNotFoundException {
				br = new BufferedReader(new FileReader(file));
			}

			public String nextLine() throws IOException {
				return br.readLine();
			}

			public void close() throws IOException {
				br.close();
			}

		}

	}

}
