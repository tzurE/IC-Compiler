package LIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import SemanticCheckerVisitor.SymbolVisitorChecker;
import SymbolTables.GlobalSymbolTable;
import SymbolTables.SymbolKinds;
import SymbolTables.SymbolTable;
import SymbolTables.ClassSymbolTable;
import SymbolTables.MethodSymbolTable; 
import SymbolTables.SymbolTableType;
import TypeTable.TypeIDs;
import TypeTable.TypeTableType;
import IC.BinaryOps;
import IC.DataTypes;
import IC.LiteralTypes;
import IC.AST.*;
import LIRInstructions.BinOpInstr;
import LIRInstructions.Immediate;
import LIRInstructions.Memory; 
import LIRInstructions.ParamOpPair; 
import LIRInstructions.ReturnInstr;
import LIRInstructions.LIRNode;
import LIRInstructions.Label;
import LIRInstructions.LibraryCall;
import LIRInstructions.MoveArrayInstr;
import LIRInstructions.MoveFieldInstr;
import LIRInstructions.MoveInstr;
import LIRInstructions.Operand;
import LIRInstructions.Operator;
import LIRInstructions.Reg;

public class LirTranslatorVisitor implements LirVisitor{
	
	private GlobalSymbolTable global;
	private String NEW_LINE = "\n";
	private Map<String, ClassLayout> classLayouts;
	private List<LIRNode> LIRProgram;
	private List<LIRNode> temp_Program;
	private List<LIRNode> StringLiterals;
	private int numStringLiterals;
	private boolean isReturnExist; 
	

	public LirTranslatorVisitor(GlobalSymbolTable global) {
		this.StringLiterals = new LinkedList<LIRNode>();
		this.numStringLiterals = 0;
		this.global = global;
		this.isReturnExist = false; 
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
			Operand arr1 = (Operand)arrayLocation.getArray().accept(this, regCount);
			String arr = arr1.toString();
			Operand reg = null, reg2 = null, reg3 = null;
			LIRNode movearr = null;
			if(!this.isRegister(arr)){
				reg = new Reg("R" + regCount++);
				LIRNode move = new MoveInstr(new Reg(arr), reg);
				temp_Program.add(move);
			}
			Operand ass = (Operand) assignment.getAssignment().accept(this, regCount);
			if(!this.isRegister(ass.toString())){
				reg3 = new Reg("R" + regCount++);
				LIRNode move3 = new MoveInstr(ass, reg3);
				temp_Program.add(move3);
				
			}
			Operand index = (Operand) arrayLocation.getIndex().accept(this, regCount);
			if(!this.isRegister(index.toString())){
				reg2 = new Reg("R" + regCount++);
				LIRNode move2 = new MoveInstr(index, reg2);
				temp_Program.add(move2);
				movearr = new MoveArrayInstr(reg, reg2, reg3, false);
			}
			else{
				movearr = new MoveArrayInstr(reg, index, reg3, false);
			}
			temp_Program.add(movearr);
			
		}
		else if(assignment.getVariable() instanceof VariableLocation){
			VariableLocation varLocation = (VariableLocation) assignment.getVariable();
			String cName = "";
			String Name = varLocation.getName();
			String class_t = "";
			boolean field = false;
			LIRNode move = null, move2 = null;
			Operand reg1 = null;
			Operand ass = (Operand) assignment.getAssignment().accept(this, regCount);
			if(this.isRegister(ass.toString())){
				regCount = this.getRegNum(ass.toString()) + 1;
				reg1 = ass;
			}
			else{
				reg1 = new Reg("R" + regCount++);
				LIRNode move1 = new MoveInstr(ass, reg1);
				temp_Program.add(move1);
				
			}
			if(varLocation.isExternal()){//external, lets get some info on it.
				field = true;
				TypeTableType classType = (TypeTableType)varLocation.getLocation().accept(new SymbolVisitorChecker(this.global),null);
				cName = classType.getName();
				//////////////////////////////////what next?
				varLocation.getLocation().accept(this, regCount);
				
			}
			else{//not external - meaning we need to do "this"
				SymbolTable currScope = varLocation.getScope();
				while(!(currScope.getType().compareTo(SymbolTableType.CLASS) == 0)){
					currScope = currScope.getFather_table();
				}
				cName = currScope.getId();
				if(this.classLayouts.get(cName).getFieldByName().containsKey(Name)){
					field = true;
					//need to add a move here
//					tr += "Move this,R" + regNum + NEW_LINE;
//					trClass = "R" + regNum; 
				}				
			}
			if(field){
//			ClassLayout cl = this.classesLayouts.get(className);
//			int fieldOffset = cl.getFieldOffset(locationName);
//
//			tr += "MoveField " + trAssignValue + "," + trClass + "." + fieldOffset + NEW_LINE; 
			}
			else{//local!
				move2 = new MoveInstr(reg1, new Label(Name));
				temp_Program.add(move2);
				return null;
				
			}
			
			
			
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
		
		this.isReturnExist = true;
		Operand oper = (Operand)returnStatement.getValue().accept(this, regCount);
		LIRNode retInst = new ReturnInstr(oper);
		temp_Program.add(retInst);
		
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
		LIRNode move = null,move1 = null;
		if (localVariable.hasInitValue()){
			Operand oper1 = (Operand)localVariable.getInitValue().accept(this, regCount);
			if(!this.isRegister(oper1.toString())){
				move = new MoveInstr(oper1, new Reg(localVariable.getName()));
				temp_Program.add(move);
			}
			else{
				move1 = new MoveInstr(oper1, new Reg(localVariable.getName()));
				temp_Program.add(move1);
			}

			
		}
		return null;
	}

