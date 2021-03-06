package SymbolTables;

import java.util.HashMap;

import TypeTable.*;
import SemanticCheckerVisitor.*;
import TypeTable.TypeTableType;
/**
 * 
 * @author Tzur Eliyahu, Yael Kinor, Tuval Rotem
 * The method symbol table, contains important information on all the variabls, variabls declerations, 
 * line declarations (to check that no var was called before init and such)
 * 
 * please notice that as the Specs requested (part 10, scope rules) - " Identifers, regardless of their kind, cannot be defined multiple times in the same scope.".
 *
 */
public class MethodSymbolTable extends SymbolTable{
	
	//all the local variables
	private HashMap<String, SymbolEntry> LocalVariables = new HashMap<String, SymbolEntry>();

	private HashMap<String, Integer> LocalVariablesDeclerationLine = new HashMap<String, Integer>();
	/////////////////
	private HashMap<String, SymbolEntry> Parameters = new HashMap<String, SymbolEntry>();

	// For Printing local variables
	private int localVarCount = 0;
	private HashMap<Integer,String> localVariablesByOrder = new HashMap<Integer,String>();
	private int parameterCount = 0;
	
	public int getParameterCount() {
		return parameterCount;
	}

	public HashMap<Integer, String> getParametersByOrder() {
		return parametersByOrder;
	}

	// For Printing parameters
	private HashMap<Integer,String> parametersByOrder = new HashMap<Integer,String>();
	
	private int statementCount = 0;
	private HashMap<Integer, StatementSymbolTable> statementChildTableList = new HashMap<Integer,StatementSymbolTable>();
	
	
	public MethodSymbolTable(SymbolTableType type, String id,
			SymbolTable father_table) {
		super(type, id, father_table);
		
	}

	@Override
	public void addEntry(String id, SymbolEntry entry, int line) {
		if(isIdExist(entry)){
			try{
				throw new SemanticError(line, "cant declare twice on '" + id + "'");
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
		if (child_table.getType().toString().equals(SymbolTableType.STATEMENT.toString())){
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

	@Override
	public SymbolEntry findTypeOfVariable(String entry_name,
			SymbolTableType symbol_table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getEntry(String name, SymbolKinds symbolKind) {
		Object Entry = searchTable(name, symbolKind);
		if (Entry!=null){
			return Entry;
		}
		else{
			return this.getFather_table().getEntry(name, symbolKind);
		}
	}

	@Override
	public SymbolEntry searchForVar(String id, int line) {
		
		if(LocalVariables.containsKey(id)){
			if(LocalVariablesDeclerationLine.get(id) <= line)
				return LocalVariables.get(id);
			}
		else if(this.Parameters.containsKey(id))
			return Parameters.get(id);
		return this.getFather_table().searchForVar(id, line);
	}

	@Override
	public Object searchTable(String name, SymbolKinds symbolKind) {
		if(symbolKind.getKind().equals(SymbolKinds.LOCAL_VARIABLE.getKind())){
			if(LocalVariables.containsKey(name)){
				return LocalVariables.get(name);
			}
		}
		else if(symbolKind.getKind().equals(SymbolKinds.PARAMETER.getKind())){
			if(Parameters.containsKey(name)){
				return Parameters.get(name);
			}
		}
		return null;
	}

	@Override
	public SymbolEntry searchForVarOuterClass(String id, int line) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTableTypeForVariable(String fieldName, TypeTableType type) {
		if(LocalVariables.containsKey(fieldName)){
			LocalVariables.get(fieldName).setType(type);
		}
		else if(Parameters.containsKey(fieldName)){
			Parameters.get(fieldName).setType(type);
		}
	}

	@Override
	public SymbolTable get_defining_scope_for_var(String id, int line, SymbolTable prevScope) {
		if( searchTable(id, SymbolKinds.PARAMETER) != null || searchTable(id, SymbolKinds.LOCAL_VARIABLE) != null)
			return this;
		return this.getFather_table().get_defining_scope_for_var(id,line,this);
	}
}
