package model;

import java.io.Serializable;
import java.util.List;

/*
 * 生成情報格納クラス
 * 生成結果がここに格納される、エラーやワーニングなども
 */
public class Generation implements Serializable {
	
	private static final long serialVersionUID = 4L;
	private String id;
	private String errorMessage;
	private List<String> warnings;

	public Generation() {};
	public Generation(String id) {
		this.id = id;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
	
	public void setWarning(String warning) {
		warnings.add(warning);
	}
	
	public String getID() {
		return id;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public List<String> getWarnings() {
		return warnings;
	}
	
}
