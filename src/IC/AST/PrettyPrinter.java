package IC.AST;
import SymbolTables.*;
import IC.*;
import TypeTable.*;
//this is not the original pretty printer - it is an edited version for the Symbol tables//
/**
 * Pretty printing visitor - travels along the AST and prints info about each
 * node, in an easy-to-comprehend format.
 * 
 * @author Tovi Almozlino 
 */
public class PrettyPrinter implements Visitor {

	private int depth = 0; // depth of indentation

	private String ICFilePath;

	/**
	 * Constructs a new pretty printer visitor.
	 * 
	 * @param ICFilePath
	 *            The path + name of the IC file being compiled.
	 */
	public PrettyPrinter(String ICFilePath) {
		this.ICFilePath = ICFilePath;
	}

	private void indent(StringBuffer output, ASTNode node) {
		output.append("\n");
		for (int i = 0; i < depth; ++i)
			output.append("    ");
		if (node != null)
			output.append(node.getLine() + ": ");
	}

	private void indent(StringBuffer output) {
		indent(output, null);
	}

	public Object visit(Program program) {
		StringBuffer output = new StringBuffer();

		indent(output);
		output.append("Abstract Syntax Tree: " + ICFilePath + "\n");
		for (ICClass icClass : program.getClasses())
			output.append(icClass.accept(this));
		return output.toString();
	}

	public Object visit(ICClass icClass) {
		StringBuffer output = new StringBuffer();
		SymbolTable classScope = icClass.getScope();
		String classType;
		
		indent(output, icClass);
		String	nameScope;
		if(classScope.getClass().toString().equals(GlobalSymbolTable.class.toString()))
			nameScope = "Global";
		else{
			nameScope = classScope.getId();
		}
		
		output.append("Declaration of class: " + icClass.getName());
		if (icClass.hasSuperClass()){
			classType = TypeTable.getTypeNameByString(icClass.getName());
			classType = classType.substring(0, classType.indexOf(","));
			output.append(", subclass of " + icClass.getSuperClassName() + ", Type:" + classType + 
					", Symbol table: " + icClass.getSuperClassName());
		}
		else{
			output.append(", Type:" + TypeTable.getTypeNameByString(icClass.getName()) + ", Symbol table: Global");
		}
		
		depth++;
		
		for (Field field : icClass.getFields())
			output.append(field.accept(this));
		for (Method method : icClass.getMethods())
			output.append(method.accept(this));
		
		depth--;
		return output.toString();
	}

	public Object visit(PrimitiveType type) {
		StringBuffer output = new StringBuffer();
		String dim = "";
		
		//indent(output, type);
		output.append("Type: ");

		for(int i = 0; i < type.getDimension(); i++){
			dim = dim + "[]";
		}
		output.append(type.getName() + dim);
		return output.toString();
	}

	public Object visit(UserType type) {
		StringBuffer output = new StringBuffer();
		String dim = "";
		
		//indent(output, type);
		output.append("Type: ");
		
		for(int i = 0; i < type.getDimension(); i++){
			dim = dim + "[]";
		}
		
		output.append(type.getName() + dim);
		return output.toString();
	}

	public Object visit(Field field) {
		StringBuffer output = new StringBuffer();
		SymbolTable FieldScope = field.getScope();
		
		indent(output, field);
		output.append("Declaration of field: " + field.getName());
		output.append(", " + field.getType().accept(this));
		output.append(", Symbol table: " + FieldScope.getId());
		return output.toString();
	}

