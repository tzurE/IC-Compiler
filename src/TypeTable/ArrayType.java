package TypeTable;
import IC.AST.NewArray;

public class ArrayType extends TypeTableType {
	private TypeTableType ElemType;
	private int dimension;
	private NewArray ASTnode;

	public ArrayType (TypeTableType elemType, int typeId, int id, NewArray node, int dimension){
		super(elemType.getName(), typeId , id);
		this.ElemType = elemType;
		this.ASTnode= node; 
		this.dimension = dimension;
	}

	public TypeTableType getElemType() {
		return this.ElemType;
	}
	
	private TypeTableType getElementType(){
		return this.ElemType;
	}
	
	public int getDimension(){
		return this.dimension;
	}
	
	@Override
	public boolean subType(TypeTableType type) {
		if (type == null){
			return false;
		}
		//an array is only a subtype of itself
		else if( this.getId() == type.getId() ){ 
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public String toString() {
		String par = "";
		for(int i = 0; i < this.getDimension(); i++){
			par = par+"[]";
		}
		return "    " + this.getId() + ": Array type: " + this.getName() + par;
	}
}
