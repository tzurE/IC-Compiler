package IC.AST;

import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Array length expression AST node.
 * 
 * @author Tovi Almozlino
 */
public class Length extends Expression {

	private Expression array;

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
	 * Constructs a new array length expression node.
	 * 
	 * @param array
	 *            Expression representing an array.
	 */
	public Length(Expression array) {
		super(array.getLine());
		this.array = array;
	}

	public Expression getArray() {
		return array;
	}

}
