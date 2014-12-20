package TypeTable;

import java.util.*;
import IC.AST.*;
import IC.*;

public class TypeTable {

	public static BooleanType booleanType;
	public static IntegerType integerType;
	public static NullType nullType;
	public static StringType stringType;
	public static VoidType voidType;
	public static int id;
	

	private static Map<Type,ArrayType> uniqueArraByTypes;

	private static Map<String,ClassType> uniqueClassByTypes;

	private static Map<Method,MethodType> uniqueMethodByTypes;
	//used to check if 2 methods has the same type of args
	private static Map<MethodType,List<Formal>> uniqueMethodByArgs;

	// For Printing the Class Types
	private static int classCount = 0;
	private static HashMap<Integer,String> classTypesByOrder = new HashMap<Integer,String>();
	
	// For Printing the Array Types
	private static int arrayCount = 0;
	private static HashMap<Integer,Type> arrayTypesByOrder = new HashMap<Integer,Type>();
	
	// For Printing the Method Types
	private static int methodCount = 0;
	private static HashMap<Integer,Method> methodTypesByOrder = new HashMap<Integer,Method>();
		
	public static void TypeTableInit(){
		booleanType = new BooleanType(2);
		integerType = new IntegerType(1);
		nullType = new NullType(3);
		stringType = new StringType(4);
		voidType = new VoidType(5);
		
		//we start the ID count after we define the 5 primitive types
		id = 5;
		
		uniqueArraByTypes = new HashMap<Type, ArrayType>();
		uniqueClassByTypes = new HashMap<String, ClassType>();
		uniqueMethodByTypes = new HashMap<Method, MethodType>();
		uniqueMethodByArgs = new HashMap<MethodType, List<Formal>>();
		
		//Construct the Main method for the class
		Type mainParamType = new PrimitiveType(-1, DataTypes.STRING);
		mainParamType.incrementDimension();
		arrayType(mainParamType);
		//main's return type
		Type mainReturnType = new PrimitiveType(-1, DataTypes.VOID);
		Formal formalMainMethod = new Formal(mainParamType, "args");
		///main's foramls
		List<Formal> listMainFormals = new LinkedList<Formal>();
		listMainFormals.add(formalMainMethod);
		//main proper insertion into the unique methods list
		Method methodMain = new StaticMethod(mainReturnType,"main", listMainFormals, null);
		methodType(methodMain);
		
	}
	
	//this creates a new array type object. it incs the unique ID and inserts it to the map.
	public static ArrayType arrayType(Type elemType) {
		if (uniqueArraByTypes.containsKey(elemType)) {
			// array type object already created – return it
			return uniqueArraByTypes.get(elemType);
		}
		else{
			// object doesn’t exist – create and return it
			id++;
			ArrayType arrt = new ArrayType(elemType, id);
			uniqueArraByTypes.put(elemType,arrt);
			
			arrayTypesByOrder.put(arrayCount, elemType);
			arrayCount++;
			
			return arrt;
		}
	}
	//this creates a new kind of method. if it exists we return it, otherwise we create a unique one!
	public static MethodType methodType(Method method) {
		//first we check if there is a method like this
		if (uniqueMethodByTypes.containsKey(method)){
			
			return uniqueMethodByTypes.get(method);
		}		
		else{
			// no method like this exists, so we create it
			id++;
			MethodType mtdt = new MethodType(method,id);
			uniqueMethodByTypes.put(method, mtdt);
			methodTypesByOrder.put(methodCount,method);
			methodCount++;
			
			return mtdt;
		}
	}
	
