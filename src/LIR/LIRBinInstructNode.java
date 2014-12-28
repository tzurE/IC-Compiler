package LIR;

public class LIRBinInstructNode extends LIRNode {

	private String bin_instruction;
	private String op1;
	private String op2;
	
	public LIRBinInstructNode(LIRNodeTypes type, String bin_instruction, String op1, String op2) {
		super(type);
		this.bin_instruction = bin_instruction;
		this.op1 = op1;
		this.op2 = op2;
	}

	public String getBin_instruction() {
		return bin_instruction;
	}

	public String getOp1() {
		return op1;
	}

	public String getOp2() {
		return op2;
	}

}
