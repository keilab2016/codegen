package generator.cakephp;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import loder.astah.Column;
import loder.astah.DataDiagram;
import loder.astah.STDiagram;
import loder.astah.Screen;
import loder.astah.Table;

import java.io.IOException;

/*
 * モデル生成クラス
 */
public class ModelCreator extends MVCCreator {
	
	private ModelCode modelCode;
	private Table table;
	private List<String> hasOne; //haeOneのリスト(Model名)
	private List<String> belongsTo; //belongsToのリスト(Model名)
	private List<String> hasMany; //hasManyのリスト(Model名)
	private boolean exitValidation = false; //バリデーションが存在するか
	
	/*
	 * コンストラクタ
	 */
	public ModelCreator(String path, DataDiagram dataDiagram, STDiagram stDiagram) {
		this.path = path + "/app/Model";
		this.dataDiagram = dataDiagram;
		this.stDiagram = stDiagram;
	}

	/*
	 * 初期化メソッド
	 */
	private void initialize() {
		modelCode = new ModelCode(tableToModel(table.getName()));
		hasOne = new ArrayList<String>();
		belongsTo = new ArrayList<String>();
		hasMany = new ArrayList<String>();
	}
	
	/*
	 * 生成メソッド(non-Javadoc)
	 * @see codegenerator.generator.cakephp.MVCCreator#create()
	 * コード生成を行い、ファイルを生成し、そのファイルにコードを記述する
	 */
	public void create() throws IOException{
		File folder = new File(path);
		if(!folder.exists()) folder.mkdirs();
		for(Table t : dataDiagram.getTables()) {
			table = t;
			initialize();
			modelCode.head();
			association();
			checkSTDiagram();
			modelCode.foot();
			File file = new File(path + "/" + tableToModel(table.getName()) + ".php");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(modelCode.getCode());
			fw.close();
		}
	}
	
	/*
	 * アソシエーション生成メソッド
	 */
	private void association() {
		Map<Table, String> relationalTables = checkRelationalTables(table);
		for(Map.Entry<Table, String> map : relationalTables.entrySet()) {
			switch(map.getValue()) {
			case "1対1": hasOne.add(tableToModel(map.getKey().getName())); break;
			case "1対多": hasMany.add(tableToModel(map.getKey().getName())); break;
			case "多対1": belongsTo.add(tableToModel(map.getKey().getName())); break;
			default: break; 
			}
		}
		if(hasOne.size() != 0) {
			modelCode.associationHead("hasOne");
			for(String s : hasOne) {
				modelCode.hasOne(s);
			}
			modelCode.associationFoot();
		}
		if(hasMany.size() != 0) {
			modelCode.associationHead("hasMany");
			for(String s : hasMany) {
				modelCode.hasMany(s);
			}
			modelCode.associationFoot();
		}
		if(belongsTo.size() != 0) {
			modelCode.associationHead("belongsTo");
			for(String s : belongsTo) {
				modelCode.belongsTo(s);
			}
			modelCode.associationFoot();
		}
	}
	
	/*
	 * 画面遷移図チェックメソッド
	 * 追加、編集機能があった場合バリデーションを、ログイン機能があった場合はログインの生成を行う
	 */
	private void checkSTDiagram() {
		boolean validate = false, login = false;
		for(Screen s : stDiagram.getScreens()) {
			if(!table.getName().equals(s.getTableName())) continue;
			switch(s.getFunction()) {
			case "add":
			case "edit":
				validate = true;
				break;
			case "login":
				login = true;
				break;
			default:
				break;	
			}
		}
		if(validate) validation();
		if(login) modelCode.login();
	}
	
	/*
	 * バリデーション生成メソッド
	 */
	private void validation() {
		for(Column c : table.getColumns()) {
			if(c.getName().equals("id")) continue;
			//if(c.getName().endsWith("_id")) continue;
			switch(c.getType()) {
			case "DATETIME":
				if(!exitValidation) modelCode.validationHead();
				modelCode.validation(c.getName(), "datetime");
				exitValidation = true;
				break;
			case "DATE":
				if(!exitValidation) modelCode.validationHead();
				modelCode.validation(c.getName(), "date");
				exitValidation = true;
				break;
			default:
				break;
			}
			if(c.isNotNull()) {
				if(!exitValidation) modelCode.validationHead();
				modelCode.validation(c.getName(), "notBlank");
				exitValidation = true;
			}
		}
		if(exitValidation) modelCode.validationFoot();
		exitValidation = false;
	}
	
}
