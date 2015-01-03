package LIR;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import SymbolTables.SymbolTable;
import IC.BinaryOps;
import IC.DataTypes;
import IC.LiteralTypes;
import IC.AST.*;
import LIRInstructions.BinOpInstr;
import LIRInstructions.Immediate;
import LIRInstructions.LIRNode;
import LIRInstructions.Label;
import LIRInstructions.MoveInstr;
import LIRInstructions.Operand;
import LIRInstructions.Operator;
import LIRInstructions.Reg;

public class LirTranslatorVisitor implements LirVisitor{

	private String NEW_LINE = "\n";
	private Map<String, ClassLayout> classLayouts;
	private List<LIRNode> LIRProgram;
	private List<LIRNode> temp_Program;
	private List<LIRNode> StringLiterals;
	private int numStringLiterals;


	public LirTranslatorVisitor() {
		this.StringLiterals = new LinkedList<LIRNode>();
		this.numStringLiterals = 0;
	}
	
	
	@Override
	public Object visit(Program program, int regCount) {
		ClassLayout icClassLayout;
		classLayouts = new HashMap<String, ClassLayout>();
		StringBuilder strbld = new StringBuilder();
		LIRNode dispatchTable = new Label("");
		temp_Program = new LinkedList<LIRNode>();
		temp_Program.add(dispatchTable);
		
		
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
				strbld.append(icClassLayout.printDispatchTable());
			}
		}
		((Label)temp_Program.get(0)).setName(strbld.toString());
		
		for(ICClass icClass : program.getClasses()) {
			
			if(icClass.getName() != "Library"){
				
				icClass.accept(this, regCount);
			}
		}
		
		// Change StringLiterals list to contain string-variable names
		for (int i = 0; i < StringLiterals.size(); i++){
			String label = ((Label)StringLiterals.get(i)).name;
			((Label)StringLiterals.get(i)).setName("str" + (i+1) + ": \"" + label + "\"");
		}
		
		//here we need to concatenate the 3 lists - literals, program, main. main has to be last!
		//we maybe wont add the main list!!!!!!!!!!!!!!! we found a solution!!!!!!		
		StringLiterals.addAll(temp_Program);
		LIRProgram = StringLiterals;
		return LIRProgram;
	}

	@Override
	public Object visit(ICClass icClass, int regCount) {
		for (Method method : icClass.getMethods()) {
			method.accept(this, regCount);
		}		
		return null;
	}

	@Override
	public Object visit(Field field, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualMethod method, int regCount) {
		methodToLir(method, regCount);
		return null;
	}


	@Override
	public Object visit(StaticMethod method, int regCount) {
		if(!method.getName().equals("main")){
			methodToLir(method, regCount);
		}
		else{
			LIRNode ic_main = new Label("_ic_main:" + NEW_LINE);
			temp_Program.add(ic_main);
			methodToLir(method, regCount);
			LIRNode ic_main_end = new Label("Library __exit(0),Rdummy" + NEW_LINE);
			temp_Program.add(ic_main_end);
			
		}
		return null;
	}

	@Override
	public Object visit(LibraryMethod method, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Formal formal, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(PrimitiveType type, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(UserType type, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Assignment assignment, int regCount) {
		
		if(assignment.getVariable() instanceof ArrayLocation){
			ArrayLocation arrayLocation = (ArrayLocation) assignment.getVariable();
		}
		else if(assignment.getVariable() instanceof VariableLocation){
			VariableLocation varLocation = (VariableLocation) assignment.getVariable();
			//assignment.getAssignment()
			assignment.getAssignment().accept(this, regCount);
			//LIRNode move = new MoveInstr(new Var, dst)
		}
		
		return null;
	}

	@Override
	public Object visit(CallStatement callStatement, int regCount) {
		callStatement.getCall().accept(this, regCount);
		return null;
	}

	@Override
	public Object visit(Return returnStatement, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(If ifStatement, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(While whileStatement, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Break breakStatement, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Continue continueStatement, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StatementsBlock statementsBlock, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable, int regCount) {
		
		if (localVariable.hasInitValue()){
			Operand oper1 = (Operand)localVariable.getInitValue().accept(this, regCount);
			Operand oper2 = new Reg(localVariable.getName());
		
			SymbolTable current_scope = localVariable.getScope();
			LIRNode move = new MoveInstr(oper1, oper2);
			temp_Program.add(move);
		}
		return null;
	}

	@Override
	public Object visit(VariableLocation location, int regCount) {
		Operand oper1 = new Immediate(Integer.parseInt(location.getName()));
		Operand oper2 = new Reg("R" + regCount);
		
		SymbolTable current_scope = location.getScope();
		LIRNode move = new MoveInstr(oper1, oper2);
		temp_Program.add(move);
		return null;
	}

	@Override
	public Object visit(ArrayLocation location, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StaticCall call, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualCall call, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(This thisExpression, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewClass newClass, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewArray newArray, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Length length, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp, int regCount) {

		Operand oper1 = (Operand)binaryOp.getFirstOperand().accept(this, regCount);
		Operand oper2 = (Operand)binaryOp.getSecondOperand().accept(this, regCount);
		Operand reg1 = null;
		Operand reg2 = null;
		LIRNode move = null;
		LIRNode binOpNode = null;
					
		reg1 = new Reg("R" + regCount);
		regCount++;
		move = new MoveInstr(oper1, reg1);
		// move oper1, reg#
		temp_Program.add(move);
		
		reg2 = new Reg("R" + regCount);
		regCount++;
		move = new MoveInstr(oper2, reg2);
		// move oper2, reg#
		temp_Program.add(move);
		
		if(binaryOp.getOperator().getOperatorString().equals(BinaryOps.PLUS.getOperatorString())){
			binOpNode = new BinOpInstr(reg1, reg2, Operator.ADD);
			temp_Program.add(binOpNode);
		}
		else if (binaryOp.getOperator().getOperatorString().equals(BinaryOps.MINUS.getOperatorString())){
			binOpNode = new BinOpInstr(reg1, reg2, Operator.SUB);
			temp_Program.add(binOpNode);
		}
		else if (binaryOp.getOperator().getOperatorString().equals(BinaryOps.MULTIPLY.getOperatorString())){
			binOpNode = new BinOpInstr(reg1, reg2, Operator.MUL);
			temp_Program.add(binOpNode);
		}
		else if (binaryOp.getOperator().getOperatorString().equals(BinaryOps.DIVIDE.getOperatorString())){
			binOpNode = new BinOpInstr(reg1, reg2, Operator.DIV);
			temp_Program.add(binOpNode);
		}
		else if (binaryOp.getOperator().getOperatorString().equals(BinaryOps.MOD.getOperatorString())){
			binOpNode = new BinOpInstr(reg1, reg2, Operator.MOD);
			temp_Program.add(binOpNode);
		}
		
	//	if (isRegister(str))
		
		return reg2;
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathUnaryOp unaryOp, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal literal, int regCount) {
		
		LIRNode oper;
		
		if (literal.getType().getDescription().equals(LiteralTypes.INTEGER.getDescription())){
			int i = Integer.parseInt(literal.getValue().toString());
			oper = new Immediate(i);
			return oper;
		}
		else if (literal.getType().getDescription().equals(LiteralTypes.STRING.getDescription())){
			
			Operand StringLitOper = new Label(literal.getValue().toString());
			String strLit; 
			if (!this.StringLiterals.contains(StringLitOper)){
				numStringLiterals++;
				strLit = "str" + numStringLiterals;
				StringLiterals.add(StringLitOper);
			}
			else{
				strLit = "str" + (this.StringLiterals.indexOf(StringLitOper)+1);
				((Label)StringLitOper).setName(strLit);
			}
				
			oper = new Label(strLit);

			return oper;
		}
		return null;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock, int regCount) {
		// TODO Auto-generated method stub
		return null;
	}

	private void methodToLir(Method method, int regCount) {
		LIRNode methodDec = new Label(""); 
		if(!method.getName().equals("main")){
			SymbolTable scope = method.getScope();
			String scope_name = scope.getId();
			((Label)methodDec).setName("_" + scope_name + "_" + method.getName() + ":" + NEW_LINE);
			temp_Program.add(methodDec);
		}
		for(Statement stmnt : method.getStatements()){
			stmnt.accept(this, regCount);
		}
	}
	
	private boolean isRegister(String str){
		if(str.startsWith("R")){
			return true;
		}
		return false;
	}
}
