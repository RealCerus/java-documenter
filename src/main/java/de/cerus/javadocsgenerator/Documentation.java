package de.cerus.javadocsgenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Documentation {

    private AccessModifier accessModifier;
    private boolean finalModifier,
            staticModifier,
            abstractModifier,
            syncModifier,
            nativeModifier,
            strictfpModifier;
    private String returnType,
            name;
    private Parameter[] parameters;
    private String description;

    public Documentation(Method method) {
        int modifiers = method.getModifiers();

        // Parses the access modifier
        AccessModifier accessModifier;
        if (Modifier.isPublic(modifiers)) {
            accessModifier = AccessModifier.PUBLIC;
        } else if (Modifier.isPrivate(modifiers)) {
            accessModifier = AccessModifier.PRIVATE;
        } else if (Modifier.isProtected(modifiers)) {
            accessModifier = AccessModifier.PROTECTED;
        } else {
            accessModifier = AccessModifier.PACKAGE_PRIVATE;
        }
        this.accessModifier = accessModifier;

        // Parses every other modifier
        this.finalModifier = Modifier.isFinal(modifiers);
        this.abstractModifier = Modifier.isAbstract(modifiers);
        this.staticModifier = Modifier.isStatic(modifiers);
        this.syncModifier = Modifier.isSynchronized(modifiers);
        this.strictfpModifier = Modifier.isStrict(modifiers);

        // Parses the return type
        Class<?> returnTypeClass = method.getReturnType();
        this.returnType = returnTypeClass.getSimpleName();

        // Parsed the name
        this.name = method.getName();

        // Parses the method parameters
        this.parameters = new Parameter[method.getParameterCount()];
        for (int i = 0; i < method.getParameters().length; i++) {
            java.lang.reflect.Parameter param = method.getParameters()[i];
            parameters[i] = new Parameter(param.getType().getSimpleName(), param.getType().isArray(), param.isVarArgs());
        }

        this.description = "";
    }

    public Documentation(AccessModifier accessModifier, boolean finalModifier, boolean staticModifier,
                         boolean abstractModifier, boolean syncModifier, boolean nativeModifier,
                         boolean strictfpModifier, String returnType, String name, Parameter[] parameters, String description) {
        this.accessModifier = accessModifier;
        this.finalModifier = finalModifier;
        this.staticModifier = staticModifier;
        this.abstractModifier = abstractModifier;
        this.syncModifier = syncModifier;
        this.nativeModifier = nativeModifier;
        this.strictfpModifier = strictfpModifier;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.description = description;
    }

    @Override
    public String toString() {
        return accessModifier.toString()
                + (hasStaticModifier() ? "static " : "")
                + (hasAbstractModifier() ? "abstract " : "")
                + (hasFinalModifier() ? "final " : "")
                + (hasNativeModifier() ? "native " : "")
                + (hasStrictfpModifier() ? "strictfp " : "")
                + (hasSyncModifier() ? "synchronized " : "")
                + returnType + " " + name + "("
                + Arrays.stream(parameters)
                .map(Parameter::toString)
                .collect(Collectors.joining(", "))
                + ")";
    }

    public String toMarkdownString() {
        return "### " + toString().replace("<", "\\<") + '\n' +
                (description == null || "".equals(description)
                        ? "*No description available*" : "```plain\n" + description + "\n```");
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    public boolean hasFinalModifier() {
        return finalModifier;
    }

    public boolean hasStaticModifier() {
        return staticModifier;
    }

    public boolean hasAbstractModifier() {
        return abstractModifier;
    }

    public boolean hasSyncModifier() {
        return syncModifier;
    }

    public boolean hasNativeModifier() {
        return nativeModifier;
    }

    public boolean hasStrictfpModifier() {
        return strictfpModifier;
    }

    public String getReturnType() {
        return returnType;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static class Parameter {
        public String className;
        public boolean array,
                varargs;

        public Parameter(String className, boolean array, boolean varargs) {
            this.className = className;
            this.array = array;
            this.varargs = varargs;
        }

        public Parameter() {
        }

        @Override
        public String toString() {
            return className.substring(0, varargs ? className.length() - 2 : className.length())
                    + (varargs ? "..." : "");
        }
    }

    enum AccessModifier {
        PACKAGE_PRIVATE,
        PRIVATE,
        PROTECTED,
        PUBLIC;

        @Override
        public String toString() {
            switch (this) {
                default:
                case PACKAGE_PRIVATE:
                    return "";
                case PRIVATE:
                    return "private ";
                case PROTECTED:
                    return "protected ";
                case PUBLIC:
                    return "public ";
            }
        }
    }
}
