package LIR;
import java.util.HashMap;
import java.util.Map;

import SymbolTables.*;
import IC.AST.*;

import java.util.Map;

public class LirPropVisitor implements LirVisitor{
	private GlobalSymbolTable globalTable;
	private int num_of_reg_used =0 ;
	// program strings literals and their names. names form is: strY where Y = 1,2,3...
	private Map<String, String>stringLiterals;

	// class names and their layouts (dispatch table)
	private Map<String,ClassLayout>  classesLayouts; 
	
	//our translation to lir
	private String LirProgram = "";

	private int lables_to_jump_if_false_counter = 1;
	private int lables_to_jump_if_true_counter = 1;
	private int end_lables_counter = 1;
	private int while_test_lable_counter = 1;
	private boolean is_main_exists = false;
	
	@Override
	public Object visit(Program program, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object visit(ICClass icClass, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Field field, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(VirtualMethod method, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(StaticMethod method, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(LibraryMethod method, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Formal formal, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(PrimitiveType type, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(UserType type, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Assignment assignment, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(CallStatement callStatement, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Return returnStatement, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(If ifStatement, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(While whileStatement, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Break breakStatement, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Continue continueStatement, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(StatementsBlock statementsBlock, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(LocalVariable localVariable, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(VariableLocation location, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(ArrayLocation location, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(StaticCall call, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(VirtualCall call, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(This thisExpression, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(NewClass newClass, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(NewArray newArray, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Length length, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(MathBinaryOp binaryOp, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(LogicalBinaryOp binaryOp, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(MathUnaryOp unaryOp, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(LogicalUnaryOp unaryOp, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Literal literal, int regNum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(ExpressionBlock expressionBlock, int regNum) {
		// TODO Auto-generated method stub
		return null;
	} 
	
}