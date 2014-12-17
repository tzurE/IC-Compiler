package TypeTable;

public class NullType extends TypeTableType {

	public NullType(int id) {
		super("null", TypeIDs.NULL, id);
	}

	@Override
	public boolean subType(TypeTableType type) {
<<<<<<< HEAD
		return false;
=======
		if(type==null){
			return false;
		}
		else{
			return ((type.getTypeId()==TypeIDs.CLASS) || (type.getTypeId()==TypeIDs.ARRAY));
		}
>>>>>>> ff9da297f048eb511d0d30a62e225c0eb5b50724
	}

	@Override
	public String toString() {
		return (this.getId() + ": Primitive type: " + this.getName());
	}
}