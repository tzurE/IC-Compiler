package IC.AST;
import SymbolTables.*;

public interface PropVisitor {

	public Object visit(Program program , SymbolTable table);

	public Object visit(ICClass icClass, SymbolTable table);

	public Object visit(Field field, SymbolTable table);

	public Object visit(Method method, SymbolTable table);
	
	public Object visit(VirtualMethod method, SymbolTable table);

	public Object visit(StaticMethod method, SymbolTable table);

	public Object visit(LibraryMethod method, SymbolTable table);

	public Object visit(Formal formal, SymbolTable table);

	public Object visit(PrimitiveType type, SymbolTable table);

	public Object visit(UserType type, SymbolTable table);

	public Object visit(Assignment assignment, SymbolTable table);

	public Object visit(CallStatement callStatement, SymbolTable table);

	public Object visit(Return returnStatement, SymbolTable table);

	public Object visit(If ifStatement, SymbolTable table);

	public Object visit(While whileStatement, SymbolTable table);

	public Object visit(Break breakStatement, SymbolTable table);

	public Object visit(Continue continueStatement, SymbolTable table);

	public Object visit(StatementsBlock statementsBlock, SymbolTable table);

	public Object visit(LocalVariable localVariable, SymbolTable table);

	public Object visit(VariableLocation location, SymbolTable table);

	public Object visit(ArrayLocation location, SymbolTable table);

	public Object visit(StaticCall call, SymbolTable table);

	public Object visit(VirtualCall call, SymbolTable table);

	public Object visit(This thisExpression, SymbolTable table);

	public Object visit(NewClass newClass, SymbolTable table);

	public Object visit(NewArray newArray, SymbolTable table);

	public Object visit(Length length, SymbolTable table);

	public Object visit(MathBinaryOp binaryOp, SymbolTable table);

	public Object visit(LogicalBinaryOp binaryOp, SymbolTable table);

	public Object visit(MathUnaryOp unaryOp, SymbolTable table);

	public Object visit(LogicalUnaryOp unaryOp, SymbolTable table);

	public Object visit(Literal literal, SymbolTable table);

	public Object visit(ExpressionBlock expressionBlock, SymbolTable table);
}
