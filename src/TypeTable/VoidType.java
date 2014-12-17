package TypeTable;

public class VoidType extends TypeTableType {

	public VoidType(int id) {
		super("void", TypeIDs.VOID, id);
	}

	@Override
	public boolean subType(TypeTableType type) {
		if(type==null){
			return false;
		}
		else if (type.getName().equals("void")){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public String toString() {
		return (this.getId() + ": Primitive type: " + this.getName());
	}
}