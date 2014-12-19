package IC.AST;

import java.util.List;

import SymbolTables.SymbolTable;

/**
 * Abstract base class for method AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Method extends ASTNode {

	protected Type type;

	protected String name;

	protected List<Formal> formals;

	protected List<Statement> statements;

	/**
	 * Constructs a new method node. Used by subclasses.
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
	protected Method(Type type, String name, List<Formal> formals,
			List<Statement> statements) {
		super(type.getLine());
		this.type = type;
		this.name = name;
		this.formals = formals;
		this.statements = statements;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public List<Formal> getFormals() {
		return formals;
	}

	public List<Statement> getStatements() {
		return statements;
	}
	
	//we override the obj equals in order to compare between to methods, so we can add new typs

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