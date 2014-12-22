package TypeTable;


public class VoidType extends TypeTableType {

	public VoidType(int id) {
		super("void", TypeIDs.VOID);
	}

	@Override
	//primitive type- only itself and null extends it
	public boolean isTypeExtendsThis(TypeTableType type) {
		if(type==null){
			return false;
		}
		else if ((type.getId() == TypeIDs.VOID)|| (type.getId() == TypeIDs.NULL)){
			return true;
		}
		else{
			return false;
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