	public static MethodType getMainMethodType() {
		return uniqueMethodByTypes.get(methodTypesByOrder.get(0));
	}
	
	
	//this is used when the user wants to build a fresh new class that doesn't exists
	//we search if one exists and then return it, if not - create it.
	public static ClassType classType(ICClass classAST) {
		if (uniqueClassByTypes.containsKey(classAST.getName())){
			// class already exists! we return it
			return uniqueClassByTypes.get(classAST.getName());
		}
		else{
			
			// class doesn't exist! lets create it
			id++;
			ClassType newClass = new ClassType(classAST, id);
			uniqueClassByTypes.put(classAST.getName(),newClass);
			
			classTypesByOrder.put(classCount, classAST.getName().toString());
			classCount++;
			
			return newClass;
		}
	}
	
	//this is used to check if the current declared class exists!
	public static ClassType classType (String className){
		if (uniqueClassByTypes.containsKey(className)){
		
			return uniqueClassByTypes.get(className);
		}
		else{
			// class type object doesn't exist - return null
			return null;
		}
	}
/*	public static String getClassTypeByName(String name){
			
		if (uniqueClassByTypes.containsKey(name)){
			String str = uniqueClassByTypes.get(name).toString();
			StringBuilder strb = new StringBuilder(); 
			strb.append(str.substring(str.indexOf("s:") +2));
			
			return strb.toString();
		}
		else{
			// class type object doesn't exist - return null
			return null;
		} 
	}*/
	
	public static String getTypeNameByString(String name){
		
		Method method;
		int i;
		
		if (name.equals(DataTypes.BOOLEAN.getDescription()) || name.equals(DataTypes.INT.getDescription()) ||
				name.equals(DataTypes.STRING.getDescription())|| name.equals(DataTypes.VOID.getDescription()))
			return name;
		
		else if (uniqueClassByTypes.containsKey(name)){
			String str = uniqueClassByTypes.get(name).toString();
			StringBuilder strb = new StringBuilder(); 
			strb.append(str.substring(str.indexOf("s:") +2));
			
			return strb.toString();
		}
		
		for (i = 0; i < methodCount; i++){
			
			method = methodTypesByOrder.get(i);
			if (method.getName().equals(name)){
				return uniqueMethodByTypes.get(method).toStringSymTable();	
			}	
		}
		
		//////////// Added for array reference
		if (arrayTypesByOrder.containsKey(name)){
			String str = arrayTypesByOrder.get(name).toString();

			return str;
		}
		//////////// Added for array reference
		
		return "";
	}
	
	//let's print the table!!!!!!!!!!!!!!!!1
	public static void printTypeTable(String fileName){
		  //as the example
		  System.out.println("Type Table: " + fileName);
		  System.out.println(integerType.toString());
		  System.out.println(booleanType.toString());
		  System.out.println(nullType.toString());
		  System.out.println(stringType.toString());
		  System.out.println(voidType.toString());
		  
		  
		  for(int i = 0; i < classCount; i++ ){
		   String classKey = classTypesByOrder.get(i);
		   System.out.println(uniqueClassByTypes.get(classKey).toString());
		  }
		  
		  for(int i = 0; i < arrayCount; i++ ){
		   Type arrayKey = arrayTypesByOrder.get(i);
		   System.out.println(uniqueArraByTypes.get(arrayKey).toString());
		  }
		  
		  for(int i = 0; i < methodCount; i++ ){
		   Method methodKey = methodTypesByOrder.get(i);
		   System.out.println(uniqueMethodByTypes.get(methodKey).toString());
		  }
		 }
	
	public static TypeTableType convertTypeToTypeTableType(Type type){
		if(type == null) { return null; }
		else{
			if (type.getDimension()>0){
				return TypeTable.arrayType(type);
			}
			else if(type.getName().equals(DataTypes.INT.getDescription())){
				return TypeTable.integerType;
			}
			else if(type.getName().equals(DataTypes.BOOLEAN.getDescription())){
				return TypeTable.booleanType;
			}
			else if(type.getName().equals(DataTypes.STRING.getDescription())){
				return TypeTable.stringType;
			}
			else if(type.getName().equals(DataTypes.VOID.getDescription())){
				return TypeTable.voidType;
			}
			else{
				return TypeTable.classType(type.getName());
			}
		}
	}
	
	
}
