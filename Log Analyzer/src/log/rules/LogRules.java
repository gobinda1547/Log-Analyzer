package log.rules;

public class LogRules {

	private int id;
	private String ruleText;
	private String printLast;
	private int lineCount;
	private boolean isEnable;

	public LogRules() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRuleText() {
		return ruleText;
	}

	public void setRuleText(String ruleText) {
		this.ruleText = ruleText.trim();
	}

	public String getPrintLast() {
		return printLast;
	}

	public void setPrintLast(String printLast) {
		this.printLast = printLast;
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

}
