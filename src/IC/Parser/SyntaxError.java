package IC.Parser;

public class SyntaxError extends Exception {

	// The class LexicalError must declare a static final serialVersionUID field
	private static final long serialVersionUID = 1L;

	// The error message to be returned
	private String serrMessage = null;

	/**
	 * SyntaxError Constructor (one parameter)
	 * 
	 * @param message A string containing the error message that would be
	 * printed on screen
	 */
	public SyntaxError(String message) {

		this.serrMessage = message;
	}

	/**
	 * SyntaxError Constructor (3 parameters)
	 * 
	 * @param line The line number of the current lexical error
	 * 
	 * @param message A string containing the error message that would be
	 * printed on screen
	 * 
	 * @param text A string containing the invalid token with its value
	 */
	public SyntaxError(int line,int column ,String message, String text) {
		if(text.equals(""))
			this.serrMessage = "Syntax error in " + line +":"+ column + message;
		else
			this.serrMessage = "Syntax error in " + line + ":"+ column + message + " '" + text + "'";
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
