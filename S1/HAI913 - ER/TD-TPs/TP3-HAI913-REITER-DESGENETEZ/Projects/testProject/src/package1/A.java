package package1;

public class A {
	private String str = "default";
	
	public A() {};
	public A(String newStr) {
		this.str = newStr;
	}
	
	public String getStr(){
		return str;
	}
	public void setStr(String newStr) {
		this.str = newStr;
	}
	public String testStr() {
		return str + str;
	}
	@Override
	public String toString() {
		return "A" + str;
	}

}
