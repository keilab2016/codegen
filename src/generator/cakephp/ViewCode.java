package generator.cakephp;

/*
 * ビュー用のコードが格納してあるクラス
 * 1つのビューファイルにつき1つ生成される
 */
public class ViewCode {
	
	private String c;
	private String modelName;
	private int datetimeCount; //登録確認画面で日付を確認するときに使う
	private int dateCount;
	
	public ViewCode(String modelName) {
		c = "";
		this.modelName = modelName;
		datetimeCount = 1;
		dateCount = 1;
	}
	
	/*
	 * actions(画面遷移先の一覧を表示する)ヘッダ部分のコード
	 */
	public void actionsHead() {
		c += "<div class=\"actions\">\n";
		c += "\t<h3>画面遷移先</h3>\n";
		c += "\t<ul>\n";
	}
	
	/*
	 * 画面遷移の一覧のコード
	 * 引数：遷移先の画面名, 遷移先のコントローラ名, 遷移先のビュー名
	 */
	public void actions(String name, String controller, String view) {
		c += "<li><?php echo $this->Html->link('" + name + "', array('controller' => '" + 
				controller + "', 'action' => '" + view + "')); ?></li>\n";
	}
	
	/*
	 * actionsのフッタ部分のコード
	 */
	public void actionsFoot() {
		c += "\t</ul>\n";
		c += "</div>\n\n";
	}
	
	/*
	 * 追加、編集機能のヘッダ部分のコード
	 * 引数：画面名
	 */
	public void addEditHead(String name) {
		c += "<div class=\"" + modelName.toLowerCase() + "s form\">\n";
		c += "\t<legend>" + name + "</legend>\n";
		c += "\t<?php echo $this->Form->create('" + modelName + "'); ?>\n";
		c += "\t<fieldset>\n";
		c += "\t\t<?php\n";
		c += "\t\t\t//フォームヘルパーを使用、入力フォームの表示\n";
	}

	/*
	 * 追加、編集機能のフォーム部分のコード
	 * 引数：ラジオボタンの生成回数, フォームの種類(input type), カラム名
	 */
	public void addEdit(int count, String type, String column) {
		c += "\t\t\techo $this->Form->input(";
		switch(type) {
		case "radio":
			c += "'" + column + "', array('type' => 'radio', 'options' => $radio" + 
					count + ", 'legend' => '" + column + "')";
			break;
		case "date":
			c += "'" + column + "', array('label' => '" + column + 
					"', 'dateFormat' => 'YMD', 'monthNames' => false, 'separator' => '/')";
			break;
		case "datetime":
			c += "'" + column + "', array('label' => '" + column + 
					"', 'dateFormat' => 'YMD', 'timeFormat' => '24', 'monthNames' => false, 'separator' => '/')";
			break;
		default:
			c += "'" + column + "', array('label' => '" + column + "')";
			break;
		}
		c += ");\n";
	}
	
	/*
	 * 追加、編集機能のフッタ部分のコード
	 * 引数：キャンセル時の遷移先のコントローラ, ビュー, 確認画面へ遷移するか
	 */
	public void addEditFoot(String controller, String view, boolean confirm) {
		c += "\t\t?>\n";
		c += "\t</fieldset>\n";
		c += "\t<div class=\"actions\">\n";
		c += "\t\t<?php //フォームの中身を送信する ?>\n";
		if(confirm) {
			c += "\t\t<?php echo $this->Form->button('確認', array('type' => 'submit', "
					+ "'name' => 'confirm', 'value' => 'confirm', 'class' => 'btn')); ?>\n";
			c += "\t\t<?php echo $this->Form->end(); ?>\n";
			c += "\t\t<br><br>\n";
		}
		else c += "\t\t<?php echo $this->Form->end('送信'); ?>\n";
		if(controller != null) { //キャンセルボタンの設置
			c += "\t\t<?php echo $this->Html->link('キャンセル', array('controller' => '" + 
					controller + "', 'action' => '" + view + "')); ?>\n";
		}
		c += "\t</div>\n";
		c += "</div>\n";
		if(confirm) {
			c += "<?php //確認ボタン用スタイル ?>\n";
			c += "<style type=\"text/css\">\n";
			c += ".btn {\n";
			c += "\twidth: 90px;\n";
			c += "\theight: 28px;\n";
			c += "\tfont-size: 12pt;\n";
			c += "}\n";
			c += "</style>\n";
		}
	}
	
