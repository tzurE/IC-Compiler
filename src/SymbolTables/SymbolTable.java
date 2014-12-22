package SymbolTables;

import java.util.List;
import TypeTable.*;

/**
 * 
 * @author Tzur Eliyahu, Yael Kinor, Tuval Rotem
 * The main abstract symbol Table. it contains all the information needed for a Scope. 
 * Type - 5 different symbol table types. method, class, global, etc...
 * 4 different kinds of search - bottom up(search for in an enclosing scope - SearchForVar), 
 * top down(from the global to all it's children and so on - searchForVarOuterClass), local search(searchTable)
 * and recursive local+global(GetEntry)
 * 
 *
 */

public abstract class SymbolTable {
	
	private SymbolTableType type;
	private String id;
	private SymbolTable father_table;

	

	public SymbolTable(SymbolTableType type, String id,
			SymbolTable father_table) {
		this.type = type;
		this.id = id;
		this.father_table = father_table;
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
	public abstract SymbolEntry searchForVarOuterClass(String id,int line);
	public abstract boolean print();
	public abstract void setTableTypeForVariable(String fieldName, TypeTableType type);
	
}
