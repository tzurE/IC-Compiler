package IC.AST;

import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Abstract AST node base class.
 * 
 * @author Tovi Almozlino
 */
public abstract class ASTNode {

	private int line;
	private SymbolTable symbolTable;

	/**
	 * Double dispatch method, to allow a visitor to visit a specific subclass.
	 * 
	 * @param visitor
	 *            The visitor.
	 * @return A value propagated by the visitor.
	 */
	public abstract Object accept(Visitor visitor);
	public abstract Object accept(PropVisitor visitor,SymbolTable table);
	public abstract Object accept(LirTranslatorVisitor visitor, int regCount);
	/**
	 * Constructs an AST node corresponding to a line number in the original
	 * code. Used by subclasses.
	 * 
	 * @param line
	 *            The line number.
	 */
	protected ASTNode(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}
	
	public void setScope(SymbolTable table) {
		this.symbolTable=table;
	}

	public SymbolTable getScope() {
		return symbolTable;
	}
	

}
