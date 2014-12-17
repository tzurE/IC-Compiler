package SymbolTables;

public abstract class SymbolEntry {
	
	private String id;
	private SymbolEntryType type;
	
	public SymbolEntry(String id, SymbolEntryType type) {
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public SymbolEntryType getType() {
		return type;
	}
}
