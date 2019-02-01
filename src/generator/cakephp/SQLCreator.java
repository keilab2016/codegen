package generator.cakephp;

import java.io.*;

import loder.astah.DataDiagram;
import loder.astah.Table;
import loder.astah.Column;

/*
 * .sqlファイル生成クラス
 * ERDiagramを基に、SQL文を記述
 */
public class SQLCreator {
	private DataDiagram diagram;
	
	/*
	 * コンストラクタ
	 * 引数：ERDiagramインスタンス
	 */
	public SQLCreator(DataDiagram diagram) {
		this.diagram = diagram;
	}
	
	/*
	 * 生成メソッド
	 * 引数：SQLファイルの出力先、ファイル名(末尾に.sqlは不要)
	 * 返り値：boolean
	 */
	public boolean create(String path, String name) {
		String text = "";
		String sqlName = name + ".sql";
		File file = new File(path + "/" + sqlName);
		for(Table table : diagram.getTables()) {
			text += write(table);
		}
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			out.write(text);
			out.close();
		}catch(Exception e) {
			return false;
		}
		return true;
	}
	
	/*
	 * sql文書き込みメソッド
	 * テーブル毎に行う
	 * 引数：テーブルインスタンス
	 * 返り値：SQLのコード
	 */
	private String write(Table table) {
		String text = "";
		text += "DROP TABLE IF EXISTS " + table.getName() + ";\n";	
		text += "CREATE TABLE " + table.getName() + "(\n";	
		for(Column column : table.getColumns()) {
			if(column.getName().equals("id")) {
				text += "\tid INT(11) NOT NULL AUTO_INCREMENT,\n";
				continue;
			}
			//データ型を判別、定義されていない場合VARCHAR
			String type = column.getType();
			if(type.equals("") || type == null) type = "VARCHAR";
			//長さを判別、定義されてない場合INT型のとき11、文字列のとき255、それ以外はなし
			String length = column.getLength().trim();
			if(length.equals("") || length == null) {
				if(type.equals("INT")) length = "(11) ";
				else if(type.equals("TEXT")) length = "(255) ";
				else length = " ";
			} else {
				length = "(" + length + ") ";
			}
			//NOT NULLかどうかを判別
			String aboutNull = "";
			if(column.isNotNull()) aboutNull = "NOT NULL";
			else aboutNull = "DEFAULT NULL";
			text += "\t" + column.getName() + " " + type + length + aboutNull + ",\n";
		}
		text += "\tPRIMARY KEY (id)\n);\n\n";
		return text;
	}
	
	
}
