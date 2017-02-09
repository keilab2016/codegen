package loder.astah;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

/*
 * クラス図データクラス
 * クラス図の読み込みを行い、クラスインスタンスを生成していく
 */
public class ClassDiagram extends Diagram implements DataDiagram {
	
	private List<Table> tables;
	private IClassDiagram classDiagram;
	private List<IClass> iClasses; //astah apiで取得したクラスのリスト

	public ClassDiagram(String filePath) {
		super(filePath);
		tables = new ArrayList<Table>();
		iClasses = new ArrayList<IClass>();
		try{
			loadClassDiagram();
			setClasses();
		}finally{
			closeProject("");
		}
	}
	
	private void loadClassDiagram() {
		IDiagram[] diagrams = null;
		try{
			diagrams = iPackage.getDiagrams();
			INamedElement[] iNamedElements = iPackage.getOwnedElements();
	        for (int i = 0; i < iNamedElements.length; i++) {
	            INamedElement iNamedElement = iNamedElements[i];
	            if (iNamedElement instanceof IClass) {
	                iClasses.add((IClass)iNamedElement);
	            }
	        }
		}catch(NullPointerException e) {
			closeProject("パッケージにクラス図が含まれていません。");
			errorMessage = "パッケージにクラス図が含まれていません。";
			return;
		}
		for(IDiagram diagram : diagrams) {
			if(diagram instanceof IClassDiagram) {
				classDiagram = (IClassDiagram)diagram;
				System.out.println(classDiagram.getName());
			}
		}
		if(classDiagram == null || iClasses.size() == 0) {
			closeProject("パッケージにクラス図が含まれていません。");
			errorMessage = "パッケージにクラス図が含まれていません。";
			return;
		}
	}
	
	private void setClasses() {
		for(IClass iClass : iClasses) {
			if(iClass.getAttributes().length == 0) continue;
			Table table = new Table(iClass.getName().toLowerCase());
			for(IAttribute attribute : iClass.getAttributes()) {
				IAssociation assosiation = attribute.getAssociation();
				try{ //関連端
				if(assosiation.getMemberEnds().length != 0) {
					String foreignClass = attribute.getType().getName();
					String foreignKey = "";
					if(attribute.isAggregate()) {
						foreignKey = foreignClass.substring(0, foreignClass.length()-1) + "_id";
						table.newRelation(foreignClass, "多対1", foreignKey);
						continue;
					}
					if(attribute.isComposite()) {
						foreignKey = foreignClass.substring(0, foreignClass.length()-1) + "_id";
						table.newRelation(foreignClass, "多対1", foreignKey);
						continue;
					} else {
						foreignKey = iClass.getName().substring(0, iClass.getName().length()-1) + "_id";
						table.newRelation(foreignClass, "1対多", foreignKey);
						continue;
					}
				}
				}catch(NullPointerException e) {}
				//属性
				String name = attribute.getName();
				name = name.replaceAll(" ", "_");
				name = name.replaceAll("　", "_");
				boolean notNull = true;
				boolean foreignKey = false;
				String type = attribute.getType().getName().toUpperCase();
				String length = attribute.getDefinition();
				length = length.replaceAll(" ", "");
				length = length.replaceAll("\n", "");
				length = length.replaceAll("　", "");
				if(attribute.getInitialValue().contains("null")) notNull = false;
				for(String stereotype : attribute.getStereotypes()) {
					if(stereotype.contains("FK")) foreignKey = true;
				}
				table.newColumn(name, notNull, type, foreignKey, length);
			}
			tables.add(table);
		}
	}
	
	public List<Table> getTables() {
		return tables;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	
}
