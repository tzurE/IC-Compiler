package IC.Parser;

public class LexicalError extends Exception
{
    public LexicalError(String msg) {
    	PrintTokenError(msg);
    }
    
    public static void PrintTokenError(String errMsg){
    	System.err.println(errMsg);
    	}
    }

