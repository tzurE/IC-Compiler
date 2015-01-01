package LIR;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import SymbolTables.SymbolTable;
import IC.AST.*;
import LIRInstructions.LIRNode;
import LIRInstructions.Label;

public class LirTranslatorVisitor implements LirVisitor{

	private String NEW_LINE = "\n";
	private Map<String, ClassLayout> classLayouts;
	private List<LIRNode> LIRProgram;
	private List<LIRNode> temp_Program;
	private List<LIRNode> StringLiterals;
	private List<LIRNode> main_ic;
	private int regCount1;

	public LirTranslatorVisitor() {
		this.StringLiterals = new LinkedList<LIRNode>();
		this.main_ic = new LinkedList<LIRNode>();
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
		//here we need to concatenate the 3 lists - literals, program, main. main has to be last!
		//we maybe wont add the main list!!!!!!!!!!!!!!! we found a solution!!!!!!
		StringLiterals.addAll(temp_Program);
		StringLiterals.addAll(main_ic);
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
		StringBuilder str = new StringBuilder();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CallStatement callStatement, int regCount) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VariableLocation location, int regCount) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
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
}
