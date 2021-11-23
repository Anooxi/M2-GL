package models;
import java.util.ArrayList;
public class ClassAndContent {
    String name;

    ArrayList<Method> methods = new ArrayList<Method>();

    public ClassAndContent(String name2) {
        this.name = name2;
    }

    public ClassAndContent() {
        // TODO Auto-generated constructor stub
    }

    public Integer getNumberOfInvocations() {
        Integer rslt = 0;
        for (Method method : methods) {
            rslt += method.numberOfInvocations();
        }
        return rslt;
    }

    /* On part du postulat qu'il est impossible d'avoir 2 methodes avec le meme nom dans une classe */
    public void addMethod(String name) {
        methods.add(new Method(name));
    }

    public void addMethod(Method method) {
        methods.add(method);
    }

    /* On part du postulat qu'il est impossible d'avoir 2 classe avec le meme nom */
    /* Verifie si la class a le meme nom (donc si c'est la meme classe) */
    public boolean isSame(String name2) {
        return this.getName().equals(name2);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Method> getMethods() {
        return methods;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(("ClassName : " + this.getName()) + "\n");
        for (Method method : methods)
            builder.append(method.toString());

        builder.append("\n");
        return builder.toString();
    }
}