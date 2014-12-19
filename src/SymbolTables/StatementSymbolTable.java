package SymbolTables;

import java.util.HashMap;
import java.util.List;

import SemanticCheckerVisitor.SemanticError;

public class StatementSymbolTable extends SymbolTable{

	private HashMap<String, SymbolEntry> LocalVariables = new HashMap<String, SymbolEntry>();
	// Do we need this ?
	private HashMap<String, Integer> LocalVariableDeclerationLine = new HashMap<String, Integer>();

	// same as in the classes, we use this to save the order of everything's
	// inserted so we can print them nicely!
	private int localVarCount = 0;
	private HashMap<Integer,String> localVariablesByOrder = new HashMap<Integer,String>();
	
	private HashMap<Integer, StatementSymbolTable> statementChildTableList = new HashMap<Integer,StatementSymbolTable>();
	private int statementCount = 0;
	
	
	public StatementSymbolTable(String id,
			SymbolTable father_table) {
		super(SymbolTableType.STATEMENT, id, father_table);
	}

	@Override
	public void addEntry(String id, SymbolEntry classSym, int line) {
		
		if(isIdExist(classSym)){
			try{
				throw new SemanticError(line, "cant declare twice on" + id);
			} catch(SemanticError e){
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		if(classSym.getKind().equals(SymbolKinds.LOCAL_VARIABLE.toString())){
			LocalVariables.put(id, classSym);
			LocalVariableDeclerationLine.put(id, line);
			localVariablesByOrder.put(localVarCount, id);
			localVarCount++;
		}
	}

	@Override
	public void addChild(String child_name, SymbolTable child_table) {
		System.out.println(child_table.getType() + "!!!!!");
		// Add child statement
		if (child_table.getType().toString().equals(SymbolTableType.STATEMENT.toString())){
			statementChildTableList.put(statementCount, (StatementSymbolTable) child_table);
			statementCount++;
		}		
	}

	@Override
	public boolean isIdExist(SymbolEntry entry) {
		
		if(LocalVariables.containsKey(entry.getId())){
				return true;
		}
		return false;
	}

	@Override
	public boolean print() {

		System.out.print("Statement Block Symbol Table ( located in " + this.getFather_table().getId() + ")");

		for (int i = 0; i < localVarCount; i++){
			String name = localVariablesByOrder.get(i);
			String type = LocalVariables.get(name).getType().toStringSymTable();
			System.out.println();
			System.out.print("\tLocal variable: " + type + " " + name);   
		}

		if(statementChildTableList.size() != 0){
			System.out.println();
			System.out.print("Children tables:");
			String name = "statement block in " + this.getId();
			for (int i = 0; i < statementCount; i++){
				if(i == 0)
					System.out.print(" " + name);
				else
					System.out.print(", " + name);
			}
		}
		
		System.out.println();
		System.out.println();

		// Print child statements tables
		if(statementChildTableList.size()!=0){
			for (int i = 0; i < statementCount; i++){
				statementChildTableList.get(i).print();
			}
		}
		return false;
	}
}
