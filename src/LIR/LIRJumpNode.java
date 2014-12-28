package LIR;

public class LIRJumpNode extends LIRNode {
	
	private String jumpCmd;
	private String jumpLable;
	
	public LIRJumpNode(LIRNodeTypes type, String jumpCmd, String jumpLable) {
		super(type);
		this.jumpCmd = jumpCmd;
		this.jumpLable = jumpLable;
	}

	public String getJumpCmd() {
		return jumpCmd;
	}

	public String getJumpLable() {
		return jumpLable;
	}

}
