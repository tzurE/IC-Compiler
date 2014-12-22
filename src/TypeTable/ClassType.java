package TypeTable;

import IC.AST.*;
import SymbolTables.ClassSymbolTable;
import SymbolTables.GlobalSymbolTable;
import SymbolTables.SymbolTable;

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
	
	//finds the SymbolTable of the class, going up from where it called (parent_table)
	public ClassSymbolTable getClassSymbolTable(SymbolTable parent_table){
		String type;
		type = parent_table.getType().getType();
		if (	(parent_table.getClass()==ClassSymbolTable.class) &&
				(type.equals(this.getName()))	){
			return (ClassSymbolTable)parent_table;
		}
		for( ; parent_table.getFather_table() != null ; parent_table = parent_table.getFather_table() ){
			type = parent_table.getType().getType();
			if (	(parent_table.getClass()==ClassSymbolTable.class) &&
					(type.equals(this.getName()))	){
				return (ClassSymbolTable)parent_table;
			}
		}
		parent_table = ((GlobalSymbolTable) parent_table).findInnerChild(this.getName());
		return (ClassSymbolTable)parent_table;
	}
	
	//finds the SymbolTable of the super class of the current Class
	public ClassSymbolTable getSuperClassTable(SymbolTable parent_table){
		parent_table = this.getClassSymbolTable(parent_table);
		if(parent_table.getFather_table() != null && parent_table.getFather_table().getClass()==ClassSymbolTable.class){
			return (ClassSymbolTable)parent_table.getFather_table();
		}
		return null;
	}
	
	@Override
	public boolean isExtendedFrom(TypeTableType type) {
		if(type == null)
			return false;
		//the same class
		else if(type.getClass().equals(ClassType.class)&& 
				type.getName().equals(classNode.getName())) {
			return true;
		}
		//null
		else if(type.getId()==TypeIDs.NULL)
			return true;
		//subtype class
		else{
			SymbolTable containingScope = classNode.getScope();
			ClassType superClass = TypeTable.classType(classNode.getSuperClassName());
			if (superClass == null){
				return false;
			}
			else{
				return superClass.isExtendedFrom(type);	
			}
		}
	}

	@Override
	public String toStringSymTable() {
	
		return this.getName();
	}
	
	public ICClass getClassNode() {
		return classNode;
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
				return "    "+this.getId() + ": Class: " + this.getName() + ", Superclass ID: " + father.getId();
			}
		}
		else{
			return "    "+this.getId() + ": Class: " + this.getName();
		}
	}
}
