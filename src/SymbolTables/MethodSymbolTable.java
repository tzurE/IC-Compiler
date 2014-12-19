package SymbolTables;

import java.util.HashMap;

import SemanticCheckerVisitor.*;

public class MethodSymbolTable extends SymbolTable{
	
	//all the local variables
	private HashMap<String, SymbolEntry> LocalVariables = new HashMap<String, SymbolEntry>();
	//do we need this?
	private HashMap<String, Integer> LocalVariablesDeclerationLine = new HashMap<String, Integer>();
	//parameters
	private HashMap<String, SymbolEntry> Parameters = new HashMap<String, SymbolEntry>();
	private HashMap<Integer, StatementSymbolTable> childTableList = new HashMap<Integer,StatementSymbolTable>();

	//same as in the classes, we use this to save the order of everything s inserted so we can print them nicely!
	private int localVarCount = 0;
	private HashMap<Integer,String> localVariablesByOrder = new HashMap<Integer,String>();

	private int paramCount = 0;
	private HashMap<Integer,String> parametersByOrder = new HashMap<Integer,String>();

	private int childCount = 0;
	private HashMap<Integer,Integer> childrenByOrder = new HashMap<Integer,Integer>();

	private int statementBlocks = 0;
	

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
		if(entry.getKind().equals(SymbolKinds.LOCAL_VARIABLE)){
			LocalVariables.put(id, entry);
			LocalVariablesDeclerationLine.put(id, line);
			localVariablesByOrder.put(localVarCount, id);
			localVarCount++;
		}
		
		if(entry.getKind().equals(SymbolKinds.PARAMETER)){
			Parameters.put(id, entry);
			parametersByOrder.put(localVarCount, id);
			paramCount++;
		}
		
		
	}


	@Override
	public void addChild(String child_name, SymbolTable child_table) {
		// TODO Auto-generated method stub
		
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
		for (int i=0; i<paramCount; i++){
			String name = parametersByOrder.get(i);
			String type = Parameters.get(name).getType().toStringForSymbolTable();
			System.out.println();
			System.out.print("    Parameter: " + type +" " +name);  

		}

		for (int i=0; i<localVarCount; i++){
			String name = localVariablesByOrder.get(i);
			String type = LocalVariables.get(name).getType().toStringForSymbolTable();
			System.out.println();
			System.out.print("    Local variable: " + type +" " +name);   
		}


		if(childTableList.size()!=0){
			System.out.println();
			System.out.print("Children tables:");
			String name = "statement block in " + this.getId();
			for (int i=0; i<childCount; i++){
				if(i==0 && i==childTableList.size()-1)
					System.out.print(" "+ name);
				else if(i==0)
					System.out.print(" "+ name);
				else if(i==childTableList.size()-1)
					System.out.print(", "+ name);
				else
					System.out.print(", "+ name);
			}
		}
		
		System.out.println();
		System.out.println();

		if(childTableList.size()!=0){
			for (int i=0; i<childCount; i++){
				int number = childrenByOrder.get(i);
				childTableList.get(number).print();
			}
		}
		return false;
	}

}
