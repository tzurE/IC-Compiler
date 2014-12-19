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
	public String toStringSymTable() {
	
		return this.getName();
	}

	@Override
	public String toString() {
		//if the class inherits - we want to get the father id
		if (classNode.getSuperClassName() != null){
			ClassType father = TypeTable.classType(classNode.getSuperClassName());
			if (father==null){

				return "Error with type table generation, or with throwing error at extands in symbol table generation";
			}
			else{
				return this.getId() + ": Class: " + this.getName() + ", Superclass ID: " + father.getId();
			}
		}
		else{
			return this.getId() + ": Class: " + this.getName();
		}
	}
}
