package generator.cakephp;

/*
 * コントローラ用のコードが格納してあるクラス
 * 1つのコントローラにつき1つ生成される
 */
public class ControllerCode {

	private String c; //codeのc
	private String modelName; //Model名(.phpなし)
	private boolean generatedDelete;
	
	public ControllerCode(String modelName) {
		this.modelName = modelName;
		c = "";
		generatedDelete = false;
	}
	
	/*
	 * ソースのヘッド部分のコード
	 */
	public void head() {
		c += "<?php\t";
		c += "class " + modelName + "sController extends AppController {\n";
		c += "\tpublic $name = '" + modelName + "s';\n\n";
	}
	
	/*
	 * $usesの先頭部分のコード
	 */
	public void usesHead() {
		c += "\t//使用するModel名を列挙\n";
		c += "\tpublic $uses = array(";
	}
	
	/*
	 * $usesのモデルを列挙する部分のコード
	 */
	public void uses(String model) {
		c += "'" + model + "', ";
	}
	
	/*
	 * $usesの末尾部分のコード
	 */
	public void usesFoot() {
		c = c.substring(0,c.length()-2);
		c += ");\n\n";
	}
	
	/*
	 * $componentsの先頭部分のコード
	 */
	public void componentsHead() {
		c += "\t//使用するComponentを列挙\n";
		c += "\tpublic $components = array(";
	}
	
	/*
	 * $componentsの使用するComponentを列挙する部分のコード
	 */
	public void components(String component) {
		c += "'" + component + "', ";
	}
	
	/*
	 * $componentsの末尾部分のコード
	 */
	public void componentsFoot() {
		c = c.substring(0,c.length()-2);
		c += ");\n\n";
	}
	
	/*
	 * beforeFilterのヘッド部分のコード
	 */
	public void beforeFilterHead() {
		c += "\tpublic function beforeFilter(){\n";
	}
	
	/*
	 * beforeFilterのコード
	 */
	public void beforeFilter(String beforeFilter) {
		switch(beforeFilter) {
		case "Auth":
			c += "\t\t$this->Auth->authError='ログインが必要です';\n";
			c += "\t\t//認証なしでも表示できる画面(アクション)を列挙する\n";
			c += "\t\t$this->Auth->allow('add');\n";
			break;
		}
	}
	
	/*
	 * beforeFilterのフッタ部分のコード
	 */
	public void beforeFilterFoot() {
		c += "\t}\n\n";
	}
	
	/*
	 * 追加機能のヘッド部分のコード
	 */
	public void addHead() {
		c += "\tpublic function add() {\n";
	}
	
	/*
	 * 追加機能のコード
	 * 引数：遷移先のコントローラ, 遷移先のビュー
	 */
	public void add(String controller, String view) {
		c += "\t\t//POST送信されたら\n";
		c += "\t\tif ($this->request->is('post')) {\n";
		c += "\t\t\t$this->" + modelName + "->create();\n";
		c += "\t\t\t//送信されたデータをテーブルに保存\n";
		c += "\t\t\tif ($this->" + modelName + "->save($this->request->data)) {\n";
		c += "\t\t\t\t//FlashComponentを使用\n";
		c += "\t\t\t\t$this->Flash->set('保存しました。');\n";
		c += "\t\t\t\t//" + controller + "の" + view + "に遷移する\n";
		c += "\t\t\t\t$this->redirect(array('controller' => '" + controller + 
				"', 'action' => '" + view + "', $this->" + modelName + "->id));\n";
		c += "\t\t\t} else {\n";
		c += "\t\t\t\t$this->Flash->set('保存に失敗しました。');\n";
		c += "\t\t\t}\n";
		c += "\t\t}\n";
		c += "\t}\n\n";
	}
	
	/*
	 * 追加確認機能のヘッドコード(addメソッドの中に記述される)
	 */
	public void confirmHead() {
		c += "\t\t//POST送信されたら\n";
		c += "\t\tif ($this->request->is('post')) {\n";
	}
	
	/*
	 * 追加確認機能の関連テーブル部分を記述するコード
	 * 引数：関連のあるモデル名, 自分のカラム名(外部キー)
	 */
	public void confirmRelation(String model, String column) {
		c += "\t\t\t$" + model.toLowerCase() + " = $this->" + model + "->find('first', array('conditions' => array('" + 
				model + ".id' => $this->request->data['" + modelName + "']['" + column + "'])));\n";
		c += "\t\t\t$this->set('" + model.toLowerCase() + "', $" + model.toLowerCase() + ");\n";
	}
	
