package SymbolTables;

import java.util.HashMap;

import SemanticCheckerVisitor.*;

public class MethodSymbolTable extends SymbolTable{
	
	//all the local variables
	private HashMap<String, SymbolEntry> LocalVariables = new HashMap<String, SymbolEntry>();
	//do we need this?
	private HashMap<String, Integer> LocalVariablesDeclerationLine = new HashMap<String, Integer>();
	/////////////////
	private HashMap<String, SymbolEntry> Parameters = new HashMap<String, SymbolEntry>();

	// For Printing local variables
	private int localVarCount = 0;
	private HashMap<Integer,String> localVariablesByOrder = new HashMap<Integer,String>();
	private int parameterCount = 0;
	
	// For Printing parameters
	private HashMap<Integer,String> parametersByOrder = new HashMap<Integer,String>();
	
	private int statementCount = 0;
	private HashMap<Integer, StatementSymbolTable> statementChildTableList = new HashMap<Integer,StatementSymbolTable>();
	
	
	public MethodSymbolTable(SymbolTableType type, String id,
			SymbolTable father_table) {
		super(SymbolTableType.METHOD, id, father_table);
		
	}

	@Override
	public void addEntry(String id, SymbolEntry entry, int line) {
		if(isIdExist(entry)){
			try{
				throw new SemanticError(line, "cant declare twice on" + id);
			} catch(SemanticError e){
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		
		// Add local variable
		if(entry.getKind().equals(SymbolKinds.LOCAL_VARIABLE)){
			LocalVariables.put(id, entry);
			LocalVariablesDeclerationLine.put(id, line);
			localVariablesByOrder.put(localVarCount, id);
			localVarCount++;
		}
		
		// Add parameter
		if(entry.getKind().equals(SymbolKinds.PARAMETER)){
			Parameters.put(id, entry);
			parametersByOrder.put(parameterCount, id);
			parameterCount++;
		}	
	}


	@Override
	public void addChild(String child_name, SymbolTable child_table) {
		
		// Add child statement
		if (child_table.getType().equals(SymbolTableType.STATEMENT.toString())){
			statementChildTableList.put(statementCount, (StatementSymbolTable) child_table);
			statementCount++;
		}
	}


	@Override
	public boolean isIdExist(SymbolEntry entry) {
		if(LocalVariables.containsKey(entry.getId()) || Parameters.containsKey(entry.getId())){
			return true;
		}
		return false;
	}


	@Override
	public boolean print() {
		
		System.out.print("Method Symbol Table: " + this.getId());
		
		for (int i = 0; i < parameterCount; i++){
			String name = parametersByOrder.get(i);
			String type = Parameters.get(name).getType().toStringSymTable();
			System.out.println();
			System.out.print("    Parameter: " + type + " " +name);  
		}

		for (int i = 0; i < localVarCount; i++){
			String name = localVariablesByOrder.get(i);
			String type = LocalVariables.get(name).getType().toStringSymTable();
			System.out.println();
			System.out.print("    Local variable: " + type + " " + name);   
		}

		if(statementChildTableList.size()!=0){
			System.out.println();
			System.out.print("Children tables:");
			String name = "statement block in " + this.getId();
			for (int i = 0; i < statementCount; i++){
				if(i == 0)
					System.out.print(" "+ name);
				else
					System.out.print(", "+ name);
			}
		}
		
		System.out.println();
		System.out.println();

		// Print child statements tables
		if(statementChildTableList.size() != 0){
			for (int i = 0; i < statementCount; i++){
				statementChildTableList.get(i).print();
			}
		}
		return false;
	}
}
