package IC.AST;

import LIR.LirTranslatorVisitor;
import SymbolTables.SymbolTable;

/**
 * Class field AST node.
 * 
 * @author Tovi Almozlino
 */
public class Field extends ASTNode {

	private Type type;

	private String name;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	public Object accept(PropVisitor visitor,SymbolTable table) {
		this.setScope(table);
		return visitor.visit((Field)this,table);
	}

	public Object accept(LirTranslatorVisitor visitor, int regCount) {
		return visitor.visit(this, regCount);
	}
	
	/**
	 * Constructs a new field node.
	 * 
	 * @param type
	 *            Data type of field.
	 * @param name
	 *            Name of field.
	 */
	public Field(Type type, String name) {
		super(type.getLine());
		this.type = type;
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

}
