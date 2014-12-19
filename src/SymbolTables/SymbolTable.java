package SymbolTables;

import java.util.List;

public abstract class SymbolTable {
	
	private SymbolTableType type;
	private String id;
	private SymbolTable father_table;
	//private List<SymbolEntryType> entry_list;
	//private List<SymbolTableType> children_table_List;
	

	public SymbolTable(SymbolTableType type, String id,
			SymbolTable father_table) {
		this.type = type;
		this.id = id;
		this.father_table = father_table;
		//this.entry_list = entry_list;
		//this.children_table_List = children_table_List;
	}

	public SymbolTableType getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public SymbolTable getFather_table() {
		return father_table;
	}
	
	//all the method every offspring must implement
	
	public void setFather_table(SymbolTable father_table) {
		this.father_table = father_table;
	}

	public abstract void addEntry(String id, SymbolEntry classSym, int line);
	public abstract void addChild(String child_name,SymbolTable child_table);
	public abstract SymbolEntry findTypeOfVariable(String entry_name, SymbolTableType symbol_table);
	public abstract boolean isIdExist(SymbolEntry entry);
	public abstract Object getEntry(String name, SymbolKinds symbolKind);
	public abstract SymbolEntry searchForVar(String id,int line);
	public abstract Object searchTable(String name, SymbolKinds symbolKind);
	public abstract boolean print();
	
}
