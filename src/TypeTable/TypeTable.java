package TypeTable;

import java.util.*;
import IC.AST.*;
import IC.*;

public class TypeTable {

	public static BooleanType boolType;
	public static IntegerType intType;
	public static NullType nullType;
	public static StringType stringType;
	public static VoidType voidType;
	public static int id;
	
	// Maps element types to array types
	private static Map<Type,ArrayType> uniqueArrayTypes;
	// Maps element types to class types
	private static Map<String,ClassType> uniqueClassTypes;
	// Maps element types to method types
	private static Map<Method,MethodType> uniqueMethodTypes;

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
		boolType = new BooleanType(2);
		intType = new IntegerType(1);
		nullType = new NullType(3);
		stringType = new StringType(4);
		voidType = new VoidType(5);
		
		//we start the ID count after we define the 5 primitive types
		id = 5;
		
		uniqueArrayTypes = new HashMap<Type, ArrayType>();
		uniqueClassTypes = new HashMap<String, ClassType>();
		uniqueMethodTypes = new HashMap<Method, MethodType>();
		
		Type mainParamType = new PrimitiveType(-1, DataTypes.STRING);
		mainParamType.incrementDimension();
		arrayType(mainParamType);
		
		Type mainReturnType = new PrimitiveType(-1, DataTypes.VOID);
		Formal fMainMethod = new Formal(mainParamType, "args");
		List<Formal> lMainFormals = new LinkedList<Formal>();
		lMainFormals.add(fMainMethod);
		Method methodMainSig = new StaticMethod(mainReturnType,"main", lMainFormals, null);
		methodType(methodMainSig);
		
	}
	//this creates a new array type object. it incs the unique ID and inserts it to the array.
	public static ArrayType arrayType(Type elemType) {
		if (uniqueArrayTypes.containsKey(elemType)) {
			// array type object already created – return it
			return uniqueArrayTypes.get(elemType);
		}
		else{
			// object doesn’t exist – create and return it
			id++;
			ArrayType arrt = new ArrayType(elemType, id);
			uniqueArrayTypes.put(elemType,arrt);
			
			arrayTypesByOrder.put(arrayCount, elemType);
			arrayCount++;
			
			return arrt;
		}
	}
	//this creates a new kind of method
	public static MethodType methodType(Method method) {
		//first we check if there is a method like this
		if (uniqueMethodTypes.containsKey(method)){
			// method type object already created – return it
			return uniqueMethodTypes.get(method);
		}
		else{
			// no method like this exists, so we create it
			id++;
			MethodType mtdt = new MethodType(method,id);
			uniqueMethodTypes.put(method, mtdt);
			methodTypesByOrder.put(methodCount,method);
			methodCount++;
			
			return mtdt;
		}
	}
	//this is used when the user wants to build a fresh new class that doesn't exists
	//
	public static ClassType classType(ICClass classAST) {
		if (uniqueClassTypes.containsKey(classAST.getName())){
			// class already exists! we return it
			return uniqueClassTypes.get(classAST.getName());
		}
		else{
			// class doesn't exist! lets create it
			id++;
			ClassType newClass = new ClassType(classAST, id);
			uniqueClassTypes.put(classAST.getName(),newClass);
			
			classTypesByOrder.put(classCount, classAST.getName());
			classCount++;
			
			return newClass;
		}
	}
	
	//this is used to check if the current declared class exists!
	public static ClassType classType (String className){
		if (uniqueClassTypes.containsKey(className)){
			// class type object exists – return it
			return uniqueClassTypes.get(className);
		}
		else{
			// class type object doesn't exist - return null
			return null;
		}
	}
	
}
