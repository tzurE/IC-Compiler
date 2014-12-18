package SymbolTables;
import TypeTable.*;
public class ParameterEntry extends VariableEntry {

	public ParameterEntry(String id, SymbolKinds kind, TypeTableType type) {
		super(id, SymbolKinds.PARAMETER, type);
	}

}
