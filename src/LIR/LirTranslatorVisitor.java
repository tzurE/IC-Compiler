package LIR;

import java.util.LinkedList;
import java.util.Map;

import IC.AST.ArrayLocation;
import IC.AST.Assignment;
import IC.AST.Break;
import IC.AST.CallStatement;
import IC.AST.Continue;
import IC.AST.ExpressionBlock;
import IC.AST.Field;
import IC.AST.Formal;
import IC.AST.ICClass;
import IC.AST.If;
import IC.AST.Length;
import IC.AST.LibraryMethod;
import IC.AST.Literal;
import IC.AST.LocalVariable;
import IC.AST.LogicalBinaryOp;
import IC.AST.LogicalUnaryOp;
import IC.AST.MathBinaryOp;
import IC.AST.MathUnaryOp;
import IC.AST.NewArray;
import IC.AST.NewClass;
import IC.AST.PrimitiveType;
import IC.AST.Program;
import IC.AST.Return;
import IC.AST.StatementsBlock;
import IC.AST.StaticCall;
import IC.AST.StaticMethod;
import IC.AST.This;
import IC.AST.UserType;
import IC.AST.VariableLocation;
import IC.AST.VirtualCall;
import IC.AST.VirtualMethod;
import IC.AST.While;

public class LirTranslatorVisitor implements LirVisitor{

	private String NEW_LINE = "\n";
	private StringBuilder dispatch_table = new StringBuilder();
	private Map<String, ClassLayout> classLayouts;
	//private LinkedList<E>
	
	@Override
	public Object visit(Program program, int regNum) {
		ClassLayout icClassLayout;
		
		for (ICClass icClass : program.getClasses()){
			if (!icClass.getName().equals("Library")){
				
				// Add icClass's dispatch table, with inherited methods if exist
				if (icClass.hasSuperClass()){
					ClassLayout superClassLayout = this.classLayouts.get(icClass.getSuperClassName());
					icClassLayout = new ClassLayout(icClass, superClassLayout);
				}
				else{
					icClassLayout = new ClassLayout(icClass);
				}
				this.classLayouts.put(icClass.getName(), icClassLayout);
				dispatch_table.append(icClassLayout.printDispatchTable() + NEW_LINE);
			}
		}
		return null;
	}

	public StringBuilder getDispatch_table() {
		return dispatch_table;
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
