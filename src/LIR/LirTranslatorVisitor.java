package LIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import SemanticCheckerVisitor.SymbolVisitorChecker;
import SymbolTables.GlobalSymbolTable;
import SymbolTables.SymbolEntry;
import SymbolTables.SymbolKinds;
import SymbolTables.SymbolTable;
import SymbolTables.ClassSymbolTable;
import SymbolTables.MethodSymbolTable; 
import SymbolTables.SymbolTableType;
import TypeTable.TypeIDs;
import TypeTable.TypeTableType;
import IC.BinaryOps;
import LIRInstructions.UnaryOpInstr; 
import IC.LiteralTypes;
import IC.AST.*;
import LIRInstructions.ArrayLengthInstr;
import LIRInstructions.BinOpInstr;
import LIRInstructions.CompareInstr;
import LIRInstructions.Cond;
import LIRInstructions.CondJumpInstr;
import LIRInstructions.Immediate;
import LIRInstructions.JumpInstr;
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
import LIRInstructions.StringLiteral;

public class LirTranslatorVisitor implements LirVisitor{
	
	private GlobalSymbolTable global;
	private String NEW_LINE = "\n";
	private Map<String, ClassLayout> classLayouts;
	private List<LIRNode> LIRProgram;
	private List<LIRNode> temp_Program;
	private List<LIRNode> StringLiterals;
	private int numStringLiterals;
	private boolean isReturnExist; 
	private int numOfWhiles;
	private int numOfIfs;
	private int numOfCmps;
	
	//private static String trueCondLbl = "_true_cond_label";
	private static String falseCondLbl = "_false_cond_label";
	private static String endCondLbl = "_end_cond_label";
	private static String trueCmpLbl = "_true_cmp_label";
	private static String falseCmpLbl = "_false_cmp_label";
	private static String endCmpLbl = "_end_cmp_label";
	private static String startWhileLbl = "_start_while_label";
	private static String endWhileLbl = "_end_while_label";

