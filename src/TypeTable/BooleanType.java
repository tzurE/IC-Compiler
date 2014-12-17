package TypeTable;

public class BooleanType extends TypeTableType {

	public BooleanType(int id) {
		super("boolean", TypeIDs.BOOLEAN , id);
	}

	@Override
	public boolean subType(TypeTableType type) {
		if(type==null){
			return false;
		}
		else{
			return (type.getTypeId()==TypeIDs.BOOLEAN);
		}
	}

	@Override
	public String toString() {
		return (this.getId() + ": Primitive type: " + this.getName());
	}
}