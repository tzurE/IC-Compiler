package SemanticCheckerVisitor;

import java.util.LinkedList;
import java.util.List;

import IC.*;
import IC.AST.*;
import TypeTable.*;
import SymbolTables.*;
import SemanticCheckerVisitor.*;

public class SymbolVisitorChecker implements PropVisitor {
	
	private GlobalSymbolTable globalTable;
	private int loopLevel = 0;
	private boolean inStaticMethod = false;

	//get a global table to work on
	public SymbolVisitorChecker(GlobalSymbolTable globalTable) {
		this.globalTable = globalTable;
	}

	public Object visit(Program program, SymbolTable table) {
		TypeTableType classType;
		boolean isTypeCheckDone = true;
		for (ICClass icClass : program.getClasses()){
			classType = (TypeTableType) icClass.accept(this, table);
			if(classType == null){
				isTypeCheckDone = false;
				try {
					throw new SemanticError("Something went wrong during semantic check");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		return isTypeCheckDone;
	}

	public Object visit(ICClass icClass, SymbolTable table) {
		for (Field field : icClass.getFields()){
			if(field.accept(this, table) == null){
				return null;
			}
		}
		for (Method method : icClass.getMethods()){
			if(method.accept(this, table) == null){
				return null;
			}
		}
		return TypeTable.classType(icClass.getName());
	}
	
	public Object visit(Method method, SymbolTable table) {
		return null;
	}

	public Object visit(Field field, SymbolTable table) {
		//semantic checks - if the type is not primitive, we change the TypeTableType value in the symbolTable to the appropriate TypeTableType
		TypeTableType type= (TypeTableType) field.getType().accept(this, table);
		//TODO : do we really need it
		//if(type!=null){
		//	field.getScope().setVeriableTypes(field.getName(), type);
		//}

		SymbolEntry fieldSymbol = (SymbolEntry)field.getScope().getEntry(field.getName(), SymbolKinds.FIELD);
		TypeTableType fieldType = (TypeTableType)fieldSymbol.getType();

		if(fieldType == null){
			return null;
		}
		else{
			return fieldType;
		}
	}

	public Object visit(VirtualMethod method, SymbolTable table) {
		//semantic checks - checks if the return type and parameters type are legal 
		method.getType().accept(this, table);

		for (Formal formal : method.getFormals()){
			formal.accept(this, table);
		}
		ClassSymbolTable enclosing = (ClassSymbolTable) method.getScope();
		SymbolEntry methodEntry =  (SymbolEntry) enclosing.searchTable(method.getName(), SymbolKinds.VIRTUAL_METHOD);
		//checks if the method is legal - i.e or overriding a method in super class,
		//or superclasses don't have an identifier with the same name as the method's name
		if(!enclosing.isIdExist(methodEntry)){
			try {
				throw new SemanticError(method.getLine(),
						"Overloading is not allowed. trying to overload the method '"
						+ method.getName()
						+ "'");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		for(Statement stmt : method.getStatements()){
			if(stmt.accept(this, table) == null){
				return null;
			}
		}

		return true;
	}

	public Object visit(StaticMethod method, SymbolTable table) {
		inStaticMethod = true;
		//semantic checks - checks if the return type and parameters type are legal 
		method.getType().accept(this, table);
		for (Formal formal : method.getFormals())
			formal.accept(this, table);
		ClassSymbolTable enclosing = (ClassSymbolTable) method.getScope();
		SymbolEntry methodEntry =  (SymbolEntry) enclosing.searchTable(method.getName(), SymbolKinds.STATIC_METHOD);
		//checks if the method is legal - i.e or overriding a method in super class,
		//or superclasses don't have an identifier with the same name as the method's name
		if(!enclosing.isIdExist(methodEntry)){
			try {
				throw new SemanticError(method.getLine(),
						"Overloading is not allowed. trying to overload the method '"
						+ method.getName()
						+ "'");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		for(Statement stmt : method.getStatements()){
			if(stmt.accept(this, table) == null){
				inStaticMethod = false;
				return null;
			}
		}
		inStaticMethod = false;
		return true;
	}

	public Object visit(LibraryMethod method, SymbolTable table) {
		//semantic checks - checks if the return type and parameters type are legal 
		method.getType().accept(this, table);
		for (Formal formal : method.getFormals())
			formal.accept(this, table);


		for(Statement stmt : method.getStatements()){
			if(stmt.accept(this, table) == null){
				return null;
			}
		}
		return true;
	}

	public Object visit(Formal formal, SymbolTable table) {
		SymbolEntry formalSymbol = (SymbolEntry)formal.getScope().getEntry(formal.getName(), SymbolKinds.PARAMETER);
		TypeTableType formalType = (TypeTableType)formalSymbol.getType();
		if(formalType == null){
			return null;
		}
		else{
			return formalType;
		}
	}

	public Object visit(PrimitiveType type, SymbolTable table) {
		return TypeTable.convertTypeToTypeTableType(type);
	}

	public Object visit(UserType type, SymbolTable table) {

		//semantic checking - checks if the class we use is declared
		if(((GlobalSymbolTable)table).findInnerChild(type.getName())==null){
			try {
				throw new SemanticError(type.getLine(),
						"Use of an undeclared class '"
						+ type.getName()
						+ "'");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
			return null;
		}
		else{
			return TypeTable.convertTypeToTypeTableType(type);
		}
	}

	public Object visit(Assignment assignment, SymbolTable table) {
		TypeTableType varType = (TypeTableType)assignment.getVariable().accept(this, table);
		TypeTableType valueType = (TypeTableType)assignment.getAssignment().accept(this, table);
		//checks if the assignment value's type is a sub type of the variable's type
		if(valueType.subType(varType)){
			return assignment;
		}
		else{
			try {
				throw new SemanticError(assignment.getLine(),
				"assignment value's type must be a sub type of the assignment location's type");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}	
		}
		return null;
	}

	public Object visit(CallStatement callStatement, SymbolTable table) {
		return callStatement.getCall().accept(this, table);
	}

	public Object visit(Return returnStatement, SymbolTable table) {
		TypeTableType returnStmtType;
		TypeTableType methodReturnType;
		if(!returnStatement.hasValue()){
			returnStmtType = (TypeTableType)TypeTable.voidType;
		}
		else{
			returnStmtType = (TypeTableType)returnStatement.getValue().accept(this, table);
		}

		// Gets the method symbol table which contains the return statement
		SymbolTable returnStmtScope = (SymbolTable)returnStatement.getScope(); 
		while((!(returnStmtScope.getType().compareTo(SymbolTableType.STATIC_METHOD)==0)) &&
				(!(returnStmtScope.getType().compareTo(SymbolTableType.VIRTUAL_METHOD)==0))){
			returnStmtScope = returnStmtScope.getFather_table();
		}
		
		// Gets the class symbol table which contains the method symbol as an entry
		SymbolTable classScope = returnStmtScope.getFather_table();	

		// Gets the method's name and kind
		String methodName = returnStmtScope.getId();
		SymbolTableType methodTableKind = returnStmtScope.getType();
		MethodEntry mSymbol;
		// Gets the method symbol, kind & return type
		if(methodTableKind.compareTo(SymbolTableType.STATIC_METHOD) == 0) {
			mSymbol = (MethodEntry)classScope.getEntry(methodName, SymbolKinds.STATIC_METHOD);
		}
		else {
			mSymbol = (MethodEntry)classScope.getEntry(methodName, SymbolKinds.VIRTUAL_METHOD);
		}
		
		// Gets the method return type
		MethodType mType = (MethodType)mSymbol.getType();
		methodReturnType = mType.getReturnType();
		//checks if the expression in the return statement is from the return type of the method
		if(!returnStmtType.subType(methodReturnType)){
			try {
				throw new SemanticError(returnStatement.getLine(),
						"the type of the returned value in method '"
						+ mType.getName()
						+ "' must be a sub type of the method's return type");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		else{ return true; }

		return null;
	}

	public Object visit(If ifStatement, SymbolTable table) {
		TypeTableType condType = (TypeTableType)ifStatement.getCondition().accept(this, table);
		if(condType == null) { return null;}
		if(ifStatement.getOperation().accept(this, table) == null){ return null; }
		if(ifStatement.hasElse()){
			if(ifStatement.getElseOperation().accept(this, table) == null){ return null; }
		}
		//checks if the If condition is from boolean type
		if(condType.subType(TypeTable.booleanType)){
			return true;
		}
		else{
			try {
				throw new SemanticError(ifStatement.getLine(),
				"If condition must be of boolean type");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}



		return true;
	}

	public Object visit(While whileStatement, SymbolTable table) {
		//we enter to while, so we increment the while counter
		loopLevel++;
		TypeTableType condExprType = (TypeTableType)whileStatement.getCondition().accept(this, table);
		//checks if the While condition is from boolean type
		if(!condExprType.subType(TypeTable.booleanType)){
			try {
				throw new SemanticError(whileStatement.getLine(),
				"While condition must be of boolean type");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		if(whileStatement.getOperation().accept(this, table) == null){
			//we exit to while, so we decrement the while counter
			loopLevel--;
			return null;
		}
		//we exit to while, so we decrement the while counter
		loopLevel--;
		return true;
	}

	public Object visit(Break breakStatement, SymbolTable table) {
		//checks if the break statement is inside a while. if not - we throw a semantic error
		if(loopLevel==0){
			try {
				throw new SemanticError(breakStatement.getLine(),
				"'break' can only be used in while statements");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		return true;
	}

	public Object visit(Continue continueStatement, SymbolTable table) {
		//checks if the continue statement is inside a while. if not - we throw a semantic error
		if(loopLevel==0){
			try {
				throw new SemanticError(continueStatement.getLine(),
				"'continue' can only be used in while statements");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		return true;
	}

	public Object visit(StatementsBlock statementsBlock, SymbolTable table) {
		for(Statement stms: statementsBlock.getStatements()){
			if(stms.accept(this, table) == null){
				return null;
			}
		}

		return true;
	}

	public Object visit(LocalVariable localVariable, SymbolTable table) {
		//semantic checks - if the type is not primitive, we change the TypeTableType value in the symbolTable to the appropriate TypeTableType
		TypeTableType varType = (TypeTableType)localVariable.getType().accept(this, table);
		//TODO :  check if needed
		//if(varType!=null){
		//	localVariable.getScope().setVeriableTypes(localVariable.getName(), varType);
		//}

		if(localVariable.hasInitValue()){
			TypeTableType initValType = (TypeTableType)localVariable.getInitValue().accept(this, table);


			if(initValType != null){
				//checks if the initialization value's type is a sub type of the variable's type
				if(!initValType.subType(varType)){
					try {
						throw new SemanticError(localVariable.getLine(),
								"In local variable '"
								+ localVariable.getName()
								+"' initialization, initialization value's type must be a sub type of the variable's type");
					}
					catch (SemanticError e) {
						System.out.println(e.getErrorMessage());
						System.exit(-1);
					}
				}
			}

		}
		return true;
	}

	public Object visit(VariableLocation location, SymbolTable table) {
		if(location.isExternal()){
			//Gets class (User Type)
			TypeTableType locationType = (TypeTableType)location.getLocation().accept(this, table);
			if(locationType == null) { return null; }

			//Gets class  by it's name from location
			String locationTypeName = locationType.getName();
			//get the class table, which the variable located in
			ClassSymbolTable classTable = ((GlobalSymbolTable)table).findInnerChild(locationTypeName);
			if(classTable==null){
				try {
					throw new SemanticError(location.getLine(),
							"Use of an undeclared class '"
							+ locationTypeName
							+ "'");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
				return null;
			}

			//Gets field type
			SymbolEntry fEntry = (SymbolEntry)classTable.getEntry(location.getName(), SymbolKinds.FIELD);
			if(fEntry==null){
				try {
					throw new SemanticError(location.getLine(),
							"Use of an undeclared field '"
							+ location.getName()
							+ "'");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
			TypeTableType fieldType = (TypeTableType)fEntry.getType();
			return fieldType;
		}
		else{
			SymbolTable enclosingTable = location.getScope();
			SymbolEntry variableSymbol = enclosingTable.searchForVar(location.getName(),location.getLine());
			if(variableSymbol==null){
				try {
					throw new SemanticError(location.getLine(),
							"the identifier '"
							+ location.getName()
							+ "' must be declared before use");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}

			TypeTableType fieldType = (TypeTableType)variableSymbol.getType();
			return fieldType;
		}

	}

	public Object visit(ArrayLocation location, SymbolTable table) {
		TypeTableType indexType = (TypeTableType)location.getIndex().accept(this, table);
		TypeTableType arrGetType = (TypeTableType)location.getArray().accept(this, table);
		ArrayType arrayType = null;
		if(arrGetType.getClass().equals(ArrayType.class)){
			arrayType = (ArrayType)arrGetType;
		}
		else{
			try {
				throw new SemanticError(location.getLine(),
						"The type of the expression must be an array type");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		if(indexType.subType(TypeTable.integerType)){
			return TypeTable.convertTypeToTypeTableType(arrayType.getElemType());
		}
		else{
			try {
				throw new SemanticError(location.getLine(),
						"Array location index must be of an int type");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		return null;
	}

	public Object visit(StaticCall call, SymbolTable table) {
		//get the class table, which the static method located in
		ClassSymbolTable classScope = ((GlobalSymbolTable)table).findInnerChild(call.getClassName());
		if(classScope==null){
			try {
				throw new SemanticError(call.getLine(),
						"Use of an undeclared class '"
						+ call.getClassName()
						+ "'");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
			return null;
		}
		String methodName = call.getName();
		MethodEntry mSymbol = (MethodEntry)classScope.getEntry(methodName, SymbolKinds.STATIC_METHOD);
		if(mSymbol==null){
			try {
				throw new SemanticError(call.getLine(),
						"Use of an undeclared method '"
						+ call.getName()
						+ "'");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
			return null;
		}
		MethodType mType = (MethodType)mSymbol.getType();
		List<Formal> formalList = mType.getmethod().getFormals();
		int formalsNum = formalList.size();
		int formalCounter = 0;

		if(call.getArguments().size() != formalsNum){
			try {
				throw new SemanticError(call.getLine(),
						"In the call for the method '" 
						+ call.getName()
						+ "' the number of arguments must be equal to the nummber of method's formals");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		for (Expression actualParam : call.getArguments()) {
			TypeTableType argType = (TypeTableType)actualParam.accept(this, table);
			TypeTableType formalType = (TypeTableType)formalList.get(formalCounter).accept(this, table);
			formalCounter++;

			if(argType.subType(formalType)){
				return mType.getReturnType();
			}
			else{
				try {
					throw new SemanticError(call.getLine(),
							"In the call for the method '" 
							+ call.getName()
							+ "' arguments' TypeTableType must be sub TypeTableType of the method's formals TypeTableType");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		return mType.getReturnType();
	}

	public Object visit(VirtualCall call, SymbolTable table) {
		List<Formal> mFormals;
		String methodName = call.getName(); 
		MethodType mType;
		if(call.isExternal()){
			TypeTableType callLocationType = (TypeTableType)call.getLocation().accept(this, table);
			if(callLocationType == null){ return null; }

			//Gets class  by it's name from location
			String callLocationTypeName = callLocationType.getName();
			//get the class table, which the virtual method located in
			ClassSymbolTable classTable = ((GlobalSymbolTable)table).findInnerChild(callLocationTypeName);
			if(classTable==null){
				try {
					throw new SemanticError(call.getLine(),
							"Use of an undeclared class '"
							+ callLocationTypeName
							+ "'");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
				return true;
			} 
			//Gets method formal parameters TypeTableType
			SymbolEntry mEntry = (SymbolEntry)classTable.getEntry(methodName, SymbolKinds.VIRTUAL_METHOD);
			if(mEntry==null){
				try {
					throw new SemanticError(call.getLine(),
							"Use of an undeclared method '"
							+ methodName
							+ "'");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
				return true;
			}
			mType = (MethodType)mEntry.getType();
			mFormals = mType.getmethod().getFormals();
		}
		else{
			SymbolTable currentScope = call.getScope();
			while(!(currentScope.getType().compareTo(SymbolTableType.CLASS) == 0)){
				currentScope = currentScope.getFather_table();
			}
			boolean virtualFound = false;
			SymbolEntry mEntry = (SymbolEntry)currentScope.getEntry(methodName, SymbolKinds.VIRTUAL_METHOD);
			if(mEntry!=null){
				virtualFound = true;
			}
			if(inStaticMethod && virtualFound){
				try {
					throw new SemanticError(call.getLine(),
							"Cannot make a static reference to the non-static method '"
							+ methodName
							+ "'");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
			if(!virtualFound){
				mEntry = (SymbolEntry)currentScope.getEntry(methodName, SymbolKinds.STATIC_METHOD);
			}
			if(mEntry==null){
				try {
					throw new SemanticError(call.getLine(),
							"Use of an undeclared method '"
							+ methodName
							+ "'");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
				return true;
			}
			mType = (MethodType)mEntry.getType();
			mFormals = mType.getmethod().getFormals();
		}

		int formalsNum = mFormals.size();
		if(call.getArguments().size() != formalsNum){
			try {
				throw new SemanticError(call.getLine(),
						"In the call for the method '" 
						+ call.getName()
						+ "' the number of arguments must be equal to the nummber of method's formals");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		int formalCounter = 0;
		for (Expression actualParam : call.getArguments()) {
			TypeTableType argType = (TypeTableType)actualParam.accept(this, table);
			TypeTableType formalType = (TypeTableType)mFormals.get(formalCounter).accept(this, table);
			formalCounter++;

			if(!argType.subType(formalType)){
				try {
					throw new SemanticError(call.getLine(),
							"In the call for the method '" 
							+ call.getName()
							+ "' arguments' TypeTableType must be sub TypeTableType of the method's formals TypeTableType");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}

		}
		return mType.getReturnType();

	}

	public Object visit(This thisExpression, SymbolTable table) {
		SymbolTable currScope = thisExpression.getScope();

		while((!(currScope.getType().compareTo(SymbolTableType.STATIC_METHOD) == 0)) && (!(currScope.getType().compareTo(SymbolTableType.VIRTUAL_METHOD) == 0))){
			currScope = currScope.getFather_table();
		}

		if(currScope.getType().compareTo(SymbolTableType.STATIC_METHOD) == 0){
			try {
				throw new SemanticError(thisExpression.getLine(),
						"'this' keyword can't be used in static methods");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		while(!(currScope.getType().compareTo(SymbolTableType.CLASS) == 0)){
			currScope = currScope.getFather_table();
		}

		String thisClassName = currScope.getId();
		//we return the ClassType of the class which the this expression refers to.
		ClassType thisClassType = TypeTable.classType(thisClassName);
		return thisClassType;

	}

	public Object visit(NewClass newClass, SymbolTable table) {
		//look for it's declaration in the global table
		if(((GlobalSymbolTable)table).findInnerChild(newClass.getName())==null){
			try {
				throw new SemanticError(newClass.getLine(),
						"Use of an undeclared class '"
						+ newClass.getName()
						+ "'");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
			return null;
		}
		return TypeTable.classType(newClass.getName());
	}

	public Object visit(NewArray newArray, SymbolTable table) {
		TypeTableType arraySizeType = (TypeTableType)newArray.getSize().accept(this, table);
		if(arraySizeType == null){ return null; }
		Type arrayElementType =  newArray.getType();
		Type newType = null;
		if(arrayElementType.getClass().equals(PrimitiveType.class)){
			newType = new PrimitiveType(-1, ((PrimitiveType)arrayElementType).getType());
			newType.setDimention(arrayElementType.getDimension() + 1);
		}
		if(arrayElementType.getClass().equals(UserType.class)){
			newType = new UserType(-1, ((UserType)arrayElementType).getName());
			newType.setDimention(arrayElementType.getDimension() + 1);
		}
		TypeTableType newArrayType = (TypeTableType)newType.accept(this, table);
		if(newArrayType == null){ return null; }

		if(arraySizeType.subType(TypeTable.integerType)){
			return newArrayType;
		}
		else{
			try {
				throw new SemanticError(newArray.getLine(),
				"Array size expression must be of int type");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}		
		}
		return null;
	}

	public Object visit(Length length, SymbolTable table) {
		TypeTableType exprType = (TypeTableType) length.getArray().accept(this, table);

		if(exprType == null) { return null; }

		if(exprType.toString().endsWith("[]")){
			return TypeTable.integerType;
		}
		else{
			try {
				throw new SemanticError(length.getLine(),
				"The length property must be called only for an array");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		return null;
	}

	public Object visit(MathBinaryOp binaryOp, SymbolTable table) {
		TypeTableType operandType1 = (TypeTableType)binaryOp.getFirstOperand().accept(this, table);
		TypeTableType operandType2 = (TypeTableType)binaryOp.getSecondOperand().accept(this, table);
		BinaryOps operator = (BinaryOps)binaryOp.getOperator();

		if(operandType1 == null || operandType2 == null){
			return null;
		}

		// If the operator is '+'
		else if(operator.compareTo(BinaryOps.PLUS) == 0){
			if(operandType1.subType(TypeTable.stringType) && operandType2.subType(TypeTable.stringType)){
				return TypeTable.stringType;
			}
			else if (operandType1.subType(TypeTable.integerType) && operandType2.subType(TypeTable.integerType)){
				return TypeTable.integerType;
			}
			else{
				try {
					throw new SemanticError(binaryOp.getLine(),
							"The Math binary operation '" 
							+ binaryOp.getOperator().getOperatorString() 
							+ "' must get 2 operands both of an int type or of a string type");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}

		// If the operator is one of {-,/,*,%}
		else{
			if(operandType1.subType(TypeTable.integerType) && operandType2.subType(TypeTable.integerType)){
				return TypeTable.integerType;
			}
			else{
				try {
					throw new SemanticError(binaryOp.getLine(),
							"The Math binary operation '" 
							+ binaryOp.getOperator().getOperatorString() 
							+ "' must get 2 operands both of an int type");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}

		return null;
	}

	public Object visit(LogicalBinaryOp binaryOp, SymbolTable table) {
		TypeTableType operandType1 = (TypeTableType)binaryOp.getFirstOperand().accept(this, table);
		TypeTableType operandType2 = (TypeTableType)binaryOp.getSecondOperand().accept(this, table);
		BinaryOps operator = (BinaryOps)binaryOp.getOperator();

		if(operandType1 == null || operandType2 == null){
			return null;
		}
		else if(operator.compareTo(BinaryOps.EQUAL) == 0 || operator.compareTo(BinaryOps.NEQUAL) == 0){
			//checks if == and !=  gets 2 operands with TypeTableType that are sub TypeTableType of each other
			if(operandType1.subType(operandType2) || operandType2.subType(operandType1)){
				return TypeTable.booleanType;
			}
			else{
				try {
					throw new SemanticError(binaryOp.getLine(),
							"The Logical binary operation '" 
							+ binaryOp.getOperator().getOperatorString() 
							+ "' must get 2 operands with TypeTableType that are sub TypeTableType of each other");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		//checks if && and || gets 2 operands of a boolean type
		else if(operator.compareTo(BinaryOps.LAND) == 0 || operator.compareTo(BinaryOps.LOR) == 0){
			if(operandType1.subType(TypeTable.booleanType) && operandType2.subType(TypeTable.booleanType)){
				return TypeTable.booleanType;
			}
			else{
				try {
					throw new SemanticError(binaryOp.getLine(),
							"The Logical binary operation '" 
							+ binaryOp.getOperator().getOperatorString() 
							+ "' must get 2 operands both of a boolean type");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		//checks if < , > , >= , <= gets 2 operands of an int type
		else if(operator.compareTo(BinaryOps.LT) == 0 || operator.compareTo(BinaryOps.LTE) == 0 || operator.compareTo(BinaryOps.GT) == 0 || operator.compareTo(BinaryOps.GTE) == 0){
			if(operandType1.subType(TypeTable.integerType) && operandType2.subType(TypeTable.integerType)){
				return TypeTable.booleanType;
			}
			else{
				try {
					throw new SemanticError(binaryOp.getLine(),
							"The Logical binary operation '" 
							+ binaryOp.getOperator().getOperatorString()
							+ "' must get 2 operands both of an int type");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}	
			}

		}

		return null;
	}

	public Object visit(MathUnaryOp unaryOp, SymbolTable table) {
		//if the operand of the math unaryOp is a literal, change it's value to minus it's value.
		if(unaryOp.getOperand().getClass().equals(Literal.class)){
			Literal literal = (Literal) unaryOp.getOperand();
			String literalType = literal.getType().getDescription();
			if(literalType.compareTo(LiteralTypes.INTEGER.getDescription()) == 0){
				String value = "-".concat((String)literal.getValue());
				literal.setValue(value);
			}
		}
		TypeTableType operandType = (TypeTableType)unaryOp.getOperand().accept(this, table);
		//checks if the Math unary operation gets 1 operand of an int type
		if(operandType.subType(TypeTable.integerType)){
			return TypeTable.integerType;
		}
		else{
			try {
				throw new SemanticError(unaryOp.getLine(),
						"The Math unary operation '" 
						+ unaryOp.getOperator().getOperatorString()
						+ "' must get 1 operand of an int type");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		return null;
	}

	public Object visit(LogicalUnaryOp unaryOp, SymbolTable table) {
		TypeTableType operandType = (TypeTableType)unaryOp.getOperand().accept(this, table);
		//checks if the Logical unary operation get 1 operand of a boolean type
		if(operandType.subType(TypeTable.booleanType)){
			return TypeTable.booleanType;
		}
		else{
			try {
				throw new SemanticError(unaryOp.getLine(),
						"The Logical unary operation " 
						+ unaryOp.getOperator().getOperatorString() 
						+ " must get 1 operand of a boolean type");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		return null;
	}

	public Object visit(Literal literal, SymbolTable table) {
		String literalType = literal.getType().getDescription();
		if(literalType.compareTo(LiteralTypes.INTEGER.getDescription()) == 0){
			//we check that the integer value is in the integer range
			if(((String)literal.getValue()).length()>11){
				try {
					throw new SemanticError(literal.getLine(),
							"the literal '"
							+ literal.getValue()
							+ "' of type int is out of boundaries");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
			else{
				long num = Long.valueOf((String)literal.getValue());
				if((num>(Math.pow(2,31))-1)||num<(-Math.pow(2,31))){
					try {
						throw new SemanticError(literal.getLine(),
								"the literal '"
								+ literal.getValue()
								+ "' of type int is out of boundaries");
					}
					catch (SemanticError e) {
						System.out.println(e.getErrorMessage());
						System.exit(-1);
					}
				}
				else{
					if(num<0){
						literal.setValue((int)(-num));
					}
					else{
						literal.setValue((int)(num));
					}
					return TypeTable.integerType;
				}
			}
		}
		else if(literalType.compareTo(LiteralTypes.STRING.getDescription()) == 0){
			return TypeTable.stringType;
		}
		else if(literalType.compareTo(LiteralTypes.FALSE.getDescription()) == 0){
			return TypeTable.booleanType;
		}
		else if(literalType.compareTo(LiteralTypes.TRUE.getDescription()) == 0){
			return TypeTable.booleanType;
		}
		else if(literalType.compareTo(LiteralTypes.NULL.getDescription()) == 0){
			return TypeTable.nullType;
		}

		return null;
	}
	public Object visit(ExpressionBlock expressionBlock, SymbolTable table) {
		return expressionBlock.getExpression().accept(this, table);
	}
}
