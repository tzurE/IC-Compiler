package IC.Parser;

import java_cup.runtime.Symbol;

public class Token extends Symbol {
	
	private int line;
	private int column;
	private int id;
	private String tag;

	//first type of builder, for strings//
	public Token(int id, String value, String tag, int line,int column ) {
        super(id, value);
        this.line = line;
        this.column = column;
        this.right = column;
        this.id = id;
        this.tag = tag;
        this.left = line;
    }

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getLine() {
		return line;
	}


	public void setLine(int line) {
		this.line = line;
	}


	public int getColumn() {
		return column;
	}


	public void setColumn(int column) {
		this.column = column;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}



}

