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

	private Map<Method, String> methodByName;
	private Map<String, Integer> methodOffsets;
	private Map<String, Field> fieldByName;
	private Map<Integer, Field> fieldOffsets;
	private int fieldCount;
	private int methodCount;
	
	private String classIdent;
	
	public String getClassIdent() {
		return classIdent;
	}

	public Map<Method, String> getMethodByName() {
		return methodByName;
	}
	
	public Map<String, Integer> getMethodOffsets() {
		return methodOffsets;
	}
	
	public Map<String, Field> getFieldByName() {
		return fieldByName;
	}

	public Map<Integer, Field> getFieldOffsets() {
		return fieldOffsets;
	}

	public int getFieldCount() {
		return fieldCount;
	}

	public int getMethodCount() {
		return methodCount;
	}

	//if a class has no superClass, we use this builder
	public ClassLayout(ICClass icclass){
		this.classIdent = icclass.getName();

		methodByName = new HashMap<Method, String>();
		methodOffsets = new HashMap<String, Integer>();
		fieldByName = new HashMap<String, Field>();
		fieldOffsets = new HashMap<Integer, Field>();
		
		fieldCount = 0;
		methodCount = 0;
		
		
		// Create HashMap of all fields
		for(Field field : icclass.getFields()){
			fieldByName.put(field.getName(), field);
			fieldOffsets.put(fieldCount, field);
			fieldCount++;
		}
		
		
		// Create hashMap of all static methods
		for(Method method : icclass.getMethods()){
			
			String method_str = "_" + icclass.getName() + "_" + method.getName();
			
			if (!isMethodStatic(method)){
				methodByName.put(method, method_str);
				methodOffsets.put(method_str, methodCount);
				methodCount++;
			}
		}
	}
	
	// We have a superClass
	@SuppressWarnings("unchecked")
	public ClassLayout(ICClass icclass, ClassLayout clSuperClass){
		
		this.classIdent = icclass.getName();

		methodByName = new HashMap<Method, String>();
		methodOffsets = new HashMap<String, Integer>();
		fieldByName = new HashMap<String, Field>();
		fieldOffsets = new HashMap<Integer, Field>();
		
		fieldCount = 0;
		methodCount = 0;
		
		fieldByName = (HashMap<String, Field>)((HashMap<String, Field>)clSuperClass.getFieldByName()).clone();
		fieldOffsets = (HashMap<Integer, Field>)((HashMap<Integer, Field>)clSuperClass.getFieldOffsets()).clone();
		fieldCount = clSuperClass.getFieldCount();
		
		methodByName = (HashMap<Method, String>)((HashMap<Method, String>)clSuperClass.getMethodByName()).clone();
		methodOffsets = (HashMap<String, Integer>)((HashMap<String, Integer>)clSuperClass.getMethodOffsets()).clone();
		methodCount = clSuperClass.getMethodCount();
		
		// Create HashMap of all fields
		for(Field field : icclass.getFields()){
			fieldByName.put(field.getName(), field);
			fieldOffsets.put(fieldCount, field);
			fieldCount++;
		}
		
		// Create hashMap of all static methods
		for(Method method : icclass.getMethods()){
			
			String method_str = "_" + icclass.getName() + "_" + method.getName();
			
			if (!isMethodStatic(method)){
				
				String method_superclass = "_" + clSuperClass.getClassIdent() + "_" + method.getName();
				
				// Method overrides superclass -> override it in the table 
				if (isMethodOverride(method_superclass, clSuperClass)){
					int methodLoc = this.methodOffsets.get(method_superclass);
					methodByName.remove(method_superclass);
					methodOffsets.remove(method_superclass);
					methodByName.put(method, method_str);
					methodOffsets.put(method_str, methodLoc);
				}
				
				// Method is unique
				else{
					this.methodByName.put(method, method_str);
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
	


	public boolean isMethodOverride(String method_superclass, ClassLayout clSuperClass){
		
		int num = clSuperClass.getMethodOffsets().get(method_superclass); 
		if (num >= 0){
			return true;
		}
		
		return false;
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
	
}