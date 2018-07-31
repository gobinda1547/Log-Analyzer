package log.analyser;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import db.Database;
import log.rules.LogRules;
import support.DropTargetHandler;
import support.GpConverter;
import support.ImportExport;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.JComboBox;
import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.BoxLayout;
import javax.swing.JList;

public class LogAnalyser extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int FONT_TYPE_PLAIN = 0;
	private static final int FONT_TYPE_BOLD = 1;
	private static final int FONT_TYPE_PLAIN_AND_BOLD = 2;

	private static LogAnalyser logAnalyser;

	private int fontSize;
	private int fontType;

	private JPanel contentPane;

	private CardLayout contenPanelLayout;

	private JTextArea consolePanelTextArea;
	private JTextArea mainPanelTextArea;

	private JTextField textFieldForTypeADD;
	private JTextField textFieldForRulesText;
	private JTextField textFieldForRulesID;
	private JTextField textFieldForRulesPrintLast;
	private JTextField textFieldForRulesLineCount;

	private JTable tableForRules;

	private JComboBox<String> comboBoxForType;
	private JComboBox<String> comboBoxForApplyRule;
	private JComboBox<String> comboBoxForRulesIsEnable;
	private JTextField textFieldForShowingSelectedDbLocation;

	private JList<String> importableRulesList;

	public LogAnalyser() {
		super();

		fontType = FONT_TYPE_PLAIN_AND_BOLD;
		fontSize = 15;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 777, 472);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JButton btnNewButton = new JButton("Upload Log Files");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				fileChooser.setMultiSelectionEnabled(true);

				FileNameExtensionFilter filter = new FileNameExtensionFilter("LOG FILES", "log");
				fileChooser.setFileFilter(filter);

				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File[] selectedFile = fileChooser.getSelectedFiles();
					LogManager.addLogFile(selectedFile);
				}
			}
		});
		menuBar.add(btnNewButton);

		JButton btnApplyRules = new JButton("Apply Rules");
		btnApplyRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshComboBoxForApplyRule();
				showThisPanelInCardLayout("mainPanel");
			}
		});
		menuBar.add(btnApplyRules);

		JButton btnRulesSettings = new JButton("Rule's Settings");
		btnRulesSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDefaultValueForComboBoxForRulesIsEnable();
				refreshTypeComboBox();
				refreshRulesTable();
				showThisPanelInCardLayout("rulesSettingsPanel");
			}
		});
		menuBar.add(btnRulesSettings);

		JButton btnShowConsole = new JButton("Show Console");
		btnShowConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showThisPanelInCardLayout("consolePanel");
			}
		});
		menuBar.add(btnShowConsole);

		int maxFontValue = 50;
		String[] fontValues = new String[maxFontValue];
		for (int i = 0; i < maxFontValue; i++) {
			fontValues[i] = String.valueOf(i);
		}
		JComboBox<String> comboBoxForFontSelection = new JComboBox<String>(fontValues);
		comboBoxForFontSelection.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					fontSize = comboBoxForFontSelection.getSelectedIndex() + 1;
					if (fontType == FONT_TYPE_PLAIN) {
						consolePanelTextArea.setFont(new Font("Courier New", Font.PLAIN, fontSize));
						mainPanelTextArea.setFont(new Font("Courier New", Font.PLAIN, fontSize));
					} else if (fontType == FONT_TYPE_BOLD) {
						consolePanelTextArea.setFont(new Font("Courier New", Font.BOLD, fontSize));
						mainPanelTextArea.setFont(new Font("Courier New", Font.BOLD, fontSize));
					} else if (fontType == FONT_TYPE_PLAIN_AND_BOLD) {
						consolePanelTextArea.setFont(new Font("Courier New", Font.PLAIN + Font.BOLD, fontSize));
						mainPanelTextArea.setFont(new Font("Courier New", Font.PLAIN + Font.BOLD, fontSize));
					}
				}
			}
		});
		menuBar.add(comboBoxForFontSelection);

		String[] fontTypesValue = new String[] { "PLAIN", "BOLD", "PLAIN & BOLD" };
		JComboBox<String> comboBoxForFontTypeSelection = new JComboBox<String>(fontTypesValue);
		comboBoxForFontTypeSelection.setSelectedIndex(2);

		comboBoxForFontTypeSelection.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					fontType = comboBoxForFontTypeSelection.getSelectedIndex();
					if (fontType == FONT_TYPE_PLAIN) {
						consolePanelTextArea.setFont(new Font("Courier New", Font.PLAIN, fontSize));
						mainPanelTextArea.setFont(new Font("Courier New", Font.PLAIN, fontSize));
					} else if (fontType == FONT_TYPE_BOLD) {
						consolePanelTextArea.setFont(new Font("Courier New", Font.BOLD, fontSize));
						mainPanelTextArea.setFont(new Font("Courier New", Font.BOLD, fontSize));
					} else if (fontType == FONT_TYPE_PLAIN_AND_BOLD) {
						consolePanelTextArea.setFont(new Font("Courier New", Font.PLAIN + Font.BOLD, fontSize));
						mainPanelTextArea.setFont(new Font("Courier New", Font.PLAIN + Font.BOLD, fontSize));
					}
				}
			}
		});
		menuBar.add(comboBoxForFontTypeSelection);

		JButton btnImportRules = new JButton("Import Rules");
		btnImportRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String preSelectedFilePath = textFieldForShowingSelectedDbLocation.getText();
				if (preSelectedFilePath != null && preSelectedFilePath.length() != 0) {
					refreshImportableRulesList(preSelectedFilePath);
				}
				showThisPanelInCardLayout("importPanel");
			}
		});
		menuBar.add(btnImportRules);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		contenPanelLayout = new CardLayout();
		contentPane.setLayout(contenPanelLayout);

		JPanel consolePanel = new JPanel();
		contentPane.add(consolePanel, "consolePanel");
		consolePanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		consolePanel.add(scrollPane, BorderLayout.CENTER);

		consolePanelTextArea = new JTextArea();
		consolePanelTextArea.setEditable(false);
		consolePanelTextArea.setFont(new Font("Courier New", Font.PLAIN + Font.BOLD, 15));
		scrollPane.setViewportView(consolePanelTextArea);

		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, "mainPanel");
		mainPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		mainPanel.add(scrollPane_1, BorderLayout.CENTER);

		mainPanelTextArea = new JTextArea();
		mainPanelTextArea.setEditable(false);
		mainPanelTextArea.setFont(new Font("Courier New", Font.PLAIN + Font.BOLD, 15));
		scrollPane_1.setViewportView(mainPanelTextArea);

		JPanel subMainPanel = new JPanel();
		mainPanel.add(subMainPanel, BorderLayout.NORTH);
		subMainPanel.setLayout(new GridLayout(0, 2, 0, 0));

		comboBoxForApplyRule = new JComboBox<>();
		subMainPanel.add(comboBoxForApplyRule);

		JButton buttonApplyRule = new JButton("Apply Rule");
		buttonApplyRule.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String ruleType = (String) logAnalyser.comboBoxForApplyRule.getSelectedItem();
				if (ruleType == null || ruleType.length() == 0) {
					JOptionPane.showMessageDialog(null, "No rules available");
					return;
				}
				logAnalyser.mainPanelTextArea.setText("");
				showMessageInConsolePanel("\nStarted Rules Applying..!", true);
				LogManager.applyRules(ruleType);
			}
		});
		subMainPanel.add(buttonApplyRule);

		setContentPane(contentPane);
		setLocationRelativeTo(null);

		consolePanelTextArea.setDropTarget(new DropTargetHandler());

		JPanel rulesSettingsPanel = new JPanel();
		contentPane.add(rulesSettingsPanel, "rulesSettingsPanel");
		rulesSettingsPanel.setLayout(new BorderLayout(0, 0));

		JPanel subRulesSettingsPanel1 = new JPanel();
		rulesSettingsPanel.add(subRulesSettingsPanel1, BorderLayout.NORTH);
		subRulesSettingsPanel1.setLayout(new GridLayout(0, 4, 0, 0));

		comboBoxForType = new JComboBox<String>();
		comboBoxForType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					refreshRulesTable();
				}
			}
		});
		subRulesSettingsPanel1.add(comboBoxForType);

		JButton buttonForTypeDelete = new JButton("Delete Type");
		buttonForTypeDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String selectedName = (String) comboBoxForType.getSelectedItem();
				if (selectedName == null || selectedName.length() == 0) {
					JOptionPane.showMessageDialog(null, "RULE TYPE NOT AVAILABLE!");
					return;
				}
				String outputMessage = "Can not DELETE this type";

				if (Database.deleteTable(selectedName)) {
					outputMessage = "DELETED the SELECTED RULE successfully!";
					refreshTypeComboBox();
					refreshRulesTable();
				}
				JOptionPane.showMessageDialog(null, outputMessage);
			}
		});
		subRulesSettingsPanel1.add(buttonForTypeDelete);

		textFieldForTypeADD = new JTextField();
		textFieldForTypeADD.setColumns(10);
		subRulesSettingsPanel1.add(textFieldForTypeADD);

		JButton buttonForTypeADD = new JButton("Add Type");
		buttonForTypeADD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String ruleName = textFieldForTypeADD.getText().trim();
				if (ruleName == null || ruleName.length() == 0) {
					JOptionPane.showMessageDialog(null, "ENTER VALID RULE TYPE!");
					return;
				}
				ruleName = ruleName.replaceAll(" ", "_");
				String outputMessage = "Can not ADD this type";

				if (Database.createTable(ruleName)) {
					outputMessage = "ADDED the NEW type successfully!";
					textFieldForTypeADD.setText("");
					refreshTypeComboBox();
					refreshRulesTable();
				}
				JOptionPane.showMessageDialog(null, outputMessage);
			}
		});
		subRulesSettingsPanel1.add(buttonForTypeADD);

		JPanel subRulesSettingsPanel2 = new JPanel();
		rulesSettingsPanel.add(subRulesSettingsPanel2, BorderLayout.CENTER);
		subRulesSettingsPanel2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_3 = new JScrollPane();
		subRulesSettingsPanel2.add(scrollPane_3, BorderLayout.CENTER);

		tableForRules = new JTable();
		tableForRules.setEnabled(false);
		tableForRules.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_3.setViewportView(tableForRules);

		JPanel subRulesSettingsPanel3 = new JPanel();
		rulesSettingsPanel.add(subRulesSettingsPanel3, BorderLayout.SOUTH);
		subRulesSettingsPanel3.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel panel_4 = new JPanel();
		subRulesSettingsPanel3.add(panel_4);
		panel_4.setLayout(new GridLayout(0, 1, 0, 0));

		textFieldForRulesText = new JTextField();
		textFieldForRulesText.setToolTipText("rules text");
		textFieldForRulesText.setColumns(10);
		panel_4.add(textFieldForRulesText);

		JPanel panel_5 = new JPanel();
		subRulesSettingsPanel3.add(panel_5);
		panel_5.setLayout(new GridLayout(1, 0, 0, 0));

		textFieldForRulesID = new JTextField();
		textFieldForRulesID.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ruleName = (String) comboBoxForType.getSelectedItem();

				int ruleID = -1;
				try {
					ruleID = Integer.parseInt(textFieldForRulesID.getText());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "NUMBER FORMAT EXCEPTION!");
					return;
				}

				LogRules logRules = Database.selectOnlyOneTableData(ruleName, ruleID);
				if (logRules == null) {
					textFieldForRulesText.setText("");
					textFieldForRulesPrintLast.setText("");
					textFieldForRulesLineCount.setText("");
					JOptionPane.showMessageDialog(null, "Invalid Rules ID!");
					return;
				}
				textFieldForRulesText.setText(logRules.getRuleText());
				textFieldForRulesPrintLast.setText(logRules.getPrintLast());
				textFieldForRulesLineCount.setText(String.valueOf(logRules.getLineCount()));
				comboBoxForRulesIsEnable.setSelectedItem(logRules.isEnable() ? "YES" : "NO");
			}
		});
		textFieldForRulesID.setToolTipText("ID");
		panel_5.add(textFieldForRulesID);
		textFieldForRulesID.setColumns(10);

		textFieldForRulesPrintLast = new JTextField();
		textFieldForRulesPrintLast.setToolTipText("print last");
		panel_5.add(textFieldForRulesPrintLast);
		textFieldForRulesPrintLast.setColumns(10);

		textFieldForRulesLineCount = new JTextField();
		textFieldForRulesLineCount.setToolTipText("line count");
		panel_5.add(textFieldForRulesLineCount);
		textFieldForRulesLineCount.setColumns(10);

		comboBoxForRulesIsEnable = new JComboBox<String>();
		comboBoxForRulesIsEnable.setToolTipText("is enable");
		panel_5.add(comboBoxForRulesIsEnable);

		JButton buttonForRulesADD = new JButton("Add");
		buttonForRulesADD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String outputMessage = "Can not add this rule!";
				try {
					String ruleName = (String) comboBoxForType.getSelectedItem();
					if (ruleName == null || ruleName.length() == 0) {
						JOptionPane.showMessageDialog(null, "Rule Type can not be EMPTY");
						return;
					}

					String ruleText = textFieldForRulesText.getText();
					if (ruleText == null || ruleText.length() == 0) {
						JOptionPane.showMessageDialog(null, "Rule Text can not be EMPTY");
						return;
					}

					String printLast = textFieldForRulesPrintLast.getText();
					int lineCount = Integer.parseInt(textFieldForRulesLineCount.getText());
					boolean isEnable = ((String) comboBoxForRulesIsEnable.getSelectedItem() == "YES") ? true : false;

					LogRules ruleData = new LogRules();
					ruleData.setRuleText(ruleText);
					ruleData.setPrintLast(printLast);
					ruleData.setLineCount(lineCount);
					ruleData.setEnable(isEnable);

					if (Database.insertTableData(ruleName, ruleData)) {
						outputMessage = "Added the rule successfully!";
						textFieldForRulesText.setText("");
						textFieldForRulesPrintLast.setText("");
						textFieldForRulesLineCount.setText("");
						refreshRulesTable();
					}
				} catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(null, "Enter Valid line count number!");
					return;
				}

				JOptionPane.showMessageDialog(null, outputMessage);
			}
		});
		panel_5.add(buttonForRulesADD);

		JButton buttonForRulesDelete = new JButton("Delete");
		buttonForRulesDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int ruleID = -1;
				try {
					ruleID = Integer.parseInt(textFieldForRulesID.getText());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "ENTER A VALID RULE ID!");
					return;
				}

				String ruleName = (String) comboBoxForType.getSelectedItem();
				if (ruleName == null || ruleName.length() == 0) {
					JOptionPane.showMessageDialog(null, "RULE TYPE NOT AVAILABLE!");
					return;
				}

				String outputMessage = "Can not DELETE this RULE";
				if (Database.deleteTableData(ruleName, ruleID)) {
					outputMessage = "Deleted the Rule successfully!";
					textFieldForRulesID.setText("");
					refreshRulesTable();
				}
				JOptionPane.showMessageDialog(null, outputMessage);
			}
		});
		panel_5.add(buttonForRulesDelete);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String outputMessage = "Can not Update this rule!";
				String ruleName = (String) comboBoxForType.getSelectedItem();
				if (ruleName == null || ruleName.length() == 0) {
					JOptionPane.showMessageDialog(null, "rule type not available!");
					return;
				}

				int ruleID = -1;
				try {
					ruleID = Integer.parseInt(textFieldForRulesID.getText());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "ENTER VALID RULE ID!");
					return;
				}
				String ruleText = textFieldForRulesText.getText();
				if (ruleText == null || ruleText.length() == 0) {
					JOptionPane.showMessageDialog(null, "rule text can not be empty!");
					return;
				}

				String printLast = textFieldForRulesPrintLast.getText();
				int lineCount = -1;
				try {
					lineCount = Integer.parseInt(textFieldForRulesLineCount.getText());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "ENTER VALID LINE COUNT NUMBER!");
					return;
				}
				boolean isEnable = ((String) comboBoxForRulesIsEnable.getSelectedItem() == "YES") ? true : false;

				LogRules logRules = new LogRules();
				logRules.setId(ruleID);
				logRules.setRuleText(ruleText);
				logRules.setPrintLast(printLast);
				logRules.setLineCount(lineCount);
				logRules.setEnable(isEnable);

				if (Database.updateTableData(ruleName, logRules)) {
					outputMessage = "Updated the rule successfully!";
					refreshRulesTable();
				}

				JOptionPane.showMessageDialog(null, outputMessage);
			}
		});
		panel_5.add(btnUpdate);

		JPanel importPanel = new JPanel();
		contentPane.add(importPanel, "importPanel");
		importPanel.setLayout(new BorderLayout(0, 0));

		JPanel subImportPanel = new JPanel();
		importPanel.add(subImportPanel, BorderLayout.NORTH);
		subImportPanel.setLayout(new BoxLayout(subImportPanel, BoxLayout.X_AXIS));

		textFieldForShowingSelectedDbLocation = new JTextField();
		textFieldForShowingSelectedDbLocation.setEditable(false);
		subImportPanel.add(textFieldForShowingSelectedDbLocation);
		textFieldForShowingSelectedDbLocation.setColumns(10);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				FileNameExtensionFilter filter = new FileNameExtensionFilter("DATABASE FILE", "db");
				fileChooser.setFileFilter(filter);

				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					textFieldForShowingSelectedDbLocation.setText(selectedFile.getAbsolutePath());
					int retValue = refreshImportableRulesList(selectedFile.getAbsolutePath());
					if (retValue == -1) {
						showMessageInConsolePanel("\nRead Write Problem Occurs. Please Select The Right File!.", true);
					} else if (retValue == 0) {
						showMessageInConsolePanel("\nNo Rules Found in the selected DB!", true);
					}
				}

			}
		});
		subImportPanel.add(btnBrowse);

		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String tableName = importableRulesList.getSelectedValue();
				if (tableName == null) {
					JOptionPane.showMessageDialog(null, "PLEASE, SELECT ONE RULE FIRST!");
					return;
				}

				String filePath = textFieldForShowingSelectedDbLocation.getText();
				if (filePath == null || filePath.length() == 0) {
					JOptionPane.showMessageDialog(null, "PLEASE, SELECT THE DB FILE FIRST!");
					return;
				}

				String message = Database.copyTable(filePath, tableName);
				JOptionPane.showMessageDialog(null, message);

			}
		});
		importPanel.add(btnImport, BorderLayout.SOUTH);

		JScrollPane scrollPane_2 = new JScrollPane();
		importPanel.add(scrollPane_2, BorderLayout.CENTER);

		importableRulesList = new JList<String>();
		scrollPane_2.setViewportView(importableRulesList);

	}

	public void refreshRulesTable() {
		String selectedType = (String) comboBoxForType.getSelectedItem();
		if (selectedType == null || selectedType.length() == 0) {
			return;
		}
		ArrayList<LogRules> rules = Database.selectAllTheTableData(selectedType);
		logAnalyser.tableForRules.setModel(GpConverter.convertIntoRulesTableModel(rules));

		final TableColumnModel columnModel = logAnalyser.tableForRules.getColumnModel();
		for (int column = 0; column < logAnalyser.tableForRules.getColumnCount(); column++) {
			int width = 150;
			for (int row = 0; row < logAnalyser.tableForRules.getRowCount(); row++) {
				TableCellRenderer renderer = logAnalyser.tableForRules.getCellRenderer(row, column);
				Component comp = logAnalyser.tableForRules.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + 25, width);
			}
			// width = Math.min(width, 300);
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	public void refreshTypeComboBox() {
		ArrayList<String> ruleTypes = Database.selectAllTheTableName();
		DefaultComboBoxModel<String> dcm = GpConverter.convertIntoComboBoxModel(ruleTypes);
		logAnalyser.comboBoxForType.setModel(dcm);
	}

	private void refreshComboBoxForApplyRule() {
		ArrayList<String> ruleTypes = Database.selectAllTheTableName();
		DefaultComboBoxModel<String> dcm = GpConverter.convertIntoComboBoxModel(ruleTypes);
		logAnalyser.comboBoxForApplyRule.setModel(dcm);
	}

	private int refreshImportableRulesList(String filePath) {
		ArrayList<String> rulesName = ImportExport.getTablesName(new File(filePath));
		if (rulesName == null) {
			return -1;
		}
		DefaultListModel<String> defaultListModel = GpConverter.convertIntoListModel(rulesName);
		logAnalyser.importableRulesList.setModel(defaultListModel);
		return rulesName.size();
	}

	public static void startService() {
		if (logAnalyser == null) {
			logAnalyser = new LogAnalyser();
			logAnalyser.setVisible(true);
		}
	}

	public static void showMessageInConsolePanel(String text, boolean comeFront) {
		logAnalyser.consolePanelTextArea.append(text + "\n");
		if (comeFront == true) {
			logAnalyser.contenPanelLayout.show(logAnalyser.contentPane, "consolePanel");
		}
	}

	public static void showMessageInMainPanel(String text, boolean comeFront) {
		logAnalyser.mainPanelTextArea.append(text + "\n");
		if (comeFront == true) {
			logAnalyser.contenPanelLayout.show(logAnalyser.contentPane, "mainPanel");
		}
	}

	public static void showThisPanelInCardLayout(String panelName) {
		logAnalyser.contenPanelLayout.show(logAnalyser.contentPane, panelName);
	}

	public static void setDefaultValueForComboBoxForRulesIsEnable() {
		ArrayList<String> vArrayList = new ArrayList<>();
		vArrayList.add("YES");
		vArrayList.add("NO");
		logAnalyser.comboBoxForRulesIsEnable.setModel(GpConverter.convertIntoComboBoxModel(vArrayList));
	}
}
