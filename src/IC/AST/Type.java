package IC.AST;

import SemanticCheckerVisitor.SemanticError;
import SymbolTables.MethodSymbolTable;

/**
 * Abstract base class for data type AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Type extends ASTNode {

	/**
	 * Number of array 'dimensions' in data type. For example, int[][] ->
	 * dimension = 2.
	 */
	private int dimension = 0;

	/**
	 * Constructs a new type node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of type declaration.
	 */
	protected Type(int line) {
		super(line);
	}

	public abstract String getName();

	public int getDimension() {
		return dimension;
	}

	public void setDimention(int newDim){
		if(newDim >= 0){
			this.dimension = newDim;
		}
		else{
			try {
				throw new SemanticError(this.getLine(),"Dimension of array can't be negative!");
			} catch (SemanticError e) {
				// TODO Auto-generated catch block
				e.getErrorMessage();
				System.exit(-1);
			}
		}
	}
	
	public void incrementDimension() {
		++dimension;
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
		Type other = (Type) obj;
		if (dimension != other.dimension){
			return false;
		}
		if (getName() == null) {
			if (other.getName() != null){
				return false;
			}
		} else if (!(getName().equals(other.getName()))){
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 2;
	}
	
}