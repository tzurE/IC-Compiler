package LIR;

import java.util.HashMap;
import java.util.Map;

import IC.AST.*;
import SymbolTables.SymbolEntry;
import SymbolTables.SymbolKinds;
/**
 * 
 * @author Tzur hagever + Yael hashava!
 *
 */
public class ClassLayout {

	private Map<String,Method> methodByName;
	private Map<String,Integer> methodOffsets;
	private Map<String,Field> fieldByName;
	private Map<Field,Integer> fieldOffsets;
	private int fieldCount;
	private int methodCount;
	
	private String classIdent;
	
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

	public Map<Field, Integer> getFieldOffsets() {
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

		methodByName = new HashMap<String, Method>();
		methodOffsets = new HashMap<String, Integer>();
		fieldByName = new HashMap<String, Field>();
		fieldOffsets = new HashMap<Field, Integer>();
		
		fieldCount = 0;
		methodCount = 0;
		
		
		// Create HashMap of all fields
		for(Field field : icclass.getFields()){
			fieldByName.put(field.getName(), field);
			fieldOffsets.put(field, fieldCount);
			fieldCount++;
		}
		
		
		// Create hashMap of all static methods
		for(Method method : icclass.getMethods()){
			
			String method_str = "_" + icclass.getName() + "_" + method.getName();
			
			if (!isMethodStatic(method)){
				methodByName.put(method_str, method);
				methodOffsets.put(method.getName(), methodCount);
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
		fieldOffsets = new HashMap<Field, Integer>();
		
		fieldCount = 0;
		methodCount = 0;
		
		fieldByName = (HashMap<String, Field>)((HashMap<String, Field>)clSuperClass.getFieldByName()).clone();
		fieldOffsets = (HashMap<Field, Integer>)((HashMap<Field, Integer>)clSuperClass.getFieldOffsets()).clone();
		fieldCount = clSuperClass.getFieldCount();
		
		methodByName = (HashMap<String, Method>)((HashMap<String, Method>)clSuperClass.getMethodByName()).clone();
		methodOffsets = (HashMap<String, Integer>)((HashMap<String, Integer>)clSuperClass.getMethodOffsets()).clone();
		methodCount = clSuperClass.getMethodCount();
		
		// Create HashMap of all fields
		for(Field field : icclass.getFields()){
			fieldByName.put(field.getName(), field);
			fieldOffsets.put(field, fieldCount);
			fieldCount++;
		}
		
		// Create hashMap of all static methods
		for(Method method : icclass.getMethods()){
			
			String method_str = "_" + icclass.getName() + "_" + method.getName();
			
			if (!isMethodStatic(method)){
				
				String method_superclass = "_" + clSuperClass.getClassIdent() + "_" + method.getName();
				
				// Method overrides superclass -> override it in the table 
				if (isMethodOverride(method_superclass, clSuperClass)){
					int methodLoc = this.methodOffsets.get(method.getName());
					methodByName.remove(method_superclass);
					methodOffsets.remove(method_superclass);
					methodByName.put(method_str, method);
					methodOffsets.put(method_str, methodLoc);
				}
				
				// Method is unique
				else{
					this.methodByName.put(method.getName(), method);
					this.methodOffsets.put(method.getName(), methodCount);
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
	
		if (clSuperClass.getMethodByName().get(method_superclass) != null){
			return true;
		}
		
		return false;
	}

	public void printDispatchTable(){
		
		System.out.print("_DV_" + this.getClassIdent() + ": [");
		
		if (this.getMethodCount() > 0){
			System.out.print(this.getMethodOffsets().get(0));
		}
		
		for (int i = 1; i < this.getMethodCount()-1; i++){
			
			System.out.print("," + this.getMethodOffsets().get(i));
		}
		
		System.out.print("]\n");
	}
	
}