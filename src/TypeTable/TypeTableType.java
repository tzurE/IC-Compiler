package TypeTable;

public abstract class TypeTableType {
	
	private String name;
	private int id;
	
	public TypeTableType(String name, int id){
		this.name = name;
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	
	// check if type is a subtypeof of the current class
	// iff type extends current class \ null
	public abstract boolean subType(TypeTableType type);
	public abstract String toStringForSymbolTable();
	
	// overwrite the toString method of object class
	public abstract String toString();
}