	public LirTranslatorVisitor(GlobalSymbolTable global) {
		this.StringLiterals = new LinkedList<LIRNode>();
		this.numStringLiterals = 0;
		this.global = global;
		this.isReturnExist = false;
		this.numOfWhiles = 0;
		this.numOfIfs = 0;
		this.numOfCmps = 0;
		//this.numEndLabel = 0;
		//this.numTrueLabel = 0;
		//this.numFalseLabel = 0;
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
				strbld.append(icClassLayout.printFieldOffsets());
			}
		}
		((Label)temp_Program.get(0)).setName(strbld.toString());
		
		for(ICClass icClass : program.getClasses()) {
			
			if(icClass.getName() != "Library"){
				icClass.accept(this, regCount);
				temp_Program.add(new Label(""));
			}
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
		// Nothing to do 
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
			LIRNode ic_main = new Label("_ic_main:");
			temp_Program.add(ic_main);
			methodToLir(method, regCount);
			
			List<Operand> lstOopers = new ArrayList<Operand>();
			Operand oper = new Immediate(0);
			lstOopers.add(oper);
			
			LIRNode ic_main_end = new LibraryCall(new Label("__exit"), lstOopers, new Reg("Rdummy"));
			temp_Program.add(ic_main_end);
		}
		return null;
	}

	@Override
	public Object visit(LibraryMethod method, int regCount) {
		// Nothing to do 
		return null;
	}

	@Override
	public Object visit(Formal formal, int regCount) {
		// Nothing to do 
		return null;
	}

	@Override
	public Object visit(PrimitiveType type, int regCount) {
		// Nothing to do 
		return null;
	}

	@Override
	public Object visit(UserType type, int regCount) {
		// Nothing to do 
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
				LIRNode move = new MoveInstr(new Memory(arr), reg);
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
				if(reg == null)
					reg = new Reg("R" + regCount++);
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
			boolean field = false;
			LIRNode move2 = null;
			Operand reg1 = null,  op = null, reg3= null, mem = null, reg2 = null;
			//one line to check
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
				TypeTableType classType = (TypeTableType)varLocation.getLocation().accept(new SymbolVisitorChecker(this.global),varLocation.getScope());
				cName = classType.getName();
				op = (Operand) varLocation.getLocation().accept(this, regCount);
				if(!this.isRegister(op.toString())){
					reg3 = new Reg("R" + regCount++);
					move2 = new MoveInstr(op, reg3);
					temp_Program.add(move2);
					op = reg3;
				}
			}
			else{//not external - meaning we need to do "this"
				SymbolTable definingScope;
				definingScope = varLocation.getScope().get_defining_scope_for_var(Name, varLocation.getLine(), null);
				Name = "v" + definingScope.getUniqueId() + Name;
				if(definingScope.getClass() == ClassSymbolTable.class){
					//it's a field
					cName = definingScope.getId();
					if(this.classLayouts.get(cName).getFieldByName().containsKey(varLocation.getName())){
						field = true;
						reg2 = new Reg("R" + regCount++);
						mem = new Memory("this");
						temp_Program.add(new MoveInstr(mem, reg2));
					}	
				}
			}
			if(field){
				ClassLayout cl = this.classLayouts.get(cName);
				int fieldOffset = cl.getFieldOffsetByName().get(varLocation.getName());
				if(op == null && reg2 != null){
					op = reg2;
				}
				LIRNode move1 = new MoveFieldInstr(op, new Immediate(fieldOffset), reg1, false);
				temp_Program.add(move1);
			}
			else{//local!
				Operand name = new Memory(Name);
				move2 = new MoveInstr(reg1, name);
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
		if(returnStatement.getValue() != null){
			Operand oper = (Operand)returnStatement.getValue().accept(this, regCount);
			if(oper == null){
				oper = new Memory("Rdummy");
			}
			LIRNode retInst = new ReturnInstr(oper);
			temp_Program.add(retInst);
		}
		else{
			temp_Program.add(new ReturnInstr(new Reg("Rdummy")));
		}
		
		return null;
	}

	@Override
	public Object visit(If ifStatement, int regCount) {

		int localNumIf = ++this.numOfIfs;
		temp_Program.add(new Label(NEW_LINE + "#begin_if"));
		Operand condOper = (Operand)ifStatement.getCondition().accept(this, regCount);
		
		if (!isRegister(condOper.toString())){
			temp_Program.add(new MoveInstr(condOper, new Reg("R" + regCount)));
			condOper = new Reg("R" + regCount);
		}
		
		temp_Program.add(new CompareInstr(new Immediate(0), condOper));
		
		if(ifStatement.hasElse()){
		
			temp_Program.add(new CondJumpInstr(new Label(LirTranslatorVisitor.falseCondLbl + localNumIf), Cond.True));
			ifStatement.getOperation().accept(this, regCount);
			temp_Program.add(new JumpInstr(new Label(LirTranslatorVisitor.endCondLbl + localNumIf)));
			temp_Program.add(new Label(LirTranslatorVisitor.falseCondLbl + localNumIf + ":"));
			ifStatement.getElseOperation().accept(this, regCount);
			temp_Program.add(new Label(LirTranslatorVisitor.endCondLbl + localNumIf + ":"));
		}
		else{	
			temp_Program.add(new CondJumpInstr(new Label(LirTranslatorVisitor.endCondLbl + localNumIf), Cond.True));
			ifStatement.getOperation().accept(this, regCount);
			temp_Program.add(new Label(LirTranslatorVisitor.endCondLbl + localNumIf + ":"));
		}
		
		temp_Program.add(new Label("#end_if"));
		
		return null;
	}

	@Override
