package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import generator.cakephp.CakePHPCreator;
import generator.cakephp.ControllerCreator;
import generator.cakephp.CorePHPRewriter;
import generator.cakephp.ModelCreator;
import generator.cakephp.RoutesPHPRewriter;
import generator.cakephp.SQLCreator;
import generator.cakephp.ViewCreator;
import loder.astah.ERDiagram;
import loder.astah.ClassDiagram;
import loder.astah.STDiagram;
import loder.astah.DataDiagram;

/*
 * ソースコード生成クラス
 */
public class Generator {
	private Path p;
	private Form f;
	private STDiagram stDiagram;
	//private DataDiagram DataDiagram;
	private String errorMessage;
	private List<String> warnings;

	public Generator(Path path, Form generation) {
		p = path;
		f = generation;
	}
	
	//生成メソッド
	public boolean generate() {
		File file = new File(p.getWorkspace() + "/" + f.getID());
		file.mkdir();
		boolean complete = false;
		switch(f.getWay()) {
			case "simple":
				complete = generateCakePHPCode("");
				break;
			case "full":
				decordCakePHPProject();
				complete = generateCakePHPCode("/cakephp");
				rewriteCorePHP(f.getSecuSalt(), f.getCiphSeed());
				if(stDiagram != null) rewriteRoutesPHP();
				break;
			case "c4sa":
				decordCakePHPProject();
				complete = generateCakePHPCode("/cakephp");
				rewriteCorePHP(f.getSecuSalt(), f.getCiphSeed());
				if(stDiagram != null) rewriteRoutesPHP();
				cpDbphp();
				break;			
		}
		compressCakePHPProject();
		FileCleaner.delete(file);
		return complete;
	}
	
	/*
	 * CakePHPコード生成メソッド
	 * 各モデル図インスタンスを生成し、CakePHPのコードを生成していく
	 * 引数：出力先（CakePHPプロジェクトを解凍したときはその配下）
	 */
	private boolean generateCakePHPCode(String output) {
		STDiagram st = new STDiagram(p.getModels() + "/" + f.getID() + "/" + f.getSTName());
		if(st.getScreens().size() == 0) {
			errorMessage = st.getErrorMessage();
			if(errorMessage.length() == 0) errorMessage = "画面遷移図に画面が1つも存在しません。";
			return false;
		}
		DataDiagram data = new ERDiagram(p.getModels() + "/" + f.getID() + "/" + f.getDataName());
		if(data.getTables().size() == 0) { //ER図がなかった場合、クラス図読み込み
			errorMessage = data.getErrorMessage();
			data = new ClassDiagram(p.getModels() + "/" + f.getID() + "/" + f.getDataName());
			if(data.getTables().size() == 0) {
				errorMessage = errorMessage + "あるいは" + data.getErrorMessage();
				if(data.getErrorMessage().length() == 0) errorMessage = "ER図(クラス図)にエンティティ(クラス)が1つも存在しません。";
				return false;
			}
		}
		stDiagram = st;
		//dataDiagram = data;
		checkDiagram(data, st);
		CakePHPCreator[] cakephp = new CakePHPCreator[3];
		cakephp[0] = new ModelCreator(p.getWorkspace() + "/" + f.getID() + "/" + output, data, st);
		cakephp[1] = new ControllerCreator(p.getWorkspace() + "/" + f.getID() + "/" + output, data, st);
		cakephp[2] = new ViewCreator(p.getWorkspace() + "/" + f.getID() + "/" + output, data, st, f.getApiKey());
		for(CakePHPCreator cake : cakephp) {
			try{
				cake.create();
			}catch(IOException e) {
				System.out.println(e.getMessage());
			}
		}
		SQLCreator sql = new SQLCreator(data);
		sql.create(p.getGenerated(), f.getID());
		return true;
	}
	
	/*
	 * cakephp.zip展開メソッド
	 */
	private void decordCakePHPProject() {
		ZipDecompressor zipFile = new ZipDecompressor(p.getCakezip(), p.getWorkspace() + "/" + f.getID());
		zipFile.decord();
	}
	
	/*
	 * CakePHPプロジェクト圧縮メソッド
	 */
	private void compressCakePHPProject() {
		ZipCompressor zip = new ZipCompressor(p.getWorkspace() + "/" + f.getID(), p.getGenerated());
		zip.compress(f.getID());
	}
	
	/*
	 * モデルチェックメソッド
	 */
	private void checkDiagram(DataDiagram data, STDiagram st) {
		DiagramChecker dc = new DiagramChecker(data, st);
		dc.checkDiagram();
		warnings = dc.getWarnings();
	}
	
	/*
	 * core.phpの中身書き換えメソッド
	 */
	private void rewriteCorePHP(String securitySalt, String cipherSeed) {
		String corePath = p.getWorkspace() + "/" + f.getID() + "/cakephp/app/Config/core.php";
		CorePHPRewriter corephp = new CorePHPRewriter();
		corephp.rewrite(corePath, securitySalt, cipherSeed);
	}
	
	/*
	 * routes.phpの中身書き換えメソッド
	 */
	private void rewriteRoutesPHP() {
		String routesPath = p.getWorkspace() + "/" + f.getID() + "/cakephp/app/Config/routes.php";
		RoutesPHPRewriter routesphp = new RoutesPHPRewriter(stDiagram);
		routesphp.rewrite(routesPath);
	}
	
	/*
	 * database.phpコピーメソッド
	 */
	private void cpDbphp() {
		try{
			Files.copy(new File(p.getDbphp()).toPath(), new File(p.getWorkspace() + "/" 
					+ f.getID() + "/cakephp/app/Config/database.php").toPath());
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public List<String> getWarnings() {
		return warnings;
	}
	
}