	/*
	 * 確認画面のヘッド部分のコード
	 * 引数：画面名
	 */
	public void confirmHead(String name) {
		c += "<div class=\"" + modelName.toLowerCase() + "s form\">\n";
		c += "\t<legend>" + name + "</legend>\n";
		c += "\t<h2>以下の内容でよろしいですか？</h2>\n";
		c += "\t<fieldset>\n";
		c += "\t\t<?php\n";
	}
	
	/*
	 * 確認画面の確認する中身を表示する部分のコード
	 * 引数：変数名, 自カラムの名前, モデル名, カラム名(自、関連含む), データ型
	 */
	public void confirm(String val, String name, String model, String column, String type) {
		switch(type) {
		case "DATETIME":
			if(model.equals(modelName)) {
				c += "\t\t\t$datetime" + datetimeCount + " = $this->request->data['" + model + "']['" + column + "'];\n";
				c += "\t\t\techo \"<p>" + column + ":\" . $datetime" + datetimeCount + "['year'].'/'.$datetime" 
						+ datetimeCount + "['month'].'/'" + ".$datetime" + datetimeCount + "['day'].' '.$datetime" 
						+ datetimeCount + "['hour'].':'.$datetime" + datetimeCount + "['min'].  \"</p>\";\n";
				datetimeCount++;
			} else {
				c += "\t\t\techo \"<p>" + name + ":\" . h(date('Y/m/d H:i', strtotime($" + 
					val + "['" + model + "']['" + column + "']))). \"</p>\";\n";
			}
			break;
		case "DATE":
			if(model.equals(modelName)) {
				c += "\t\t\t$date" + dateCount + " = $this->request->data['" + model + "']['" + column + "'];\n";
				c += "\t\t\techo \"<p>" + column + ":\" . $date" + dateCount + "['year'].'/'.$date" + dateCount 
						+ "['month'].'/'" + ".$date" + dateCount + "['day'].  \"</p>\";\n";
				dateCount++;
			} else {
				c += "\t\t\techo \"<p>" + name + ":\" . h(date('Y/m/d', strtotime($" + 
						val + "['" + model + "']['" + column + "']))). \"</p>\";\n";
			}
			break;
		default:
			c += "\t\t\techo \"<p>" + name + ":\" . $" + val + 
				"['" + model + "']['" + column + "'] . \"</p>\";\n";
			break;
		}
	}
	
	/*
	 * 確認画面のフッタ部分のコード
	 */
	public void confirmFoot() {
		c += "\t\t?>\n";
		c += "\t</fieldset>\n";
		c += "\t<?php\n";
		c += "\t\techo $this->Form->create('" + modelName + "');\n";
		c += "\t\tforeach ($this->request->data['" + modelName + "'] as $key => $val) {\n";
		c += "\t\t\tif(isset($val['year'])) {\n";
		c += "\t\t\t\tif(isset($val['hour'])) {\n";
		c += "\t\t\t\t\techo $this->Form->hidden($key, array('value' => $val['year'].'-'."
				+ "$val['month'].'-'.$val['day'].' '.$val['hour'].':'.$val['min'].':00'));\n";
		c += "\t\t\t\t} else {\n";
		c += "\t\t\t\t\techo $this->Form->hidden($key, array('value' => $val['year'].'-'.$val['month'].'-'.$val['day']));\n";
		c += "\t\t\t\t}\n";
		c += "\t\t\t} else {\n";
		c += "\t\t\t\techo $this->Form->hidden($key, array('value' => $val));\n";
		c += "\t\t\t}\n";
		c += "\t\t}\n";
		c += "\t\techo $this->Form->button('修正する', array('type' => 'submit', 'name' => 'confirm', 'value' => 'revise'));\n";
		c += "\t\techo $this->Form->button('確定する', array('type' => 'submit', 'name' => 'confirm', 'value' => 'complete'));\n";
		c += "\t?>\n";
		c += "</div>\n";
	}
	
	/*
	 * 一覧表示、検索結果表示、履歴表示画面のヘッダ部分のコード
	 * 引数：画面名, 検索機能をつけるか
	 */
	public void indexHistoryResultHead(String name) {
		c += "<div class=\"" + modelName.toLowerCase() + "s index\">\n";
		c += "\t<legend>" + name + "</legend>\n";
	}
	
