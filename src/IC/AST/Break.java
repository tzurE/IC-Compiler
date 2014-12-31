package IC.AST;

import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Break statement AST node.
 * 
 * @author Tovi Almozlino
 */
public class Break extends Statement {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	public Object accept(PropVisitor visitor,SymbolTable table) {
		this.setScope(table);
		return visitor.visit(this,table);
	}

	/**
	 * Constructs a break statement node.
	 * 
	 * @param line
	 *            Line number of break statement.
	 */
	public Break(int line) {
		super(line);
	}
	
	@Override
	public Object accept(LirTranslatorVisitor visitor, int regNum) {
		return visitor.visit(this, regNum);
	}

}
