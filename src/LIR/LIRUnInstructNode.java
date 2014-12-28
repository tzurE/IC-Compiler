package LIR;

public class LIRUnInstructNode extends LIRNode{

	private String bin_instruction;
	private String op;
	
	public LIRUnInstructNode(LIRNodeTypes type, String bin_instruction, String op) {
		super(type);
		this.setBin_instruction(bin_instruction);
		this.setOp(op);
	}

	public String getBin_instruction() {
		return bin_instruction;
	}

	public void setBin_instruction(String bin_instruction) {
		this.bin_instruction = bin_instruction;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}
	
}
