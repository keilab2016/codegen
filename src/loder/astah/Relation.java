package loder.astah;

/*
 * 関係データクラス
 */
public class Relation {
	
	private String relationTableName; //関連のあるテーブル名
	private String multiplicity; //多重度
	private String foreignKey; //外部キー

	/*
	 * コンストラクタ
	 */
	public Relation(String relationTableName, String multiplicity, String foreignKey) {
		this.relationTableName = relationTableName;
		this.multiplicity = multiplicity;
		this.foreignKey = foreignKey;
	}
	
	public String getRelationTableName() {
		return relationTableName;
	}
	
	public String getMultiplicity() {
		return multiplicity;
	}
	
	public String getForeignKey() {
		return foreignKey;
	}
}
