package TypeTable;

import java.util.ArrayList;
import IC.AST.Method;

public class MethodType extends TypeTableType {
	private Method methodNode;
	private TypeTableType returnType;
	private ArrayList<TypeTableType> formalsList;

	public MethodType(Method methodNode, int id, TypeTableType returnType, ArrayList<TypeTableType> formalsList) {
		super(method.getName(), TypeIDs.METHOD, id);
		this.methodNode = methodNode;
		this.returnType = returnType;
		this.formalsList = formalsList;
	}


	public TypeTableType getReturnType(){
		return returnType;
	}


	public Method getMethodNode() {
		return this.methodNode;
	}

	@Override
	public boolean subType(TypeTableType type) {
		if (type==null){
			return false;
		}
		//check if it's the same function
		else if(this.getId() == type.getId()){
			return true;
		}
		else{
			return false;
		}
	}


	private String toStringHelper(){
		String str = "";
		int j = 0;
		for (Formal formal : method.getFormals()) {
			if (j == (method.getFormals().size()-1)){
				if (formal.getType().getDimension() == 0){
					str = str + formal.getType().getName();
				}
				else{
					String par = "";
					for(int i = 0; i < formal.getType().getDimension(); i++){
						par = par+"[]";
					}
					str = str + formal.getType().getName() + par;
				}
			}
			else{
				if (formal.getType().getDimension() == 0){
					str = str + formal.getType().getName() + ", ";
				}
				else{
					String par = "";
					for(int i = 0; i < formal.getType().getDimension(); i++){
						par = par+"[]";
					}
					str = str + formal.getType().getName() + par+ ", ";
				}
			}
			j++;
		}

		return str;
	}

	private String returnTypeToString(){
		if(method.getType().getDimension()==0)
			return method.getType().getName();
		else{
			String par = "";
			for(int i = 0; i < method.getType().getDimension(); i++){
				par = par+"[]";
			}
			return method.getType().getName() + par;}
	}


	@Override
	public String toString() {
		return "    " + this.getId() + ": Method type: {" + this.toStringHelper() + " -> " + returnTypeToString() + "}";
	}


	@Override
	public String toStringForSymbolTable() {

		return "{" + this.toStringHelper() + " -> " + returnTypeToString() + "}";
	}
}
