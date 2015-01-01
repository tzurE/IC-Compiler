package LIR;

public abstract class LIRNode {

	private LIRNodeTypes type;

	public LIRNode(LIRNodeTypes type) {
		this.type = type;
	}

	public LIRNodeTypes getType() {
		return type;
	}
	
}