public Object visit(While whileStatement, int regCount) {
		
		int localNumOfWhiles = ++this.numOfWhiles;
		
		temp_Program.add(new Label(LirTranslatorVisitor.startWhileLbl + localNumOfWhiles + ":"));

		// Translating loop's condition
		Operand condOper = (Operand) whileStatement.getCondition().accept(this, regCount);
		
		temp_Program.add(new CompareInstr(new Immediate(0), condOper));
		temp_Program.add(new CondJumpInstr(new Label(LirTranslatorVisitor.endWhileLbl + localNumOfWhiles), Cond.True));
		
		whileStatement.getOperation().accept(this, regCount);
		temp_Program.add(new JumpInstr(new Label(LirTranslatorVisitor.startWhileLbl + localNumOfWhiles)));
		temp_Program.add(new Label(LirTranslatorVisitor.endWhileLbl + localNumOfWhiles + ":"));
		
		return null;
	}

	@Override
	public Object visit(Break breakStatement, int regCount) {
		
		LIRNode jump_inst = new JumpInstr(new Label(LirTranslatorVisitor.endWhileLbl + this.numOfWhiles));
		temp_Program.add(jump_inst);
		return null;
	}

	@Override
	public Object visit(Continue continueStatement, int regCount) {

		return null;
	}

	@Override
	public Object visit(StatementsBlock statementsBlock, int regCount) {

		// Tranlate all enclosed statements of block
		for(Statement stmnt : statementsBlock.getStatements()){
			stmnt.accept(this, regCount);
		}
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable, int regCount) {
		String lName = "v" + localVariable.getScope().getUniqueId() + localVariable.getName();
		LIRNode move = null;
		
		
		Operand localVar = new Memory(lName);
		
		if (localVariable.hasInitValue()){
			Operand oper1 = (Operand)localVariable.getInitValue().accept(this, regCount);
			
			if(!this.isRegister(oper1.toString())){
				Operand tmpReg = new Reg("R" + regCount); 
				move = new MoveInstr(oper1, tmpReg);
				temp_Program.add(move);
				move = new MoveInstr(tmpReg, localVar);
				temp_Program.add(move);
			}
			else{
				move = new MoveInstr(oper1, localVar);
				temp_Program.add(move);				
			}
		}
		return localVar;
	}

	@Override
	public Object visit(VariableLocation location, int regCount) {
		String name = location.getName();
		String className = "className"; //sign for us if its init
		String class_t = "class_t"; //sign for us if its init
		LIRNode move = null;
		Operand reg1 = null;
		Operand reg2 = null;
		Operand op = null;
		boolean field = false;
		if(location.isExternal()){
			field = true;
			TypeTableType	classType = (TypeTableType)location.getLocation().accept(new SymbolVisitorChecker(this.global), location.getScope());
			className = classType.getName();
			 op = (Operand) location.getLocation().accept(this, regCount); /// we don't know what's happening here - go deeper
			 if(!this.isRegister(op.toString())){
				 Operand reg = new Reg("R" +regCount++);
				 temp_Program.add(new MoveInstr(op, reg));
				 class_t = reg.toString();
			 }	
			 else{
				 regCount++;
				 class_t = op.toString();
			 }
		}
		else{
			SymbolTable definingScope;
			definingScope = location.getScope().get_defining_scope_for_var(name, location.getLine(), null);
			SymbolEntry sym = definingScope.searchForVar(location.getName(), location.getLine());
			if(sym != null && sym.getKind().equals(SymbolKinds.PARAMETER)){
				name = location.getName();
			}
			else{
				name = "v" + definingScope.getUniqueId() + name;
			}
			//get the class where it defined
//			for(classScope = definingScope; classScope.getClass() != ClassSymbolTable.class; classScope = classScope.getFather_table());
//			className = classScope.getId();
			SymbolTable classScope = location.getScope();
			while(!(classScope.getType().compareTo(SymbolTableType.CLASS) == 0)){
				if(classScope.getType().compareTo(SymbolTableType.STATEMENT) == 0){
					if(classScope.getEntry(location.getName(), SymbolKinds.LOCAL_VARIABLE)!=null){
						return new Memory(name);
					}
				}
				if(classScope.getType().compareTo(SymbolTableType.STATIC_METHOD) == 0 || classScope.getType().compareTo(SymbolTableType.VIRTUAL_METHOD) == 0 ){
					if(classScope.getEntry(location.getName(), SymbolKinds.LOCAL_VARIABLE)!=null || classScope.getEntry(location.getName(), SymbolKinds.PARAMETER)!=null){
						return new Memory(name);
					}
				}
				
				
				classScope = classScope.getFather_table();
			}
			
			className = classScope.getId();
			/* from assignment
			if(this.classLayouts.get(cName).getFieldByName().containsKey(Name)){
				field = true;
				reg2 = new Reg("R" + regCount++);
				mem = new Memory("this");
				temp_Program.add(new MoveInstr(mem, reg2));
			}
			*/				
			// Check if is a field. do we need this?
			if(this.classLayouts.get(className).getFieldByName().containsKey(location.getName())){
				field = true;
				reg1 = new Memory("this");
				reg2 = new Reg("R" + regCount++);
				move = new MoveInstr(reg1, reg2);
				temp_Program.add(move);
				class_t = reg2.toString();
			}
			else
				return new Memory(name);
		}
		if(field){
			ClassLayout clay = this.classLayouts.get(className);
			int fieldOffset = clay.getFieldOffsetByName().get(location.getName());
			Operand reg3 = new Reg("R" + regCount++);
			
			LIRNode movef = new MoveFieldInstr(new Memory(class_t), new Immediate(fieldOffset), reg3, true);
			temp_Program.add(movef);
			return reg3;
		}
		
		return null;
	}

	@Override
	public Object visit(ArrayLocation location, int regCount) {
		Operand arr = (Operand)location.getArray().accept(this, regCount);
		Operand index, reg1 = null, reg2 = null, indexreg = null;;
		LIRNode move1, move, move2 = null;
		index = (Operand) location.getIndex().accept(this, regCount);
		if(!this.isRegister(index.toString())){
			indexreg = new Reg("R" + regCount++);
			move2 = new MoveInstr(index,indexreg);
			temp_Program.add(move2);
			index = indexreg;
		}
		if(!this.isRegister(arr.toString())){
			reg1 = new Reg("R"+regCount++);
			move = new MoveInstr(arr, reg1);
			temp_Program.add(move);
			reg2 = new Reg("R"+regCount++);
			 move1 = new MoveArrayInstr(reg1, index, reg2, true);
			temp_Program.add(move1);
		}
		else{
			regCount++;
			reg2 = new Reg("R"+regCount++);
			move1 = new MoveArrayInstr(arr, index, reg2, true);
			temp_Program.add(move1);
		}

		return reg2;
	}

	@Override
	public Object visit(StaticCall call, int regCount) {
		LIRNode callNode = null;
		Operand reg = null, reg3 = null;
		Operand callParamOper = null;
		Label func = null;
		 	
		// Static call is a library method
		if(call.getClassName().equals("Library")){
			List<Operand> strOpers = new ArrayList<Operand>();
			ClassSymbolTable classSymbolTable = this.global.getChildTableList().get(call.getClassName());

			// Collect all params of call
			for(Expression callParam : call.getArguments()){		
				callParamOper = (Operand) callParam.accept(this, regCount);
				if(!this.isRegister(callParamOper.toString())){
					reg3 = new Reg("R" + regCount++);
					temp_Program.add(new MoveInstr(callParamOper, reg3));
					callParamOper = reg3;
				}
				strOpers.add(callParamOper);
			}
			
			// Creates new Static call instruction
			func = new Label("__" + call.getName());
			if(call.getName().startsWith("print"))
				reg = new Reg("Rdummy");
			else
				reg = new Reg("R" + regCount++);
			callNode = new LibraryCall(func, strOpers, (Reg)reg);
		}
		// Not a library method
		else{
			
			List<ParamOpPair> paramPairs = new ArrayList<ParamOpPair>();
			ClassSymbolTable classSymbolTable = this.global.findInnerChild(call.getClassName());
			MethodSymbolTable methodST = (MethodSymbolTable) classSymbolTable.getMethodChildTableList().get(call.getName());
			int numFormals = 1;
			ParamOpPair pop = null; 
			
			// Gets formal params 
			HashMap<Integer, String> formals = methodST.getParametersByOrder();
			
			// Collect all params of call
			for(Expression callParam : call.getArguments()){
				//"v" + classSymbolTable.getUniqueId() + formals.get(numFormals-1)
				Memory mem = new Memory(formals.get(numFormals-1));
				Operand value = (Operand)callParam.accept(this, regCount);
				if(!this.isRegister(value.toString())){
					reg3 = new Reg("R" + regCount++);
					temp_Program.add(new MoveInstr(value, reg3));
					value = reg3;
				}
				pop = new ParamOpPair(mem, value);
				paramPairs.add(pop);
				numFormals++;
			}
			
			// Creates new Static call instruction
			func = new Label("_" + call.getClassName() + "_" + call.getName());
			reg = new Reg("R" + regCount++);
			callNode = new LIRInstructions.StaticCall(func, paramPairs, (Reg)reg);
		}
		
		temp_Program.add(callNode);			
		return reg;
	}

	@Override
	public Object visit(VirtualCall call, int regCount) {
		
		// The virtual method to be called 
		String methodName = call.getName();
		String	className = "";
		Operand loc = null, reg1 = null, reg2 = null, reg3 = null;

		if(call.isExternal()){

			// we get here the method's class name	
			TypeTableType classType = (TypeTableType)call.getLocation().accept(new SymbolVisitorChecker(this.global), call.getScope());
			className = classType.getName();

			// Gets the the address of the receiver object  
			loc = (Operand)call.getLocation().accept(this, regCount);
			if(!this.isRegister(loc.toString())){
				reg1 = new Reg("R" + regCount++);
				temp_Program.add(new MoveInstr(loc, reg1));
				loc = reg1;
			}
		}
		else{
			SymbolTable scope = call.getScope();
			while(!(scope.getType().equals(SymbolTableType.CLASS))){
				scope = scope.getFather_table();
			}
			
			className = scope.getId();
			reg2 = new Reg("R" + regCount++);
			temp_Program.add(new MoveInstr(new Memory("this"), reg2));
			loc = reg2;
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
		
		//get the method offset from the dispatch
		ClassLayout clay = this.classLayouts.get(className);
		String method_str = clay.methodOverride(methodName, className);
		int methodoff = clay.getMethodOffsets().get(method_str);
		Method m = clay.getMethodByName().get(method_str);
		
		
		List<Formal> formals = m.getFormals();
		int numFormals = 0;
		ParamOpPair pop = null; 
		List<ParamOpPair> paramPairs = new ArrayList<ParamOpPair>();
		
		// Collect all params of call
		for(Expression callParam : call.getArguments()){
			int method_unique =  global.findUniqueId(m.getScope().getId(), call.getName(), SymbolTableType.VIRTUAL_METHOD);
			//"v"+method_unique+formals.get(numFormals).getName()
			Memory mem = new Memory(formals.get(numFormals).getName());
			Operand value = (Operand)callParam.accept(this, regCount);
			if(!this.isRegister(value.toString())){
				reg3 = new Reg("R" + regCount++);
				temp_Program.add(new MoveInstr(value, reg3));
				value = reg3;
			}
			pop = new ParamOpPair(mem, value);
			paramPairs.add(pop);
			numFormals++;
		}
		//Operand func = new Label("_" + className + "_" + call.getName());
		Operand reg = new Reg("R" + regCount++);
		LIRNode callNode = new LIRInstructions.VirtualCall((Reg) loc, new Immediate(methodoff), paramPairs, (Reg)reg);
		temp_Program.add(callNode);
		return reg;
	}

	@Override
	public Object visit(This thisExpression, int regCount) {

		// Move 'this' to reg
		Operand reg = new Reg("R" + regCount);
		LIRNode move = new MoveInstr(new Memory("this"), reg);
		temp_Program.add(move);
		
		return reg;
	}

	@Override
	public Object visit(NewClass newClass, int regCount) {
		
		ClassLayout cClassLayout = this.classLayouts.get(newClass.getName());
		
		// Create AllocateObject call
		Operand cSize = new Immediate(cClassLayout.getDispatchTableSize());
		Operand reg1 = new Reg("R" + regCount++);
		Label func = new Label("__allocateObject");
		List<Operand> opers = new ArrayList<Operand>();
		opers.add(cSize);
		
		// Create MoveField Node
		LIRNode classAlloc = new LibraryCall(func, opers, (Reg)reg1); 
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
		Operand reg = null;
		Reg reg1 = new Reg("R" + regCount++);
		int size = 0;
		if(oper_size.toString().startsWith("v")){
			reg = new Reg("R"+regCount++);
			temp_Program.add(new MoveInstr(oper_size, reg));
			temp_Program.add(new BinOpInstr(new Immediate(4), reg, Operator.MUL));
			oper_size = reg;
			
		}
		else {
			size = Integer.parseInt(oper_size.toString())*4;
			oper_size = new Immediate(size);
		}
		

		// Create AllocateArray library call 
		List<Operand> strOpers = new ArrayList<Operand>();
		strOpers.add(oper_size);
		Label func = new Label("__allocateArray");
		
		allocate = new LibraryCall(func, strOpers, reg1);
		temp_Program.add(allocate);
		return reg1;
	}

	@Override
	//////// MUST CHECK IF IT'S OK !!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public Object visit(Length length, int regCount) {
		
		// Create ArrayLength Instruction
		Operand arrLength = (Operand)length.getArray().accept(this, regCount);
		Operand reg = new Reg("R" + regCount++);
		LIRNode arryLenInst = new ArrayLengthInstr(arrLength, (Reg)reg);
		temp_Program.add(arryLenInst);

		return reg;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp, int regCount) {

		Operand oper1 = (Operand)binaryOp.getFirstOperand().accept(this, regCount);
		Operand oper2 = null; //(Operand)binaryOp.getSecondOperand().accept(this, regCount);
		LIRNode move = null;
		LIRNode binOpNode = null;
					
		
		// Added for verification!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if (isRegister(oper1.toString())){ 
			regCount = Integer.parseInt(oper1.toString().substring(1)) + 1;
			oper2 = (Operand)binaryOp.getSecondOperand().accept(this, regCount++);			
		}
		else{ 
			move = new MoveInstr(oper1, new Reg("R" + regCount));
			temp_Program.add(move);
			oper1 = new Reg("R" + regCount++);
			oper2 = (Operand)binaryOp.getSecondOperand().accept(this, regCount);
		}
		
		if (!isRegister(oper2.toString())){ 			 
			move = new MoveInstr(oper2, new Reg("R" + regCount));
			temp_Program.add(move);
			oper2 = new Reg("R" + regCount++);
		}
		// Added for verification!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		
		if(binaryOp.getOperator().getOperatorString().equals(BinaryOps.PLUS.getOperatorString())){
			TypeTableType operandsType = (TypeTableType)binaryOp.getFirstOperand().accept (new SymbolVisitorChecker(this.global), binaryOp.getScope());
			if (operandsType.getId() == TypeIDs.INT){
				binOpNode = new BinOpInstr(oper2, oper1, Operator.ADD);
			}
			
			else if (operandsType.getId() == TypeIDs.STRING){
				List<Operand> strOpers = new ArrayList<Operand>();
				strOpers.add(oper1);
				strOpers.add(oper2);
				Label func = new Label("__stringCat");
				//reg2 = new Reg("R" + regCount++);
				binOpNode = new LibraryCall(func, strOpers, (Reg)oper1);
			}
			temp_Program.add(binOpNode);
		}
		else if (binaryOp.getOperator().getOperatorString().equals(BinaryOps.MINUS.getOperatorString())){
			binOpNode = new BinOpInstr(oper2, oper1, Operator.SUB);
			temp_Program.add(binOpNode);
		}
		else if (binaryOp.getOperator().getOperatorString().equals(BinaryOps.MULTIPLY.getOperatorString())){
			binOpNode = new BinOpInstr(oper2, oper1, Operator.MUL);
			temp_Program.add(binOpNode);
		}
		else if (binaryOp.getOperator().getOperatorString().equals(BinaryOps.DIVIDE.getOperatorString())){
			binOpNode = new BinOpInstr(oper2, oper1, Operator.DIV);
			temp_Program.add(binOpNode);
		}
		else if (binaryOp.getOperator().getOperatorString().equals(BinaryOps.MOD.getOperatorString())){
			binOpNode = new BinOpInstr(oper2, oper1, Operator.MOD);
			temp_Program.add(binOpNode);
		}
		
		return oper1;
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp, int regCount) {

		Operand oper1 = (Operand) binaryOp.getFirstOperand().accept(this,
				regCount);
		Operand oper2 = null;
		LIRNode move = null;
		int localNumOfCmp = ++this.numOfCmps;

		if (isRegister(oper1.toString())) {
			regCount = Integer.parseInt(oper1.toString().substring(1)) + 1;
			oper2 = (Operand) binaryOp.getSecondOperand().accept(this,
					regCount++);
		} else {
			move = new MoveInstr(oper1, new Reg("R" + regCount));
			temp_Program.add(move);
			oper1 = new Reg("R" + regCount++);
			oper2 = (Operand) binaryOp.getSecondOperand()
					.accept(this, regCount);
		}

		if (!isRegister(oper2.toString())) {
			move = new MoveInstr(oper2, new Reg("R" + regCount));
			temp_Program.add(move);
			oper2 = new Reg("R" + regCount++);
		}

		LIRNode cmp = null;

		if (binaryOp.getOperator().compareTo(BinaryOps.LOR) != 0
				&& binaryOp.getOperator().compareTo(BinaryOps.LAND) != 0) {
			cmp = new CompareInstr(oper2, oper1);
			temp_Program.add(cmp);
		}

		if (binaryOp.getOperator().compareTo(BinaryOps.EQUAL) == 0) {
			temp_Program
					.add(new CondJumpInstr(new Label(
							LirTranslatorVisitor.trueCmpLbl + localNumOfCmp),
							Cond.True));
		} else if (binaryOp.getOperator().compareTo(BinaryOps.NEQUAL) == 0) {
			temp_Program.add(new CondJumpInstr(new Label(
					LirTranslatorVisitor.trueCmpLbl + localNumOfCmp),
					Cond.False));
		} else if (binaryOp.getOperator().compareTo(BinaryOps.GT) == 0) {
			temp_Program.add(new CondJumpInstr(new Label(
					LirTranslatorVisitor.trueCmpLbl + localNumOfCmp), Cond.G));
		} else if (binaryOp.getOperator().compareTo(BinaryOps.GTE) == 0) {
			temp_Program.add(new CondJumpInstr(new Label(
					LirTranslatorVisitor.trueCmpLbl + localNumOfCmp), Cond.GE));
		} else if (binaryOp.getOperator().compareTo(BinaryOps.LT) == 0) {
			temp_Program.add(new CondJumpInstr(new Label(
					LirTranslatorVisitor.trueCmpLbl + localNumOfCmp), Cond.L));
		} else if (binaryOp.getOperator().compareTo(BinaryOps.LTE) == 0) {
			temp_Program.add(new CondJumpInstr(new Label(
					LirTranslatorVisitor.trueCmpLbl + localNumOfCmp), Cond.LE));
		} else if (binaryOp.getOperator().compareTo(BinaryOps.LAND) == 0) {

			temp_Program.add(new CompareInstr(new Immediate(0), oper1));
			temp_Program
					.add(new CondJumpInstr(new Label(
							LirTranslatorVisitor.endCmpLbl + localNumOfCmp),
							Cond.True));
			temp_Program.add(new BinOpInstr(oper2, oper1, Operator.AND));
			temp_Program.add(new Label(LirTranslatorVisitor.endCmpLbl
					+ localNumOfCmp + ":"));
			return oper1;
		} else if (binaryOp.getOperator().compareTo(BinaryOps.LOR) == 0) {

			temp_Program.add(new CompareInstr(new Immediate(1), oper1));
			temp_Program
					.add(new CondJumpInstr(new Label(
							LirTranslatorVisitor.endCmpLbl + localNumOfCmp),
							Cond.True));
			temp_Program.add(new BinOpInstr(oper2, oper1, Operator.OR));
			temp_Program.add(new Label(LirTranslatorVisitor.endCmpLbl
					+ localNumOfCmp + ":"));
			return oper1;
		}

		temp_Program.add(new Label(LirTranslatorVisitor.falseCmpLbl
				+ localNumOfCmp + ":"));
		temp_Program.add(new MoveInstr(new Immediate(0), oper1));
		temp_Program.add(new JumpInstr(new Label(LirTranslatorVisitor.endCmpLbl
				+ localNumOfCmp)));
		temp_Program.add(new Label(LirTranslatorVisitor.trueCmpLbl
				+ localNumOfCmp + ":"));
		temp_Program.add(new MoveInstr(new Immediate(1), oper1));
		temp_Program.add(new Label(LirTranslatorVisitor.endCmpLbl
				+ localNumOfCmp + ":"));

		return oper1;
	}

	@Override
	public Object visit(MathUnaryOp unaryOp, int regCount) {
		
		Operand oper = (Operand)unaryOp.getOperand().accept(this, regCount);
		if(!this.isRegister(oper.toString())){
			LIRNode move = new MoveInstr(oper, new Reg("R" + ++regCount));
			temp_Program.add(move); 
		}

		Operand register = new Reg("R" + regCount);
		LIRNode unOpInst = new UnaryOpInstr(register, Operator.NEG);
		temp_Program.add(unOpInst);

		return register;
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp, int regCount) {

		int localNumOfCmp = ++this.numOfCmps;
		Operand oper = (Operand) unaryOp.getOperand().accept(this, regCount);
		if (!this.isRegister(oper.toString())) {

			LIRNode move = new MoveInstr(oper, new Reg("R" + regCount));
			temp_Program.add(move);
		}
		
		oper = new Reg("R" + regCount++);
		temp_Program.add(new CompareInstr(new Immediate(0), oper));
		temp_Program.add(new CondJumpInstr(new Label(LirTranslatorVisitor.trueCmpLbl + localNumOfCmp),Cond.True));
		temp_Program.add(new Label(LirTranslatorVisitor.falseCmpLbl	+ localNumOfCmp + ":"));
		temp_Program.add(new MoveInstr(new Immediate(0), oper));
		temp_Program.add(new JumpInstr(new Label(LirTranslatorVisitor.endCmpLbl + localNumOfCmp)));
		temp_Program.add(new Label(LirTranslatorVisitor.trueCmpLbl	+ localNumOfCmp + ":"));
		temp_Program.add(new MoveInstr(new Immediate(1), oper));
		temp_Program.add(new Label(LirTranslatorVisitor.endCmpLbl
				+ localNumOfCmp + ":"));		

		return oper;
	}

	@Override
	public Object visit(Literal literal, int regCount) {
		
		LIRNode oper = null;
		boolean isStrLitExist = false;
		String litType = literal.getType().getDescription();
		Object litVal = literal.getValue();
		
		if (literal.getType().getDescription().equals(LiteralTypes.INTEGER.getDescription())){
			int i = Integer.parseInt(literal.getValue().toString());
			oper = new Immediate(i);
			return oper;
		}
		else if (literal.getType().getDescription().equals(LiteralTypes.STRING.getDescription())){
			
			LIRNode strLit = new StringLiteral("str" + (numStringLiterals+1), literal.getValue().toString());
			
			for(LIRNode str : this.StringLiterals){
				if (((StringLiteral)str).getValue().equals(((StringLiteral)strLit).getValue())){
					((StringLiteral)strLit).setVar(((StringLiteral)str).getVar());
					isStrLitExist = true;
				}
			}
			
			if (!isStrLitExist){
				numStringLiterals++;
				StringLiterals.add(strLit);
			}
				
			oper = new Memory(((StringLiteral)strLit).getVar());
			return oper;
		}
		//LiteralTypes.
		else if (litType.equals(LiteralTypes.TRUE.getDescription()) && litVal.equals(LiteralTypes.TRUE.getValue())){
			oper = new Immediate(1);
		}
		else if (litType.equals(LiteralTypes.FALSE.getDescription()) && litVal.equals(LiteralTypes.FALSE.getValue())){
			oper = new Immediate(0);
		}
		else if (litType.equals(LiteralTypes.NULL) && litVal.equals(LiteralTypes.NULL.getValue())){
			oper = new Immediate(0);
		}
		
		return oper;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock, int regCount) {

		Operand oper = (Operand)expressionBlock.getExpression().accept(this, regCount);
		return oper;
	}

	private void methodToLir(Method method, int regCount) {
		
		LIRNode methodDec = null; 
		
		if(!method.getName().equals("main")){
			SymbolTable scope = method.getScope();
			String scope_name = scope.getId();
			methodDec = new Label("_" + scope_name + "_" + method.getName() + ":");
			temp_Program.add(methodDec);
		}
		
		for(Statement stmnt : method.getStatements()){
			stmnt.accept(this, regCount);
		}
		
		if (!method.getName().equals("main") && !this.isReturnExist){
			temp_Program.add(new ReturnInstr(new Reg("Rdummy")));
			this.isReturnExist = false;
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
