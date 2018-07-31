package main;

import log.analyser.LogAnalyser;

public class Main {

	static {
		//Database.deleteDatabaseFile();
	}
	
	public static void main(String[] args) {
		LogAnalyser.startService();
	}

}
