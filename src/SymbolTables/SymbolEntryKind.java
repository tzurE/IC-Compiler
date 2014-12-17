package SymbolTables;

public enum SymbolEntryKind {
	
	CLASS("Class"),
	VIRTUAL_METHOD("Virtual Method"), 
	STATIC_METHOD("Static Method"), 
	FIELD("Field"), 
	PARAMETER("Parameter"),
	LOCAL_VARIABLE("Local Variable");
	
	private String type;

	private SymbolEntryKind(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}	

}
