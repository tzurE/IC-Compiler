package SymbolTables;

public enum SymbolKinds {
	CLASS("Class"),
	VIRTUAL_METHOD("Virtual Method"), 
	STATIC_METHOD("Static Method"), 
	FIELD("Field"), 
	PARAMETER("Parameter"),
	LOCAL_VARIABLE("Local Variable");
	
	private String kind;
	
	private SymbolKinds(String kind) {
		this.kind = kind;
	}
	
	public String getKind() {
		return this.kind;
	}
}
