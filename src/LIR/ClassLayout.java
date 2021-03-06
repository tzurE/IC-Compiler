package LIR;

import java.util.HashMap;
import java.util.Map;

import IC.AST.*;
import SymbolTables.SymbolEntry;
import SymbolTables.SymbolKinds;
/**
 * 
 * @author
 *
 */
public class ClassLayout {

	private Map<String, Method> methodByName;
	private Map<String, Integer> methodOffsets;
	private Map<String, Field> fieldByName;
	private Map<Integer, Field> fieldByOffset;
	private Map<String, Integer> fieldOffsetByName;
	
	private ClassLayout clSuper;
	private int fieldCount;
	private int methodCount;
	
	private String classIdent;
	private boolean hasSuperClass;
	
	public ClassLayout getSuperClass(){
		return this.clSuper;
	}
	
	public String getClassIdent() {
		return classIdent;
	}

	public Map<String, Method> getMethodByName() {
		return methodByName;
	}
	
	public Map<String, Integer> getMethodOffsets() {
		return methodOffsets;
	}
	
	public Map<String, Field> getFieldByName() {
		return fieldByName;
	}

	public Map<Integer, Field> getFieldByOffset() {
		return fieldByOffset;
	}

	public Map<String, Integer> getFieldOffsetByName() {
		return fieldOffsetByName;
	}
	
	public int getFieldCount() {
		return fieldCount;
	}

	public int getMethodCount() {
		return methodCount;
	}
	
	public int getDispatchTableSize(){
		int size = 4*(fieldByOffset.size() + 1);
		return size;
	}
	

	//if a class has no superClass, we use this builder
	public ClassLayout(ICClass icclass){
		this.classIdent = icclass.getName();

		methodByName = new HashMap<String, Method>();
		methodOffsets = new HashMap<String, Integer>();
		fieldByName = new HashMap<String, Field>();
		fieldByOffset = new HashMap<Integer, Field>();
		fieldOffsetByName = new HashMap<String, Integer>();
		
		fieldCount = 1;
		methodCount = 0;
		hasSuperClass = false;
		clSuper = null;
		
		// Create HashMap of all fields
		for(Field field : icclass.getFields()){
			fieldByName.put(field.getName(), field);
			fieldByOffset.put(fieldCount, field);
			fieldOffsetByName.put(field.getName(), fieldCount);
			fieldCount++;
		}
		
		
		// Create hashMap of all static methods
		for(Method method : icclass.getMethods()){
			
			String method_str = "_" + icclass.getName() + "_" + method.getName();
			
			if (!isMethodStatic(method)){
				methodByName.put(method_str, method);
				methodOffsets.put(method_str, methodCount);
				methodCount++;
			}
		}
	}
	
	// We have a superClass
	@SuppressWarnings("unchecked")
	public ClassLayout(ICClass icclass, ClassLayout clSuperClass){
		
		this.classIdent = icclass.getName();

		methodByName = new HashMap<String, Method>();
		methodOffsets = new HashMap<String, Integer>();
		fieldByName = new HashMap<String, Field>();
		fieldByOffset = new HashMap<Integer, Field>();
		fieldOffsetByName = new HashMap<String, Integer>();
		
		clSuper = clSuperClass; 
		fieldCount = 1;
		methodCount = 0;
		
		hasSuperClass = true;
		
		fieldByName = (HashMap<String, Field>)((HashMap<String, Field>)clSuperClass.getFieldByName()).clone();
		fieldByOffset = (HashMap<Integer, Field>)((HashMap<Integer, Field>)clSuperClass.getFieldByOffset()).clone();
		fieldOffsetByName = (HashMap<String, Integer>)((HashMap<String, Integer>)clSuperClass.getFieldOffsetByName()).clone();
		fieldCount = clSuperClass.getFieldCount();
		
		methodByName = (HashMap<String, Method>)((HashMap<String, Method>)clSuperClass.getMethodByName()).clone();
		methodOffsets = (HashMap<String, Integer>)((HashMap<String, Integer>)clSuperClass.getMethodOffsets()).clone();
		methodCount = clSuperClass.getMethodCount();
		
		// Create HashMap of all fields
		for(Field field : icclass.getFields()){
			fieldByName.put(field.getName(), field);
			fieldByOffset.put(fieldCount, field);
			fieldOffsetByName.put(field.getName(), fieldCount);
			fieldCount++;
		}
		
		// Create hashMap of all static methods
		for(Method method : icclass.getMethods()){
			
			String method_str = "_" + icclass.getName() + "_" + method.getName();
			
			if (!isMethodStatic(method)){
				
				// Method overrides superclass -> override it in the table
				String method_superclass = methodOverride(method.getName(), icclass.getName());
				if (!method_superclass.equals("")){
					int methodLoc = this.methodOffsets.get(method_superclass);
					methodByName.remove(method_superclass);
					methodOffsets.remove(method_superclass);
					methodByName.put(method_str, method);
					methodOffsets.put(method_str, methodLoc);
				}
				
				// Method is unique
				else{
					this.methodByName.put(method_str, method);
					this.methodOffsets.put(method_str, methodCount);
					this.methodCount++;
				}
			}
		}
	}
	
	
	public static boolean isMethodStatic(Method method){
		
		SymbolEntry methodEntry;
		methodEntry = (SymbolEntry)method.getScope().searchTable(method.getName(), SymbolKinds.STATIC_METHOD);
		
		if (methodEntry != null)
			return true;
		
		return false;
	}
	


	public String methodOverride(String method_name, String class_name){
		
		ClassLayout clLayout = this;
		String method_str, this_method_str = "_" + class_name + "_" + method_name;
		
		if (clLayout.getMethodByName().containsKey(this_method_str)){
			return this_method_str;
		}
		while (clLayout.hasSuperClass){
			clLayout = clLayout.getSuperClass();
			method_str = "_" + clLayout.getClassIdent() + "_" + method_name;
			if (clLayout.getMethodByName().containsKey(method_str))
				return method_str;
		}
		return "";
	}

	public StringBuilder printDispatchTable(){
		
		String[] dv_funcs = new String[getMethodCount()];
		StringBuilder string = new StringBuilder();
		string.append("_DV_" + this.getClassIdent() + ": [");
		
		for (String method_name : getMethodOffsets().keySet()){
			
			dv_funcs[getMethodOffsets().get(method_name)] = method_name; 
		}
		
		for (int i = 0; i < this.getMethodCount(); i++){
			
			if (i == 0)
				string.append(dv_funcs[i]);
			else
				string.append("," + dv_funcs[i]);
		}
		
		string.append("]\n");
		return string;
	}
	
public StringBuilder printFieldOffsets(){
		
		String[] dv_fields = new String[getFieldCount()];
		StringBuilder string = new StringBuilder();
		string.append("# Class_" + this.getClassIdent() + " Offset Assignment: [");
		
		for (String field_name : getFieldOffsetByName().keySet()){
			
			dv_fields[getFieldOffsetByName().get(field_name)] = field_name; 
		}
		
		for (int i = 1; i < this.getFieldCount(); i++){	
			if (i == 1)
				string.append(i + ":" + dv_fields[i]);
			else
				string.append("," + i + ":" + dv_fields[i]);
		}
		
		string.append("]\n");
		return string;
	}
	
}