package IC.AST;

import IC.BinaryOps;
import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Logical binary operation AST node.
 * 
 * @author Tovi Almozlino
 */
public class LogicalBinaryOp extends BinaryOp {

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
	 * Constructs a new logical binary operation node.
	 * 
	 * @param operand1
	 *            The first operand.
	 * @param operator
	 *            The operator.
	 * @param operand2
	 *            The second operand.
	 */
	public LogicalBinaryOp(Expression operand1, BinaryOps operator,
			Expression operand2) {
		super(operand1, operator, operand2);
	}

}