	/*
	 * 追加確認機能の主な部分のコード
	 * 引数：遷移先のコントローラ, 遷移先のビュー
	 */
	public void confirm(String controller, String view) {
		c += "\t\t\tswitch($this->request->data['confirm']) {\n";
		c += "\t\t\t\tcase 'confirm':\n";
		c += "\t\t\t\t$this->render('confirm');\n";
		c += "\t\t\t\tbreak;\n";
		c += "\t\t\t\tcase 'complete':\n";
		c += "\t\t\t\t$this->" + modelName + "->create();\n";
		c += "\t\t\t\t//送信されたデータをテーブルに保存\n";
		c += "\t\t\t\tif ($this->" + modelName + "->save($this->request->data)) {\n";
		c += "\t\t\t\t\t//FlashComponentを使用\n";
		c += "\t\t\t\t\t$this->Flash->set('保存しました。');\n";
		c += "\t\t\t\t\t//" + controller +"の" + view + "に遷移する\n";
		c += "\t\t\t\t\t$this->redirect(array('controller' => '" + controller + 
				"', 'action' => '" + view + "', $this->" + modelName + "->id));\n";
		c += "\t\t\t\t} else {\n";
		c += "\t\t\t\t\t$this->Flash->set('保存に失敗しました。');\n";
		c += "\t\t\t\t}\n";
		c += "\t\t\t\tbreak;\n";
		c += "\t\t\t}\n";
		c += "\t\t}\n";
		c += "\t}\n\n";
	}
	
	/*
	 * 編集機能のヘッド部分のコード
	 */
	public void editHead() {
		c += "\tpublic function edit($id = null) {\n";
		c += "\t\t$this->" + modelName + "->id = $id;\n";
		c += "\t\t//そのidが存在しなかった場合\n";
		c += "\t\tif (!$this->" + modelName + "->exists()) {\n";
		c += "\t\t\tthrow new NotFoundException('そのidのレコードは存在しません。');\n";
		c += "\t\t}\n";
	}
	
	/*
	 * 編集機能のコード
	 * 引数：遷移先のコントローラ, 遷移先のビュー
	 */
	public void edit(String controller, String view) {
		c += "\t\t//POST, PUT送信されたら\n";
		c += "\t\tif ($this->request->is('post') || $this->request->is('put')) {\n";
		c += "\t\t\t//送信されたデータをテーブルに保存\n";
		c += "\t\t\tif ($this->" + modelName + "->save($this->request->data)) {\n";
		c += "\t\t\t\t//FlashComponentを使用\n";
		c += "\t\t\t\t$this->Flash->set('更新しました。');\n";
		c += "\t\t\t\t//" + controller + "の" + view + "に遷移する\n";
		c += "\t\t\t\t$this->redirect(array('controller' => '" + controller + 
				"', 'action' => '" + view + "', $this->" + modelName + "->id));\n";
		c += "\t\t\t} else {\n";
		c += "\t\t\t\t$this->Flash->set('更新に失敗しました。');\n";
		c += "\t\t\t}\n";
		c += "\t\t} else {\n";
		c += "\t\t\t//そのidのレコードを取得\n";
		c += "\t\t\t$options = array('conditions' => array('" + modelName + 
				".' . $this->" + modelName + "->primaryKey => $id));\n";
		c += "\t\t\t$this->request->data = $this->" + modelName + "->find('first', $options);\n";
		c += "\t\t}\n";
		c += "\t}\n\n";
	}
	
	/*
	 * ラジオボタンのコード
	 * 引数：ラジオボタンの生成回数, 対象のモデル名, 表示するカラム名
	 */
	public void radio(int count, String model, String column) {
		c += "\t\t//ラジオボタン用に" + model + "の値を取得\n";
		c += "\t\t$radio" + count +" = $this->" + model + 
				"->find('list', array('fields' => array('id', '" + column + "')));\n";
		c += "\t\t$this->set('radio" + count + "', $radio" + count + ");\n";
	}
	
