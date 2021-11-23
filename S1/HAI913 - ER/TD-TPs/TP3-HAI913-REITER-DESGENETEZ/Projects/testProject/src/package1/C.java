package package1;

public class C {
	private A classeA = new A("ClasseC");
	private B classeB = new B("ClasseC");

	public String returnBoth() {
		return classeA.testStr() + classeB.testStr();
	}
	
	@Override
	public String toString() {
		return "Classe C " + classeA.toString();
	}
}
