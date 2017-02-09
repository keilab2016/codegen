package model;

import java.io.Serializable;

/*
 * パス情報格納クラス
 */
public class Path implements Serializable {
	private static final long serialVersionUID = 2L;
	private String models; //modelsフォルダのパス
	private String workspace; //workspaceフォルダのパス
	private String generated; //generatedフォルダのパス
	private String cakezip; //cakephp.zipファイルのパス
	private String dbphp; //database.phpファイルのパス

	public Path(){};
	public Path(String models, String workspace, String generated, String cakezip, String dbphp) {
		this.models = models;
		this.workspace = workspace;
		this.generated = generated;
		this.cakezip = cakezip;
		this.dbphp = dbphp;
	}
	
	public String getModels() {
		return models;
	}
	public String getWorkspace() {
		return workspace;
	}
	public String getGenerated() {
		return generated;
	}
	public String getCakezip() {
		return cakezip;
	}
	public String getDbphp() {
		return dbphp;
	}
}
