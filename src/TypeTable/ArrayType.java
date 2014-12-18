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
		else if(type.getClass().equals(this.getClass())){
			if((( this.getId() == type.getId() || (type.getId()==TypeIDs.NULL))&&(arrayType.getDimension()==((ArrayType) type).getArrayType().getDimension()))){
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
	
	
	private Type getElementType(Type arrayType){
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
	

	@Override
	public String toString() {
		String par = "";
		for(int i = 0; i < arrayType.getDimension(); i++){
			par = par+"[]";
		}
		return "    " + this.getId() + ": Array type: " + this.getName() + par;
	}

	@Override
	public String toStringForSymbolTable() {
		// TODO Auto-generated method stub
		return null;
	}
}
