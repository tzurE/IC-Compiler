package TypeTable;

public class IntegerType extends TypeTableType {

	public IntegerType(int id) {
		super("int", TypeIDs.INT);
	}

	@Override
	public boolean subType(TypeTableType type) {
		if(type==null){
			return false;
		}
		else{
			return ((type.getId()==TypeIDs.INT) || (type.getId()==TypeIDs.NULL));
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