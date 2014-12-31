package IC.AST;

import java.util.List;

import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Static method AST node.
 * 
 * @author Tovi Almozlino
 */
public class StaticMethod extends Method {

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
	 * Constructs a new static method node.
	 * 
	 * @param type
	 *            Data type returned by method.
	 * @param name
	 *            Name of method.
	 * @param formals
	 *            List of method parameters.
	 * @param statements
	 *            List of method's statements.
	 */
	public StaticMethod(Type type, String name, List<Formal> formals,
			List<Statement> statements) {
		super(type, name, formals, statements);
	}

}
