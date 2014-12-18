package SymbolTables;

import TypeTable.*;
public class FieldEntry extends VariableEntry{

	public FieldEntry(String id, SymbolKinds kind, TypeTableType type) {
		super(id, SymbolKinds.FIELD, type);
	}


}
