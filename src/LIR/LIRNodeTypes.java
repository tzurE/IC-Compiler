package LIR;

public enum LIRNodeTypes {
	LABEL("label"),
	BINARY_INSTRUCTION("binary_instruction"),
	JUMP("jump"), 
	UNARY_INSTRUCTION("unary_instruction");
	
	
	private String type;

	private LIRNodeTypes(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}		
}
