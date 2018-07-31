package support;

import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

import log.rules.LogRules;

public class GpConverter {

	public static DefaultComboBoxModel<String> convertIntoComboBoxModel(ArrayList<String> types) {
		DefaultComboBoxModel<String> dcm = new DefaultComboBoxModel<>();
		for (String type : types) {
			dcm.addElement(type);
		}
		return dcm;
	}

	public static DefaultTableModel convertIntoRulesTableModel(ArrayList<LogRules> rulesList) {

		String[] cols = new String[] { "ID", "Log Text", "Print Last", "Line Count", "IsEnabled" };
		DefaultTableModel dtm = new DefaultTableModel(cols, 0);

		for (LogRules rules : rulesList) {
			cols[0] = String.valueOf(rules.getId());
			cols[1] = rules.getRuleText();
			cols[2] = rules.getPrintLast();
			cols[3] = String.valueOf(rules.getLineCount());
			cols[4] = String.valueOf(rules.isEnable());
			dtm.addRow(cols);
		}

		return dtm;
	}

	public static DefaultListModel<String> convertIntoListModel(ArrayList<String> rulesName) {
		DefaultListModel<String> dlm = new DefaultListModel<>();
		for(String ss: rulesName){
			dlm.addElement(ss);
		}
		return dlm;
	}

}
