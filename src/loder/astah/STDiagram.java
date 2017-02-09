package loder.astah;

import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IStateMachineDiagram;
import com.change_vision.jude.api.inf.model.IStateMachine;
import com.change_vision.jude.api.inf.model.ITransition;
import com.change_vision.jude.api.inf.model.IVertex;
import com.change_vision.jude.api.inf.model.IFinalState;
import java.util.List;
import java.util.ArrayList;

/*
 * 画面遷移図データクラス
 * 画面遷移図を読み込み、画面インスタンスを生成していく
 */
public class STDiagram extends Diagram {

	private List<Screen> screens; //画面インスタンスのリスト
	private IStateMachine stateMachine;
	
	/*
	 * コンストラクタ
	 */
	public STDiagram(String filePath) {
		super(filePath);
		screens = new ArrayList<Screen>();
		try{
			loadSTDiagram();
			setScreens();
		}finally{
			closeProject("");
		}
	}
	
	/*
	 * 画面遷移図読み込みメソッド
	 */
	private void loadSTDiagram() {
		IDiagram[] diagrams = null;
		try{
			diagrams = iPackage.getDiagrams();
		}catch(NullPointerException e) {
			closeProject("パッケージにステートマシン図が含まれていません。");
			errorMessage = "パッケージにステートマシン図が含まれていません。";
			return;
		}
		for(IDiagram diagram : diagrams) {
			if(diagram instanceof IStateMachineDiagram) {
				IStateMachineDiagram stateMachineDiagram = (IStateMachineDiagram)diagram;
				stateMachine = stateMachineDiagram.getStateMachine();
			}
		}
		if(stateMachine == null) {
			closeProject("パッケージにステートマシン図が含まれていません。");
			errorMessage = "パッケージにステートマシン図が含まれていません。";
			return;
		}
	}
	
	/*
	 * 画面セットメソッド
	 */
	private void setScreens() {
		if(stateMachine == null) return;
		IVertex[] vertexs = stateMachine.getVertexes();
		//int number = 0; //テーブル名が存在しなかった場合のページ番号
		for(IVertex vertex : vertexs) {
			if(vertex.getName().contains("開始疑似状態")) continue; //開始疑似状態を省く
			if(vertex instanceof IFinalState) continue; //終了擬似状態を省く
			String tableName = vertex.getDefinition();
			tableName = tableName.replaceAll(" ", "");
			tableName = tableName.replaceAll("\n", "");
			tableName = tableName.replaceAll("　", "");
			if(tableName == null) tableName = "";
			Screen screen = new Screen(vertex.getName(), tableName);
			setTransition(vertex, screen);
			screens.add(screen);
		}
	}
	
	/*
	 * 遷移情報セットメソッド
	 */
	private void setTransition(IVertex vertex, Screen screen) {
		ITransition[] inComes = vertex.getIncomings(); //してくる遷移
		ITransition[] outGo = vertex.getOutgoings(); //する遷移
		for(ITransition transition : inComes) { 
			screen.newTransition(transition.getSource().getName(), transition.getName(), true);
			if(transition.getSource().getName().contains("開始疑似状態")) screen.setTopPage();
		}
		for(ITransition transition : outGo) {
			screen.newTransition(transition.getTarget().getName(), transition.getName(), false);
		}
	}
	
	public List<Screen> getScreens() {
		return screens;
	}
}
