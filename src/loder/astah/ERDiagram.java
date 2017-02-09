package loder.astah;

import com.change_vision.jude.api.inf.model.IERModel;
import com.change_vision.jude.api.inf.model.IERSchema;
import com.change_vision.jude.api.inf.model.IEREntity;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IERAttribute;
import com.change_vision.jude.api.inf.model.IERRelationship;

import java.util.List;
import java.util.ArrayList;

/*
 * ER図データクラス
 * ER図の読み込みを行い、テーブルインスタンスを生成していく
 */
public class ERDiagram extends Diagram implements DataDiagram {
	
	private List<Table> tables; //テーブルインスタンスのリスト
	private IERModel erModel;
	
	/*
	 * コンストラクタ
	 */
	public ERDiagram(String filePath) {
		super(filePath); //親のコンストラクタ呼び出し
		tables = new ArrayList<Table>();
		try{
			loadERDiagram();
			setTables();
		}finally{
			closeProject("");
		}
	}

	/*
	 * ER図読み込みメソッド
	 */
	public boolean loadERDiagram() {
		INamedElement[] subPackages = null;
		try{
			subPackages = iPackage.getOwnedElements();
		}catch(NullPointerException e) {
			closeProject("パッケージにER図が含まれていません。");
			errorMessage = "パッケージにER図が含まれていません。";
			return false;
		}
		for(INamedElement subPackage : subPackages) {
			if(subPackage instanceof IERModel) {
				erModel = (IERModel)subPackage;
			}
		}
		if(erModel == null) {
			closeProject("パッケージにER図が含まれていません。");
			errorMessage = "パッケージにER図が含まれていません。";
			return false;
		}
		return true;
	}
	
	/*
	 * テーブルセットメソッド
	 */
	private void setTables() {
		if(erModel == null) return;
		IERSchema[] schemas = erModel.getSchemata(); //スキーマのリスト
		IEREntity[] entities = null; //エンティティのリスト
		
		//とりあえずスキーマは1個しかないとしている
		for(IERSchema schema : schemas) {
			entities = schema.getEntities();
		}
		
		for(IEREntity entity : entities) {
			Table table = new Table(entity.getLogicalName());
			setAttribute(entity, table); //要素をセット
			setRelation(entity, table);//関係をセット
			tables.add(table); //リストにテーブルを追加
		}
	}
	
	/*
	 * 要素セットメソッド
	 */
	private void setAttribute(IEREntity entity, Table table) {
		IERAttribute[] primaryKeys = entity.getPrimaryKeys(); //主キーの要素
		IERAttribute[] nonPrimaryKeys = entity.getNonPrimaryKeys(); //それ以外の要素
		for(IERAttribute primaryKey : primaryKeys) {
			String name = primaryKey.getLogicalName().replaceAll(" ", "_");
			name = name.replaceAll("　", "_");
			table.newColumn(name, primaryKey.isNotNull(), 
					primaryKey.getDatatype().getName(), primaryKey.isForeignKey(), primaryKey.getLengthPrecision());
		}
		for(IERAttribute nonPrimaryKey : nonPrimaryKeys) {
			String name = nonPrimaryKey.getLogicalName().replaceAll(" ", "_");
			name = name.replaceAll("　", "_");
			table.newColumn(name, nonPrimaryKey.isNotNull(), 
					nonPrimaryKey.getDatatype().getName(), nonPrimaryKey.isForeignKey(), nonPrimaryKey.getLengthPrecision());
		}
	}
	
	/*
	 * 関係セットメソッド
	 * 多重度は「1対1」、「1対多」、「多対1」、「多対多」の4つのみ
	 * 直接関係のあるもののみセットする(テーブルを跨いだものは格納されない)
	 */
	private void setRelation(IEREntity entity, Table table) {
		IERRelationship[] parents = entity.getParentRelationships(); //親エンティティへの関係(自分が子)
		IERRelationship[] children = entity.getChildrenRelationships(); //子エンティティへの関係(自分が親)
		for(IERRelationship parent : parents) {
			String tableName = parent.getParent().getName();
			String relation = "";
			if(parent.isMultiToMulti()) {
				relation = "多対多";
				table.newRelation(tableName, relation, parent.getForeignKeys()[0].getName());
				continue;
			}
			relation += checkCardinality(parent.getCardinality()) + "対1";
			table.newRelation(tableName, relation, parent.getForeignKeys()[0].getName());
		}
		for(IERRelationship child : children) {
			String tableName = child.getChild().getName();
			String relation = "";
			if(child.isMultiToMulti()) {
				relation = "多対多";
				table.newRelation(tableName, relation, child.getForeignKeys()[0].getName());
				continue;
			}
			relation += "1対" + checkCardinality(child.getCardinality());
			table.newRelation(tableName, relation, child.getForeignKeys()[0].getName());
		}
	}
	
	/*
	 * カーディナリティチェックメソッド
	 */
	private String checkCardinality(String cardinality) {
		switch (cardinality) {
			case "0orMore":
				return "多";
			case "1orMore":
				return "多";
			case "0or1":
				return "1";
		}
		int i = Integer.parseInt(cardinality);
		if(i > 1) {
			return "多";
		} else {
			return "1";
		}
	}
	
	public List<Table> getTables() {
		return tables;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
}
