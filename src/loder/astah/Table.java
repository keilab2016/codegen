package loder.astah;

import java.util.ArrayList;

/*
 * テーブルデータクラス
 */
public class Table {

	private String name; //テーブル名
	private ArrayList<Column> columns; //カラムインスタンスのリスト
	private ArrayList<Relation> relations; //関係テーブルインスタンスのリスト
	
	/*
	 * コンストラクタ
	 */
	public Table(String name) {
		this.name = name;
		columns = new ArrayList<Column>();
		relations = new ArrayList<Relation>();
	}
	
	/*
	 * 新しいカラムを生成
	 */
	public void newColumn(String name, boolean notNull, String type, boolean foreignKey, String length) {
		columns.add(new Column(name, notNull, type, foreignKey, length));
	}
	
	/*
	 * 新しい関係を生成
	 */
	public void newRelation(String foreignTable, String multiplicity, String foreignKey) {
		relations.add(new Relation(foreignTable, multiplicity, foreignKey));
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Column> getColumns() {
		return columns;
	}
	
	public ArrayList<Relation> getRelations() {
		return relations;
	}
}
