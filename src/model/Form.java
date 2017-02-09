package model;

import java.io.Serializable;

/*
 * フォーム情報格納クラス
 */
public class Form implements Serializable {
	private static final long serialVersionUID = 3L;
	private String id; //id(postされる度に生成されるランダムな文字列5文字+現在時刻)
	private String dataName; //ER図、あるいはクラス図のファイル名
	private String stName; //画面遷移図のファイル名
	private String way; //生成方式
	private String secuSalt; //securitySaltの値
	private String ciphSeed; //cipherSeedの値
	private String apiKey; //地図表示機能に使うAPI_KEY
	
	public Form() {};
	public Form(String id) {
		this.id = id;
	}
	
	//ゲッター
	public String getID() {	return id; }
	public String getDataName() { return dataName; }
	public String getSTName() { return stName; }
	public String getWay() { return way; }
	public String getSecuSalt() { return secuSalt; }
	public String getCiphSeed() { return ciphSeed; }
	public String getApiKey() { return apiKey; }
	
	//セッター
	public void setDataName(String dataName) { this.dataName = dataName; }
	public void setSTName(String stName) { this.stName = stName; }
	public void setWay(String way) { this.way = way; }
	public void setSecuSalt(String secuSalt) { this.secuSalt = secuSalt; }
	public void setCiphSeed(String ciphSeed) { this.ciphSeed = ciphSeed; }
	public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