	@Override
	public Object visit(VariableLocation location, int regCount) {
		String name = location.getName();
		String className = "className"; //sign for us if its init
		String class_t = "class_t"; //sign for us if its init
		LIRNode move = null;
		Operand reg1 = null;
		Operand reg2 = null;
		boolean field = false;
		if(location.isExternal()){
			field = true;
			
			TypeTableType	classType = (TypeTableType)location.getLocation().accept(new SymbolVisitorChecker(this.global), location.getScope());
			className= classType.getName();
			 class_t = (String) location.getLocation().accept(this, regCount); /// we don't know what's happening here - go deeper
			
		}
		else{
			SymbolTable currScope = location.getScope();
			while(!(currScope.getType().compareTo(SymbolTableType.CLASS) == 0)){
				if(currScope.getType().compareTo(SymbolTableType.STATEMENT) == 0){
					if(currScope.getEntry(name, SymbolKinds.LOCAL_VARIABLE)!=null){
						return new Label(name);
					}
				}
				if(currScope.getType().compareTo(SymbolTableType.STATIC_METHOD) == 0 || currScope.getType().compareTo(SymbolTableType.VIRTUAL_METHOD) == 0 ){
					if(currScope.getEntry(name, SymbolKinds.LOCAL_VARIABLE)!=null || currScope.getEntry(name, SymbolKinds.PARAMETER)!=null){
						return new Label(name);
					}
				}
			}
			currScope = currScope.getFather_table();
			
			if(currScope.getType().compareTo(SymbolTableType.CLASS) == 0){
				className = currScope.getId();
			}
			// Check if is a field
			if(this.classLayouts.get(className).getFieldByName().containsKey(name)){
				field = true;
				reg1 = new Reg(name);
				reg2 = new Reg("R" + regCount++);
				move = new MoveInstr(reg1, reg2);
				temp_Program.add(move);
			}
			else
				return new Label(name);
		}
		if(field){
			ClassLayout clay = this.classLayouts.get(className);
			int fieldOffset = clay.getFieldOffsetByName().get(name);
			Operand reg3 = new Reg("R" + regCount++);
			LIRNode movef = new MoveFieldInstr(new Reg(class_t), new Immediate(fieldOffset), reg3, true);

			temp_Program.add(movef);
		}
		
		return null;
	}

	@Override
	public Object visit(ArrayLocation location, int regCount) {
		String arr = (String)location.getArray().accept(this, regCount);
		Operand reg1;
		reg1 = (Operand) location.getIndex().accept(this, regCount);
		
		
		return arr + "[" + reg1.toString() + "]";
	}

