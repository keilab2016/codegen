package generator.cakephp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import loder.astah.Column;
import loder.astah.DataDiagram;
import loder.astah.STDiagram;
import loder.astah.Screen;
import loder.astah.Table;
import loder.astah.Transition;

/*
 * ビュー生成クラス
 */
public class ViewCreator extends MVCCreator{
	
	private List<String> controllers; //コントローラ名のリスト(テーブル名のリストとほぼ同じ)
	private ViewCode viewCode;
	private Table table; //テーブルインスタンス(テーブルが存在しない場合null)
	private List<Screen> screens; //画面のリスト
	private Screen screen; //画面インスタンス
	private Map<Table, String> relationalTables; //関係のあるテーブル<テーブルインスタンス、多重度>
	private String apiKey; //apiKey
	
	public ViewCreator(String path, DataDiagram dataDiagram, STDiagram stDiagram, String apiKey) {
		this.path = path + "/app/View";
		this.dataDiagram = dataDiagram;
		this.stDiagram = stDiagram;
		this.apiKey = apiKey;
		controllers = new ArrayList<String>();
		for(Screen s : stDiagram.getScreens()) {
			if(s.getTableName()==null || s.getTableName().length()==0) continue;
			if(!controllers.contains(s.getTableName())) controllers.add(s.getTableName());
		}
	}

	/*
	 * 初期化メソッド
	 */
	private void initialize() {
		viewCode = new ViewCode(tableToModel(screen.getTableName()));
		table = searchTable(screen.getTableName());
		if(table != null) relationalTables = checkRelationalTables(table);
	}
	
	/*
	 * 生成メソッド(non-Javadoc)
	 * @see codegenerator.generator.cakephp.MVCCreator#create()
	 * ビューフォルダを生成し、コード生成を行い、ファイルを生成し、そのファイルにコードを記述する
	 */
	public void create() throws IOException{
		for(String controller : controllers) {
			String viewPath = path + "/" +upFirstChar(controller);
			File folder = new File(viewPath);
			if(!folder.exists()) folder.mkdirs();
			screens = new ArrayList<Screen>();
			for(Screen s : stDiagram.getScreens()) {
				if(s.getTableName().equals(controller)) screens.add(s);
			}
			for(Screen s : screens) {
				screen = s;
				initialize();
				createCode();
				File file = new File(viewPath + "/" + screen.getFunction() + ".ctp");
				file.createNewFile();
				FileWriter fw = new FileWriter(file);
				fw.write(viewCode.getCode());
				fw.close();	
			}
		}
	}
	
	/*
	 * コード生成メソッド
	 */
	private void createCode() {
		head();
		if(table == null) {
			switch(screen.getFunction()) {
			case "map": map(); break;
			default: other(); break;
			}
			return;
		}
		switch(screen.getFunction()) {
		case "add": addEdit(); break;
		case "confirm": confirm(); break;
		case "edit": addEdit(); break;
		case "index": indexHistoryResult(); break;
		case "history": indexHistoryResult(); break;
		case "result": indexHistoryResult(); break;
		case "view": view(); break;
		case "delete_all": delete_all(); break;
		case "login": login(); break;
		case "map": map(); break;
		default: other(); break;
		}
	}
	
