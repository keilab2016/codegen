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
 * コントローラ生成クラス
 */
public class ControllerCreator extends MVCCreator {
	
	private List<String> controllers; //コントローラ名のリスト(実質テーブル名のリストと同じ)
	private boolean existLogin = false; //ログイン機能が存在するか
	private ControllerCode controllerCode;
	private Table table; //テーブルインスタンス(テーブルが存在しない場合null)
	private List<Screen> screens; //画面のリスト
	private Map<Table, String> relationalTables; //関係のあるテーブル<テーブルインスタンス、多重度>
	
	/*
	 * コンストラクタ
	 */
	public ControllerCreator(String path, DataDiagram dataDiagram, STDiagram stDiagram) {
		this.path = path + "/app/Controller";
		this.dataDiagram = dataDiagram;
		this.stDiagram = stDiagram;
		controllers = new ArrayList<String>();
		for(Screen s : stDiagram.getScreens()) {
			if(s.getTableName()==null || s.getTableName().length()==0) continue;
			if(!controllers.contains(s.getTableName())) controllers.add(s.getTableName());
			if(s.getFunction().equals("login")) existLogin = true;
		}
	}

	/*
	 * 初期化メソッド
	 */
	private void initialize(String controller) {
		controllerCode = new ControllerCode(tableToModel(controller));
		table = searchTable(controller);
		screens = new ArrayList<Screen>();
		for(Screen s : stDiagram.getScreens()) {
			if(s.getTableName().equals(controller)) screens.add(s);
		}
		if(table != null) relationalTables = checkRelationalTables(table);
	}
	
	/*
	 * 生成メソッド(non-Javadoc)
	 * @see codegenerator.generator.cakephp.MVCCreator#create()
	 * コード生成を行い、ファイルを生成し、そのファイルにコードを記述する
	 */
	public void create() throws IOException {
		File folder = new File(path);
		if(!folder.exists()) folder.mkdirs();
		for(String controller : controllers) {
			initialize(controller);
			createCode();
			File file = new File(path + "/" + upFirstChar(controller) + "Controller.php");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(controllerCode.getCode());
			fw.close();
		}
	}
	
	/*
	 * コード生成メソッド
	 */
	private void createCode() {
		controllerCode.head();
		if(table == null) {
			for(Screen s : screens) {
				other(s);
			}
			controllerCode.foot();
			return;
		}
		uses();
		components();
		beforeFilter();
		for(Screen s : screens) {
			switch(s.getFunction()) {
			case "add": add(s); break;
			case "confirm": break;
			case "edit": edit(s); break;
			case "index": index(s); break;
			case "result": result(s); break;
			case "view": view(s); break;
			case "history": history(s); break;
			case "delete_all": delete_all(s); break;
			case "login": login(s); break;
			case "map": map(s); break;
			default: other(s); break;
			}
		}
		controllerCode.foot();
	}
	
	private void uses() {
		controllerCode.usesHead();
		controllerCode.uses(tableToModel(table.getName()));
		for(Map.Entry<Table, String> map : relationalTables.entrySet()) {
			if(map.getValue().equals("1対1") || map.getValue().equals("多対1")) {
				controllerCode.uses(tableToModel(map.getKey().getName()));
			}
		}
		controllerCode.usesFoot();
	}
	
	private void components() {
		controllerCode.componentsHead();
		controllerCode.components("Flash");
		if(existLogin) controllerCode.components("Auth");
		for(Screen s : screens) {
			if(s.getFunction().equals("index")) {
				controllerCode.components("Paginator");
				break;
			}
		}
		controllerCode.componentsFoot();
	}
	
	private void beforeFilter() {
		controllerCode.beforeFilterHead();
		if(existLogin) controllerCode.beforeFilter("Auth");
		controllerCode.beforeFilterFoot();
	}
	
