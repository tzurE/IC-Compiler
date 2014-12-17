package SymbolTables;

import java.util.HashMap;
import java.util.List;

public class StatementSymbolTable extends SymbolTable{

	private HashMap<String, SymbolEntry> LocalVariableEntries = new HashMap<String, SymbolEntry>();
	private HashMap<String, Integer> LocalVariableDeclerationLine = new HashMap<String, Integer>();
	private HashMap<Integer, StatementSymbolTable> childTableList = new HashMap<Integer,StatementSymbolTable>();
	
	public StatementSymbolTable(SymbolTableType type, String id,
			SymbolTable father_table, List<SymbolEntryType> entry_list,
			List<SymbolTableType> children_table_List) {
		super(SymbolTableType.STATEMENT, id, father_table);
	}

	@Override
	public void addEntry(String id, SymbolEntry classSym, int line) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addChild(String child_name, SymbolTable child_table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIdExist(SymbolEntry entry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean print() {
		// TODO Auto-generated method stub
		return false;
	}
}
