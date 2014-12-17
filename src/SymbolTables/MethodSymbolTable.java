package SymbolTables;

import java.util.HashMap;
import java.util.List;

public class MethodSymbolTable extends SymbolTable{
	
	private HashMap<String, SymbolEntry> LocalVariableEntries = new HashMap<String, SymbolEntry>();
	private HashMap<String, Integer> LocalVariableDeclerationLine = new HashMap<String, Integer>();
	private HashMap<String, SymbolEntry> ParameterEntries = new HashMap<String, SymbolEntry>();
	private HashMap<Integer, StatementSymbolTable> childTableList = new HashMap<Integer,StatementSymbolTable>();
	

	public MethodSymbolTable(SymbolTableType type, String id,
			SymbolTable father_table) {
		super(SymbolTableType.METHOD, id, father_table);
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
