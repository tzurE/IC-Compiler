package SymbolTables;

import java.util.HashMap;
import java.util.Map.Entry;
import TypeTable.TypeTableType;
/**
 * 
 * @author Tzur Eliyahu, Yael Kinor, Tuval Rotem
 * main class table, holdes all the info of the various entities that live in each class.
 * used to traverse the tree.
 */
public class ClassSymbolTable extends SymbolTable {

	private HashMap<String, SymbolEntry> fieldEntries = new HashMap<String, SymbolEntry>();
	private HashMap<String, SymbolEntry> staticMethodEntries = new HashMap<String, SymbolEntry>();
	private HashMap<String, SymbolEntry> virtualMethodEntries = new HashMap<String, SymbolEntry>();
	private HashMap<String, ClassSymbolTable> classChildTableList = new HashMap<String, ClassSymbolTable>();
	private HashMap<String, MethodSymbolTable> methodChildTableList = new HashMap<String, MethodSymbolTable>();

	public HashMap<String, MethodSymbolTable> getMethodChildTableList() {
		return methodChildTableList;
	}

	// For Printing Fields
	private int fieldCount = 0;
	private HashMap<Integer, String> fieldsByOrder = new HashMap<Integer, String>();

	// For Printing Static Methods and Virtual Methods
	private int staticMethodCount = 0;
	private HashMap<Integer, String> staticMethodByOrder = new HashMap<Integer, String>();
	private int virtualMethodCount = 0;
	private HashMap<Integer, String> virtualMethodsByOrder = new HashMap<Integer, String>();

	// For Printing Method Children
	private int methodChildCount = 0;
	private HashMap<Integer, String> methodChildByOrder = new HashMap<Integer, String>();

	// For Printing Class Children
	private int classChildCount = 0;
	private HashMap<Integer, String> classChildByOrder = new HashMap<Integer, String>();

	public ClassSymbolTable(String id, SymbolTable father_table) {
		super(SymbolTableType.CLASS, id, father_table);
	}

	@Override
	public void addEntry(String id, SymbolEntry entry, int line) {
		String myType = entry.getKind().toString();
		if (!isIdExist(entry)) {

			// Add field entry
			if (myType.equals(SymbolKinds.FIELD.toString())) {
				fieldEntries.put(id, entry);
				fieldsByOrder.put(fieldCount, id);
				fieldCount++;
			}

			// Add static method
			else if (myType.equals(SymbolKinds.STATIC_METHOD.toString())) {
				staticMethodEntries.put(id, entry);
				staticMethodByOrder.put(staticMethodCount, id);
				staticMethodCount++;

			}

			// Add virtual method
			else if (myType.equals(SymbolKinds.VIRTUAL_METHOD.toString())) {
				virtualMethodEntries.put(id, entry);
				virtualMethodsByOrder.put(virtualMethodCount, id);
				virtualMethodCount++;
			}
		}

		else {
			if (myType.equals(SymbolKinds.FIELD.toString())) {
				System.out.println("Error! duplicate Field Name: " + id);
			} else if (myType.equals(SymbolKinds.STATIC_METHOD.toString())) {
				System.out
						.println("Error in Line: "+ line +"! duplicate method Name: " + id);

			} else if (myType.equals(SymbolKinds.VIRTUAL_METHOD.toString())) {
				System.out
						.println("Error in Line: "+ line +"! duplicate method Name: " + id);
			}

			System.exit(-1);
		}
	}

	@Override
	public void addChild(String child_name, SymbolTable child_table) {

		String childType = child_table.getType().toString();

		// Add child method
		if (childType.equals(SymbolTableType.STATIC_METHOD.toString())
				|| childType.equals(SymbolTableType.VIRTUAL_METHOD.toString())) {
			methodChildTableList.put(child_name,
					(MethodSymbolTable) child_table);
			methodChildByOrder.put(methodChildCount, child_name);
			methodChildCount++;

		}

		// Add child class
		if (childType.equals(SymbolTableType.CLASS.toString())) {
			classChildTableList.put(child_name, (ClassSymbolTable) child_table);
			classChildByOrder.put(classChildCount, child_name);
			classChildCount++;
		}
	}

	@Override
	public boolean isIdExist(SymbolEntry entry) {

//		String entryType = entry.getKind().toString();
//		if (entryType.equals(SymbolKinds.FIELD.toString())) {
//			return fieldEntries.containsKey(entry.getId());
//		} else if (entryType.equals(SymbolKinds.STATIC_METHOD.toString())) {
//			return staticMethodEntries.containsKey(entry.getId());
//		} else if (entryType.equals(SymbolKinds.VIRTUAL_METHOD.toString())) {
//			return virtualMethodEntries.containsKey(entry.getId());
//		}
		
		//in the scope rules - Identifers, regardless of their kind, cannot be defined multiple times in the same scope. so..
		return((fieldEntries.containsKey(entry.getId()) || staticMethodEntries.containsKey(entry.getId()) || virtualMethodEntries.containsKey(entry.getId())));
	}

