package TypeTable;

public class StringType extends TypeTableType {

	public StringType(int id) {
		super("string", TypeIDs.STRING, id);
	}

	@Override
	public boolean subType(TypeTableType type) {
		if(type==null){
			return false;
		}
		else{
			return (type.getTypeId()==TypeIDs.STRING);
		}
	}

	@Override
	public String toString() {
		return (this.getId() + ": Primitive type: " + this.getName());
	}
}