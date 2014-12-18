package SemanticCheckerVisitor;

import com.sun.xml.internal.ws.spi.db.MethodSetter;

import IC.AST.*;
import SymbolTables.*;
import TypeTable.*;

public class SymbolVisitorBuilder implements PropVisitor{

	private String prog_name;
	private boolean mainExists = false;
	
	public SymbolVisitorBuilder(String prog_name){
		this.prog_name = prog_name;
	}
	
	@Override
	public Object visit(Program program, SymbolTable table) {
		
		//no father - it is the main table!
		SymbolTable symbol_table = new GlobalSymbolTable(prog_name, null);
		for (ICClass icClass : program.getClasses()){
			//we start thhe type table!
			TypeTable.TypeTableInit();
			//we create the class as a type in the type table
			TypeTableType classt = TypeTable.classType(icClass);
			
			//for each class in the program, we add an entry in the global symbol table
			symbol_table.addEntry(icClass.getName(), new ClassEntry(icClass.getName(), classt), icClass.getLine() );
			if(!icClass.hasSuperClass()){
				symbol_table.addChild(icClass.getName(), new ClassSymbolTable(icClass.getName(), symbol_table));
				icClass.accept(this, symbol_table);
			}
			else{
				if(icClass.getName().equals(icClass.getSuperClassName())){
					try {
						throw new SemanticError(icClass.getLine(),"the class '" + icClass.getName() + "' extends itself!");
					}
					catch (SemanticError e) {
						System.out.println(e.getErrorMessage());
						System.exit(-1);
					}

				}
				
				GlobalSymbolTable symbol_table1 = (GlobalSymbolTable) symbol_table;		
				ClassSymbolTable superClass = symbol_table1.findInnerChild(icClass.getSuperClassName());
				//symbol_table1.getChildTableList().get(icClass.getSuperClassName()).addChild(icClass.getName(), new ClassSymbolTable(icClass.getName(), symbol_table1.getChildTableList().get(icClass.getSuperClassName())));
				//System.out.println(superClass.getId());
				if(superClass == null){
					//if a class is being extended before being declared
					try {
						throw new SemanticError(icClass.getLine(),
								"the class '"
										+ icClass.getSuperClassName()
										+ "' Is not declared yet! first declare, then extend it");
					}
					catch (SemanticError e) {
						System.out.println(e.getErrorMessage());
						System.exit(-1);
					}
					
				}
				superClass.addChild(icClass.getName(), new ClassSymbolTable(icClass.getName(), superClass));
				icClass.accept(this, superClass);
			}
			
			
		}
		
		return symbol_table;
	}

	@Override
	public Object visit(ICClass icClass, SymbolTable parent_table) {
		StaticMethod method1;
		VirtualMethod method2;
		
		ClassSymbolTable symbol_table = new ClassSymbolTable(icClass.getName(), parent_table);
		
		for (Field field : icClass.getFields()){
			field.accept(this, symbol_table);
		}
		for (Method method : icClass.getMethods()){
			if(method.getClass().toString().toLowerCase().equals("staticmethod") || method.getClass().toString().toLowerCase().equals("librarymethod")){
				method1 = (StaticMethod)method;
				method1.accept(this, symbol_table);
			}
			else if(method.getClass().toString().toLowerCase().equals("virtualmethod")){
				method2 = (VirtualMethod)method;
				method2.accept(this, symbol_table);
			}
		}
		
		return null;
	}

	@Override
	public Object visit(Field field, SymbolTable parent_table) {
//		if(field.getType().getClass() == PrimitiveType.class){
//			parent_table.addEntry(field.getName(), new FieldEntry(field.getName(),this.getTypesForPrimitiveType((PrimitiveType) field.getType())), field.getLine());
//		}
//		else{
//			if(field.getType().getDimension()==0)
//				parent_table.addEntry(field.getName(), new FieldEntry(field.getName(),new ClassType(field.getType().getName())), field.getLine());
//			else
//				parent_table.addEntry(field.getName(), new FieldEntry(field.getName(), arrayTypeHelper(field.getType())), field.getLine());
//		}
//		field.getType().accept(this, parent_table);
		return null;
	}

	@Override
	public Object visit(VirtualMethod method, SymbolTable parent_table) {
		if(method.getName().toLowerCase().equals("main")){
			try {
				throw new SemanticError(method.getLine(),
						"the main class must be a static class, change it");
			}
			catch (SemanticError e) {
				System.out.println(e.getErrorMessage());
				System.exit(-1);
			}
		}
		parent_table.addEntry(method.getName(), new MethodSymbole(method.getName(),SymbolKinds.VIRTUAL_METHOD,), line);
		
		
		return null;
	}

	@Override
	public Object visit(StaticMethod method, SymbolTable table) {

		
		
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

	@Override
	public Object visit(Method method, SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
