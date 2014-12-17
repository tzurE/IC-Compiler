package TypeTable;

import java.util.LinkedList;
import java.util.List;

import IC.*;
import IC.AST.*;

public class SemanticCheckVisitor implements Visitor{

	public Object visit(Program program) {
		TypeTableType classType;
		for (ICClass icClass : program.getClasses()){
			classType = (TypeTableType) icClass.accept(this);
			if(classType == null){
				return null;
			}
		}
		return program;
	}

	public Object visit(ICClass icClass) {
		for (Field field : icClass.getFields()){
			if(field.accept(this) == null){
				return null;
			}
		}
		for (Method method : icClass.getMethods()){
			if(method.accept(this) == null){
				return null;
			}
		}
		return icClass;
	}

	public Object visit(Field field) {
		//semantic checks - if the type is not primitive, we change the Types value in the symbolTable to the appropriate Types
		Types type= (Types) field.getType().accept(this);
		if(type!=null){
			field.getEnclosingScope().setVeriableTypes(field.getName(), type);
		}


		SymbolEntry fieldSymbol = (SymbolEntry)field.getEnclosingScope().getEntityByName(field.getName(), SymbolKinds.FIELD);
		Types fieldType = (Types)fieldSymbol.getType();

		if(fieldType == null){
			return null;
		}
		else{
			return fieldType;
		}
	}

	public Object visit(VirtualMethod method) {
		//semantic checks - checks if the return type and parameters type are legal 
		method.getType().accept(this);

		for (Formal formal : method.getFormals()){
			formal.accept(this);
		}
		ClassSymbolTable enclosing = (ClassSymbolTable) method.getEnclosingScope();
		SymbolEntry methodEntry =  (SymbolEntry) enclosing.searchInCurrentTable(method.getName(), SymbolKinds.VIRTUAL_METHOD);
		//checks if the method is legal - i.e or overriding a method in super class,
		//or superclasses don't have an identifier with the same name as the method's name
		if(!enclosing.isLegalMethod(methodEntry)){
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
			if(stmt.accept(this) == null){
				return null;
			}
		}

		return true;
	}

	public Object visit(StaticMethod method) {
		inStaticMethod = true;
		//semantic checks - checks if the return type and parameters type are legal 
		method.getType().accept(this);
		for (Formal formal : method.getFormals())
			formal.accept(this);
		ClassSymbolTable enclosing = (ClassSymbolTable) method.getEnclosingScope();
		SymbolEntry methodEntry =  (SymbolEntry) enclosing.searchInCurrentTable(method.getName(), SymbolKinds.STATIC_METHOD);
		//checks if the method is legal - i.e or overriding a method in super class,
		//or superclasses don't have an identifier with the same name as the method's name
		if(!enclosing.isLegalMethod(methodEntry)){
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
			if(stmt.accept(this) == null){
				inStaticMethod = false;
				return null;
			}
		}
		inStaticMethod = false;
		return true;
	}

	public Object visit(LibraryMethod method) {
		//semantic checks - checks if the return type and parameters type are legal 
		method.getType().accept(this);
		for (Formal formal : method.getFormals())
			formal.accept(this);


		for(Statement stmt : method.getStatements()){
			if(stmt.accept(this) == null){
				return null;
			}
		}
		return true;
	}

	public Object visit(Formal formal) {
		Types type= (Types) formal.getType().accept(this);
		if(type!=null){
			formal.getEnclosingScope().setVeriableTypes(formal.getName(), type);
		}
		SymbolEntry formalSymbol = (SymbolEntry)formal.getEnclosingScope().getEntityByName(formal.getName(), SymbolKinds.PARAMETER);
		Types formalType = (Types)formalSymbol.getType();
		if(formalType == null){
			return null;
		}
		else{
			return formalType;
		}
	}

	public Object visit(PrimitiveType type) {
		return TypeTable.convertTypeToTypes(type);
	}

	public Object visit(UserType type) {

		//semantic checking - checks if the class we use is declared
		if(searchForClassTableByName(type.getName())==null){
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
			return TypeTable.convertTypeToTypes(type);
		}
	}

