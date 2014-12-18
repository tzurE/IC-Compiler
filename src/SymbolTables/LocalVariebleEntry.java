package SymbolTables;
import TypeTable.*;
public class LocalVariebleEntry extends VariableEntry {

	public LocalVariebleEntry(String id, SymbolKinds kind, TypeTableType type) {
		super(id, SymbolKinds.LOCAL_VARIABLE, type);
	}

}
