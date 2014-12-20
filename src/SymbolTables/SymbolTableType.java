package SymbolTables;

public enum SymbolTableType {
	GLOBAL("Global Symbol Table"),
	CLASS("Class Symbol Table"),
	VIRTUAL_METHOD("Virtual Method Symbol Table"), 
	STATIC_METHOD("Static Method Symbol Table"), 
	STATEMENT("Statement Block Symbol Table");
	
	private String type;

	private SymbolTableType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}		

}