	public Object visit(Assignment assignment) {
		Types varType = (Types)assignment.getVariable().accept(this);
		Types valueType = (Types)assignment.getAssignment().accept(this);
		//checks if the assignment value's type is a sub type of the variable's type
		if(valueType.subtypeof(varType)){
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

	public Object visit(CallStatement callStatement) {
		return callStatement.getCall().accept(this);
	}

	public Object visit(Return returnStatement) {
		Types returnStmtType;
		Types methodReturnType;
		if(!returnStatement.hasValue()){
			returnStmtType = (Types)TypeTable.voidType;
		}
		else{
			returnStmtType = (Types)returnStatement.getValue().accept(this);
		}

		// Gets the method symbol table which contains the return statement
		SymbolTable returnStmtScope = (SymbolTable)returnStatement.getEnclosingScope(); 
		while(!(returnStmtScope.getTableKind().compareTo(TableKinds.VIRTUAL_METHOD) == 0) && !(returnStmtScope.getTableKind().compareTo(TableKinds.STATIC_METHOD) == 0)){
			returnStmtScope = returnStmtScope.getParent();
		}

		// Gets the method's name and kind
		String methodName = returnStmtScope.getTableName();
		TableKinds methodTableKind = returnStmtScope.getTableKind();
		SymbolKinds methodSymbolKind;
		if(methodTableKind.compareTo(TableKinds.STATIC_METHOD) == 0) { methodSymbolKind = SymbolKinds.STATIC_METHOD; }
		else { methodSymbolKind = SymbolKinds.VIRTUAL_METHOD; }

		// Gets the class symbol table which contains the method symbol as an entry
		SymbolTable classScope = returnStmtScope.getParent();

		// Gets the method symbol
		MethodSymbol mSymbol = (MethodSymbol)classScope.getEntityByName(methodName, methodSymbolKind);

		// Gets the method return type
		MethodType mType = (MethodType)mSymbol.getType();
		methodReturnType = mType.getReturnType();
		//checks if the expression in the return statement is from the return type of the method
		if(!returnStmtType.subtypeof(methodReturnType)){
			try {
				throw new SemanticError(returnStatement.getLine(),
						"the type of the returned value in method '"
						+ mType.getMethod().getName()
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

	public Object visit(If ifStatement) {
		Types condType = (Types)ifStatement.getCondition().accept(this);
		if(condType == null) { return null;}
		if(ifStatement.getOperation().accept(this) == null){ return null; }
		if(ifStatement.hasElse()){
			if(ifStatement.getElseOperation().accept(this) == null){ return null; }
		}
		//checks if the If condition is from boolean type
		if(condType.subtypeof(TypeTable.boolType)){
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

	public Object visit(While whileStatement) {
		//we enter to while, so we increment the while counter
		whileCounter++;
		Types condExprType = (Types)whileStatement.getCondition().accept(this);
		//checks if the While condition is from boolean type
		if(!condExprType.subtypeof(TypeTable.boolType)){
			try {
				throw new SemanticError(whileStatement.getLine(),
				"While condition must be of boolean type");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		if(whileStatement.getOperation().accept(this) == null){
			//we exit to while, so we decrement the while counter
			whileCounter--;
			return null;
		}
		//we exit to while, so we decrement the while counter
		whileCounter--;
		return true;
	}

	public Object visit(Break breakStatement) {
		//checks if the break statement is inside a while. if not - we throw a semantic error
		if(whileCounter==0){
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

	public Object visit(Continue continueStatement) {
		//checks if the continue statement is inside a while. if not - we throw a semantic error
		if(whileCounter==0){
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

	public Object visit(StatementsBlock statementsBlock) {
		for(Statement stms: statementsBlock.getStatements()){
			if(stms.accept(this) == null){
				return null;
			}
		}

		return true;
	}

	public Object visit(LocalVariable localVariable) {
		//semantic checks - if the type is not primitive, we change the Types value in the symbolTable to the appropriate Types
		Types varType = (Types)localVariable.getType().accept(this);
		if(varType!=null){
			localVariable.getEnclosingScope().setVeriableTypes(localVariable.getName(), varType);
		}

		if(localVariable.hasInitValue()){
			Types initValType = (Types)localVariable.getInitValue().accept(this);


			if(initValType != null){
				//checks if the initialization value's type is a sub type of the variable's type
				if(!initValType.subtypeof(varType)){
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

	public Object visit(VariableLocation location) {
		if(location.isExternal()){
			//Gets class (User Type)
			Types locationType = (Types)location.getLocation().accept(this);
			if(locationType == null) { return null; }

			//Gets class  by it's name from location
			String locationTypeName = locationType.getName();
			//get the class table, which the variable located in
			ClassSymbolTable classTable = searchForClassTableByName(locationTypeName);
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
			SymbolEntry fEntry = (SymbolEntry)classTable.getEntityByName(location.getName(), SymbolKinds.FIELD);
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
			Types fieldType = (Types)fEntry.getType();
			return fieldType;
		}
		else{
			SymbolTable enclosingTable = location.getEnclosingScope();
			SymbolEntry variableSymbol = enclosingTable.searchForNonExternalVariableIdentifier(location.getName(),location.getLine());
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

			Types fieldType = (Types)variableSymbol.getType();
			return fieldType;
		}

	}

	public Object visit(ArrayLocation location) {
		Types indexType = (Types)location.getIndex().accept(this);
		Types arrGetType = (Types)location.getArray().accept(this);
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

		if(indexType.subtypeof(TypeTable.intType)){
			return TypeTable.convertTypeToTypes(arrayType.getElemType());
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

	public Object visit(StaticCall call) {
		//get the class table, which the static method located in
		ClassSymbolTable classScope = searchForClassTableByName(call.getClassName());
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
		MethodSymbol mSymbol = (MethodSymbol)classScope.getEntityByName(methodName, SymbolKinds.STATIC_METHOD);
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
		List<Formal> formalList = mType.getMethod().getFormals();
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
			Types argType = (Types)actualParam.accept(this);
			Types formalType = (Types)formalList.get(formalCounter).accept(this);
			formalCounter++;

			if(argType.subtypeof(formalType)){
				return mType.getReturnType();
			}
			else{
				try {
					throw new SemanticError(call.getLine(),
							"In the call for the method '" 
							+ call.getName()
							+ "' arguments' types must be sub types of the method's formals types");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		return mType.getReturnType();
	}

	public Object visit(VirtualCall call) {
		List<Formal> mFormals;
		String methodName = call.getName(); 
		MethodType mType;
		if(call.isExternal()){
			Types callLocationType = (Types)call.getLocation().accept(this);
			if(callLocationType == null){ return null; }

			//Gets class  by it's name from location
			String callLocationTypeName = callLocationType.getName();
			//get the class table, which the virtual method located in
			ClassSymbolTable classTable = searchForClassTableByName(callLocationTypeName);
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
			//Gets method formal parameters types
			SymbolEntry mEntry = (SymbolEntry)classTable.getEntityByName(methodName, SymbolKinds.VIRTUAL_METHOD);
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
			mFormals = mType.getMethod().getFormals();
		}
		else{
			SymbolTable currentScope = call.getEnclosingScope();
			while(!(currentScope.getTableKind().compareTo(TableKinds.CLASS) == 0)){
				currentScope = currentScope.getParent();
			}
			boolean virtualFound = false;
			SymbolEntry mEntry = (SymbolEntry)currentScope.getEntityByName(methodName, SymbolKinds.VIRTUAL_METHOD);
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
				mEntry = (SymbolEntry)currentScope.getEntityByName(methodName, SymbolKinds.STATIC_METHOD);
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
			mFormals = mType.getMethod().getFormals();
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
			Types argType = (Types)actualParam.accept(this);
			Types formalType = (Types)mFormals.get(formalCounter).accept(this);
			formalCounter++;

			if(!argType.subtypeof(formalType)){
				try {
					throw new SemanticError(call.getLine(),
							"In the call for the method '" 
							+ call.getName()
							+ "' arguments' types must be sub types of the method's formals types");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}

		}
		return mType.getReturnType();

	}

	public Object visit(This thisExpression) {
		SymbolTable currScope = thisExpression.getEnclosingScope();

		while(!(currScope.getTableKind().compareTo(TableKinds.VIRTUAL_METHOD) == 0) && !(currScope.getTableKind().compareTo(TableKinds.STATIC_METHOD) == 0)){
			currScope = currScope.getParent();
		}

		if(currScope.getTableKind().compareTo(TableKinds.STATIC_METHOD) == 0){
			try {
				throw new SemanticError(thisExpression.getLine(),
						"'this' keyword can't be used in static methods");
			} 
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		while(!(currScope.getTableKind().compareTo(TableKinds.CLASS) == 0)){
			currScope = currScope.getParent();
		}

		String thisClassName = currScope.getTableName();
		//we return the ClassType of the class which the this expression refers to.
		ClassType thisClassType = TypeTable.classType(thisClassName);
		return thisClassType;

	}

	public Object visit(NewClass newClass) {
		if(searchForClassTableByName(newClass.getName())==null){
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

	public Object visit(NewArray newArray) {
		Types arraySizeType = (Types)newArray.getSize().accept(this);
		if(arraySizeType == null){ return null; }
		TypeTableType arrayElementType =  newArray.getType();
		TypeTableType newType = null;
		if(arrayElementType.getClass().equals(PrimitiveType.class)){
			newType = new PrimitiveType(-1, ((PrimitiveType)arrayElementType).getDataType());
			newType.setDimention(arrayElementType.getDimension() + 1);
		}
		if(arrayElementType.getClass().equals(UserType.class)){
			newType = new UserType(-1, ((UserType)arrayElementType).getName());
			newType.setDimention(arrayElementType.getDimension() + 1);
		}
		Types newArrayType = (Types)newType.accept(this);
		if(newArrayType == null){ return null; }

		if(arraySizeType.subtypeof(TypeTable.intType)){
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

	public Object visit(Length length) {
		Types exprType = (Types) length.getArray().accept(this);

		if(exprType == null) { return null; }

		if(exprType.toString().endsWith("[]")){
			return TypeTable.intType;
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

	public Object visit(MathBinaryOp binaryOp) {
		Types operandType1 = (Types)binaryOp.getFirstOperand().accept(this);
		Types operandType2 = (Types)binaryOp.getSecondOperand().accept(this);
		BinaryOps operator = (BinaryOps)binaryOp.getOperator();

		if(operandType1 == null || operandType2 == null){
			return null;
		}

		// If the operator is '+'
		else if(operator.compareTo(BinaryOps.PLUS) == 0){
			if(operandType1.subtypeof(TypeTable.stringType) && operandType2.subtypeof(TypeTable.stringType)){
				return TypeTable.stringType;
			}
			else if (operandType1.subtypeof(TypeTable.intType) && operandType2.subtypeof(TypeTable.intType)){
				return TypeTable.intType;
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
			if(operandType1.subtypeof(TypeTable.intType) && operandType2.subtypeof(TypeTable.intType)){
				return TypeTable.intType;
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

	public Object visit(LogicalBinaryOp binaryOp) {
		Types operandType1 = (Types)binaryOp.getFirstOperand().accept(this);
		Types operandType2 = (Types)binaryOp.getSecondOperand().accept(this);
		BinaryOps operator = (BinaryOps)binaryOp.getOperator();

		if(operandType1 == null || operandType2 == null){
			return null;
		}
		else if(operator.compareTo(BinaryOps.EQUAL) == 0 || operator.compareTo(BinaryOps.NEQUAL) == 0){
			//checks if == and !=  gets 2 operands with types that are sub types of each other
			if(operandType1.subtypeof(operandType2) || operandType2.subtypeof(operandType1)){
				return TypeTable.boolType;
			}
			else{
				try {
					throw new SemanticError(binaryOp.getLine(),
							"The Logical binary operation '" 
							+ binaryOp.getOperator().getOperatorString() 
							+ "' must get 2 operands with types that are sub types of each other");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		//checks if && and || gets 2 operands of a boolean type
		else if(operator.compareTo(BinaryOps.LAND) == 0 || operator.compareTo(BinaryOps.LOR) == 0){
			if(operandType1.subtypeof(TypeTable.boolType) && operandType2.subtypeof(TypeTable.boolType)){
				return TypeTable.boolType;
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
			if(operandType1.subtypeof(TypeTable.intType) && operandType2.subtypeof(TypeTable.intType)){
				return TypeTable.boolType;
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

	public Object visit(MathUnaryOp unaryOp) {
		//if the operand of the math unaryOp is a literal, change it's value to minus it's value.
		if(unaryOp.getOperand().getClass().equals(Literal.class)){
			Literal literal = (Literal) unaryOp.getOperand();
			if(literal.getType().getValue().equals(DataTypes.INT.getDefaultValue())){
				String value = "-".concat((String)literal.getValue());
				literal.setValue(value);
			}
		}
		Types operandType = (Types)unaryOp.getOperand().accept(this);
		//checks if the Math unary operation gets 1 operand of an int type
		if(operandType.subtypeof(TypeTable.intType)){
			return TypeTable.intType;
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

	public Object visit(LogicalUnaryOp unaryOp) {
		Types operandType = (Types)unaryOp.getOperand().accept(this);
		//checks if the Logical unary operation get 1 operand of a boolean type
		if(operandType.subtypeof(TypeTable.boolType)){
			return TypeTable.boolType;
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

	public Object visit(Literal literal) {
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
					return TypeTable.intType;
				}
			}
		}
		else if(literalType.compareTo(LiteralTypes.STRING.getDescription()) == 0){
			return TypeTable.stringType;
		}
		else if(literalType.compareTo(LiteralTypes.FALSE.getDescription()) == 0){
			return TypeTable.boolType;
		}
		else if(literalType.compareTo(LiteralTypes.TRUE.getDescription()) == 0){
			return TypeTable.boolType;
		}
		else if(literalType.compareTo(LiteralTypes.NULL.getDescription()) == 0){
			return TypeTable.nullType;
		}

		return null;
	}
	public Object visit(ExpressionBlock expressionBlock) {
		return expressionBlock.getExpression().accept(this);
	}
}
