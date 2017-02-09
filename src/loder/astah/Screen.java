package loder.astah;

import java.util.List;
import java.util.ArrayList;

/*
 * 画面データクラス
 */
public class Screen {

	private String name; //画面名
	private String function; //メソッド名
	private String tableName; //プロパティビューのテーブル名
	private List<Transition> transitions; //遷移情報のリスト
	private boolean topPage; //トップページかどうか
	private static int noneCount = 1; //不明メソッドカウント
	
	/*
	 * コンストラクタ
	 */
	public Screen(String name, String tableName) {
		this.name = name;
		this.tableName = tableName;
		transitions = new ArrayList<Transition>();
		topPage = false;
		setFunction();
	}
	
	/*
	 * メソッド(機能)選択メソッド
	 */
	private void setFunction() {
		if(name.contains("認証")||name.contains("ログイン")) function = "login";
		else if(name.contains("追加")||name.contains("登録")||name.contains("保存")||name.contains("作成")) {
			if(name.contains("確認")) function = "confirm";
			else function = "add";
		}
		else if(name.contains("編集")||name.contains("変更")) function = "edit";
		else if(name.contains("全削除")) function = "delete_all";
		else if(name.contains("一覧")) function = "index";
		else if(name.contains("詳細")) function = "view";
		else if(name.contains("履歴")) function = "history";
		else if(name.contains("ホーム")) function = "home";
		else if(name.contains("トップ")) function = "top";
		else if(name.contains("地図")) function = "map";
		else if(name.contains("検索結果")) function = "result";
		else {
			function = "none" + noneCount; //該当なし
			noneCount++;
		}
	}
	
	public void newTransition(String opponent, String label, boolean enter) {
		transitions.add(new Transition(opponent, label, enter));
	}
	
	public void setTopPage() {
		topPage = true;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFunction() {
		return function;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public List<Transition> getTransitions() {
		return transitions;
	}
	
	public boolean isTopPage() {
		return topPage;
	}
}