	/*
	 * 一覧表示機能のヘッド部分のコード
	 * 引数：検索遷移が存在するか
	 */
	public void indexHead(boolean search) {
		c += "\tpublic function index() {\n";
		if(search) {
			c += "\t\t$keyword = \"\";\n";
			c += "\t\tif(isset($this->request->query['keyword'])){\n";
			c += "\t\t\t$keyword = $this->request->query['keyword'];\n";
			c += "\t\t}\n";
		}
		c += "\t\t//Paginationの設定(limit：1ページの表示件数、order：並び順)\n";
		c += "\t\t$this->Paginator->settings = array(\n";
		c += "\t\t\t'limit' => 10,\n";
		c += "\t\t\t'order' => array('" + modelName + ".id' => 'desc')";
		if(search) {
			c += ",\n";
			c += "\t\t\t'conditions' => array(\n";
			c += "\t\t\t'or' => array(\n";
		}

	}
	
	/*
	 * 一覧表示機能のフッタ部分のコード
	 * 引数：検索遷移が存在するか
	 */
	public void indexFoot(boolean search) {
		if(search) {
			c = c.substring(0,c.length()-2) + "\n";
			c += "\t\t\t)\n";
			c += "\t\t)\n";
		} else {
			c += "\n";
		}
		c += "\t\t);\n";
		c += "\t\t//ビューに値をセット\n";
		c += "\t\t$this->set('data', $this->Paginator->paginate('" + modelName + "'));\n";
		c += "\t}\n\n";
	}
	
	/*
	 * 検索結果機能のヘッド部分のコード
	 */
	public void resultHead() {
		c += "\tpublic function result() {\n";
		c += "\t\t$keyword = \"\";\n";
		c += "\t\tif(isset($this->request->query['keyword'])){\n";
		c += "\t\t\t$keyword = $this->request->query['keyword'];\n";
		c += "\t\t}\n";
		c += "\t\t//Paginationの設定(limit：1ページの表示件数、order：並び順)\n";
		c += "\t\t$this->Paginator->settings = array(\n";
		c += "\t\t\t'limit' => 10,\n";
		c += "\t\t\t'order' => array('" + modelName + ".id' => 'desc')";
		c += ",\n";
		c += "\t\t\t'conditions' => array(\n";
		c += "\t\t\t'or' => array(\n";
	}
	
	/*
	 * 検索結果機能のフッタ部分のコード
	 */
	public void resultFoot() {
		c = c.substring(0,c.length()-2) + "\n";
		c += "\t\t\t)\n";
		c += "\t\t)\n";
		c += "\t\t);\n";
		c += "\t\t//ビューに値をセット\n";
		c += "\t\t$this->set('data', $this->Paginator->paginate('" + modelName + "'));\n";
		c += "\t}\n\n";
	}
	
	/*
	 * 検索機能の検索対象を記述する部分のコード
	 */
	public void search(String model, String column) {
		c += "\t\t\t\tarray('" + model + "." + column + " LIKE' => '%'.$keyword.'%'),\n";
	}
	
	/*
	 * 検索対象が見つからなかったときのコード
	 */
	public void searchTargetNotFound() {
		c += ")";
	}
	
	/*
	 * 詳細表示機能のコード
	 */
	public void view() {
		c += "\tpublic function view($id = null) {\n";
		c += "\t\t//そのidが存在しなかった場合\n";
		c += "\t\tif (!$this->" + modelName + "->exists($id)) {\n";
		c += "\t\t\tthrow new NotFoundException('そのidのレコードは存在しません。');\n";
		c += "\t\t}\n";
		c += "\t\t//そのidのレコードを取得\n";
		c += "\t\t$options = array('conditions' => array('" + modelName + 
				".' . $this->" + modelName + "->primaryKey => $id));\n";
		c += "\t\t$this->set('data', $this->" + modelName + "->find('first', $options));\n";
		c += "\t}\n\n";
	}
	
