package LIR;

import IC.AST.*;

public interface LirVisitor {

	public Object visit(Program program , int regNum);

	public Object visit(ICClass icClass, int regNum);

	public Object visit(Field field, int regNum);

	public Object visit(VirtualMethod method, int regNum);

	public Object visit(StaticMethod method, int regNum);

	public Object visit(LibraryMethod method, int regNum);

	public Object visit(Formal formal, int regNum);

	public Object visit(PrimitiveType type, int regNum);

	public Object visit(UserType type, int regNum);

	public Object visit(Assignment assignment, int regNum);

	public Object visit(CallStatement callStatement, int regNum);

	public Object visit(Return returnStatement, int regNum);

	public Object visit(If ifStatement, int regNum);

	public Object visit(While whileStatement, int regNum);

	public Object visit(Break breakStatement, int regNum);

	public Object visit(Continue continueStatement, int regNum);

	public Object visit(StatementsBlock statementsBlock, int regNum);

	public Object visit(LocalVariable localVariable, int regNum);

	public Object visit(VariableLocation location, int regNum);

	public Object visit(ArrayLocation location, int regNum);

	public Object visit(StaticCall call, int regNum);

	public Object visit(VirtualCall call, int regNum);

	public Object visit(This thisExpression, int regNum);

	public Object visit(NewClass newClass, int regNum);

	public Object visit(NewArray newArray, int regNum);

	public Object visit(Length length, int regNum);

	public Object visit(MathBinaryOp binaryOp, int regNum);

	public Object visit(LogicalBinaryOp binaryOp, int regNum);

	public Object visit(MathUnaryOp unaryOp, int regNum);

	public Object visit(LogicalUnaryOp unaryOp, int regNum);

	public Object visit(Literal literal, int regNum);

	public Object visit(ExpressionBlock expressionBlock, int regNum);


}
