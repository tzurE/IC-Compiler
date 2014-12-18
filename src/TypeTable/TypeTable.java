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
	
	// Maps element types to array types
	private static Map<Type,ArrayType> uniqueArraByTypes;
	// Maps element types to class types
	private static Map<String,ClassType> uniqueClassByTypes;
	// Maps element types to method types
	private static Map<Method,MethodType> uniqueMethodByTypes;

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
			
			classTypesByOrder.put(classCount, classAST.getName());
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
		   System.out.println();
		   System.out.print(uniqueClassByTypes.get(classKey).toString());
		  }
		  
		  for(int i = 0; i < arrayCount; i++ ){
		   Type arrayKey = arrayTypesByOrder.get(i);
		   System.out.println();
		   System.out.print(uniqueArraByTypes.get(arrayKey).toString());
		  }
		  
		  for(int i = 0; i < methodCount; i++ ){
		   Method methodKey = methodTypesByOrder.get(i);
		   System.out.println();
		   System.out.print(uniqueMethodByTypes.get(methodKey).toString());
		  }
		 }
	
	
}
