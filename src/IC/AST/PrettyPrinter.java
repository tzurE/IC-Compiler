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
			output.append("\t");
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
		
		indent(output, icClass);
		String	nameScope;
		if(classScope.getClass().toString().equals(GlobalSymbolTable.class.toString()))
			nameScope = "Global";
		else{
			nameScope = classScope.getId();
		}
		
		output.append("Declaration of class: " + icClass.getName() );
		if (icClass.hasSuperClass()){
			output.append(", subclass of " + icClass.getSuperClassName() + ", Type:" + TypeTable.getTypeNameByString(icClass.getName()) + 
					", Symbol table: " + classScope.getId());
		}
		else
			output.append(", Type:" + TypeTable.getTypeNameByString(icClass.getName()) +    ", Symbol table: "+ nameScope);
		
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
		
		output.append(TypeTable.getTypeNameByString(type.getName()) + dim);
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
		
		indent(output, method);
		output.append("Declaration of library method: " + method.getName());
		depth++;
		output.append(method.getType().accept(this));
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
		output.append(assignment.getAssignment().accept(this) + ", Symbol table: " + assignmentScope.getId());
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
			output.append(", Symbol table: " + returnScope.getId());
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

		indent(output, breakStatement);
		output.append("Break statement");
		return output.toString();
	}

	public Object visit(Continue continueStatement) {
		StringBuffer output = new StringBuffer();

		indent(output, continueStatement);
		output.append("Continue statement");
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

		indent(output, localVariable);
		output.append("Declaration of local variable: "
				+ localVariable.getName());
		if (localVariable.hasInitValue()) {
			output.append(", with initial value");
		}
		localVariable.getType().setScope(localVariableScope);
		output.append(", " + localVariable.getType().accept(this));
	//	output.append(", Symbol table: "+ localVariableScope.getId());
		
		++depth;
		if (localVariable.hasInitValue()) {
			localVariable.getInitValue().setScope(localVariableScope);
			output.append(localVariable.getInitValue().accept(this));
			output.append(", Symbol table: " + localVariableScope.getId());
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

		indent(output, location);
		output.append("Reference to array");
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
		depth++;
		for (Expression argument : call.getArguments())
			output.append(argument.accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(VirtualCall call) {
		StringBuffer output = new StringBuffer();

		indent(output, call);
		output.append("Call to virtual method: " + call.getName());
		if (call.isExternal())
			output.append(", in external scope");
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

		indent(output, thisExpression);
		output.append("Reference to 'this' instance");
		return output.toString();
	}

	public Object visit(NewClass newClass) {
		StringBuffer output = new StringBuffer();

		indent(output, newClass);
		output.append("Instantiation of class: " + newClass.getName());
		return output.toString();
	}

	public Object visit(NewArray newArray) {
		StringBuffer output = new StringBuffer();
		SymbolTable newArrayScope = newArray.getScope();
		
		indent(output, newArray);
		output.append("Array allocation");
		depth++;
		output.append(" " + newArray.getType().accept(this));
		newArray.getSize().setScope(newArrayScope);
		output.append(newArray.getSize().accept(this));
		output.append(", Type: " + newArray.getType()); 
		output.append(", Symbol table: " + newArrayScope.getId());
		depth--;
		return output.toString();
	}

	public Object visit(Length length) {
		StringBuffer output = new StringBuffer();

		indent(output, length);
		output.append("Reference to array length");
		++depth;
		output.append(length.getArray().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(MathBinaryOp binaryOp) {
		StringBuffer output = new StringBuffer();

		indent(output, binaryOp);
		output.append("Mathematical binary operation: "
				+ binaryOp.getOperator().getDescription());
		depth++;
		output.append(binaryOp.getFirstOperand().accept(this));
		output.append(binaryOp.getSecondOperand().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(LogicalBinaryOp binaryOp) {
		StringBuffer output = new StringBuffer();

		indent(output, binaryOp);
		output.append("Logical binary operation: "
				+ binaryOp.getOperator().getDescription());
		depth++;
		output.append(binaryOp.getFirstOperand().accept(this));
		output.append(binaryOp.getSecondOperand().accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(MathUnaryOp unaryOp) {
		StringBuffer output = new StringBuffer();

		indent(output, unaryOp);
		output.append("Mathematical unary operation: "
				+ unaryOp.getOperator().getDescription());
		++depth;
		output.append(unaryOp.getOperand().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(LogicalUnaryOp unaryOp) {
		StringBuffer output = new StringBuffer();

		indent(output, unaryOp);
		output.append("Logical unary operation: "
				+ unaryOp.getOperator().getDescription());
		++depth;
		output.append(unaryOp.getOperand().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(Literal literal) {
		StringBuffer output = new StringBuffer();
		SymbolTable literalScope = literal.getScope();
		
		indent(output, literal);
		output.append(literal.getType().getDescription() + ": "
				+ literal.getType().toFormattedString(literal.getValue()));
		output.append(", Type: " + LiteralTypes.literalTypeByName(literal.getType().getDescription()));
		//output.append(", Symbol table: " + literalScope.getId());
		
		return output.toString();
	}

	public Object visit(ExpressionBlock expressionBlock) {
		StringBuffer output = new StringBuffer();

		indent(output, expressionBlock);
		output.append("Parenthesized expression");
		++depth;
		output.append(expressionBlock.getExpression().accept(this));
		--depth;
		return output.toString();
	}
}