	private void head() {
		viewCode.actionsHead();
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("遷移")) {
				Screen os = searchScreen(t.getOpponent()); //os:opponentScreen
				viewCode.actions(os.getName(), os.getTableName(), os.getFunction());
			}
		}
		viewCode.actionsFoot();
	}
	
	private void addEdit() {
		viewCode.addEditHead(screen.getName());
		int count = 1;
		boolean exitConfirm = false;
		for(Column c : table.getColumns()) {
			if(c.getName().equals("id")) continue;
			if(c.getName().endsWith("_id")) {
				viewCode.addEdit(count, "radio", c.getName());
				count ++;
				continue;
			}
			switch(c.getType()) {
			case "DATE": viewCode.addEdit(0, "date", c.getName()); break;
			case "DATETIME": viewCode.addEdit(0, "datetime", c.getName()); break;
			default: viewCode.addEdit(0, "text", c.getName()); break;
			}
		}
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("確認")) exitConfirm = true;
		}
		Screen cancelScrren = checkCancel(); //キャンセル後の遷移先の画面
		if(cancelScrren == null) viewCode.addEditFoot(null, null, exitConfirm);
		else viewCode.addEditFoot(cancelScrren.getTableName(), cancelScrren.getFunction(), exitConfirm);
	}
	
	private void confirm() {
		viewCode.confirmHead(screen.getName());
		for(Column c : table.getColumns()) {
			if(c.getName().equals("id")) continue;
			String val = "this->request->data";
			String model = tableToModel(table.getName());
			String column = c.getName();
			String type = c.getType();
			if(c.getName().endsWith("_id")) {
				val = c.getName().substring(0, c.getName().length()-3);
				model = upFirstChar(val);
				Table relationalTable = searchTable(model.toLowerCase() + "s");
				for(Column rc : relationalTable.getColumns()) {
					if(rc.getName().equals("id")) continue;
					if(rc.getName().endsWith("_id")) continue;
					column = rc.getName();
					type = rc.getType();
					break;
				}
			}
			viewCode.confirm(val, c.getName(), model, column, type);
		}
		viewCode.confirmFoot();
	}
	
	private void indexHistoryResult() {
		boolean search = false; //検索機能を搭載するか
		viewCode.indexHistoryResultHead(screen.getName());
		if(screen.getFunction().equals("index")) search = search();
		viewCode.indexHistoryResultTableHead();
		List<String> types = new ArrayList<String>(); //カラムの種類のリスト
		List<String> models = new ArrayList<String>(); //モデル名のリスト(typesと対応している)
		List<String> columns = new ArrayList<String>(); //カラム名のリスト(typesと対応している)
		for(Column c : table.getColumns()) {
			if(c.getName().equals("id")) continue;
			if(c.getName().endsWith("_id")) {
				String relationalName = c.getName().substring(0, c.getName().length()-3) + "s";
				Table relationalTable = searchTable(relationalName);
				if(relationalTable == null) continue;
				for(Column rc : relationalTable.getColumns()) {
					if(!rc.getName().equals("id")&&!rc.getName().endsWith("_id")) {
						viewCode.indexHistoryResultSort(rc.getName());
						types.add(rc.getType());
						models.add(tableToModel(relationalName));
						columns.add(rc.getName());
						break;
					}
				}
				continue;
			}
			viewCode.indexHistoryResultSort(c.getName());
			types.add(c.getType());
			models.add(tableToModel(table.getName()));
			columns.add(c.getName());
		}
		viewCode.indexHistoryResult();
		for(int i = 0; i < types.size(); i++) {
			switch(types.get(i)) {
			case "DATETIME": viewCode.indexHistoryResultDisplay("datetime", models.get(i), columns.get(i)); break;
			case "DATE": viewCode.indexHistoryResultDisplay("date", models.get(i), columns.get(i)); break;
			default: viewCode.indexHistoryResultDisplay("text", models.get(i), columns.get(i)); break;
			}
		}
		String edit = null, view = null, delete = null;
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("削除")) {
				delete = "削除";
				continue;
			}
			Screen os = searchScreen(t.getOpponent()); //os:opponentScrren
			if(os.getName().contains("編集")) edit = "編集";
			else if(os.getName().contains("変更")) edit = "変更";
			else if(os.getName().contains("詳細")) view = "詳細";
		}
		viewCode.indexHistoryResultFoot(edit, view, delete);
		if(search) viewCode.searchStyle();
	}
	
	private boolean search() {
		boolean result = false; //検索結果画面に遷移するか
		for(Transition t : screen.getTransitions()) {
			if(t.getLabel().contains("検索")) {
				if(t.isEnter()) {
					viewCode.search("index");
					return true;
				} else {
					result = true;
				}
			}
		}
		if(result) {
			viewCode.search("result");
			return true;
		}
		return false;
	}
	
	private void view() {
		viewCode.viewHead(screen.getName());
		//まず関連のあるテーブルの中身を表示
		for(Map.Entry<Table, String> map : relationalTables.entrySet()) {
			if(map.getValue().equals("多対多")||map.getValue().equals("1対多")) continue;
			for(Column c : map.getKey().getColumns()) {
				if(c.getName().equals("id")) continue;
				if(c.getName().endsWith("_id")) continue;
				switch(c.getType()) {
				case "DATETIME":
					viewCode.view("datetime", tableToModel(map.getKey().getName()), c.getName());
					break;
				case "DATE":
					viewCode.view("date", tableToModel(map.getKey().getName()), c.getName());
					break;
				default:
					viewCode.view("text", tableToModel(map.getKey().getName()), c.getName());
					break;
				}
			}
		}
		//現在のテーブルの中身を表示
		for(Column c : table.getColumns()) {
			if(c.getName().equals("id")) continue;
			if(c.getName().endsWith("_id")) continue;
			switch(c.getType()) {
			case "DATETIME":
				viewCode.view("datetime", tableToModel(table.getName()), c.getName());
				break;
			case "DATE":
				viewCode.view("date", tableToModel(table.getName()), c.getName());
				break;
			default:
				viewCode.view("text", tableToModel(table.getName()), c.getName());
				break;
			}
		}
		String edit = null, delete = null;
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("削除")) {
				delete = "削除";
				continue;
			}
			Screen os = searchScreen(t.getOpponent()); //os:opponentScreen
			if(os.getName().contains("編集")) edit = "編集";
			else if(os.getName().contains("変更")) edit = "変更";
		}
		viewCode.viewFoot(edit, delete);
	}
	
	private void delete_all() {
		Screen cancelScreen = checkCancel();
		if(cancelScreen == null) viewCode.delete_all(screen.getName(), null, null);
		else viewCode.delete_all(screen.getName(), cancelScreen.getTableName(), cancelScreen.getFunction());
	}
	
	private void login() {
		viewCode.login(screen.getName());
	}
	
	private void map() {
		if(table == null) {
			viewCode.mapHead(screen.getName());
			viewCode.mapMiddle();
			viewCode.mapFoot(apiKey);
			return;
		}
		viewCode.mapHead(screen.getName());
		boolean exitAddress = false;
		String geocoderModel = "";
		for(Column c : table.getColumns()) {
			if(c.getName().equals("address")) {
				exitAddress = true;
				geocoderModel = tableToModel(table.getName());
				break;
			}
		}
		for(Map.Entry<Table, String> map : relationalTables.entrySet()) {
			if(exitAddress) break;
			if(map.getValue().equals("多対多")||map.getValue().equals("1対多")) continue;
			for(Column c : map.getKey().getColumns()) {
				if(c.getName().equals("address")) {
					exitAddress = true;
					geocoderModel = tableToModel(map.getKey().getName());
					break;
				}
			}
		}
		if(exitAddress) {
			viewCode.geocoderHead();
			for(Column c : table.getColumns()) {
				String model = tableToModel(table.getName());
				String column = c.getName();
				String type = c.getType();
				if(c.getName().equals("id")) continue;
				if(c.getName().endsWith("_id")) {
					String val = c.getName().substring(0, c.getName().length()-3);
					model = upFirstChar(val);
					Table relationalTable = searchTable(model.toLowerCase() + "s");
					for(Column rc : relationalTable.getColumns()) {
						if(rc.getName().equals("id")) continue;
						if(rc.getName().endsWith("_id")) continue;
						column = rc.getName();
						type = rc.getType();
						break;
					}
				}
				viewCode.geocoder(c.getName(), model, column, type);
			}
			viewCode.geocoderFoot(geocoderModel);
		}
		viewCode.mapMiddle();
		if(exitAddress) viewCode.geocodeAddress();
		if(apiKey.equals("") || apiKey == null) apiKey = "YOUR_API_KEY";
		viewCode.mapFoot(apiKey);
	}
	
	private void other() {
		viewCode.other(screen.getName());
	}
	
	/*
	 * キャンセル遷移があるか確認するメソッド
	 * 返り値：遷移先の画面(キャンセル遷移がない場合null)
	 */
	private Screen checkCancel() {
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("キャンセル")) {
				return searchScreen(t.getOpponent());
			}
		}
		return null;
	}

}