	public Object visit(LibraryMethod method) {
		StringBuffer output = new StringBuffer();
		SymbolTable MethodScope = method.getScope();
		String s;//debug
		
		indent(output, method);
		output.append("Declaration of library method: " + method.getName());
		depth++;
		output.append(", Type: " + TypeTable.getTypeNameByString(method.getName()) +
				", Symbol table: " + MethodScope.getId());
		for (Formal formal : method.getFormals())
			output.append(formal.accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(Formal formal) {
		StringBuffer output = new StringBuffer();
		SymbolTable formalScope = formal.getScope();
		
		indent(output, formal);
		output.append("Parameter: " + formal.getName());
		output.append(", " + formal.getType().accept(this));
		output.append(", Symbol table: " + formalScope.getId());
		
		return output.toString();
	}

	public Object visit(VirtualMethod method) {
		StringBuffer output = new StringBuffer();
		SymbolTable virtualMethodScope = method.getScope();
		
		indent(output, method);
		output.append("Declaration of virtual method: " + method.getName());
		depth++;
		
		output.append(", Type: " + TypeTable.getTypeNameByString(method.getName()) +
				", Symbol table: " + virtualMethodScope.getId());
		for (Formal formal : method.getFormals())
			output.append(formal.accept(this));
		for (Statement statement : method.getStatements())
			output.append(statement.accept(this));
		
		depth--;
		return output.toString();
	}

	public Object visit(StaticMethod method) {
		StringBuffer output = new StringBuffer();
		SymbolTable staticMethodScope = method.getScope();
		
		indent(output, method);
		output.append("Declaration of static method: " + method.getName());
		output.append(", Type: " + TypeTable.getTypeNameByString(method.getName()) +
				", Symbol table: " + staticMethodScope.getId());
		depth++;
		for (Formal formal : method.getFormals())
			output.append(formal.accept(this));
		for (Statement statement : method.getStatements())
			output.append(statement.accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(Assignment assignment) {
		StringBuffer output = new StringBuffer();
		SymbolTable assignmentScope = assignment.getScope();
		
		indent(output, assignment);
		output.append("Assignment statement, Symbol table: " + assignmentScope.getId());
		
		depth++;
		output.append(assignment.getVariable().accept(this));
		output.append(assignment.getAssignment().accept(this));
		//+ ", Symbol table: " + assignmentScope.getId());
		depth--;
		return output.toString();
	}

	public Object visit(CallStatement callStatement) {
		StringBuffer output = new StringBuffer();
		SymbolTable callStatementScope = callStatement.getScope();
		
		indent(output, callStatement);
		output.append("Method call statement, Symbol table: " + callStatementScope.getId());
		depth++;
		output.append(callStatement.getCall().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(Return returnStatement) {
		StringBuffer output = new StringBuffer();
		SymbolTable returnScope = returnStatement.getScope();
		
		indent(output, returnStatement);
		output.append("Return statement");
		if (returnStatement.hasValue())
			output.append(", with return value");
		output.append(", Symbol table: " + returnScope.getId());
		
		if (returnStatement.hasValue()) {
			depth++;
			output.append(returnStatement.getValue().accept(this));
			depth--;
		}
		return output.toString();
	}

	public Object visit(If ifStatement) {
		StringBuffer output = new StringBuffer();
		SymbolTable ifStatementScope = ifStatement.getScope();
		
		indent(output, ifStatement);
		output.append("If statement");
		if (ifStatement.hasElse())
			output.append(", with Else operation");
		output.append(", Symbol table: " + ifStatementScope.getId());
		
		depth++;
		output.append(ifStatement.getCondition().accept(this));
		output.append(ifStatement.getOperation().accept(this));
		if (ifStatement.hasElse())
			output.append(ifStatement.getElseOperation().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(While whileStatement) {
		StringBuffer output = new StringBuffer();
		SymbolTable whileStatementScope = whileStatement.getScope();
		
		indent(output, whileStatement);
		output.append("While statement, Symbol table: " + whileStatementScope.getId());
		depth++;
		output.append(whileStatement.getCondition().accept(this));
		output.append(whileStatement.getOperation().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(Break breakStatement) {
		StringBuffer output = new StringBuffer();
		SymbolTable breakStatementScope = breakStatement.getScope();
		
		indent(output, breakStatement);
		output.append("Break statement, Symbol table: " + breakStatementScope.getId());
		return output.toString();
	}

	public Object visit(Continue continueStatement) {
		StringBuffer output = new StringBuffer();
		SymbolTable continueStatementScope = continueStatement.getScope();
		
		indent(output, continueStatement);
		output.append("Continue statement, Symbol table: " + continueStatementScope.getId());
		return output.toString();
	}

	public Object visit(StatementsBlock statementsBlock) {
		StringBuffer output = new StringBuffer();
		SymbolTable statementsBlockScope = statementsBlock.getScope();
		
		indent(output, statementsBlock);
		output.append("Block of statements, Symbol table: " + statementsBlockScope.getId());
		depth++;
		for (Statement statement : statementsBlock.getStatements())
			output.append(statement.accept(this));
		depth--;
		
		return output.toString();
	}

	public Object visit(LocalVariable localVariable) {
		StringBuffer output = new StringBuffer();
		SymbolTable localVariableScope = localVariable.getScope();
		ArrayLocation arr2;
		NewArray arr3;
		String str = null;
		int i;
		
		indent(output, localVariable);
		output.append("Declaration of local variable: "
				+ localVariable.getName());
		if (localVariable.hasInitValue()) {
			output.append(", with initial value");
		}
		localVariable.getType().setScope(localVariableScope);
		output.append(", " + localVariable.getType().accept(this));
		output.append(", Symbol table: "+ localVariableScope.getId());
		
		++depth;
		if (localVariable.hasInitValue()) {
			localVariable.getInitValue().setScope(localVariableScope);
//			if (localVariable.getType().getDimension() == 1){
//				((NewArray)(localVariable.getInitValue())).getType().setDimention(localVariable.getType().getDimension());
//			}

			if(localVariable.getType().getDimension() > 0){
				if(localVariable.getInitValue().getClass().equals(ArrayLocation.class)){
					arr2 = ((ArrayLocation)(localVariable.getInitValue()));
					
					for (i = 0; i < 10; i++){
						
						str = arr2.getArray().getClass().toString();
						str = str.substring(str.lastIndexOf(".")+1, str.length());
						
						if (!str.equals("ArrayLocation"))
							break;
						else
							arr2 = ((ArrayLocation)(arr2.getArray()));
					}
					
					//((NewArray)(arr2.getArray())).getType().setDimention(localVariable.getType().getDimension());
				}
			}
			output.append(localVariable.getInitValue().accept(this));
		}
		--depth;
		
		return output.toString();
	}

	public Object visit(VariableLocation location) {
		StringBuffer output = new StringBuffer();
		SymbolTable locationScope = location.getScope();
		
		indent(output, location);
		output.append("Reference to variable: " + location.getName());
		if (location.isExternal())
			output.append(", in external scope");
		
		output.append(", Type: " + locationScope.searchForVar(location.getName(), location.getLine()).getType().toStringSymTable()); 
		output.append(", Symbol table: " + locationScope.getId());
		
		if (location.isExternal()) {
			depth++;
			output.append(location.getLocation().accept(this));
			depth--;
		}
		
		return output.toString();
	}

	public Object visit(ArrayLocation location) {
		StringBuffer output = new StringBuffer();
		SymbolTable locationScope = location.getScope();
		String str;
		int i = 0, j = 0, k = 0;
		ArrayLocation arr = null;
		VariableLocation arr3 = null;
		String arrType;
		ArrayType arr2 = null;
		indent(output, location);
		
		output.append("Reference to array");
		
		str = location.getArray().getClass().toString();
		str = str.substring(str.lastIndexOf(".") + 1, str.length());
		//String arrType = locationScope.searchForVar(((NewArray)(location.getArray())).getType().getName();
		//if(arrType.endsWith("[]"))
			//arrType = arrType.substring(0, arrType.length() -2);
		if (str.equals("ArrayLocation")){
			arr  = (ArrayLocation)location.getArray();
			j = 1;
		
			for (i = 0; i < 10; i++){
	
				str = arr.getArray().getClass().toString();
				str = str.substring(str.lastIndexOf(".") + 1, str.length());	
				
				if (str.equals("ArrayLocation")){
					arr  = (ArrayLocation)arr.getArray();
				}
				else{
					break;
				}
			
			}
		}
		if (str.equals("VariableLocation")){
			if ((i > 0) || (j > 0)){
				arr2 = ((ArrayType)(locationScope.searchForVar( ((VariableLocation)(arr.getArray())).getName(), arr.getLine()).getType()));
				arrType = arr2.getArrayType().getName();
				int dim = arr2.getArrayType().getDimension();
				for (k = 0; k < dim-i-j-1; k++){
					arrType += "[]";
				}
			}
			else{
				arr2 = ((ArrayType)(locationScope.searchForVar( ((VariableLocation)(location.getArray())).getName(), location.getLine()).getType()));
				arrType = arr2.getArrayType().getName();
				int dim = arr2.getArrayType().getDimension();
				for (j = 0; j < dim-1; j++){
					arrType += "[]";
				}
				
			}
		//	if(arrType.endsWith("[]"))  
				//arrType = arrType.substring(0, arrType.length() -2);
			output.append(", Type: " + arrType);
		}	
		else{
			if ((i > 0) || (j > 0))
				output.append(", Type: " + ((NewArray)(arr.getArray())).getType().getName());
			else
				output.append(", Type: " + ((NewArray)(location.getArray())).getType().getName());
		}
		output.append(", Symbol table: " + locationScope.getId());
		

		depth++;
		output.append(location.getArray().accept(this));
		output.append(location.getIndex().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(StaticCall call) {
		StringBuffer output = new StringBuffer();

		indent(output, call);
		output.append("Call to static method: " + call.getName()
				+ ", in class " + call.getClassName());
		output.append(", Type: " + getReturnTypeString(TypeTable.getTypeNameByString(call.getName()))
				+ ", Symbol Table: " + call.getScope().getId());
		depth++;
		for (Expression argument : call.getArguments())
			output.append(argument.accept(this));
		depth--;
		return output.toString();
	}
	
	public static String getReturnTypeString(String MethodType){
		String returnTypeString;
		int beginIndex = MethodType.indexOf("-> ");
		int endIndex = MethodType.indexOf('}');
		returnTypeString = MethodType.substring(beginIndex+3, endIndex);
		return returnTypeString;
	}
	
	
	public Object visit(VirtualCall call) {
		StringBuffer output = new StringBuffer();
		
		indent(output, call);
		output.append("Call to virtual method: " + call.getName());
		if (call.isExternal())
			output.append(", in external scope");
		output.append(", Type: " + getReturnTypeString(TypeTable.getTypeNameByString(call.getName())) +
				", Symbol table: " + call.getScope().getId());
		depth++;
		if (call.isExternal())
			output.append(call.getLocation().accept(this));
		for (Expression argument : call.getArguments())
			output.append(argument.accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(This thisExpression) {
		StringBuffer output = new StringBuffer();
		SymbolTable thisExpScope = thisExpression.getScope();
		
		indent(output, thisExpression);
		output.append("Reference to 'this' instance");
		output.append(", Symbol table: " + thisExpScope.getId());
		return output.toString();
	}

	public Object visit(NewClass newClass) {
		StringBuffer output = new StringBuffer();
		SymbolTable newClassScope = newClass.getScope();
			
		indent(output, newClass);
		output.append("Instantiation of class: " + newClass.getName());
		output.append(", Type: " + newClass.getName());
		output.append(", Symbol table: " + newClassScope.getId());
		return output.toString();
	}

	public Object visit(NewArray newArray) {
		StringBuffer output = new StringBuffer();
		SymbolTable newArrayScope = newArray.getScope();
		indent(output, newArray);
		output.append("Array allocation");
		output.append(", " + newArray.getType().accept(this));
		output.append("[]");	
		output.append(", Symbol table: " + newArrayScope.getId());
		
		depth++;
		newArray.getSize().setScope(newArrayScope);
		output.append(newArray.getSize().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(Length length) {
		StringBuffer output = new StringBuffer();
		SymbolTable lengthScope = length.getScope();		
		
		indent(output, length);
		output.append("Reference to array length");
		output.append(", Type: int");
		output.append(", Symbol table: " + lengthScope.getId());
		
		++depth;
		output.append(length.getArray().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(MathBinaryOp binaryOp) {
		StringBuffer output = new StringBuffer();
		SymbolTable binaryOpScope = binaryOp.getScope();
		SymbolEntry entry;
		String str, str2;
		
		indent(output, binaryOp);
		output.append("Mathematical binary operation: "
				+ binaryOp.getOperator().getDescription());
		
		str = binaryOp.getSecondOperand().getClass().toString();
		str = str.substring(str.lastIndexOf(".")+1, str.length());
		
		if (str.equals("VariableLocation"))
			output.append(", Type: " + binaryOpScope.searchForVar(((VariableLocation)binaryOp.getSecondOperand()).getName(), binaryOp.getLine()).getType().getName());
		else if(str.equals("Literal")){
			str2 = ((Literal)(binaryOp.getSecondOperand())).getType().getDescription();
			if (str2.substring(0, str2.indexOf(" ")).equalsIgnoreCase("Integer"))
				output.append(", Type: int");
			if (str2.substring(0, str2.indexOf(" ")).equalsIgnoreCase("String"))
				output.append(", Type: string");
		}
			
		output.append(", Symbol table: " + binaryOpScope.getId());
		depth++;
		output.append(binaryOp.getFirstOperand().accept(this));
		output.append(binaryOp.getSecondOperand().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(LogicalBinaryOp binaryOp) {
		StringBuffer output = new StringBuffer();
		SymbolTable binaryOpScope = binaryOp.getScope();
		
		indent(output, binaryOp);
		output.append("Logical binary operation: "
				+ binaryOp.getOperator().getDescription());
		output.append(", Type: boolean");
		output.append(", Symbol table: " + binaryOpScope.getId());
		depth++;
		output.append(binaryOp.getFirstOperand().accept(this));
		output.append(binaryOp.getSecondOperand().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(MathUnaryOp unaryOp) {
		StringBuffer output = new StringBuffer();
		SymbolTable unaryOpScope = unaryOp.getScope();
		
		indent(output, unaryOp);
		output.append("Mathematical unary operation: "
				+ unaryOp.getOperator().getDescription());
		output.append(", Type: int");
		output.append(", Symbol table: " + unaryOpScope.getId());
		++depth;
		output.append(unaryOp.getOperand().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(LogicalUnaryOp unaryOp) {
		StringBuffer output = new StringBuffer();
		SymbolTable unaryOpScope = unaryOp.getScope();
		
		indent(output, unaryOp);
		output.append("Logical unary operation: "
				+ unaryOp.getOperator().getDescription());
		output.append(", Type: boolean");
		output.append(", Symbol table: " + unaryOpScope.getId());
		++depth;
		output.append(unaryOp.getOperand().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(Literal literal) {
		StringBuffer output = new StringBuffer();
		SymbolTable literalScope = literal.getScope();
		
		indent(output, literal);
		if (literal.getType().toFormattedString(literal.getValue()).equals("null")){
			output.append("Null literal: null, Type: null");	
		}
		else{
			output.append(literal.getType().getDescription() + ": "
					+ literal.getType().toFormattedString(literal.getValue()));
			output.append(", Type: " + LiteralTypes.literalTypeByName(literal.getType().getDescription()));
		}
		output.append(", Symbol table: " + literalScope.getId());
		
		return output.toString();
	}

	public Object visit(ExpressionBlock expressionBlock) {
		StringBuffer output = new StringBuffer();
		SymbolTable expressionBlockScope = expressionBlock.getScope();
		
		indent(output, expressionBlock);
		output.append("Parenthesized expression");
		output.append(", Symbol table: " + expressionBlockScope.getId());
		++depth;
		output.append(expressionBlock.getExpression().accept(this));
		--depth;
		return output.toString();
	}
}