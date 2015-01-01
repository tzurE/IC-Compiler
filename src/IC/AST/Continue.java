package IC.AST;

import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Continue statement AST node.
 * 
 * @author Tovi Almozlino
 */
public class Continue extends Statement {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	public Object accept(PropVisitor visitor,SymbolTable table) {
		this.setScope(table);
		return visitor.visit(this,table);
	}
	
	public Object accept(LirTranslatorVisitor visitor, int regCount) {
		return visitor.visit(this, regCount);
	}

	/**
	 * Constructs a continue statement node.
	 * 
	 * @param line
	 *            Line number of continue statement.
	 */
	public Continue(int line) {
		super(line);
	}

}
