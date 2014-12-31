package IC.AST;

import IC.BinaryOps;
import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Mathematical binary operation AST node.
 * 
 * @author Tovi Almozlino
 */
public class MathBinaryOp extends BinaryOp {

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
	 * Constructs a new mathematical binary operation node.
	 * 
	 * @param operand1
	 *            The first operand.
	 * @param operator
	 *            The operator.
	 * @param operand2
	 *            The second operand.
	 */
	public MathBinaryOp(Expression operand1, BinaryOps operator, Expression operand2) {
		super(operand1, operator, operand2);
	}

}
