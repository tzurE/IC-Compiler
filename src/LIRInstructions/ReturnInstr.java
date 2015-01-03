package LIRInstructions;

/** A 'Return' instruction.
 */
public class ReturnInstr extends Instruction {
	public final Operand dst;
	
	public ReturnInstr(Operand dst) {
		this.dst = dst;
	}
	
	public String toString() {
		return "Return " + dst;
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}
}