package metric;
import graphs.CallGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import models.ClassAndContent;
import models.Method;
import models.MethodInvocated;
public class GenerateClassesAndContent {
    private CallGraph callGraph;

    private ArrayList<String> classesName = new ArrayList<String>();

    private Map<String, Map<String, Integer>> invocations = new HashMap<>();

    ArrayList<ClassAndContent> classes = new ArrayList<ClassAndContent>();

    public GenerateClassesAndContent(CallGraph callGraph2) {
        this.callGraph = callGraph2;
        this.invocations = callGraph2.getInvocations();
        this.classesName = callGraph2.getClassesName();
        this.classes = this.getClassesAndContent();
    }

    public ArrayList<ClassAndContent> getClasses() {
        return classes;
    }

    /* * * * * * * * * * * *

     METHODS PUBLIC ADDED
     
     * * * * * * * * *
     */
    // verifie si la class est dans la liste, si c'est le cas renvois sont index
    private Integer classIsAlreadyIn(ArrayList<ClassAndContent> classes, String className) {
        Integer output = -1;
        Integer i = 0;
        for (ClassAndContent classAndContent : classes) {
            if (classAndContent.getName().equals(className)) {
                output = i;
            }
            i++;
        }
        return output;
    }

    // verifie si la class est dans la liste, si c'est le cas renvois sont index
    private boolean MethodIsAlreadyIn(ClassAndContent classe, String methodName) {
        boolean rslt = false;
        for (Method method : classe.getMethods()) {
            if (method.getName().equals(methodName))
                rslt = true;

        }
        return rslt;
    }

    public ArrayList<ClassAndContent> getOnlyClassesAndContentWithInvocation() {
        ArrayList<ClassAndContent> classes = new ArrayList<ClassAndContent>();
        for (String source : invocations.keySet()) {
            // ajout de la class. Format initial de source : elemStockage.AElemStock2::absoluteAddess
            String[] words = source.split("::");
            ClassAndContent classToAdd = new ClassAndContent();
            Method method = new Method();
            if (words.length == 2) {
                // verifie si la classe n'existe pas deja, sinon la crée
                if (classIsAlreadyIn(classes, words[0]) > (-1)) {
                    classToAdd = classes.get(classIsAlreadyIn(classes, words[0]));
                    // on supprime sont ancienne version
                    classes.remove(classes.get(classIsAlreadyIn(classes, words[0])));
                } else
                    classToAdd = new ClassAndContent(words[0]);

                method = new Method(words[1]);
                for (String destination : invocations.get(source).keySet()) {
                    MethodInvocated methodInvocated = new MethodInvocated();
                    // ajout de la method invoque. Format initial de la source elemStockage.AElemStock2::size
                    String[] words2 = destination.split("::");
                    if (words2.length == 2) {
                        methodInvocated = new MethodInvocated(words2[1], words2[0]);
                        // recuperation du nombre d'invocation
                        methodInvocated.setNumberOfTime(invocations.get(source).get(destination));
                    }
                    method.addMethodInvocation(methodInvocated);
                }
                classToAdd.addMethod(method);
            }
            if (classToAdd != null)
                classes.add(classToAdd);

        }
        return classes;
    }

    // Ajoute les classes qui on des méthodes mais pas d'invocations
    public ArrayList<ClassAndContent> getClassesAndContent() {
        ArrayList<ClassAndContent> output = new ArrayList<ClassAndContent>();
        ArrayList<ClassAndContent> OnlyClassesWithInvocation = this.getOnlyClassesAndContentWithInvocation();
        // System.out.println(classesName.toString());
        for (String s : classesName) {
            boolean bool = false;
            for (ClassAndContent cac : OnlyClassesWithInvocation) {
                if (cac.isSame(s)) {
                    bool = true;
                }
            }
            if (!bool) {
                output.add(new ClassAndContent(s));
            }
        }
        output.addAll(OnlyClassesWithInvocation);
        // System.out.println(output.toString());
        return output;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Integer method = 0;
        Integer methodInvocation = 0;
        for (ClassAndContent cac : classes) {
            method += cac.getMethods().size();
            methodInvocation += cac.getNumberOfInvocations();
        }
        builder.append(("Number of Method with invocation = " + method) + "\n");
        builder.append(("Number of Invocation (including none-Class invocations) = " + methodInvocation) + "\n");
        return builder.toString();
    }

    boolean isClass(String s) {
        String sTemp = new String(s);
        String[] words = sTemp.split(".");
        boolean isClass = Character.isUpperCase(sTemp.charAt(0));
        if (words.length > 1) {
            isClass = isClass || Character.isUpperCase(words[words.length - 1].charAt(0));
        }
        isClass = isClass && (!((((((sTemp.equals("System.out") || sTemp.equals("String")) || sTemp.equals("DJ")) || sTemp.equals("Optional")) || sTemp.equals("Arrays")) || sTemp.equals("System.err")) || sTemp.equals("Arrays.asList(this.getFilledArray())")));
        return isClass;
    }
}