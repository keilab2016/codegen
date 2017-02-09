package generator.cakephp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import loder.astah.STDiagram;
import loder.astah.Screen;

/*
 * routes.php書き換えクラス
 * トップページをドメイン「/」でアクセスできるよう、書き換える
 */
public class RoutesPHPRewriter {
	
	private STDiagram stDiagram;
	
	/*
	 * コンストラクタ
	 * 引数：画面遷移図のインスタンス
	 */
	public RoutesPHPRewriter(STDiagram stDiagram) {
		this.stDiagram = stDiagram;
	}
	
	/*
	 * 書き換えメソッド
	 */
	public boolean rewrite(String filePath) {
		for(Screen s : stDiagram.getScreens()) {
			if(s.isTopPage()) {
				File file = new File(filePath);
				String text = "";
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String str = "";
					for(int i=1;(str=br.readLine())!=null;i++) { //1行ずつ読み込む
						if(i==28) { //28行目を編集
							text += "\tRouter::connect('/', array('controller' => '" + s.getTableName() +
									"', 'action' => '" + s.getFunction() + "'));\n";
							continue;
						}
						text += str + "\n";
					}
					br.close();
				}catch(Exception e) {
					return false;
				}
				
				try {
					FileWriter fw = new FileWriter(file);
					fw.write(text);
					fw.close();
				}catch(Exception e) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

}