	public ClassSymbolTable findChild(String className) {
		boolean found = false;
		ClassSymbolTable curr = null;

		if (classChildTableList.containsKey(className)) {
			return classChildTableList.get(className);
		}
		for (int i = 0; i < classChildCount; i++) {
			curr = classChildTableList.get(classChildByOrder.get(i)).findChild(
					className);
			if (curr != null) {
				found = true;
				break;
			}
		}
		if (found)
			return curr;
		return null;
	}

	@Override
	public boolean print() {
		int i;

		System.out.println("Class Symbol Table: " + this.getId());
		for (i = 0; i < fieldCount; i++) {
			String name = fieldsByOrder.get(i);
			String fType = fieldEntries.get(name).getType().toStringSymTable();
			System.out.println("    Field: " + fType + " " + name);
		}

		for (i = 0; i < virtualMethodCount; i++) {
			String name = virtualMethodsByOrder.get(i);
			String methodType = virtualMethodEntries.get(name).getType()
					.toStringSymTable();
			System.out
					.println("    Virtual method: " + name + " " + methodType);
		}
		
		for (i = 0; i < staticMethodCount; i++) {
			String name = staticMethodByOrder.get(i);
			String methodType = staticMethodEntries.get(name).getType()
					.toStringSymTable();
			System.out.println("    Static method: " + name + " " + methodType);

		}

		if ((methodChildTableList.size() != 0)
				|| (classChildTableList.size() != 0)) {
			System.out.print("Children tables: ");

			for (i = 0; i < methodChildCount; i++) {
				String name = methodChildByOrder.get(i);
				if (i == 0)
					System.out.print(name);
				else
					System.out.print(", " + name);
			}
			if (classChildCount != 0)
				System.out.print(", ");

			for (i = 0; i < classChildCount; i++) {
				String name = classChildByOrder.get(i);
				if (i == 0)
					System.out.print(name);
				else
					System.out.print(", " + name);
			}

			System.out.println();
		}
		System.out.println();

		// Print child method Symbol Tables
		if (methodChildCount != 0) {
			for (i = 0; i < methodChildCount; i++) {
				String name = methodChildByOrder.get(i);
				methodChildTableList.get(name).print();
			}
		}

		// Print child class Symbol Tables
		if (classChildCount != 0) {
			for (i = 0; i < classChildCount; i++) {
				String name = classChildByOrder.get(i);
				classChildTableList.get(name).print();
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
		if (Entry != null) {
			return Entry;
		} else {
			return this.getFather_table().getEntry(name, symbolKind);
		}
	}

	@Override
	public SymbolEntry searchForVar(String id, int line) {
		if (this.fieldEntries.containsKey(id))
			return fieldEntries.get(id);
		return this.getFather_table().searchForVar(id, line);
	}

	@Override
	public Object searchTable(String name, SymbolKinds symbolKind) {

		if (symbolKind.getKind().equals(SymbolKinds.VIRTUAL_METHOD.getKind())) {
			if (virtualMethodEntries.containsKey(name)) {
				return virtualMethodEntries.get(name);
			}
		}

		else if (symbolKind.getKind().equals(
				SymbolKinds.STATIC_METHOD.getKind())) {
			if (staticMethodEntries.containsKey(name)) {
				return staticMethodEntries.get(name);
			}
		}

		else if (symbolKind.getKind().equals(SymbolKinds.FIELD.getKind())) {
			if (fieldEntries.containsKey(name)) {
				return fieldEntries.get(name);
			}
		}
		return null;
	}
	
	public SymbolEntry getIdOfAnythingInsideClass(String name) {
		if (virtualMethodEntries.containsKey(name)) {
			return virtualMethodEntries.get(name);
		}
		if (staticMethodEntries.containsKey(name)) {
			return staticMethodEntries.get(name);
		}
		if (fieldEntries.containsKey(name)) {
			return fieldEntries.get(name);
		}
		return null;
	}

	@Override
	public SymbolEntry searchForVarOuterClass(String id, int line) {
		if (this.fieldEntries.containsKey(id))
			return fieldEntries.get(id);
		else {
			SymbolEntry entry;
			for (Entry<String, ClassSymbolTable> classi : this.classChildTableList.entrySet()) {
				entry = classi.getValue().searchForVarOuterClass(id, line);
				if (entry != null) {
					return entry;
				}
			}
		}
		return null;
	}

	@Override
	public void setTableTypeForVariable(String fieldName, TypeTableType type) {
		fieldEntries.get(fieldName).setType(type);	
	}
	
	public ClassSymbolTable getChildClassTable(String className){
		return classChildTableList.get(className);
	}
}

