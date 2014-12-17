package SymbolTables;

import java.util.HashMap;

import SymbolTables.FieldEntry;
import SymbolTables.StaticMethodEntry;
import SymbolTables.SymbolEntryKind;

public class ClassSymbolTable extends SymbolTable {
	
	private HashMap<String, SymbolEntry> fieldEntries = new HashMap<String, SymbolEntry>();
	private HashMap<String, SymbolEntry> staticMethodEntries = new HashMap<String, SymbolEntry>();
	private HashMap<String, SymbolEntry> virtualMethodEntries = new HashMap<String, SymbolEntry>();
	private HashMap<String, ClassSymbolTable> classChildTableList = new HashMap<String,ClassSymbolTable>();
	private HashMap<String, MethodSymbolTable> methodChildTableList = new HashMap<String,MethodSymbolTable>();	
	
	// For Printing the Class Symbol Table (Fields)
	private int fieldCount = 0;
	private HashMap<Integer,String> fieldsByOrder = new HashMap<Integer,String>();

	// For Printing the Class Symbol Table (Static Methods and Virtual Methods)
	private int staticMethodCount = 0;
	private HashMap<Integer,String> staticMethodByOrder = new HashMap<Integer,String>();
	
	// For Printing the Class Symbol Table (Static Methods and Virtual Methods)
	private int virtualMethodCount = 0;
	private HashMap<Integer,String> virtualMethodsByOrder = new HashMap<Integer,String>();

	// For Printing the Class Symbol Table (Method Children)
	private int methodChildCount = 0;
	private HashMap<Integer,String> methodChildByOrder = new HashMap<Integer,String>();

	// For Printing the Class Symbol Table (Class Children)
	private int classChildCount = 0;
	private HashMap<Integer,String> classChildByOrder = new HashMap<Integer,String>();

	public ClassSymbolTable(String id,
			SymbolTable father_table) {
		super(SymbolTableType.CLASS, id, father_table);
	}

	@Override
	public void addEntry(String id, SymbolEntry entry, int line) {
		
		if(!isIdExist(entry)){
		
			String myType = entry.getKind().toString();
			if (myType.equals(SymbolEntryKind.FIELD.toString())){
				fieldEntries.put(id, (FieldEntry) entry);
				fieldsByOrder.put(fieldCount, id);
				fieldCount++;
			}
			else if (myType.equals(SymbolEntryKind.STATIC_METHOD.toString())){
				staticMethodEntries.put(id, (StaticMethodEntry) entry);
				staticMethodByOrder.put(staticMethodCount, id);
				staticMethodCount++;
			}
			else if (myType.equals(SymbolEntryKind.VIRTUAL_METHOD.toString())){
				virtualMethodEntries.put(id, (VirtualMethodEntry) entry);
				virtualMethodsByOrder.put(virtualMethodCount, id);
				virtualMethodCount++;
			}	
		}
		
		else{
			//add error!!
			System.out.println("Error! duplicate Class Name!");
			System.exit(1);
			
		}
	}

	@Override
	public void addChild(String child_name, SymbolTable child_table) {
		
		String childType = child_table.getType().toString();
		if (childType.equals(SymbolTableType.METHOD.toString())){
			methodChildTableList.put(child_name, (MethodSymbolTable) child_table);
			methodChildByOrder.put(methodChildCount, child_name);
			methodChildCount++;
		}
		if (childType.equals(SymbolTableType.CLASS.toString())){
			classChildTableList.put(child_name, (ClassSymbolTable) child_table);
			classChildByOrder.put(classChildCount, child_name);
			classChildCount++;
		}
	}

	@Override
	public boolean isIdExist(SymbolEntry entry) {
		
		String entryType = entry.getKind().toString();
		if (entryType.equals(SymbolEntryKind.FIELD.toString())){
			return fieldEntries.containsKey(entry.getId());
		}
		else if (entryType.equals(SymbolEntryKind.STATIC_METHOD.toString())){
			return staticMethodEntries.containsKey(entry.getId());
		}
		else if (entryType.equals(SymbolEntryKind.VIRTUAL_METHOD.toString())){
			return virtualMethodEntries.containsKey(entry.getId());		
		}
		return false;
	}
	
	public ClassSymbolTable findChild(String className){
		boolean found = false;
		ClassSymbolTable curr = null;
		
		if(classChildTableList.containsKey(className)){
			return classChildTableList.get(className);
		}
		for(int i = 0 ; i < classChildCount ; i++){
			curr = classChildTableList.get(classChildByOrder.get(i)).findChild(className);
			if(curr != null){
				found = true;
				break;
			}
		}
		if(found)
			return curr;
		return null;
	}

	@Override
	public boolean print() {
		int i;
		
		System.out.println("Class Symbol Table: " + this.getId());
		for (i = 0; i < fieldCount; i++){
			String name = fieldsByOrder.get(i);
			System.out.println("\tField: " + name);  
		}
		
		for (i = 0; i < staticMethodCount; i++){
			String name = staticMethodByOrder.get(i);
			System.out.println("\tStatic method: " + name);  
		}
		
		for (i = 0; i < virtualMethodCount; i++){
			String name = virtualMethodsByOrder.get(i);
			System.out.println("\tVirtual method: " + name);  
		}
		
		if((methodChildTableList.size() != 0) || (classChildTableList.size() != 0)){
			System.out.print("Children tables: ");
		
			for (i = 0; i < methodChildCount; i++){
				String name = methodChildByOrder.get(i);
				if (i == 0)
					System.out.print(name);
				else
					System.out.print(", " + name);
			} 
			
			for (i = 0; i < classChildCount; i++){
				String name = classChildByOrder.get(i);
				if(i == 0)
					System.out.print(name);
				else
					System.out.print(", " + name);
			} 
			//betweeen classes
			System.out.println();
		}

		System.out.println();

		// Print Method Symbol Tables
		if(methodChildCount != 0){
			for (i = 0; i < methodChildCount; i++){
				String name = methodChildByOrder.get(i);
				methodChildTableList.get(name).print();
			} 
		}
		
		// Print Class Symbol Tables
		if(classChildCount != 0){
			for (i = 0; i < classChildCount; i++){
				String name = classChildByOrder.get(i);
				classChildTableList.get(name).print();
				
			}
			
		}
		
		return false;
	}
	
}
