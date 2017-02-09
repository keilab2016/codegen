package generator.cakephp;

/*
 * モデル用のコードが格納してあるクラス
 * 1つのモデルファイルに対し、1つ生成される
 * belongsTo、hasMany以外のassociationは現段階では生成されない
 */
public class ModelCode {
	private String c; //codeのc
	private String name; //Model名(.phpなし)
	
	public ModelCode(String name) {
		this.name = name;
		c = "";
	}
	
	/*
	 * ソースのヘッド部分のコード
	 */
	public void head() {
		c += "<?php\n";
		c += "class " + name + " extends AppModel {\n";
		c += "\tpublic $name = '" + name + "';\n";
		c += "\tpublic $useTable = '" + name.toLowerCase() + "s';\n\n";
	}
	
	/*
	 * アソシエーションヘッド部分生成コード
	 * 引数：アソシエーション名(belongsTo, hasMany, hasOneのいずれか)
	 */
	public void associationHead(String associationName) {
		c += "\tpublic $" + associationName + " = array(\n";
	}
	
	/*
	 * belongsToのコード
	 * 引数：関連のあるモデル名
	 */
	public void belongsTo(String relationalName) {
		c += "\t\t'" + relationalName + "' => array(\n";
		c += "\t\t\t'className' => '" + relationalName + "',\n";
		c += "\t\t\t'foreignKey' => '" + relationalName.toLowerCase() + "_id'\n";
		c += "\t\t),\n";
	}
	
	/*
	 * hasManyのコード
	 * 引数：関連のあるモデル名
	 */
	public void hasMany(String relationalName) {
		c += "\t\t'" + relationalName + "' => array(\n";
		c += "\t\t\t'className' => '" + relationalName + "',\n";
		c += "\t\t\t'foreignKey' => '" + name.toLowerCase() + "_id'\n";
		c += "\t\t),\n";
	}
	
	/*
	 * hasOneのコード
	 * 引数：関連のあるモデル名
	 */
	public void hasOne(String relationalName) {
		c += "\t\t'" + relationalName + "' => array(\n";
		c += "\t\t\t'className' => '" + relationalName + "',\n";
		c += "\t\t\t'foreignKey' => '" + name.toLowerCase() + "_id'\n";
		c += "\t\t),\n";
	}
	
	/*
	 * アソシエーションのフッタ部分のコード
	 */
	public void associationFoot() {
		c = c.substring(0,c.length()-2); //最後の余計な「,」を消去
		c += "\n\t);\n\n";
	}
	
	/*
	 * バリデーションのヘッド部分のコード
	 */
	public void validationHead() {
		c += "\tpublic $validate = array(\n";
	}
	
	/*
	 * バリデーションの中身のコード
	 * 引数：対象のカラム, バリデーションの種類
	 */
	public void validation(String column, String validate) {
		c += "\t\t'" + column + "' => '" +validate + "',\n";
	}
	
	/*
	 * バリデーションのフッタ部分のコード
	 */
	public void validationFoot() {
		c = c.substring(0,c.length()-2); //最後の余計な「,」を消去
		c += "\n\t);\n\n";
	}
	
	/*
	 * ログイン機能に必要なコード
	 */
	public void login() {
		c += "\tpublic function beforeSave($options = array()) {\n";
		c += "\t\t$this->data['" + name
				+ "']['password'] = AuthComponent::password($this->data['" + name + "']['password']);\n";
		c += "\t\treturn true;\n";
		c += "\t}\n\n";
	}
	
	/*
	 * ソースのフッタ部分のコード
	 */
	public void foot() {
		c += "}\n";
		c += "?>\n";
	}
	
	/*
	 * コード取得メソッド
	 */
	public String getCode() {
		return c;
	}
	
}
