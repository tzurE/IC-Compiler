package SymbolTables;
import TypeTable.*;

public abstract class SymbolEntry {
	
	private String id;
	private SymbolKinds kind;
	private TypeTableType type;
	
	public SymbolEntry(String id, SymbolKinds kind, TypeTableType type) {
		this.id = id;
		this.kind = kind;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public SymbolKinds getKind() {
		return kind;
	}
}
