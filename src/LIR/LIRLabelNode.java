package LIR;

public class LIRLabelNode extends LIRNode{

	private String labelName;
	
	public LIRLabelNode(LIRNodeTypes type, String labelName) {
		super(type);
		this.labelName = labelName;
		
	}

	public String getLabelName() {
		return labelName;
	}

}
