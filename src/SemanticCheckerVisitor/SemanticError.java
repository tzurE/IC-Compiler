package SemanticCheckerVisitor;

public class SemanticError extends Exception {

	// The class SemanticError must declare a static final serialVersionUID
	// field
	private static final long serialVersionUID = 1L;

	// The error message to be returned
	private String serrMessage = null;

	/**
	 * SemanticError Constructor (one parameter)
	 * 
	 * @param message
	 *            A string containing the error message that would be printed on
	 *            screen
	 */
	public SemanticError(String message) {

		// Prints the error message to screen
		this.serrMessage = message;
	}

	/**
	 * SemanticError Constructor (3 parameters)
	 * 
	 * @param line
	 *            The line number of the current semantic error
	 * 
	 * @param message
	 *            A string containing the error message that would be printed on
	 *            screen
	 * 
	 * @param text
	 *            A string containing the invalid token with its value
	 */
	public SemanticError(int line, String message) {
		this.serrMessage = "semantic error at line " + line + ": " + message
				+ ".";
	}

	/**
	 * getErrorMessage method
	 * 
	 * @return a string of the current error message for the user
	 */
	public String getErrorMessage() {
		return this.serrMessage;
	}

}
