package SymbolTables;

import TypeTable.*;

public class ClassEntry extends SymbolEntry {

	public ClassEntry(String id, TypeTableType type) {
		super(id, SymbolKinds.CLASS, type);
	}

}
