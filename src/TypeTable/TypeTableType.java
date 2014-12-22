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
	public abstract boolean isExtendedFrom(TypeTableType type);
	
	// overwrite the toString method of object class
	public abstract String toString();
	
	// To string for symbol table print function
	public abstract String toStringSymTable();
}