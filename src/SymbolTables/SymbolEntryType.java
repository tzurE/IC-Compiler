package SymbolTables;

public enum SymbolEntryType {
	
	CLASS("Class"),
	VIRTUAL_METHOD("Virtual Method"), 
	STATIC_METHOD("Static Method"), 
	FIELD("Field"), 
	PARAMETER("Parameter"),
	LOCAL_VARIABLE("Local Variable");
	
	private String type;

	private SymbolEntryType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}	

}