	/*
	 * 一覧表示、検索結果表示、履歴表示画面のテーブル表示のヘッダ部分のコード
	 */
	public void indexHistoryResultTableHead() {
		c += "\t<?php //テーブル表示 ?>\n";
		c += "\t<table cellpadding=\"0\" cellspacing=\"0\">\n";
		c += "\t\t<tr>\n";
	}
	
	/*
	 * 一覧表示、検索結果表示、履歴表示のソートを行う部分のコード
	 * 引数：カラム名
	 */
	public void indexHistoryResultSort(String column) {
		c += "\t\t\t<th><?php echo $this->Paginator->sort('" + column + 
				"', '" + column + "'); ?></th>\n";
	}
	
	/*
	 * 一覧表示、検索結果表示、履歴表示の真ん中部分のコード
	 */
	public void indexHistoryResult() {
		c += "\t\t\t<th class=\"actions\">操作</th>\n";
		c += "\t\t</tr>\n";
		c += "\t\t<?php //foreach文(要素を1つずつ取り出す) ?>\n";
		c += "\t\t<?php foreach($data as $value): ?>\n";
		c += "\t\t<tr>\n";
	}
	
	/*
	 * 一覧表示、検索結果表示、履歴表示のレコードの表示を行う部分のコード
	 * 引数：そのカラムの種類, そのカラムのモデル名, カラム名
	 */
	public void indexHistoryResultDisplay(String type, String model, String column) {
		c += "\t\t\t<td><?php echo";
		switch(type) {
		case "datetime":
			c += " h(date('Y/m/d H:i', strtotime($value['" + model + "']['" + column + "'])));";
			break;
		case "date":
			c += " h(date('Y/m/d', strtotime($value['" + model + "']['" + column + "'])));";
			break;
		default:
			c += "($value['" + model + "']['" + column + "']);";
		}
		c += " ?>&nbsp;</td>\n";
	}
	
	/*
	 * 一覧表示、検索結果表示、履歴表示画面のフッタ部分のコード
	 * 引数：編集名, 詳細名, 削除名
	 */
	public void indexHistoryResultFoot(String edit, String view, String delete) {
		c += "\t\t\t<td class=\"actions\">\n";
		if(edit != null) c += "\t\t\t\t<?php echo $this->Html->link('" + edit + 
				"', array('action' => 'edit', $value['" + modelName + "']['id'])); ?>\n";
		if(view != null) c += "\t\t\t\t<?php echo $this->Html->link('" + view + 
				"', array('action' => 'view', $value['" + modelName + "']['id'])); ?>\n";
		if(delete != null) c += "\t\t\t\t<?php echo $this->Form->postLink('" + delete + 
				"', array('action' => 'delete', $value['" + modelName + "']['id']), null, '本当に削除しますか？'); ?>\n";
		c += "\t\t\t</td>\n";
		c += "\t\t</tr>\n";
		c += "\t\t<?php endforeach; ?>\n";
		c += "\t</table>\n\n";
		c += "\t<div class=\"paging\">\n";
		c += "\t\t<?php\n";
		c += "\t\t\techo $this->Paginator->prev('< ' . '前のページ', array(), null, array('class' => 'prev disabled'));\n";
		c += "\t\t\techo $this->Paginator->numbers(array('separator' => ''));\n";
		c += "\t\t\techo $this->Paginator->next('次のページ' . ' >', array(), null, array('class' => 'next disabled'));\n";
		c += "\t\t?>\n";
		c += "\t</div>\n";
		c += "</div>\n";
	}
	
	/*
	 * 検索機能本体のコード
	 * 引数：検索後の遷移先
	 */
	public void search(String action) {
		c += "\t<?php //キーワード検索 ?>\n";
		c += "\t<form action=\"<?php echo Router::url('/'); ?>" +  modelName.toLowerCase()
				+ "s/" + action + "\" method=\"get\">\n";
		c += "\t<div class=\"search\">\n";
		c += "\t\t<input type=\"text\" name=\"keyword\" placeholder=\"キーワード\">\n";
		c += "\t\t<input type=\"submit\" value=\"検索\"> \n";
		c += "\t</div>\n";
		c += "\t</form>\n";
	}
	
