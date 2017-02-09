package generator.cakephp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * core.php書き換えクラス
 * 現在はSecurity.saltとSecurity.cipherSeedの内容を書き換えることができる
 */
public class CorePHPRewriter {
	
	/*
	 * 書き換えメソッド
	 * 引数：core.phpのファイルパス、書き換えるSecurity.saltの文字列、書き換えるSecurity.cipherSeedの文字列
	 */
	public boolean rewrite(String filePath, String securitySalt, String cipherSeed) {
		File file = new File(filePath);
		String text = "";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = "";
			for(int i=1;(str=br.readLine())!=null;i++) { //1行ずつ読み込む
				if(i==228) { //Security.saltは228行目に書かれている
					text += "\tConfigure::write('Security.salt', '" + securitySalt + "');\n";
					continue;
				}
				if(i==233) {
					text += "\tConfigure::write('Security.cipherSeed', '" + cipherSeed + "');\n";
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
