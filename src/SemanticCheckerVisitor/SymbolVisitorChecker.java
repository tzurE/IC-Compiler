package SemanticCheckerVisitor;

import java.util.LinkedList;
import java.util.List;

import IC.*;
import IC.AST.*;
import TypeTable.*;
import SymbolTables.*;

public class SymbolVisitorChecker implements PropVisitor {
	
	private GlobalSymbolTable globalTable;
	private int loopLevel = 0; //how unclosed while have we encountered so far
	private boolean insideStaticMethodScope = false; //if we are inside static method scope and therefore cannot use virtual calls without specifying variable location.

	//get the global table after the builder made it
	public SymbolVisitorChecker(GlobalSymbolTable globalTable) {
		this.globalTable = globalTable;
	}
	
	public Object visit(Program program, SymbolTable parent_table) {
		TypeTableType classType;	
		boolean isTypeCheckCorrect = true;
		for (ICClass icClass : program.getClasses()){
			classType = (TypeTableType) icClass.accept(this, globalTable);
			if(classType == null	){
				isTypeCheckCorrect = false;
				try {
					throw new SemanticError("semantic check of class "+ icClass.getName() + " failed");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		return isTypeCheckCorrect;
	}
	
	public SymbolEntry getFromSuperClasses(ICClass icClass, String ident) {
		SymbolEntry elem;
		ClassSymbolTable class_table = globalTable.findInnerChild(icClass.getSuperClassName());
		if(class_table == null)
			return null;
		elem = (SymbolEntry) class_table.getEntry(ident, SymbolKinds.FIELD);
		if( elem!= null)
			return elem;
		elem = (SymbolEntry) class_table.getEntry(ident, SymbolKinds.STATIC_METHOD);
		if( elem!= null)
			return elem;
		elem = (SymbolEntry) class_table.getEntry(ident, SymbolKinds.VIRTUAL_METHOD);
		if( elem!= null)
			return elem;
		return null;
	}

	public Object visit(ICClass icClass, SymbolTable parent_table) {
		ClassSymbolTable class_table = ((GlobalSymbolTable)parent_table).findInnerChild(icClass.getName());
		SymbolEntry superClassReference;
		for (Field field : icClass.getFields()){
			if(field.accept(this, class_table) == null){
				return null;
			}
			if( getFromSuperClasses(icClass, field.getName()) != null){
				try {
					throw new SemanticError(field.getLine(),
					"field " +
					field.getName() + 
					" is already defined in superclass");
				}
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}	
			}
		}
		for (Method method : icClass.getMethods()){
			if(method.accept(this, class_table) == null){
				return null;
			}
			superClassReference = getFromSuperClasses(icClass, method.getName()); 
			if(superClassReference != null){
				if(! sameMethodDefinition(method, superClassReference)){
					try {
						throw new SemanticError(method.getLine(),
						"new decleration of method " +
						method.getName() + 
						" which is already defined in superclass");
					}
					catch (SemanticError e) {
						System.out.println(e.getErrorMessage());
						System.exit(-1);
					}	
				}
			}
		}
		return TypeTable.classType(icClass.getName());
	}
	
	private static boolean sameMethodDefinition(Method method, SymbolEntry superClassReference) {
		ClassSymbolTable classScope = ((ClassSymbolTable) method.getScope());
		SymbolEntry methodEntry = (SymbolEntry) classScope.getIdOfAnythingInsideClass( method.getName() ); 
		TypeTableType type1, type2;
		if(superClassReference == null)
			return false;
		
		type1 = methodEntry.getType();
		type2 = superClassReference.getType();
		return (type1.getId() == type2.getId());
	}

	//dummy func
	public Object visit(Method method, SymbolTable c) {
		return null;
	}

	public Object visit(Field field, SymbolTable parent_table) {

		//if type is not primitive, update it in it's containing scope
		TypeTableType type= (TypeTableType) field.getType().accept(this, parent_table);
		if(type!=null){
			field.getScope().setTableTypeForVariable(field.getName(), type);
			return (SymbolEntry)parent_table.getEntry(field.getName(), SymbolKinds.FIELD);
		}
		//primitive type - get it from containing scope
		else{
			SymbolEntry fieldEntry = (SymbolEntry)parent_table.getEntry(field.getName(), SymbolKinds.FIELD);
			TypeTableType fieldType = (TypeTableType)fieldEntry.getType();
			if(fieldType == null){
				return null;
			}
			else{
				return fieldEntry;
			}
		}
	}

	public Object visit(VirtualMethod method, SymbolTable parent_table) {
		//semantic checks - checks if the return type and parameters type are legal
		Type methodType = method.getType();
		methodType.accept(this, parent_table);

		for (Formal formal : method.getFormals()){
			formal.accept(this, formal.getScope());
		}
		ClassSymbolTable containingClassTable = (ClassSymbolTable) method.getScope();
		SymbolEntry methodEntry =  (SymbolEntry) containingClassTable.searchTable(method.getName(), SymbolKinds.VIRTUAL_METHOD);
		//checks if the method is legal - i.e or overriding a method in super class,
		//or superclasses don't have an identifier with the same name as the method's name
		if(!containingClassTable.isIdExist(methodEntry)){
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
			if(stmt.accept(this, stmt.getScope()) == null){
				return null;
			}
		}

		return true;
	}

	public Object visit(StaticMethod method, SymbolTable parent_table) {
		insideStaticMethodScope = true;
		//semantic checks - checks if the return type and parameters type are legal 
		method.getType().accept(this, parent_table);
		for (Formal formal : method.getFormals())
			formal.accept(this, formal.getScope());
		ClassSymbolTable enclosing = (ClassSymbolTable) method.getScope();
		SymbolEntry methodEntry =  (SymbolEntry) enclosing.searchTable(method.getName(), SymbolKinds.STATIC_METHOD);
		//checks if the method is legal - i.e or overriding a method in super class,
		//or superclasses don't have an identifier with the same name as the method's name
		if(!enclosing.isIdExist(methodEntry)){
			try {
				throw new SemanticError(method.getLine(),
						"Overloading function '"
						+ method.getName()
						+ "' is not allowed in IC");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		for(Statement stmt : method.getStatements()){
			if(stmt.accept(this, stmt.getScope()	) == null){
				insideStaticMethodScope = false;
				return null;
			}
		}
		insideStaticMethodScope = false;
		return true;
	}

	public Object visit(LibraryMethod method, SymbolTable parent_table) {
		//semantic checks - checks if the return type and parameters type are legal 
		method.getType().accept(this, parent_table);
		for (Formal formal : method.getFormals())
			formal.accept(this, formal.getScope());


		for(Statement stmt : method.getStatements()){
			if(stmt.accept(this, stmt.getScope()) == null){
				return null;
			}
		}
		return true;
	}

	public Object visit(Formal formal, SymbolTable parent_table) {
		//if type is not primitive, update it in it's containing scope
		TypeTableType type= (TypeTableType) formal.getType().accept(this, parent_table);
		if(type!=null){
			formal.getScope().setTableTypeForVariable(formal.getName(), type);
			return (SymbolEntry)parent_table.getEntry(formal.getName(), SymbolKinds.PARAMETER);
		}
		//primitive type - get it from containing scope
		else{
			SymbolEntry fieldEntry = (SymbolEntry)parent_table.getEntry(formal.getName(), SymbolKinds.PARAMETER);
			TypeTableType fieldType = (TypeTableType)fieldEntry.getType();
			if(fieldType == null){
				return null;
			}
			else{
				return fieldEntry;
			}
		}
	}

	public Object visit(PrimitiveType type, SymbolTable parent_table) {
		return TypeTable.convertTypeToTypeTableType(type);
	}

	public Object visit(UserType type, SymbolTable parent_table) {

		//semantic checking - checks if the class we use is declared
		if(globalTable.findInnerChild(type.getName())==null){
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

	public Object visit(Assignment assignment, SymbolTable parent_table) {
		TypeTableType varType = (TypeTableType)assignment.getVariable().accept(this, parent_table);
		TypeTableType valueType = (TypeTableType)assignment.getAssignment().accept(this, parent_table);
		//checks if the assignment value's type is a sub type of the variable's type
		if(valueType.isExtendedFrom(varType)){
			return assignment;
		}
		else{
			try {
				throw new SemanticError(assignment.getLine(),
				"assignment value type and assigment location in lvalue's type mismatch - must be a sub type of the lvalue type");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}	
		}
		return null;
	}

	public Object visit(CallStatement callStatement, SymbolTable parent_table) {
		return callStatement.getCall().accept(this, parent_table);
	}

	public Object visit(Return returnStatement, SymbolTable parent_table) {
		TypeTableType returnStmtType;
		TypeTableType methodReturnType;
		if(!returnStatement.hasValue()){
			returnStmtType = (TypeTableType)TypeTable.voidType;
		}
		else{
			returnStmtType = (TypeTableType)returnStatement.getValue().accept(this, parent_table);
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
		if(!returnStmtType.isExtendedFrom(methodReturnType)){
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

	public Object visit(If ifStatement, SymbolTable parent_table) {
		TypeTableType condType = (TypeTableType)ifStatement.getCondition().accept(this, parent_table);
		if(condType == null) { return null;}
		if(ifStatement.getOperation().accept(this, parent_table) == null){ return null; }
		if(ifStatement.hasElse()){
			if(ifStatement.getElseOperation().accept(this, parent_table) == null){ return null; }
		}
		//checks if the If condition is from boolean type
		if(condType.isExtendedFrom(TypeTable.booleanType)){
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

	public Object visit(While whileStatement, SymbolTable parent_table) {
		//we enter to while, so we increment the while counter
		loopLevel++;
		TypeTableType condExprType = (TypeTableType)whileStatement.getCondition().accept(this, parent_table);
		//checks if the While condition is from boolean type
		if(!condExprType.isExtendedFrom(TypeTable.booleanType)){
			try {
				throw new SemanticError(whileStatement.getLine(),
				"While condition must be of boolean type");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}

		if(whileStatement.getOperation().accept(this, parent_table) == null){
			//error encountered - decrement loop level and return null
			loopLevel--;
			return null;
		}
		//while operation closed successfully. 
		loopLevel--;
		return true;
	}

	public Object visit(Break breakStatement, SymbolTable parent_table) {
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

	public Object visit(Continue continueStatement, SymbolTable parent_table) {
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

	public Object visit(StatementsBlock statementsBlock, SymbolTable parent_table) {
		for(Statement stms: statementsBlock.getStatements()){
			if(stms.accept(this, stms.getScope()) == null){
				return null;
			}
		}

		return true;
	}

	public Object visit(LocalVariable localVariable, SymbolTable parent_table) {
		//semantic checks - if the type is not primitive, we change the TypeTableType value in the symbolTable to the appropriate TypeTableType
		TypeTableType variableType = (TypeTableType)localVariable.getType().accept(this, parent_table);
		if(variableType != null){
			localVariable.getScope().setTableTypeForVariable(localVariable.getName(), variableType);
		}
		if(localVariable.hasInitValue()){
			TypeTableType initValueType = (TypeTableType)localVariable.getInitValue().accept(this, parent_table);
			if(initValueType != null){
				//checks if the initialization value's type is a sub type of the variable's type
				if(! initValueType.isExtendedFrom(variableType)){
					try {
						throw new SemanticError(localVariable.getLine(),
								"Initialization of local variable '"
								+ localVariable.getName()
								+"' failed, initialization value's type isn't a sub type of the variable's type");
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

	public Object visit(VariableLocation location, SymbolTable parent_table) {
		if(location.isExternal()){
			//Gets class (User Type)
			TypeTableType locationType = (TypeTableType)location.getLocation().accept(this, parent_table);
			if(locationType == null) { return null; }

			//Gets class  by it's name from location
			String locationTypeName = locationType.getName();
			//get the class table, which the variable located in
			ClassSymbolTable classTable = globalTable.findInnerChild(locationTypeName);
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
			SymbolEntry variableSymbol = null;
			if(insideStaticMethodScope){
				// search only in scopes inside the method the method scope
				do{
				variableSymbol = (SymbolEntry) enclosingTable.searchTable(location.getName(), SymbolKinds.LOCAL_VARIABLE);
				if(variableSymbol == null)
					variableSymbol = (SymbolEntry) enclosingTable.searchTable(location.getName(), SymbolKinds.PARAMETER);
				if(variableSymbol == null)
					enclosingTable = enclosingTable.getFather_table();
				} while(variableSymbol ==null && enclosingTable != null && enclosingTable.getClass() !=  ClassSymbolTable.class);
			}
			else
				variableSymbol = enclosingTable.searchForVar(location.getName(),location.getLine());
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

	public Object visit(ArrayLocation location, SymbolTable parent_table) {
		TypeTableType indexType = (TypeTableType)location.getIndex().accept(this, parent_table);
		TypeTableType arrGetType = (TypeTableType)location.getArray().accept(this, parent_table);
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

		if(TypeTable.integerType.isExtendedFrom(indexType)){
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
	
	public Object visit(StaticCall call, SymbolTable parent_table) {
		//get the class table, which the static method located in
		ClassSymbolTable classScope = globalTable.findInnerChild(call.getClassName());
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
		MethodSymbolTable methodscope = null;
		if(formalsNum>0)
			methodscope = (MethodSymbolTable) formalList.get(0).getScope();
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
			TypeTableType argType = (TypeTableType)actualParam.accept(this, parent_table);
			ParameterEntry formalEntry = (ParameterEntry)formalList.get(formalCounter).accept(this, methodscope);
			TypeTableType formalType = formalEntry.getType();
			formalCounter++;

			if( argType.isExtendedFrom(formalType) ){
				return mType.getReturnType();
			}
			else{
				try {
					throw new SemanticError(call.getLine(),
							"calling the static method '" 
							+ call.getName()
							+ "' faild. arguments' type must be subType of the method's formals' Types");
				} 
				catch (SemanticError e) {
					System.out.println(e.getErrorMessage());
					System.exit(-1);
				}
			}
		}
		return mType.getReturnType();
	}

	public Object visit(VirtualCall call, SymbolTable parent_table) {
		List<Formal> formalList;
		String methodName = call.getName(); 
		MethodType mType;
		if(call.isExternal()){
			TypeTableType callLocationType = (TypeTableType)call.getLocation().accept(this, parent_table);
			if(callLocationType == null){ return null; }

			//Gets class  by it's name from location
			String callLocationTypeName = callLocationType.getName();
			//get the class table, which the virtual method located in
			ClassSymbolTable classTable = globalTable.findInnerChild(callLocationTypeName);
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
			formalList = mType.getmethod().getFormals();
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
			if(insideStaticMethodScope && virtualFound){
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
			formalList = mType.getmethod().getFormals();
		}

		int formalsNum = formalList.size();
		MethodSymbolTable methodScope = null;
		if(formalsNum>0)
			methodScope = (MethodSymbolTable) formalList.get(0).getScope();
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
			TypeTableType argType = (TypeTableType)actualParam.accept(this, parent_table);
			ParameterEntry formalEntry = (ParameterEntry)formalList.get(formalCounter).accept(this, methodScope);
			TypeTableType formalType = formalEntry.getType();
			formalCounter++;

			if(!argType.isExtendedFrom(formalType)){
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

	public Object visit(This thisExpression, SymbolTable parent_table) {
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

	public Object visit(NewClass newClass, SymbolTable parent_table) {
		//look for it's declaration in the global table
		if(globalTable.findInnerChild(newClass.getName())==null){
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

	public Object visit(NewArray newArray, SymbolTable parent_table) {
		TypeTableType arraySizeType = (TypeTableType)newArray.getSize().accept(this, parent_table);
		if(arraySizeType == null){ return null; }
		Type arrayElementType =  newArray.getType();
		Type newType = null;
		if(arrayElementType.getClass().equals(PrimitiveType.class)){
			newType = new PrimitiveType(-1, ((PrimitiveType)arrayElementType).getType());
		}
		if(arrayElementType.getClass().equals(UserType.class)){
			newType = new UserType(-1, ((UserType)arrayElementType).getName());
		}
		newType.setDimention(arrayElementType.getDimension() + 1);
		//put in containing table
		TypeTableType newArrayType = (TypeTableType)newType.accept(this, parent_table);
		if(newArrayType == null){ return null; }

		if(arraySizeType.isExtendedFrom(TypeTable.integerType)){
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

	public Object visit(Length length, SymbolTable parent_table) {
		TypeTableType exprType = (TypeTableType) length.getArray().accept(this, parent_table);

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

	public Object visit(MathBinaryOp binaryOp, SymbolTable parent_table) {
		TypeTableType operandType1 = (TypeTableType)binaryOp.getFirstOperand().accept(this, parent_table);
		TypeTableType operandType2 = (TypeTableType)binaryOp.getSecondOperand().accept(this, parent_table);
		BinaryOps operator = (BinaryOps)binaryOp.getOperator();

		if(operandType1 == null || operandType2 == null){
			return null;
		}

		// If the operator is '+'
		else if(operator.compareTo(BinaryOps.PLUS) == 0){
			if(operandType1.isExtendedFrom(TypeTable.stringType) && operandType2.isExtendedFrom(TypeTable.stringType)){
				return TypeTable.stringType;
			}
			else if (operandType1.isExtendedFrom(TypeTable.integerType) && operandType2.isExtendedFrom(TypeTable.integerType)){
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
			if(operandType1.isExtendedFrom(TypeTable.integerType) && operandType2.isExtendedFrom(TypeTable.integerType)){
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
	
	public Object visit(LogicalBinaryOp binaryOp, SymbolTable parent_table) {
		
		TypeTableType operandType1 = (TypeTableType)binaryOp.getFirstOperand().accept(this, parent_table);
		TypeTableType operandType2 = (TypeTableType)binaryOp.getSecondOperand().accept(this, parent_table);
		//if(operandType1.getClass().equals(ClassType))
		//	System.out.println();
		BinaryOps operator = (BinaryOps)binaryOp.getOperator();
		

		if(operandType1 == null || operandType2 == null){
			return null;
		}
		else if(operator.compareTo(BinaryOps.EQUAL) == 0 || operator.compareTo(BinaryOps.NEQUAL) == 0){
			//checks if == and !=  gets 2 operands with TypeTableType that are sub TypeTableType of each other
			if(operandType1.isExtendedFrom(operandType2) || operandType2.isExtendedFrom(operandType1)){
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
			if(operandType1.isExtendedFrom(TypeTable.booleanType) && operandType2.isExtendedFrom(TypeTable.booleanType)){
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
			if(operandType1.isExtendedFrom(TypeTable.integerType) && operandType2.isExtendedFrom(TypeTable.integerType)){
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

	public Object visit(MathUnaryOp unaryOp, SymbolTable parent_table) {
		//if the operand of the math unaryOp is a literal, change it's value to minus it's value.
		if(unaryOp.getOperand().getClass().equals(Literal.class)){
			Literal literal = (Literal) unaryOp.getOperand();
			String literalType = literal.getType().getDescription();
			if(literalType.compareTo(LiteralTypes.INTEGER.getDescription()) == 0){
				String value = "-".concat((String)literal.getValue());
				literal.setValue(value);
			}
		}
		TypeTableType operandType = (TypeTableType)unaryOp.getOperand().accept(this, parent_table);
		//checks if the Math unary operation gets 1 operand of an int type
		if(operandType.isExtendedFrom(TypeTable.integerType)){
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

	public Object visit(LogicalUnaryOp unaryOp, SymbolTable parent_table) {
		TypeTableType operandType = (TypeTableType)unaryOp.getOperand().accept(this, parent_table);
		//checks if the Logical unary operation get 1 operand of a boolean type
		if(operandType.isExtendedFrom(TypeTable.booleanType)){
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

	public Object visit(Literal literal, SymbolTable parent_table) {
		String literalType = literal.getType().getDescription();
		if(literalType.compareTo(LiteralTypes.INTEGER.getDescription()) == 0){
			//we check that the integer value is in the integer range
			if((literal.getValue().toString()).length()>11){
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
				long num = Long.valueOf(literal.getValue().toString());
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
	public Object visit(ExpressionBlock expressionBlock, SymbolTable parent_table) {
		return expressionBlock.getExpression().accept(this, parent_table);
	}
	
}


