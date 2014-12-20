package TypeTable;
import IC.AST.*;
import IC.*;

public class ArrayType extends TypeTableType {
	private Type arrayType;
	private Type ElemType;

	public ArrayType (Type elemType, int id){
		super(elemType.getName(), id);
		this.arrayType = elemType;
		this.ElemType = getElementType(this.arrayType);
	}

	public Type getElemType() {
		return this.ElemType;
	}
	
	public Type getArrayType() {
		return arrayType;
	}
	
	@Override
	public boolean subType(TypeTableType type) {
		if (type == null){
			return false;
		}
		else if (type.getId()==TypeIDs.NULL){
			return true;
		}
		else{ 
			if((type.getClass().equals(this.getClass())) &&
				(this.getId() == type.getId()) &&
				(arrayType.getDimension()==((ArrayType) type).getArrayType().getDimension()))
			{
				return true;
			}
			return false;
		}
	}
	
	//return the sub elemnet for the array. for exampe sub of a[][] -> a[]
	private Type getElementType(Type arrayType){
		String typeName = arrayType.getName();
		Type elementType;
		if(arrayType.getClass().equals(PrimitiveType.class)){
			DataTypes primitiveEnum;
			if(typeName.compareTo("int") == 0){
				primitiveEnum = DataTypes.INT;
			}
			else if(typeName.compareTo("string") == 0){
				primitiveEnum = DataTypes.STRING;
			}
			else{
				primitiveEnum = DataTypes.BOOLEAN;
			}
			elementType = new PrimitiveType(-1, primitiveEnum);
			
		}
		else{
			elementType = new UserType(-1, typeName);
		}
		elementType.setDimention(arrayType.getDimension() - 1);
		return elementType;
	}
	

	@Override
	public String toString() {
		
		String dim = "";
		for(int i = 0; i < arrayType.getDimension(); i++){
			dim = dim + "[]";
		}
		return "    "+this.getId() + ": Array type: " + this.getName() + dim;
	}

	public String toStringSymTable() {
		
		String dim = "";
		for(int i = 0; i < arrayType.getDimension(); i++){
			dim = dim + "[]";
		}
		
		return this.getName() + dim;
	}
}
