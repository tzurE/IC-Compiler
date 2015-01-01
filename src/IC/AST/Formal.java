package IC.AST;

import LIR.LirTranslatorVisitor;
import SymbolTables.MethodSymbolTable;
import SymbolTables.SymbolTable;

/**
 * Method parameter AST node.
 * 
 * @author Tovi Almozlino
 */
public class Formal extends ASTNode {

	private Type type;

	private String name;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	public Object accept(PropVisitor visitor,SymbolTable table) {
		this.setScope(table);
		return visitor.visit(this,table);
	}
	public Object accept(LirTranslatorVisitor visitor, int regCount) {
		return visitor.visit(this, regCount);
	}

	/**
	 * Constructs a new parameter node.
	 * 
	 * @param type
	 *            Data type of parameter.
	 * @param name
	 *            Name of parameter.
	 */
	public Formal(Type type, String name) {
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;	
		}
		
		if (this == obj){
			return true;	
		}
		
		if (getClass() != obj.getClass()){
			return false;	
		}
		
		Formal other = (Formal) obj;
		if(name == null){
			if (other.name != null)
				return false;
		}
		else{
			if(!name.toString().equals(other.getName().toString()))
				return false;
		}
		
		if (type == null) {
			if (other.type != null){
				return false;
			}
		} else if (!(type.equals(other.type))){
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 1;
	}
	


}
