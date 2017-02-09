package loder.astah;

/*
 * 遷移データクラス
 */
public class Transition {

	private String opponent; //遷移先（元）の画面名
	private String label; //ラベル
	private boolean enter;  //入ってくる遷移か、否か
	
	/*
	 * コンストラクタ
	 */
	public Transition(String opponent, String label, boolean enter) {
		this.opponent = opponent;
		this.label = label;
		this.enter = enter;
	}
	
	public String getOpponent() {
		return opponent;
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean isEnter() {
		return enter;
	}
	
}
