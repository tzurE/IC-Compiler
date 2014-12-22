package TypeTable;


public class StringType extends TypeTableType {

	public StringType(int id) {
		super("string", TypeIDs.STRING);
	}

	//primitive type- only itself and null extends it
	@Override
	public boolean isTypeExtendsThis(TypeTableType type) {
		if(type==null){
			return false;
		}
		else{
			return ((type.getId()==TypeIDs.STRING) || (type.getId() == TypeIDs.NULL));
		}
	}

	@Override
	public String toString() {
		return ("    "+this.getId() + ": Primitive type: " + this.getName());
	}

	@Override
	public String toStringSymTable() {
		
		return this.getName();
	}
}