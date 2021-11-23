package graphs;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

public class SpoonGraph {
    private String source;
    public SpoonAPI spoon = new Launcher();
    private List<CtElement> extract;

    public SpoonGraph(String source) {
        this.source = source;
        spoon.addInputResource(source);
        spoon.buildModel();
        this.extract = spoon.getModel().getRootPackage().getElements(new TypeFilter<CtElement>(CtElement.class));
    }

    public List<CtInvocation> getListInvocationClass(CtClass classParam) {
        List<CtInvocation> listInvoc = new ArrayList();
        for(CtInvocation<?> invocation : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtInvocation>(CtInvocation.class))){
            listInvoc.add(invocation);
        }
        return listInvoc;
    }

    public void couplageEntre2Classes (CtClass a, CtClass b){
        int counter = 0;
        List<CtInvocation> listInvocationClassA = getListInvocationClass(a);
        List<CtInvocation> listInvocationClassB = getListInvocationClass(b);

        for (CtInvocation<?> invoque : listInvocationClassA){
            if (invoque.getExecutable().getDeclaringType().getSimpleName().equals(b.getSimpleName())){
                counter ++;
            }
        }

        for (CtInvocation<?> invoque : listInvocationClassB){
            if (invoque.getExecutable().getDeclaringType().getSimpleName().equals(a.getSimpleName())){
                counter ++;
            }
        }

        System.out.println("Nombre de couplage entre la classe : " + a.getSimpleName() + " et la classe : " + b.getSimpleName() + " est de : " + counter);

    }

    public void printCouplageAll(){
        for (CtClass classA : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtClass>(CtClass.class))){
            for (CtClass classB : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtClass>(CtClass.class))){
                if(!classA.equals(classB)){
                    couplageEntre2Classes(classA, classB);
                }
            }
        }
    }

}
