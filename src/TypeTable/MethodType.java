package TypeTable;

import IC.AST.*;
import IC.*;


public class MethodType extends TypeTableType {
	private Method method;

	public MethodType(Method method, int id) {
		super(method.getName(), id);
		this.method = method;
	}

	public Method getmethod() {
		return this.method;
	}

	@Override
	//only itself and null are subtype of it
	public boolean isTypeExtendsThis(TypeTableType type) {
		if (type==null){
			return false;
		}
		//check if it's the same function
		else if(this.getId() == type.getId() || (type.getId()==TypeIDs.NULL)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public TypeTableType getReturnType(){
		if(this.method.getType().getDimension() > 0){
			return TypeTable.arrayType(this.method.getType());
		}
		else if(this.method.getType().getName().equals(DataTypes.BOOLEAN.getDescription())){
			return TypeTable.booleanType;
		}
		else if(this.method.getType().getName().equals(DataTypes.INT.getDescription())){
			return TypeTable.integerType;
		}
		else if(this.method.getType().getName().equals(DataTypes.STRING.getDescription())){
			return TypeTable.stringType;
		}
		else if(this.method.getType().getName().equals(DataTypes.VOID.getDescription())){
			return TypeTable.voidType;
		}
		else{
			return TypeTable.classType(this.method.getType().getName());
		}
	}
	


	private String toStringHelper(){
		String str = "";
		int j = 0;
		for (Formal formal : method.getFormals()) {
			if (j == (method.getFormals().size()-1)){
				if (formal.getType().getDimension() == 0){
					str = str + formal.getType().getName();
				}
				else{
					String par = "";
					for(int i = 0; i < formal.getType().getDimension(); i++){
						par = par+"[]";
					}
					str = str + formal.getType().getName() + par;
				}
			}
			else{
				if (formal.getType().getDimension() == 0){
					str = str + formal.getType().getName() + ", ";
				}
				else{
					String par = "";
					for(int i = 0; i < formal.getType().getDimension(); i++){
						par = par+"[]";
					}
					str = str + formal.getType().getName() + par+ ", ";
				}
			}
			j++;
		}

		return str;
	}

	private String returnTypeToString(){
		if(method.getType().getDimension()==0)
			return method.getType().getName();
		else{
			String par = "";
			for(int i = 0; i < method.getType().getDimension(); i++){
				par = par+"[]";
			}
			return method.getType().getName() + par;}
	}


	@Override
	public String toString() {
		return "    "+this.getId() + ": Method type: {" + this.toStringHelper() + " -> " + returnTypeToString() + "}";
	}


	@Override
	public String toStringSymTable() {

		return "{" + this.toStringHelper() + " -> " + returnTypeToString() + "}";
	}
}