	private void add(Screen screen) {
		controllerCode.addHead();
		radio();
		String controller = screen.getTableName(); //遷移先の初期値
		String view = screen.getFunction(); //遷移先の初期値、遷移先が不明な場合は自身に戻ってくるようにする
		boolean exitConfirm = false; //確認ラベルが存在するか
		Screen confirmScreen = null;
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("追加")||t.getLabel().contains("登録")||
					t.getLabel().contains("保存")||t.getLabel().contains("作成")) {
				Screen opponentScreen = searchScreen(t.getOpponent());
				controller = opponentScreen.getTableName();
				view = opponentScreen.getFunction();
			}
			if(t.getLabel().contains("確認")) {
				exitConfirm = true;
				confirmScreen = searchScreen(t.getOpponent());
			}
		}
		if(exitConfirm) confirm(confirmScreen);
		else controllerCode.add(controller, view);
	}
	
	private void confirm(Screen screen) {
		controllerCode.confirmHead();
		for(Column c : table.getColumns()) {
			if(!c.getName().endsWith("_id")) continue;
			String model = upFirstChar(c.getName().substring(0, c.getName().length()-3));
			String column = c.getName();
			controllerCode.confirmRelation(model, column);
		}
		String controller = screen.getTableName(); //遷移先の初期値
		String view = screen.getFunction(); //遷移先の初期値、遷移先が不明な場合は自身に戻ってくるようにする
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("追加")||t.getLabel().contains("登録")||
					t.getLabel().contains("保存")||t.getLabel().contains("作成")) {
				Screen opponentScreen = searchScreen(t.getOpponent());
				controller = opponentScreen.getTableName();
				view = opponentScreen.getFunction();
				break;
			}
		}
		controllerCode.confirm(controller, view);
	}
	
	private void edit(Screen screen) {
		controllerCode.editHead();
		radio();
		String controller = screen.getTableName(); //遷移先の初期値
		String view = screen.getFunction(); //遷移先の初期値、遷移先が不明な場合は自身に戻ってくるようにする
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("完了")) {
				Screen opponentScreen = searchScreen(t.getOpponent());
				controller = opponentScreen.getTableName();
				view = opponentScreen.getFunction();
				break;
			}
		}
		controllerCode.edit(controller, view);
	}
	
	private void radio() {
		int count = 1; //ラジオボタンの生成回数
		for(Column c : table.getColumns()) {
			if(c.getName().endsWith("_id")) {
				String relationalName = c.getName().substring(0, c.getName().length()-3) + "s";
				String model = tableToModel(relationalName);
				Table relationalTable = searchTable(relationalName);
				if(relationalTable == null) continue;
				for(Column rc : relationalTable.getColumns()) {
					if(!rc.getName().equals("id")&&!rc.getName().endsWith("_id")) {
						controllerCode.radio(count, model, rc.getName());
						count++;
						break;
					}
				}
			}
		}
	}
	
	private void index(Screen screen) {
		boolean exitSearch = false;
		for(Transition t : screen.getTransitions()) {
			if(!t.isEnter()) continue;
			if(t.getLabel().contains("検索")) {
				exitSearch = true; 
				break;
			}
		}
		controllerCode.indexHead(exitSearch);
		if(exitSearch) checkSearch();
		controllerCode.indexFoot(exitSearch);
		checkDelete(screen);
	}
	
	private void result(Screen screen) {
		controllerCode.resultHead();
		checkSearch();
		controllerCode.resultFoot();
	}
	
	/*
	 * 検索対象をチェックするメソッド
	 */
	private void checkSearch() {
		boolean target = false; //検索対象が見つかったか
		for(Map.Entry<Table, String> map : relationalTables.entrySet()) {
			if(!map.getValue().equals("1対1") && !map.getValue().equals("多対1")) continue;
			for(Column c : map.getKey().getColumns()) {
				if(c.getName().equals("id")) continue;
				if(c.getName().endsWith("_id")) continue;
				if(!c.getType().equals("CHAR")&&!c.getType().equals("VARCHAR")) continue;
				controllerCode.search(tableToModel(map.getKey().getName()), c.getName());
				target = true;
				break;
			}
		}
		for(Column c : table.getColumns()) {
			if(c.getName().equals("id")) continue;
			if(c.getName().endsWith("id")) continue;
			if(!c.getType().equals("CHAR")&&!c.getType().equals("VARCHAR")) continue;
			controllerCode.search(tableToModel(table.getName()), c.getName());
			target = true;
		}
		if(!target) controllerCode.searchTargetNotFound();
	}
	
	private void view(Screen screen) {
		controllerCode.view();
		checkDelete(screen);
	}
	
	/*
	 * 削除ラベルが存在するかチェックするメソッド
	 * index, viewで使用
	 */
	private void checkDelete(Screen screen) {
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("削除")) {
				delete(t);
				break;
			}
		}
	}
	
	private void delete(Transition transition) {
		String controller = searchScreen(transition.getOpponent()).getTableName();
		String view = searchScreen(transition.getOpponent()).getFunction();
		controllerCode.delete(controller, view);
	}
	
	private void history(Screen screen) {
		for(Table t : dataDiagram.getTables()) {
			for(Column c : t.getColumns()) {
				if(c.getType().equals("DATETIME")||c.getType().equals("DATE")) {
					controllerCode.history(tableToModel(t.getName()), c.getName());
					return;
				}
			}
		}
		controllerCode.history(null, null); //DATE、DATETIME型が見つからなかったとき
	}
	
	private void delete_all(Screen screen) {
		String controller = screen.getTableName(); //遷移先の初期値
		String view = screen.getFunction(); //遷移先の初期値、遷移先が不明な場合は自身に戻ってくるようにする
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("全削除")) {
				Screen opponentScreen = searchScreen(t.getOpponent());
				controller = opponentScreen.getTableName();
				view = opponentScreen.getFunction();
				break;
			}
		}
		controllerCode.delete_all(controller, view);
	}
	
	private void login(Screen screen) {
		String controller = screen.getTableName(); //遷移先の初期値
		String view = screen.getFunction(); //遷移先の初期値、遷移先が不明な場合は自身に戻ってくるようにする
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("認証")||t.getLabel().contains("ログイン")) {
				Screen opponentScreen = searchScreen(t.getOpponent());
				controller = opponentScreen.getTableName();
				view = opponentScreen.getFunction();
				break;
			}
		}
		controllerCode.login(controller, view);		
	}
	
	private void map(Screen screen) {
		controllerCode.map();
	}
	
	private void other(Screen screen) {
		controllerCode.other(screen.getFunction());
	}
	
}
