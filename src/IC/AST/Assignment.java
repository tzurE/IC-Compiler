package IC.AST;

import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Assignment statement AST node.
 * 
 * @author Tovi Almozlino
 */
public class Assignment extends Statement {

	private Location variable;

	private Expression assignment;

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
	 * Constructs a new assignment statement node.
	 * 
	 * @param variable
	 *            Variable to assign a value to.
	 * @param assignment
	 *            Value to assign.
	 */
	public Assignment(Location variable, Expression assignment) {
		super(variable.getLine());
		this.variable = variable;
		this.assignment = assignment;
	}

	public Location getVariable() {
		return variable;
	}

	public Expression getAssignment() {
		return assignment;
	}

}