	/*
	 * 検索機能のスタイルのコード
	 */
	public void searchStyle() {
		c += "<?php //キーワード検索用スタイル ?>\n";
		c += "<style type=\"text/css\">\n";
		c += "input {\n";
		c += "\tpadding: 0%;\n";
		c += "\twidth: 200px;\n";
		c += "}\n\n";
		c += ".search {\n";
		c += "\ttext-align: right;\n";
		c += "}\n";
		c += "</style>\n";
	}
	
	/*
	 * 詳細表示画面のヘッダ部分のコード
	 * 引数：画面名
	 */
	public void viewHead(String name) {
		c += "<div class=\"" + modelName.toLowerCase() + "s view\">";
		c += "\t<legend>" + name + "</legend>\n";
		c += "\t<dl>\n";
	}
	
	/*
	 * 詳細表示画面のレコードの中身を表示するコード
	 * 引数：タイプ, モデル名, カラム名
	 */
	public void view(String type, String model, String column) {
		c += "\t\t<dt>" + column + "</dt>\n";
		c += "\t\t<dd>\n";
		switch(type) {
		case "date":
			c += "\t\t\t<?php echo h(date('Y/m/d', strtotime($data['" + 
					model + "']['" + column + "']))); ?>&ensp;\n";
			break;
		case "datetime":
			c += "\t\t\t<?php echo h(date('Y/m/d H:i', strtotime($data['" + 
					model + "']['" + column + "']))); ?>&ensp;\n";
			break;
		default:
			c += "\t\t\t<?php echo h($data['" + model + "']['" + column + "']); ?>&ensp;\n";
			break;
		}
		c += "\t\t</dd>\n";
	}
	
	/*
	 * 詳細表示画面のフッタ部分のコード
	 * 引数：編集名, 削除名
	 */
	public void viewFoot(String edit, String delete) {
		c += "\t</dl>\n";
		c += "\t<div class=\"actions\">\n";
		if(delete != null) c += "\t\t<?php echo $this->Form->postLink('" + delete + 
				"', array('action' => 'delete', $data['" + modelName + "']['id']), null, '本当に削除しますか？'); ?>\n";
		if(edit != null) c += "\t\t<?php echo $this->Html->link('" + edit + 
				"', array('action' => 'edit', $data['" + modelName + "']['id'])); ?>\n";
		c += "\t</div>\n";
		c += "\t</div>\n";
	}
	
	/*
	 * 全削除画面のコード
	 * 引数：画面名, コントローラ名, ビュー名（キャンセル時の遷移先）
	 */
	public void delete_all(String name, String controller, String view) {
		c += "<div class=\"" + modelName.toLowerCase() + "s index\">\n";
		c += "\t<legend>" + name + "</legend>\n";
		c += "\t<?php echo $this->Form->create('" + modelName + 
				"', array('onsubmit'=>'return confirm(\"本当に削除してもいいですか？\");')); ?>\n";
		c += "\t<p><h2>テーブルの中身を全削除します。よろしいですか？</h2></p>\n";
		c += "\t<div class=\"actions\">\n";
		c += "\t\t<?php echo $this->Form->submit('はい'); ?>\n";
		if(controller != null) { //キャンセルボタンの設置
			c += "\t\t<?php echo $this->Html->link('キャンセル', array('controller' => '" + 
					controller + "', 'action' => '" + view + "')); ?>\n";
		}
		c += "\t</div>\n";
		c += "</div>\n";
	}
	
	/*
	 * ログイン画面のコード
	 */
	public void login(String name) {
		c += "<div class=\"" + modelName.toLowerCase() + "s form\">\n";
		c += "\t<?php echo $this->Form->create('" + modelName + "'); ?>\n";
		c += "\t<legend>" + name + "</legend>\n";
		c += "\t<fieldset>\n";
		c += "\t\t<?php\n";
		c += "\t\t\t//Formヘルパーを使用\n";
		c += "\t\t\techo $this->Form->input('username', array('label' => 'ID'));\n";
		c += "\t\t\techo $this->Form->input('password', array('label' => 'パスワード'));\n";
		c += "\t\t?>\n";
		c += "\t</fieldset>\n";
		c += "\t<?php //フォームの中身を送信する ?>\n";
		c += "\t<?php echo $this->Form->end('送信'); ?>\n";
		c += "</div>\n";
	}
	