	/*
	 * 削除機能のコード
	 * 引数：遷移先のコントローラ名, 遷移先のビュー名
	 */
	public void delete(String controller, String view) {
		if(generatedDelete) return; //同じコードを生成しないように
		c += "\tpublic function delete($id = null) {\n";
		c += "\t\t$this->" + modelName + "->id = $id;\n";
		c += "\t\t//そのidが存在しなかった場合\n";
		c += "\t\tif (!$this->" + modelName + "->exists()) {\n";
		c += "\t\t\tthrow new NotFoundException('そのidのレコードは存在しません。');\n";
		c += "\t\t}\n";
		c += "\t\t//post,delteのときのみ実行される\n";
		c += "\t\t$this->request->onlyAllow('post', 'delete');\n";
		c += "\t\tif ($this->" + modelName + "->delete()) {\n";
		c += "\t\t\t//FlashComponentを使用\n";
		c += "\t\t\t$this->Flash->set('削除しました。');\n";
		c += "\t\t\t//" + controller + "の" + view + "に遷移する\n";
		c += "\t\t\t$this->redirect(array('controller' => '" + controller + "','action' => '" + view + "'));\n";
		c += "\t\t} else {\n";
		c += "\t\t\t$this->Flash->set('削除に失敗しました。');";
		c += "\t\t}\n";
		c += "\t}\n\n";
		generatedDelete = true;
	}
	
	/*
	 * 履歴表示機能のコード
	 * 引数：履歴表示に使うモデル名、履歴表示に使うカラム名
	 */
	public void history(String model, String column) {
		c += "\tpublic function history() {\n";
		c += "\t\t//Paginationの設定(limit：1ページの表示件数、order：並び順、conditions：取得するデータの対象)\n";
		c += "\t\t$this->Paginator->settings = array(\n";
		c += "\t\t\t'limit' => 10,\n";
		c += "\t\t\t'order' => array('" + modelName + ".id' => 'desc')";
		if(model != null) c += ",\n\t\t\t'conditions' => array('" + model + "." + column + " <' => date('Y-m-d'))";
		c += ");\n";
		c += "\t\t//ビューに値をセット\n";
		c += "\t\t$this->set('data', $this->Paginator->paginate('" + modelName + "'));\n";
		c += "\t}\n\n";
	}
	
	/*
	 * 全削除機能のコード
	 * 引数：遷移先のコントローラ名, 遷移先のビュー名
	 */
	public void delete_all(String controller, String view) {
		c += "\tpublic function delete_all() {\n";
		c += "\t\tif($this->request->is('post','delete_all')) {\n";
		c += "\t\t\t//テーブルの全レコードを削除\n";
		c += "\t\t\tif ($this->" + modelName + "->query('TRUNCATE " + modelName.toLowerCase() + "s;')) {\n";
		c += "\t\t\t\t//FlashComponentを使用\n";
		c += "\t\t\t\t$this->Flash->set('" + modelName.toLowerCase() + "sテーブルの全レコードを削除しました。');\n";
		c += "\t\t\t\t//" + controller + "の" + view + "に遷移する\n";
		c += "\t\t\t\t$this->redirect(array('controller' => '" + controller + "','action' => '" + view + "'));\n";
		c += "\t\t\t} else {\n";
		c += "\t\t\t\t$this->Flash->set('全削除に失敗しました。');\n";
		c += "\t\t\t}\n";
		c += "\t\t}\n";
		c += "\t}\n\n";
	}
	
	/*
	 * ログインのコード
	 * 引数：遷移先のコントローラ名, 遷移先のビュー名
	 */
	public void login(String controller, String view) {
		c += "\tpublic function login() {\n";
		c += "\t\t//POST送信されたら\n";
		c += "\t\tif ($this->request->is('post')) {\n";
		c += "\t\t\tif ($this->Auth->login()) {\n";
		c += "\t\t\t\t$this->redirect(array('controller' => '" + controller + "', 'action' => '" + view + "'));\n";
		c += "\t\t\t}\n";
		c += "\t\t\telse $this->Flash->set('ログインに失敗しました。');\n";
		c += "\t\t}\n";
		c += "\t}\n\n";
	}
	
	/*
	 * 地図表示のコード
	 */
	public void map() {
		c += "\tpublic function map() {\n";
		c += "\t\t$data = $this->" + modelName + "->find('all');\n";
		c += "\t\t$this->set('data', $data);\n";
		c += "\t}\n\n";
	}
	
	/*
	 * その他のコード(noneやhomeや未実装の機能など)
	 */
	public void other(String action) {
		c += "\tpublic function " + action + "() {\n";
		c += "\t}\n\n";
	}
	
	/*
	 * ソースのフッタ部分のコード
	 */
	public void foot() {
		c += "}\n";
		c += "?>\n";
	}
	
	public String getCode() {
		return c;
	}
}
