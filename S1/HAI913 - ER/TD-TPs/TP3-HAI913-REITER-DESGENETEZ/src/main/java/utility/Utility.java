package utility;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Utility {
    public static String getClassFullyQualifiedName(TypeDeclaration typeDeclaration) {
        String name = typeDeclaration.getName().getIdentifier();

        if (typeDeclaration.getRoot().getClass() == CompilationUnit.class) {
            CompilationUnit root = (CompilationUnit) typeDeclaration.getRoot();

            if (root.getPackage() != null)
                name = root.getPackage().getName().getFullyQualifiedName() + "." + name;
        }

        return name;
    }

    public static String getMethodFullyQualifiedName(MethodDeclaration method) {
        StringBuilder builder = new StringBuilder();
        builder.append(getClassFullyQualifiedName((TypeDeclaration) method.getParent()));
        builder.append("::");
        builder.append(method.getName() + "(");

        if (!method.parameters().isEmpty()) {
            for (Object param : method.parameters()) {
                SingleVariableDeclaration parameter = (SingleVariableDeclaration) param;
                ITypeBinding typeBinding = parameter.getType().resolveBinding();
                builder.append(typeBinding.getQualifiedName() + ", ");
            }

            removeStringBuilderTrailingComma(builder);
        }

        builder.append(")");

        return builder.toString();
    }

    public static String getMethodInvocationDefaultFormat(MethodInvocation methodInvocation) {
        /* declaringTypeFQN.method([argTypes]) */
        StringBuilder builder = new StringBuilder();
        IMethodBinding invokedMethod = methodInvocation.resolveMethodBinding();

        if (invokedMethod != null) {
            // get the declaring type FQN
            ITypeBinding declaringType = invokedMethod.getDeclaringClass();
            builder.append(declaringType.getQualifiedName());
            builder.append("::");

            // get the method signature
            builder.append(invokedMethod.getName() + "(");

            // get the argument types
            if (!methodInvocation.arguments().isEmpty()) {
                for (ITypeBinding argumentType : invokedMethod.getParameterTypes())
                    builder.append(argumentType.getQualifiedName() + ", ");
                removeStringBuilderTrailingComma(builder);
            }
        }

        builder.append(")");
        return builder.toString();
    }
    // Same as getMethodInvocationDefaultFormat but for SuperInvocations
    public static String getMethodSuperInvocationDefaultFormat(SuperMethodInvocation superMethodInvocation) {
        StringBuilder builder = new StringBuilder();
        IMethodBinding invokedMethod = superMethodInvocation.resolveMethodBinding();

        if (invokedMethod != null) { // the invoked super method
            // get the declaring type FQN
            ITypeBinding declaringType = invokedMethod.getDeclaringClass();
            builder.append(declaringType.getQualifiedName());
            builder.append("::");

            // get the method signature
            builder.append(invokedMethod.getName() + "(");

            // get the argument types
            if (!superMethodInvocation.arguments().isEmpty()) {
                for (ITypeBinding argumentType : invokedMethod.getParameterTypes())
                    builder.append(argumentType.getQualifiedName() + ", ");
                removeStringBuilderTrailingComma(builder);
            }
        }
        builder.append(")");
        return builder.toString();
    }


    public static String getAnyMethodInvocationDeclaringClass(MethodInvocation invocation) {
        StringBuilder builder = new StringBuilder();
        IMethodBinding methodBinding = invocation.resolveMethodBinding();

        if (methodBinding != null) {
            ITypeBinding typeBinding = methodBinding.getDeclaringClass();
            builder.append(typeBinding.getQualifiedName());
        }
        return builder.toString();
    }

    public static void removeStringBuilderTrailingComma(StringBuilder builder) {
        builder.delete(builder.toString().length() - 2,
                builder.toString().length());
    }
}