	/*
	 * 地図画面ヘッドのコード
	 */
	public void mapHead(String name) {
		c += "<div class=\"" + modelName.toLowerCase() + "s form\">\n";
		c += "<legend>" + name + "</legend>\n";
		c += "<style type=\"text/css\">\n";
		c += "\thtml, body { height: 100%; margin: 0; padding: 0; }\n";
		c += "\t#map { height: 500px; width: 860px; margin: 0 auto; }\n";
		c += "</style>\n";
		c += "<div id=\"map\"></div>\n";
		c += "<script type=\"text/javascript\">\n";
		c += "function initMap() {\n";
		c += "\tvar map = new google.maps.Map(document.getElementById('map'), {\n";
		c += "\t\tzoom: 8,\n";
		c += "\t\tcenter: {lat: 43.062, lng: 141.353}\n";
		c += "\t});\n";
	}
	
	/*
	 * geocoderのヘッド部分のコード
	 */
	public void geocoderHead() {
		c += "\tvar geocoder = new google.maps.Geocoder();\n";
		c += "\t<?php foreach($data as $value):?>\n";
		c += "\t<?php\n";
		c += "\t\t$text = '';\n";
	}
	
	/*
	 * geocoderのポップアップで表示するテキスト部分のコード
	 */
	public void geocoder(String name, String model, String column, String type) {
		switch(type) {
		case "DATE":
			c += "\t\t$text .= '<p>" + name + ":' . h(date('Y/m/d', strtotime($value['" + model + "']['" + column + "']))) . '</p>';\n";
			break;
		case "DATETIME":
			c += "\t\t$text .= '<p>" + name + ":' . h(date('Y/m/d H:i', strtotime($value['" + model + "']['" + column + "']))) . '</p>';\n";
			break;
		default:
			c += "\t\t$text .= '<p>" + name + ":' . $value['" + model + "']['" + column + "'] . '</p>';\n";
			break;
		}
	}
	
	/*
	 * geocoderのフッタ部分のコード
	 */
	public void geocoderFoot(String model) {
		c += "\t?>\n";
		c += "\tgeocodeAddress(geocoder, map, '<?php echo $value['" + model 
				+ "']['address']; ?>', '<?php echo $text; ?>');\n";
		c += "\t<?php endforeach; ?>\n";
	}
	
	/*
	 * 地図画面の真ん中部分のコード
	 */
	public void mapMiddle() {
		c += "}\n\n";
	}
	
	/*
	 * geocodeAddressのコード
	 */
	public void geocodeAddress() {
		c += "function geocodeAddress(geocoder, resultsMap, address, innerText) {\n";
		c += "\tgeocoder.geocode({'address': address}, function(results, status) {\n";
		c += "\t\tif (status === google.maps.GeocoderStatus.OK) {\n";
		c += "\t\t\tresultsMap.setCenter(results[0].geometry.location);\n";
		c += "\t\t\tvar marker = new google.maps.Marker({\n";
		c += "\t\t\t\tmap: resultsMap,\n";
		c += "\t\t\t\tposition: results[0].geometry.location\n";
		c += "\t\t\t});\n";
		c += "\t\t\tmarker.addListener('click', function() {\n";
		c += "\t\t\t\tnew google.maps.InfoWindow({content: innerText}).open(resultsMap, marker);\n";
		c += "\t\t\t});\n";
		c += "\t\t} else {\n";
		c += "\t\t\talert('ジオコーディングは以下の理由で失敗しました: ' + status);\n";
		c += "\t\t}\n";
		c += "\t});\n";
		c += "}\n\n";
	}
	
	/*
	 * 地図画面のフッタ部分のコード
	 */
	public void mapFoot(String apiKey) {
		c += "</script>\n";
		c += "<script async defer\n";
		c += "\tsrc=\"https://maps.googleapis.com/maps/api/js?key=" + apiKey + "&signed_in=true&callback=initMap\"></script>\n";
		c += "</div>\n";
	}
	
	/*
	 * その他のコード
	 * 引数：画面名, 関数名
	 */
	public void other(String name) {
		c += "<div class=\"" + modelName.toLowerCase() + "s form\">\n";
		c += "\t<legend>" + name + "</legend>\n";
		c += "</div>\n";
	}
	

	public String getCode() {
		return c;
	}
	
}
