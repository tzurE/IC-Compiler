package IC.AST;

import java.util.List;

import SymbolTables.SymbolTable;

/**
 * Static method AST node.
 * 
 * @author Tovi Almozlino
 */
public class StaticMethod extends Method {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	public Object accept(PropVisitor visitor,SymbolTable table) {
		this.setScope(table);
		return visitor.visit(this,table);
	}

	/**
	 * Constructs a new static method node.
	 * 
	 * @param type
	 *            Data type returned by method.
	 * @param name
	 *            Name of method.
	 * @param formals
	 *            List of method parameters.
	 * @param statements
	 *            List of method's statements.
	 */
	public StaticMethod(Type type, String name, List<Formal> formals,
			List<Statement> statements) {
		super(type, name, formals, statements);
	}
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
		
		Method other = (Method) obj;
		
		if (formals == null) {
			if (other.formals != null){
				return false;	
			}
		} else{
			if (formals.size() == other.getFormals().size()){
				int k = 0;
				for (Formal formal : formals) {
					Formal otherFormal = other.getFormals().get(k);
					if (!(formal.getType().equals(otherFormal.getType()))){
						return false;
					}
					k++;
				}
			}
			else{
				return false;
			}
		}
		
		if (type == null) {
			if (other.type != null){
				return false;
			}
		} else if (!(type.toString().equals(other.type.toString()))){
			return false;
		}
		return true;
	}

}
