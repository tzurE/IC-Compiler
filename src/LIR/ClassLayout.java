package LIR;

import java.util.HashMap;
import java.util.Map;

import IC.AST.*;
/**
 * 
 * @author Tzur hagever + Yael hashava!
 *
 */
public class ClassLayout {
	
	private String classIdent;
	
	private Map<String,Method> methodByName;
	private Map<String,Integer> methodOffsets;
	private Map<String,Field> fieldByName;
	private Map<Field,Integer> fieldOffsets;
	private int fieldCount;
	private int methodCount;
	
	//if a class has no superClass, we use this builder
	public ClassLayout(ICClass icclass){
		this.classIdent = icclass.getName();

		methodByName = new HashMap<String, Method>();
		methodOffsets = new HashMap<String, Integer>();
		fieldByName = new HashMap<String, Field>();
		fieldOffsets = new HashMap<Field, Integer>();
		
		fieldCount = 0;
		methodCount = 0;
		
		
		for(Field field : icclass.getFields()){
			fieldByName.put(field.getName(), field);
			fieldOffsets.put(field, fieldCount);
			fieldCount++;
		}
		
		
		for(Method method : icclass.getMethods()){
			methodByName.put(method.getName(), method)
		}
	}
	
	
	
}
