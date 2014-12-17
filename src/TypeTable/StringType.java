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
<<<<<<< HEAD
			return (type.getTypeId()==TypeIDs.STRING);
=======
			return (type.getTypeId()==TypeIDs.INT);
>>>>>>> ff9da297f048eb511d0d30a62e225c0eb5b50724
		}
	}

	@Override
	public String toString() {
		return (this.getId() + ": Primitive type: " + this.getName());
	}
}