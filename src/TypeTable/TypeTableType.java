package TypeTable;

public abstract class TypeTableType {
	
	private String name;
	private int id;
	private int typeId;
	
	public TypeTableType(String name, int typeId, int id){
		this.name = name;
		this.typeId = typeId;
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public int getTypeId(){
		return typeId;
	}
	
	public int getId(){
		return id;
	}
	
	
	// check if type is a subtypeof of the current class
	// iff type extends current class \ null
	public abstract boolean subType(TypeTableType type);
	
	// overwrite the toString method of object class
	public abstract String toString();
}