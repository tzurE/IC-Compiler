package SymbolTables;

import TypeTable.*;
public class VariableEntry extends SymbolEntry{

	public VariableEntry(String id, SymbolKinds kind, TypeTableType type) {
		super(id, SymbolKinds.VIRTUAL_METHOD, type);
	}

}
