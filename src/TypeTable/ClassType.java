package TypeTable;

import IC.AST.*;

public class ClassType extends TypeTableType {
	
	
	
	public ClassType(ICClass classNode, int id) {
		super(classNode.getName(), id);
		this.classNode = classNode;
	}
	
	public ClassType (String name){
		super(name, -1);
		classNode = null;
	}
	

	//we use the AST class in order to make things simpler. the node holds all the information 
	//we need so we can use it
	private ICClass classNode;

	@Override
	public boolean subType(TypeTableType type) {
		if(type == null)
			return false;
		else if(type.getClass().equals(ClassType.class)&& 
				type.getName().equals(classNode.getName())) {
			return true;
		}
		else{
			ClassType tt = TypeTable.classType(classNode.getSuperClassName());
			if (tt==null){
				return false;
			}
			else{
				return tt.subType(type);	
			}
		}
	}

	@Override
	public String toStringForSymbolTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
}
