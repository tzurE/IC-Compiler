package TypeTable;

public class IntegerType extends TypeTableType {

	public IntegerType(int id) {
		super("integer", TypeIDs.INT, id);
	}

	@Override
	public boolean subType(TypeTableType type) {
		if(type==null){
			return false;
		}
		else{
			return (type.getTypeId()==TypeIDs.INT);
		}
	}

	@Override
	public String toString() {
		return (this.getId() + ": Primitive type: " + this.getName());
	}
}