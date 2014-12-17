package TypeTable;
import IC.AST.NewArray;

public class ArrayType extends TypeTableType {
<<<<<<< HEAD
=======

	private TypeTableType arrayType;
>>>>>>> ff9da297f048eb511d0d30a62e225c0eb5b50724
	private TypeTableType ElemType;
	private int dimension;
	private NewArray ASTnode;

	public ArrayType (TypeTableType elemType, int typeId, int id, NewArray node, int dimension){
		super(elemType.getName(), typeId , id);
<<<<<<< HEAD
=======
		this.arrayType = elemType;
>>>>>>> ff9da297f048eb511d0d30a62e225c0eb5b50724
		this.ElemType = elemType;
		this.ASTnode= node; 
		this.dimension = dimension;
	}

<<<<<<< HEAD
=======
	public TypeTableType getArrayType() {
		return arrayType;
	}

>>>>>>> ff9da297f048eb511d0d30a62e225c0eb5b50724
	public TypeTableType getElemType() {
		return this.ElemType;
	}
	
	public int getDimension(){
		return this.dimension;
	}
	
	@Override
<<<<<<< HEAD
	public boolean subType(TypeTableType type) {
		if (type == null){
			return false;
		}
		else if(type.getTypeId() == TypeIDs.ARRAY){
			if((this.getName() == type.getName())
					&& (this.getDimension() == ((ArrayType)type).getDimension())){
=======
	public boolean subType(TypeTableType t) {
		if (t == null){
			return false;
		}
		else if(t.getClass().equals(this.getClass())){
			if((arrayType.getName().equals(((ArrayType) t).getArrayType().getName()))
					&&(arrayType.getDimension()==((ArrayType) t).getArrayType().getDimension())){
>>>>>>> ff9da297f048eb511d0d30a62e225c0eb5b50724
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	@Override
	public String toString() {
		String par = "";
<<<<<<< HEAD
		for(int i = 0; i < this.getDimension(); i++){
			par = par+"[]";
		}
		return "    " + this.getId() + ": Array type: " + this.getName() + par;
=======
		for(int i = 0; i < this.arrayType.getDimension(); i++){
			par = par+"[]";
		}
		return "    " + this.getId() + ": Array type: " + this.arrayType.getName() + par;
	}

	@Override
	public String toStringForSymbolTable() {
		String par = "";
		for(int i = 0; i < this.arrayType.getDimension(); i++){
			par = par+"[]";
		}
		return this.arrayType.getName() + par;
>>>>>>> ff9da297f048eb511d0d30a62e225c0eb5b50724
	}
	
	private TypeTableType getElementType(TypeTableType arrayType){
		String typeName = arrayType.getName();
		if(arrayType.getClass().equals(PrimitiveType.class)){
			PrimitiveType elementType;
			if(typeName.compareTo("int") == 0){
				elementType = new PrimitiveType(-1, DataTypes.INT);
			}
			else if(typeName.compareTo("string") == 0){
				elementType = new PrimitiveType(-1, DataTypes.STRING);
			}
			else{
				elementType = new PrimitiveType(-1, DataTypes.BOOLEAN);
			}
			elementType.setDimention(arrayType.getDimension() - 1);
			return elementType;
			
		}
		else{
			UserType elementType = new UserType(-1, typeName);
			elementType.setDimention(arrayType.getDimension() - 1);
			return elementType;
		}
	}
}
