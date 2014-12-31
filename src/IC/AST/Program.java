package IC.AST;

import java.util.List;

import LIR.LirTranslatorVisitor;
import SymbolTables.*;

/**
 * Root AST node for an IC program.
 * 
 * @author Tovi Almozlino
 */
public class Program extends ASTNode {

	private List<ICClass> classes;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
	public Object accept(PropVisitor visitor,SymbolTable table) {
		this.setScope(table);
		return visitor.visit(this,table);
	} 

	public Object accept(LirTranslatorVisitor visitor, int regNum) {
		return visitor.visit(this, regNum);
	}
	
	/**
	 * Constructs a new program node.
	 * 
	 * @param classes
	 *            List of all classes declared in the program.
	 */
	public Program(List<ICClass> classes) {
		super(0);
		this.classes = classes;
	}

	public List<ICClass> getClasses() {
		return classes;
	}
	
	public void addLibraryClass(ICClass icClass) {
		this.classes.add(0,icClass);
	}

}
