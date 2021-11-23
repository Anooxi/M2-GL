package package1;

public class B extends A{
	public B(String newStr) {
		setStr(newStr);
	}
	
	@Override
	public String testStr() {
		return getStr() + getStr() + getStr();
	}
	
	@Override
	public String toString() {
		return getStr();
	}
	
}
