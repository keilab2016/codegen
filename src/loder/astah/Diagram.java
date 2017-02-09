package loder.astah;
import java.io.IOException;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

/*
 * astahファイル抽象クラス
 * パッケージの読み込みまでを行う
 */
public abstract class Diagram {

	private AstahAPI api;
	private ProjectAccessor projectAccess;
	private IModel project;
	protected IPackage iPackage;
	private String filePath;
	protected String errorMessage;
	
	/*
	 * コンストラクタ
	 */
	public Diagram(String filePath) {
		this.filePath = filePath;
		errorMessage = "";
		loadAPI();
		loadPackage();
	}
	
	/*
	 * API読み込みメソッド
	 */
	private void loadAPI() {
		try{
			api = AstahAPI.getAstahAPI();
		}catch(ClassNotFoundException e) {
			errorMessage = "astah APIの読み込みに失敗しました。";
			e.printStackTrace();
			return;
		}
	}
	
	/*
	 * パッケージ読み込みメソッド
	 */
	private void loadPackage() {
		projectAccess = api.getProjectAccessor();
		try{
			projectAccess.open(filePath);
			project = projectAccess.getProject();
			iPackage = (IPackage) project;
		} catch (ClassNotFoundException e) {
			errorMessage = "ClassNotFoundExceptionが発生しました。";
		} catch (LicenseNotFoundException e) {
			errorMessage = "API実行に必要なライセンスがインストールされていません。";
		} catch (ProjectNotFoundException e) {
			errorMessage = "プロジェクトファイルが見つかりません。";
		} catch (NonCompatibleException e) {
			errorMessage = "古いモデルバージョン（プロジェクトを最後に編集したastah*よりもAPIのバージョンが古い）です。";
		} catch (IOException e) {
			errorMessage = "ファイルへのアクセス権限がありません。";
		} catch (ProjectLockedException e) {
			errorMessage = "プロジェクトがすでに開かれています。";
		}
	}
	
	/*
	 * プロジェクト閉じメソッド
	 */
	public void closeProject(String message) {
		System.out.print(message);
		projectAccess.close();
	}
	
	/*
	 * エラー取得メソッド
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
}
