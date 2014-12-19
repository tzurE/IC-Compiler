package IC.AST;
import SymbolTables.*;

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
		indent(output, icClass);
		SymbolTable classScope = icClass.getScope();
		String	nameScope = classScope.getId();
		
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
		output.append("User-defined data type: ");
		
		for(int i = 0; i < type.getDimension(); i++){
			dim = dim + "[]";
		}
		
		output.append(type.getName() + dim);
		return output.toString();
	}

	public Object visit(Field field) {
		StringBuffer output = new StringBuffer();
		SymbolTable classScope = field.getScope();
		String nameScope = classScope.getId();
		
		indent(output, field);
		output.append("Declaration of field: " + field.getName());
		output.append(", " + field.getType().accept(this));
		output.append(", Symbol table: " + nameScope);
		return output.toString();
	}

	public Object visit(LibraryMethod method) {
		StringBuffer output = new StringBuffer();

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
		SymbolTable classScope = formal.getScope();
		SymbolTableType classScopeType = classScope.getType();
		
		indent(output, formal);
		output.append("Parameter: " + formal.getName());
		output.append(", " + formal.getType().accept(this));
		output.append(", Symbol table: "+ classScope.getId());
		
		return output.toString();
	}

	public Object visit(VirtualMethod method) {
		StringBuffer output = new StringBuffer();
		SymbolTable classScope = method.getScope();
		
		indent(output, method);
		output.append("Declaration of virtual method: " + method.getName());
		depth++;
		
		output.append(", Type: " + TypeTable.getTypeNameByString(method.getName()) +
				", Symbol table: "+ classScope.getId());
		for (Formal formal : method.getFormals())
			output.append(formal.accept(this));
		for (Statement statement : method.getStatements())
			output.append(statement.accept(this));
		
		depth--;
		return output.toString();
	}

	public Object visit(StaticMethod method) {
		StringBuffer output = new StringBuffer();
		SymbolTable classScope = method.getScope();
		
		indent(output, method);
		output.append("Declaration of static method: " + method.getName());
		depth++;
		
		output.append(", Type: " + TypeTable.getTypeNameByString(method.getName()) +
				", Symbol table: "+ classScope.getId());
		for (Formal formal : method.getFormals())
			output.append(formal.accept(this));
		for (Statement statement : method.getStatements())
			output.append(statement.accept(this));
		
		depth--;
		return output.toString();
	}

	public Object visit(Assignment assignment) {
		StringBuffer output = new StringBuffer();

		indent(output, assignment);
		output.append("Assignment statement");
		depth += 2;
		output.append(assignment.getVariable().accept(this));
		output.append(assignment.getAssignment().accept(this));
		depth -= 2;
		return output.toString();
	}

	public Object visit(CallStatement callStatement) {
		StringBuffer output = new StringBuffer();

		indent(output, callStatement);
		output.append("Method call statement");
		++depth;
		output.append(callStatement.getCall().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(Return returnStatement) {
		StringBuffer output = new StringBuffer();

		indent(output, returnStatement);
		output.append("Return statement");
		if (returnStatement.hasValue())
			output.append(", with return value");
		if (returnStatement.hasValue()) {
			++depth;
			output.append(returnStatement.getValue().accept(this));
			--depth;
		}
		return output.toString();
	}

	public Object visit(If ifStatement) {
		StringBuffer output = new StringBuffer();
		SymbolTable classScope = ifStatement.getScope();
		
		indent(output, ifStatement);
		output.append("If statement");
		if (ifStatement.hasElse())
			output.append(", with Else operation");
		output.append(", Symbol table: " + classScope.getId());
		
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

		indent(output, whileStatement);
		output.append("While statement");
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

		indent(output, statementsBlock);
		output.append("Block of statements");
		depth++;
		for (Statement statement : statementsBlock.getStatements())
			output.append(statement.accept(this));
		depth--;
		return output.toString();
	}

	public Object visit(LocalVariable localVariable) {
		StringBuffer output = new StringBuffer();
		SymbolTable classScope = localVariable.getScope();
		SymbolTableType classScopeType = classScope.getType();
		String	nameScope = classScope.getId();

		indent(output, localVariable);
		output.append("Declaration of local variable: "
				+ localVariable.getName());
		if (localVariable.hasInitValue()) {
			output.append(", with initial value");
		}

		output.append(", " + localVariable.getType().accept(this));
		output.append(", Symbol table: "+ classScope.getId());
		
		++depth;
		if (localVariable.hasInitValue()) {
			output.append(localVariable.getInitValue().accept(this));
			output.append(", " + localVariable.getType().accept(this));
			output.append(", Symbol table: " + classScope.getId());
		}
		--depth;
		
		return output.toString();
	}

	public Object visit(VariableLocation location) {
		StringBuffer output = new StringBuffer();
		SymbolTable classScope = location.getScope();
		
		indent(output, location);
		output.append("Reference to variable: " + location.getName());
		if (location.isExternal())
			output.append(", in external scope");
		if (location.isExternal()) {
			output.append(location.getLocation().accept(this));
		}
		
		output.append(", Type: " + location.getName());
		//output.append(", Symbol table: " + classScope.getId());
		
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

		indent(output, newArray);
		output.append("Array allocation");
		depth++;
		output.append(newArray.getType().accept(this));
		output.append(newArray.getSize().accept(this));
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

		indent(output, literal);
		output.append(literal.getType().getDescription() + ": "
				+ literal.getType().toFormattedString(literal.getValue()));
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