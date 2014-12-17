package SymbolTables;
import TypeTable.*;

public abstract class SymbolEntry {
	
	private String id;
	private SymbolEntryKind kind;
	private TypeTableType type;
	
	public SymbolEntry(String id, SymbolEntryKind kind) {
		this.id = id;
		this.kind = kind;
	}

	public String getId() {
		return id;
	}

	public SymbolEntryKind getKind() {
		return kind;
	}
}
