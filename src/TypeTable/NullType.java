package TypeTable;

public class NullType extends TypeTableType {

	public NullType(int id) {
		super("null", TypeIDs.NULL, id);
	}

	@Override
	public boolean subType(TypeTableType type) {
		return false;
	}

	@Override
	public String toString() {
		return (this.getId() + ": Primitive type: " + this.getName());
	}
}