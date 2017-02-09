package model;

import java.util.ArrayList;
import java.util.List;

import loder.astah.DataDiagram;
import loder.astah.STDiagram;
import loder.astah.Table;
import loder.astah.Column;
import loder.astah.Screen;
import loder.astah.Transition;

public class DiagramChecker {
	
	private DataDiagram dataDiagram;
	private STDiagram stDiagram;
	private List<String> warnings;
	
	public DiagramChecker(DataDiagram dataDiagram, STDiagram stDiagram) {
		this.dataDiagram = dataDiagram;
		this.stDiagram = stDiagram;
		warnings = new ArrayList<String>();
	}
	
	public void checkDiagram() {
		checkERDiagram();
		checkSTDiagram();
	}
	
	private void checkERDiagram() {
		for(Table t : dataDiagram.getTables()) {
			if(!t.getName().endsWith("s")) warnings.add(t.getName() + "のテーブル名が複数形になっていません。");
			boolean exitID = false;
			for(Column c : t.getColumns()) {
				if(c.getName().equals("id")) exitID = true;
				if(c.isForeignKey()) {
					String cn = c.getName(); //cn:column name
					if(!cn.endsWith("_id")) {
						warnings.add(t.getName() + "の外部キーの末尾が_idになっていません。");
						continue;
					}
					boolean correctRelation = false;
					for(Table table : dataDiagram.getTables()) {
						String s = table.getName();
						if(s.substring(0, s.length() - 1).equals(cn.substring(0, cn.length()-3))) {
							correctRelation = true;
							break;
						}
					}
					if(!correctRelation) warnings.add(t.getName() + "の" + 
							cn + "が「関係のあるテーブル名の単数形」＋「_id」の形になっていません。");
				}
			}
			if(!exitID) warnings.add(t.getName() + "に主キーであるidが存在しません。");
			boolean exitTableName = false;
			for(Screen s : stDiagram.getScreens()) {
				if(s.getTableName().equals(t.getName())) {
					exitTableName = true;
					break;
				}
			}
			if(!exitTableName) warnings.add(t.getName() + "が画面遷移図のプロパティビューに一切存在しません。");
		}
	}
	
	private void checkSTDiagram() {
		for(Screen s : stDiagram.getScreens()) {
			if(s.getTableName()==null||s.getTableName().length()==0||s.getTableName().startsWith("nottable")) {
				warnings.add(s.getName() + "のプロパティビューにテーブル名が記入されていません。");
			}
			if(s.getFunction().equals("login")) {
				boolean username = false, password = false;
				for(Table t : dataDiagram.getTables()) {
					if(t.getName().equals(s.getTableName())) {
						for(Column c : t.getColumns()) {
							if(c.getName().equals("username")) username = true;
							if(c.getName().equals("password")) password = true;
						}
					}
				}
				if(!username) {
					String message = s.getName() + "でログイン機能を生成しようとしましたが、";
					message += s.getTableName() + "にusernameの属性が存在しませんでした。";
					warnings.add(message);
				}
				if(!password) {
					String message = s.getName() + "でログイン機能を生成しようとしましたが、";
					message += s.getTableName() + "にpasswordの属性が存在しませんでした。";
					warnings.add(message);
				}
			}
			switch(s.getFunction()) {
			case "index": index(s); break;
			case "add": add(s); break;
			case "confirm": confirm(s); break;
			case "view": view(s); break;
			case "edit": edit(s); break;
			case "login": login(s); break;
			case "result": result(s); break;
			}
		}
		
	}
	
	private void add(Screen screen) {
		boolean exitAddLabel = false;
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("登録")||t.getLabel().contains("追加")||t.getLabel().contains("保存")
					||t.getLabel().contains("作成")||t.getLabel().contains("確認")) {
				exitAddLabel = true;
				break;
			}
		}
		if(!exitAddLabel) warnings.add(screen.getName() + "で登録(追加、保存、作成)、あるいは確認の遷移が存在しません。");
	}
	
	private void confirm(Screen screen) {
		boolean exitAddLabel = false;
		boolean exitConfirmLabel = false;
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) {
				if(t.getLabel().contains("確認")) exitConfirmLabel = true;
			} else {
				if(t.getLabel().contains("登録")||t.getLabel().contains("追加")||t.getLabel().contains("保存")
						||t.getLabel().contains("作成")) {
					exitAddLabel = true;
					break;
				}
			}
		}
		if(!exitAddLabel) warnings.add(screen.getName() + "で登録(追加、保存、作成)後の遷移が存在しません。");
		if(!exitConfirmLabel) warnings.add(screen.getName() + "に遷移してくるラベルに「確認」が含まれていません。");
	}
	
	private void index(Screen screen) {
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("検索")) {
				Screen os = searchScreen(t.getOpponent()); //os:opponent screen
				if(!os.getFunction().equals("index")&&!os.getFunction().equals("result")) {
					warnings.add(screen.getName() + "で検索後の遷移が正しくありません。");
				}
				if(!screen.getTableName().equals(os.getTableName())) {
					warnings.add(screen.getName() + "のテーブル名とその検索先の" + os.getTableName() + "のテーブル名が一致しません。");
				}
			}
		}
	}
	
	private void view(Screen screen) {
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) {
				if(!t.getLabel().contains("登録")&&!t.getLabel().contains("追加")&&!t.getLabel().contains("保存")&&
						!t.getLabel().contains("作成")&&!t.getLabel().contains("詳細")&&!t.getLabel().contains("完了")) {
					warnings.add(screen.getName() + "に遷移してくる「" + t.getLabel() + "」ラベルが正しくないです。");
				}
				if(!screen.getTableName().equals(searchScreen(t.getOpponent()).getTableName())) {
					warnings.add(screen.getName() + "のテーブル名とそこに遷移してくる" + t.getOpponent() + "のテーブル名が一致しません。");
				}
			}
		}
	}
	
	private void edit(Screen screen) {
		boolean exitEditLabel = false;
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("完了")) {
				exitEditLabel = true;
				break;
			}
		}
		if(!exitEditLabel) warnings.add(screen.getName() + "で変更(編集)後の遷移が存在しません。");
	}
	
	private void login(Screen screen) {
		boolean exitLoginLabel = false;
		for(Transition t : screen.getTransitions()) {
			if(t.isEnter()) continue;
			if(t.getLabel().contains("ログイン")||t.getLabel().contains("認証")) {
				exitLoginLabel = true;
				break;
			}
		}
		if(!exitLoginLabel) warnings.add(screen.getName() + "でログイン(認証)後の遷移が存在しません。");
	}
	
	private void result(Screen screen) {
		boolean exitSearch = false;
		for(Transition t : screen.getTransitions()) {
			if(!t.isEnter()) continue;
			if(t.getLabel().contains("検索")) {
				exitSearch = true;
				break;
			}
		}
		if(!exitSearch) warnings.add(screen.getName() + "に遷移してくるラベルに「検索」が存在しません。");
	}
	
	/*
	 * 画面名から一致する画面インスタンスを探すメソッド
	 * 引数：画面名(String)
	 * 返り値：画面インスタンス(Screen)
	 */
	private Screen searchScreen(String screenName) {
		for(Screen screen : stDiagram.getScreens()) {
			if(screen.getName().equals(screenName)) return screen;
		}
		return null;
	}
	
	
	public List<String> getWarnings() {
		return warnings;
	}
	

}
