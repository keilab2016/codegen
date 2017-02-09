package generator.cakephp;

import java.util.Map;

import loder.astah.Column;
import loder.astah.DataDiagram;
import loder.astah.Relation;
import loder.astah.STDiagram;
import loder.astah.Screen;
import loder.astah.Table;

import java.util.HashMap;
import java.io.IOException;

public abstract class MVCCreator implements CakePHPCreator {

	protected String path;
	protected DataDiagram dataDiagram;
	protected STDiagram stDiagram;
	
	public abstract void create() throws IOException;
	
	/*
	 * 文字列の最初の文字を大文字にするメソッド
	 * 引数：最初の文字を大文字にしたいテキスト(String)
	 * 返り値：String
	 */
	public String upFirstChar(String text) {
		return text.substring(0,1).toUpperCase() + text.substring(1, text.length());
	}
	
	/*
	 * 文字列の最後の文字を消すメソッド
	 * 引数：最後の文字を削除したいテキスト(String)
	 * 返り値：String
	 */
	public String delLastChar(String text) {
		return text.substring(0, text.length()-1);
	}
	
	/*
	 * テーブル名からModel名に変換するメソッド
	 * 引数：テーブル名(String)
	 * 返り値：Model名(String)
	 */
	public String tableToModel(String tableName) {
		String modelName = upFirstChar(delLastChar(tableName));
		return modelName;
	}
	
	/*
	 * 画面名から一致する画面インスタンスを探すメソッド
	 * 引数：画面名(String)
	 * 返り値：画面インスタンス(Screen)
	 */
	public Screen searchScreen(String screenName) {
		for(Screen screen : stDiagram.getScreens()) {
			if(screen.getName().equals(screenName)) return screen;
		}
		return null;
	}
	
	/*
	 * テーブル名から一致するテーブルインスタンスを探すメソッド
	 * 引数：テーブル名(String)
	 * 返り値：テーブルインスタンス(Table)
	 */
	public Table searchTable(String tableName) {
		for(Table table : dataDiagram.getTables()) {
			if(table.getName().equals(tableName)) return table;
		}
		return null;
	}
	
	/*
	 * テーブルが外部キーを持っているか判定するメソッド
	 * 引数：テーブルインスタンス(Table)
	 * 返り値：boolean
	 */
	public boolean hasForeignKey(Table table) {
		boolean have = false;
		for(Column column : table.getColumns()) {
			if(column.isForeignKey()) {
				have = true;
				break;
			}
		}
		if(have) return true;
		return false;
	}
	
	/*
	 * テーブルインスタンスに含まれているカラムの外部キーと外部テーブルの名前が一致するか確認するメソッド
	 * 引数：テーブルインスタンス(Table)、外部テーブル名(String)
	 * 返り値：boolean
	 */
	public boolean checkForeignKey(Table table, String foraignTableName) {
		for(Column column : table.getColumns()) {
			if(column.getName().equals(delLastChar(foraignTableName) + "_id")) return true;
		}
		return false;
	}
	
	/*
	 * テーブルと関係のあるテーブルを階層的に(テーブルを跨いで)確認するメソッド
	 * 引数：テーブルインスタンス(Table)
	 * 返り値：Map<Table, String>(関係のあるテーブルインスタンス, 多重度)
	 */
	public Map<Table, String> checkRelationalTables(Table table) {
		Map<Table, String> relationalTables = new HashMap<Table, String>();
		for(Relation r : table.getRelations()) {
			Table relationalTable = searchTable(r.getRelationTableName());
			switch(r.getMultiplicity()) {
			case "1対1": relationalTables.put(relationalTable, "1対1"); break;
			case "1対多": relationalTables.put(relationalTable, "1対多"); break;
			case "多対1": relationalTables.put(relationalTable, "多対1"); break;
			case "多対多": relationalTables.put(relationalTable, "多対多"); break;
			}
			if(r.getMultiplicity().equals("1対1")||r.getMultiplicity().equals("多対1")) {
				checkMultiplicity(table.getName(), r.getRelationTableName(), relationalTables);
			}
		}
		return relationalTables;
	}
	
	/*
	 * 関係のあるテーブルに、さらに関係のあるテーブルが存在するか確認するメソッド
	 * 引数：現在のテーブル名(String), 関係のあるテーブル名(String), 値を保持しておくマップ
	 */
	private void checkMultiplicity(String thisTableName, String relationalTableName, 
			Map<Table, String> relationalTables) {
		Table t = searchTable(relationalTableName);
		for(Relation r : t.getRelations()) {
			if(thisTableName.equals(r.getRelationTableName())) continue;
			Table relationalTable = searchTable(r.getRelationTableName());
			switch(r.getMultiplicity()) {
			case "1対1": relationalTables.put(relationalTable, "1対1"); break;
			case "1対多": return;//relationalTables.put(relationalTable, "1対多"); return;
			case "多対1": relationalTables.put(relationalTable, "多対1"); break;
			case "多対多": return;//relationalTables.put(relationalTable, "多対多"); return;
			}
			checkMultiplicity(relationalTableName, r.getRelationTableName(), relationalTables);
		}
	}
}
