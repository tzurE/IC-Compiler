package SemanticCheckerVisitor;


import IC.AST.*;
import SymbolTables.*;
import TypeTable.*;
import IC.*;

public class SymbolVisitorBuilder implements PropVisitor{

	private String prog_name;
	private boolean mainExists = false;
	
	public SymbolVisitorBuilder(String prog_name){
		this.prog_name = prog_name;
	}
	
	@Override
	public Object visit(Program program, SymbolTable table) {
		
		TypeTable.TypeTableInit();
		//no father - it is the main table! therefore we send "null"
		SymbolTable symbol_table = new GlobalSymbolTable(prog_name, null);
		for (ICClass icClass : program.getClasses()){
			
			//we create the class as a type in the type table
			TypeTableType classt = TypeTable.classType(icClass);
			//for each class in the program, we add an entry in the global symbol table
			symbol_table.addEntry(icClass.getName(), new ClassEntry(icClass.getName(), classt), icClass.getLine());
			
			icClass.accept(this, symbol_table);
		}
		//if we went over all of the classes and we did not find any main class, we throw an exception
		if(!mainExists){
			try{
				throw new SemanticError("Error: an IC program must have a main method");
			}catch (SemanticError e){
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		
		return symbol_table;
	}

	@Override
	public Object visit(ICClass icClass, SymbolTable parent_table) {
		ClassSymbolTable symbol_table = new ClassSymbolTable(icClass.getName(), parent_table);
		//first we check inheritance! 
		if(!icClass.hasSuperClass()){
			symbol_table.setFather_table(parent_table);
			symbol_table.getFather_table().addChild(icClass.getName(), symbol_table);
			//symbol_table.addChild(icClass.getName(), new ClassSymbolTable(icClass.getName(), symbol_table));
		}
		else{
			if(icClass.getName().equals(icClass.getSuperClassName())){
				try {
					throw new SemanticError(icClass.getLine(),"Error: the class '" + icClass.getName() + "' extends itself!");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}

			}
			//we search for the father
			ClassSymbolTable superClass = ((GlobalSymbolTable)parent_table).findInnerChild(icClass.getSuperClassName());
			
			if(superClass == null){
				//if a class is being extended before being declared
				try {
					throw new SemanticError(icClass.getLine(),
							"the class '"
									+ icClass.getSuperClassName()
									+ "' Is not declared yet! first declare, then extend it");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
				
			}
			superClass.addChild(icClass.getName(), symbol_table);
			symbol_table.setFather_table(superClass);
		}
		
		for (Field field : icClass.getFields()){
			
			field.accept(this, symbol_table);
		}
		for (Method method : icClass.getMethods()){
			
			method.accept(this, symbol_table);
		}
		
		return symbol_table;
	}

	@Override
	public Object visit(Field field, SymbolTable parent_table) {
		//is it a primitive type? if so, we send it to a function that returns the correct type for us
		//we check if it has the same class as the PrimitiveType class, same as we did in the global table for AST printing
		if(field.getType().getClass().equals(PrimitiveType.class)){
			parent_table.addEntry(field.getName(), new FieldEntry(field.getName(),SymbolKinds.FIELD ,this.TypesForPrimitive((PrimitiveType) field.getType())), field.getLine());
		}
		else{
			if(field.getType().getDimension()==0)
				parent_table.addEntry(field.getName(), new FieldEntry(field.getName(), SymbolKinds.FIELD ,new ClassType(field.getType().getName())), field.getLine());
			else
				parent_table.addEntry(field.getName(), new FieldEntry(field.getName(), SymbolKinds.FIELD,getArrayType(field.getType())), field.getLine());
		}
		field.getType().accept(this, parent_table);
		return null;
	}

	@Override
	public Object visit(VirtualMethod method, SymbolTable parent_table) {
		if(method.getName().toLowerCase().equals("main")){
			try {
				throw new SemanticError(method.getLine(),
						"the main class must be a static class, change it");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		//we add the new method to the list of the class it belongs to, for easier printing
		parent_table.addEntry(method.getName(), new MethodEntry(method.getName(),SymbolKinds.VIRTUAL_METHOD,TypeTable.methodType(method)), method.getLine());
		MethodSymbolTable symbol_table = new MethodSymbolTable(SymbolTableType.METHOD,method.getName(),parent_table);
		
		
		//we add the method as a child, in order to (pretty)print it. 
		parent_table.addChild(method.getName(), symbol_table);
		method.getType().accept(this, symbol_table);
		
		//go over everything in the method, formals and stmnt
		for (Formal formal : method.getFormals()){
			formal.accept(this, symbol_table);
		}
		
		for (Statement statement : method.getStatements()){
			statement.accept(this, symbol_table);
		}
		
		
		return symbol_table;
	}

	@Override
	public Object visit(StaticMethod method, SymbolTable parent_table) {
		MethodType methodType;
		
		//first we check if it's the main function!
		//we go over all the qualifications that belong to the main function, and check 
		//Whether they all works
		if(!method.getName().toString().equals("main"))
			methodType = TypeTable.methodType(method);
		
		else
		{	
			//if there is already a main function declared - we throw a semantic error
			if(mainExists){
				try {
					throw new SemanticError(method.getLine(),
							"Only one main method is allowed!");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
			
			//check the main return type
			if(!method.getType().getName().equals("void")){
				try {
					throw new SemanticError(method.getLine(),
							"Error: main function return type haas to be void");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
			
			//if there is more than one formal - throw
			if (method.getFormals().size() != 1){
				try {
					throw new SemanticError(method.getLine(),
							"Main hold only 1 arguemnt, an array of strings");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
			
			//is it a string[]?
			Formal mainParameter = method.getFormals().get(0);
			if(!mainParameter.getType().getName().equals("string") || mainParameter.getType().getDimension() != 1){
				try {
					throw new SemanticError(method.getLine(),
							"the 'main' method's argument must bee an arrau of strings - ('string[]')");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
			
			//if all checks out good - we inform the program
			mainExists = true;
			methodType = TypeTable.getMainMethodType();
		}
		
		parent_table.addEntry(method.getName(),new MethodEntry(method.getName(), SymbolKinds.STATIC_METHOD, methodType), method.getLine());
		MethodSymbolTable symbol_table = new MethodSymbolTable(SymbolTableType.METHOD,method.getName(), parent_table);
		parent_table.addChild(method.getName(), symbol_table);
		
		method.getType().accept(this, symbol_table);
		for (Formal formal : method.getFormals()){
			formal.accept(this, symbol_table);
		}
		for (Statement statement : method.getStatements()){
			
			statement.accept(this, symbol_table);
		}
		
		
		return symbol_table;
	}

	@Override
	public Object visit(LibraryMethod method, SymbolTable table) {
		
		//library = static. so we send it to the static method
		StaticMethod method1 = new StaticMethod(method.getType(), method.getName(), method.getFormals(), method.getStatements());
			this.visit(method1, table);
		return null;
	}

	@Override
	public Object visit(Formal formal, SymbolTable parent_table) {
		//pretty much the same as the fields for the class, so we just copied it
		if(formal.getType().getClass().equals(PrimitiveType.class)){
			parent_table.addEntry(formal.getName(), new ParameterEntry(formal.getName(),SymbolKinds.PARAMETER ,this.TypesForPrimitive((PrimitiveType) formal.getType())), formal.getLine());
		}
		else{
			if(formal.getType().getDimension()==0)
				parent_table.addEntry(formal.getName(), new ParameterEntry(formal.getName(), SymbolKinds.PARAMETER ,new ClassType(formal.getType().getName())), formal.getLine());
			else
				parent_table.addEntry(formal.getName(), new ParameterEntry(formal.getName(), SymbolKinds.PARAMETER,getArrayType(formal.getType())), formal.getLine());
		}
		formal.getType().accept(this, parent_table);
		return null;
	}

	@Override
	public Object visit(PrimitiveType type, SymbolTable table) {

		return null;
	}

	@Override
	public Object visit(UserType type, SymbolTable table) {

		return null;
	}

	@Override
	public Object visit(Assignment assignment, SymbolTable table) {
		assignment.getVariable().accept(this,table);
		assignment.getAssignment().accept(this, table);
		return table;
	}

	@Override
	public Object visit(CallStatement callStatement, SymbolTable table) {
		callStatement.getCall().accept(this,table);
		return table;
	}

	@Override
	public Object visit(Return returnStatement, SymbolTable table) {
		if(returnStatement.hasValue()){
			returnStatement.getValue().accept(this, table);}
		return table;
	}

	@Override
	public Object visit(If ifStatement, SymbolTable table) {
		//we send the visitor onward
		ifStatement.getCondition().accept(this,table);
		ifStatement.getOperation().accept(this,table);
		
		if (ifStatement.hasElse()){
			ifStatement.getElseOperation().accept(this, table);
		}
		
		return table;
	}

	@Override
	public Object visit(While whileStatement, SymbolTable table) {

		whileStatement.getCondition().accept(this, table);
		whileStatement.getOperation().accept(this,table);
		
		return table;
	}

	@Override
	public Object visit(Break breakStatement, SymbolTable table) {
		return null;
	}

	@Override
	public Object visit(Continue continueStatement, SymbolTable table) {
		return null;
	}

	@Override
	public Object visit(StatementsBlock statementsBlock, SymbolTable parent_table) {
		StatementSymbolTable symbol_table = new StatementSymbolTable("statement block in "+ parent_table.getId(), parent_table);
		for(Statement statement : statementsBlock.getStatements()){
			statement.accept(this, symbol_table);
		}
		parent_table.addChild(symbol_table.getId(), symbol_table);
		return symbol_table;
	}

	@Override
	public Object visit(LocalVariable localVariable, SymbolTable parent_table) {
		//pretty much the same as the fields for the class, so we just copied it
		if(localVariable.getType().getClass().equals(PrimitiveType.class)){
			parent_table.addEntry(localVariable.getName(), new LocalVariebleEntry(localVariable.getName(),SymbolKinds.LOCAL_VARIABLE ,this.TypesForPrimitive((PrimitiveType) localVariable.getType())), localVariable.getLine());
		}
		else{
			if(localVariable.getType().getDimension()==0)
				parent_table.addEntry(localVariable.getName(), new LocalVariebleEntry(localVariable.getName(), SymbolKinds.LOCAL_VARIABLE ,new ClassType(localVariable.getType().getName())), localVariable.getLine());
			else
				parent_table.addEntry(localVariable.getName(), new LocalVariebleEntry(localVariable.getName(), SymbolKinds.LOCAL_VARIABLE,getArrayType(localVariable.getType())), localVariable.getLine());
		}
		localVariable.getType().accept(this, parent_table);
		if(localVariable.hasInitValue())
			localVariable.getInitValue().accept(this, parent_table);
	return parent_table;
	}

	@Override
	public Object visit(VariableLocation location, SymbolTable table) {
		if(location.isExternal())
			location.getLocation().accept(this, table);
		return table;
	}

	@Override
	public Object visit(ArrayLocation location, SymbolTable table) {
		
		location.getArray().accept(this, table);
		location.getIndex().accept(this, table);
		return table;
	}

	@Override
	public Object visit(StaticCall call, SymbolTable table) {
		for (Expression argument : call.getArguments()){
			argument.accept(this,table);
		}
		return table;
	}

	@Override
	public Object visit(VirtualCall call, SymbolTable table) {
		
		if (call.isExternal())
			call.getLocation().accept(this,table);
		
		for (Expression argument : call.getArguments())
			argument.accept(this,table);
		return table;
	}

	@Override
	public Object visit(This thisExpression, SymbolTable table) {
		thisExpression.accept(this,table);
		return table;
	}

	@Override
	public Object visit(NewClass newClass, SymbolTable table) {
		return null;
	}

	@Override
	public Object visit(NewArray newArray, SymbolTable table) {
		
		newArray.getType().accept(this,table);
		newArray.getSize().accept(this,table);
		
		return table;
	}

	@Override
	public Object visit(Length length, SymbolTable table) {
		length.getArray().accept(this,table);
		return table;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp, SymbolTable table) {
		
		binaryOp.getFirstOperand().accept(this,table);
		binaryOp.getSecondOperand().accept(this,table);
		
		return table;
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp, SymbolTable table) {

		binaryOp.getFirstOperand().accept(this,table);
		binaryOp.getSecondOperand().accept(this,table);
		
		return table;
	}

	@Override
	public Object visit(MathUnaryOp unaryOp, SymbolTable table) {
		
		unaryOp.getOperand().accept(this, table);
		
		return table;
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp, SymbolTable table) {

		unaryOp.getOperand().accept(this, table);
		
		return table;
	}

	@Override
	public Object visit(Literal literal, SymbolTable table) {
		return null;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock, SymbolTable table) {
		expressionBlock.accept(this,table);
		expressionBlock.getExpression().accept(this,table);
		
		return table;
	}

	@Override
	public Object visit(Method method, SymbolTable table) {
		method.accept(this,table);
		//do we need this? the dynamic type tells the method where to go.
		//dont use it
		return table;
	}

	private TypeTableType TypesForPrimitive(PrimitiveType type){
		if(type.getDimension()>0){
			return getArrayType(type);
		}
		else{
			if(type.getName()=="boolean")
				return TypeTable.booleanType;
			
			else if(type.getName()=="int")
				return TypeTable.integerType;
			
			else if(type.getName()=="string")
				return TypeTable.stringType;
			
			else 
				return TypeTable.voidType;
		}
	}
	
	
	private TypeTableType getArrayType(Type array){
		
		//here we check what kind of array type are we dealing with
		String type_name = array.getName();
		if(array.getClass().equals(PrimitiveType.class)){
			if(type_name == "int"){
				for(int i = 1; i < array.getDimension() ; i++){
					PrimitiveType type = new PrimitiveType(-1, DataTypes.INT);
					type.setDimention(i);
					TypeTable.arrayType(type);
				}
			}
			
			else if(type_name == "string"){
				for(int i = 1 ; i < array.getDimension() ; i++){
					PrimitiveType type = new PrimitiveType(-1, DataTypes.STRING);
					type.setDimention(i);
					TypeTable.arrayType(type);
				}
			}
			
			else if(type_name == "boolean"){
				for(int i = 1 ; i < array.getDimension(); i++){
					PrimitiveType type = new PrimitiveType(-1, DataTypes.BOOLEAN);
					type.setDimention(i);
					TypeTable.arrayType(type);
				}
			}
			else{
				try {
					throw new SemanticError(array.getLine(),
							"Error: array cant be of this type! - "+TypeTable.convertTypeToTypeTableType(array).toString()
									);
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		else if(array.getClass().equals(UserType.class)){
			for(int i=1;i<array.getDimension();i++){
				UserType type = new UserType(-1, type_name);
				type.setDimention(i);
				TypeTable.arrayType(type);
			}

		}
		return TypeTable.arrayType(array);
	}
	

}
