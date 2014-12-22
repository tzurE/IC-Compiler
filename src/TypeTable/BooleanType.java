package TypeTable;


public class BooleanType extends TypeTableType {

	public BooleanType(int id) {
		super("boolean", TypeIDs.BOOLEAN);
	}

	@Override
	//primitive type- only itself and null extends it
	public boolean isTypeExtendsThis(TypeTableType type) {
		if(type==null){
			return false;
		}
		else{
			return ((type.getId()==TypeIDs.BOOLEAN) || (type.getId()==TypeIDs.NULL));
		}
	}

	@Override
	public String toString() {
		return ("    "+this.getId() + ": Primitive type: " + this.getName());
	}

	@Override
	public String toStringSymTable(){
		return this.getName();
	}
}