	@Override
	public Object visit(StaticCall call, int regCount) {
		LIRNode callNode = null;
		Operand reg = null;
		Operand callParamOper = null;
		Label func = null;
		 	
		// Static call is a library method
		if(call.getClassName().equals("Library")){
			List<Operand> strOpers = new ArrayList<Operand>();
			
			for(Expression callParam : call.getArguments()){		
				callParamOper = (Operand) callParam.accept(this, regCount);
				strOpers.add(callParamOper);
			}
			
			func = new Label("__" + call.getName());
			reg = new Reg("R" + regCount++);
			callNode = new LibraryCall(func, strOpers, (Reg)reg);
		}
		// Not a library method
		else{
			
			List<ParamOpPair> paramPairs = new ArrayList<ParamOpPair>();
			// Gets the method object
			ClassSymbolTable classSymbolTable = this.global.getChildTableList().get(call.getClassName());
			MethodSymbolTable methodST = (MethodSymbolTable) classSymbolTable.getMethodChildTableList().get(call.getName());
			int numFormals = 1;
			ParamOpPair pop = null; 
			// Gets formal parameters 
			HashMap<Integer, String> formals = methodST.getParametersByOrder();
			
			for(Expression callParam : call.getArguments()){
				Memory mem = new Memory(formals.get(numFormals-1));
				Operand val = (Operand)callParam.accept(this, regCount);
				pop = new ParamOpPair(mem, val);
				paramPairs.add(pop);
				numFormals++;
			}
			
			
			func = new Label("_" + call.getClassName() + "_" + call.getName());
			reg = new Reg("R" + regCount++);
			callNode = new LIRInstructions.StaticCall(func, paramPairs, (Reg)reg);
		}
		
		temp_Program.add(callNode);			
		return reg;
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
		ClassLayout cClassLayout = this.classLayouts.get(newClass.getName()); 
		int cSize = cClassLayout.getDispatchTableSize();
		Operand reg1 = new Reg("R" + regCount++);
		LIRNode classAlloc = new Label("Library __allocateObject(" + cSize + ")" + reg1.toString());
		temp_Program.add(classAlloc);
		Operand c = new Label("_DV_"+ cClassLayout.getClassIdent());
		LIRNode dv = new MoveFieldInstr(reg1, new Immediate(0), c, false);
		temp_Program.add(dv);
		return reg1;
	}

	@Override
	public Object visit(NewArray newArray, int regCount) {
		Operand oper_size = (Operand)newArray.getSize().accept(this, regCount);
		LIRNode allocate = null;
		LIRNode reg1 = null;
		reg1 = new Reg("R" + regCount++);
		allocate = new Label("Library __allocateArray(" + oper_size.toString() + "),"+reg1.toString());
		temp_Program.add(allocate);
		return reg1;
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
					
		// move oper1, reg#
		reg1 = new Reg("R" + regCount++); 
		move = new MoveInstr(oper1, reg1);
		temp_Program.add(move);
		
		// move oper2, reg#
		reg2 = new Reg("R" + regCount++);
		move = new MoveInstr(oper2, reg2);
		temp_Program.add(move);
		
		if(binaryOp.getOperator().getOperatorString().equals(BinaryOps.PLUS.getOperatorString())){
			TypeTableType operandsType = (TypeTableType)binaryOp.getFirstOperand().accept (new SymbolVisitorChecker(this.global), binaryOp.getScope());
			if (operandsType.getId() == TypeIDs.INT){
				binOpNode = new BinOpInstr(reg1, reg2, Operator.ADD);
			}
			
			else if (operandsType.getId() == TypeIDs.STRING){
				List<Operand> strOpers = new ArrayList<Operand>();
				strOpers.add(oper1);
				strOpers.add(oper2);
				Label func = new Label("__stringCat");
				reg2 = new Reg("R" + regCount++);
				binOpNode = new LibraryCall(func, strOpers, (Reg)reg2);
			}
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
	private int getRegNum(String reg){
		return Integer.parseInt(reg.substring(1));
	}
}
