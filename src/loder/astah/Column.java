package loder.astah;

/*
 * カラムデータクラス
 */
public class Column {

	private String name;
	private boolean notNull;
	private String type;
	private boolean foreignKey;
	private String length;
	
	/*
	 * コンストラクタ
	 */
	public Column(String name, boolean notNull, String type, boolean foreignKey, String length) {
		this.name = name;
		this.notNull = notNull;
		this.type = type;
		this.foreignKey = foreignKey;
		this.length = length;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isNotNull() {
		return notNull;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean isForeignKey() {
		return foreignKey;
	}
	
	public String getLength() {
		return length;
	}
	
}
