package TypeTable;


public class NullType extends TypeTableType {

	public NullType(int id) {
		super("null", TypeIDs.NULL);
	}

	@Override
	//primitive type- only subtype of itself
	public boolean isTypeExtendsThis(TypeTableType type) {
		return (type.getId() == TypeIDs.NULL);
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