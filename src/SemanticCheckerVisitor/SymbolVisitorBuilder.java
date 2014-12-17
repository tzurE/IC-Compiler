package SemanticCheckerVisitor;

import IC.AST.*;
import SymbolTables.*;

public class SymbolVisitorBuilder implements PropVisitor{

	private String prog_name;
	
	public SymbolVisitorBuilder(String prog_name){
		this.prog_name = prog_name;
	}
	
	@Override
	public Object visit(Program program, SymbolTable table) {
		
		//no father - it is the main table!
		SymbolTable symbol_table = new GlobalSymbolTable(prog_name, null);
		for (ICClass icClass : program.getClasses()){
			//something like this for the types? 
			//Types classTypes = TypeTable.classType(icClass);
			
			//for each class in the program, we add an entry in the global symbol table
			symbol_table.addEntry(icClass.getName(), new ClassEntry(icClass.getName()), icClass.getLine());
			if(!icClass.hasSuperClass()){
				symbol_table.addChild(icClass.getName(), new ClassSymbolTable(icClass.getName(), symbol_table));
				icClass.accept(this, symbol_table);
			}
			else{
				GlobalSymbolTable symbol_table1 = (GlobalSymbolTable) symbol_table;		
				ClassSymbolTable superClass = symbol_table1.findInnerChild(icClass.getSuperClassName());
				//symbol_table1.getChildTableList().get(icClass.getSuperClassName()).addChild(icClass.getName(), new ClassSymbolTable(icClass.getName(), symbol_table1.getChildTableList().get(icClass.getSuperClassName())));
				//System.out.println(superClass.getId());
				superClass.addChild(icClass.getName(), new ClassSymbolTable(icClass.getName(), superClass));
				icClass.accept(this, superClass);
			}
			
			
		}
		
		return symbol_table;
	}

	@Override
	public Object visit(ICClass icClass, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Field field, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualMethod method, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StaticMethod method, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LibraryMethod method, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Formal formal, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(PrimitiveType type, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(UserType type, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Assignment assignment, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CallStatement callStatement, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Return returnStatement, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(If ifStatement, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(While whileStatement, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Break breakStatement, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Continue continueStatement, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StatementsBlock statementsBlock, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VariableLocation location, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ArrayLocation location, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StaticCall call, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualCall call, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(This thisExpression, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewClass newClass, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewArray newArray, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Length length, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathUnaryOp unaryOp, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal literal, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
