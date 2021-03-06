package com.crazicrafter1.jripper.util;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private Util() {}

    /**
     * Get the binary simple name from the specified class binary name
     * @param binaryName 'java/lang/Object'
     * @return 'Object'
     */
    public static String getBinarySimpleName(String binaryName) {
        assert !binaryName.contains(".") : "Binary name must not contain '.'";

        int lastSlash = binaryName.lastIndexOf('/');
        if (lastSlash != -1)
            return binaryName.substring(lastSlash + 1);
        return binaryName;
    }

    /**
     * Get the binary package and binary simple name from the specified class binary name
     * @param binaryName 'java/lang/Object'
     * @return {'java/lang', 'Object'}
     */
    public static String[] getBinaryPackageAndClass(String binaryName) {
        int lastSlash = binaryName.lastIndexOf('/');

        if (lastSlash != -1) {
            return new String[] {binaryName.substring(0, lastSlash), binaryName.substring(lastSlash + 1)};
        } else {
            return new String[] {null, getBinarySimpleName(binaryName)};
        }
    }

    /**
     *
     * @param methodDescriptor A method descriptor,
     *                   "(Ljava/util/ArrayList<Ljava/lang/Integer;>;Ljava/util/HashMap<Ljava/lang/Integer;Ljava/lang/Integer;>;)Ljava/util/ArrayList<Ljava/lang/Object;>;"
     * @return ReturnInfo structure, containing the method arguments in valid Java
     */
    public static ArrayList<String> getParameterTypes(String methodDescriptor, Set<String> outImports, StringBuilder outReturnType) {
        assert methodDescriptor.contains("(") : "Must be a method descriptor";

        ArrayList<String> args = new ArrayList<>();

        StringBuilder remaining = new StringBuilder(
                methodDescriptor.substring(1, methodDescriptor.indexOf(")")));

        while (remaining.length() != 0) {
            args.add(stripNextFieldType(remaining.toString(), outImports, remaining));
        }

        if (outReturnType != null) {
            outReturnType.replace(0, outReturnType.length(),
                    getFieldType(methodDescriptor.substring(methodDescriptor.indexOf(")") + 1), outImports));
        }

        return args;
    }

    /**
     * Get the left-most type from a parameterDescriptor
     * @param parameterDescriptor A method parameterDescriptor, {FILjava/util/ArrayList<Ljava/lang/Integer;>;}
     * @return A valid Java type, { [float, int, ArrayList<Integer>] }
     */
    private static String stripNextFieldType(String parameterDescriptor, Set<String> outImports, StringBuilder outRemainder) {

        assert parameterDescriptor != null : "descriptor must not be null";
        assert !parameterDescriptor.contains("(") : "Must be a field descriptor or stripped method (arg only) descriptor";

        outRemainder.replace(0, outRemainder.length(),
                parameterDescriptor.substring(1));

        switch (parameterDescriptor.charAt(0)) {
            case 'B': return "byte";
            case 'C': return "char";
            case 'D': return "double";
            case 'F': return "float";
            case 'I': return "int";
            case 'J': return "long";
            case 'S': return "short";
            case 'Z': return "boolean";
            case 'V': return "void";
            case 'L': {
                /*
                 * Remove the first family of delimiters if they are present
                 */
                int openDelimiters = 0;
                for (int i = 0; i < parameterDescriptor.length(); i++) {
                    // if a ';' is found before a <> delimiter
                    // then cut early, and break
                    char ch = parameterDescriptor.charAt(i);
                    if (openDelimiters == 0 && ch == ';') {
                        outRemainder.replace(0, outRemainder.length(),
                                parameterDescriptor.substring(i + 1));

                        parameterDescriptor = parameterDescriptor.substring(0, i + 1);
                        break;
                    }

                    if (ch == '<') {
                        openDelimiters++;
                    } else if (ch == '>') {
                        openDelimiters--;
                        if (openDelimiters == 0) {
                            // then record
                            outRemainder.replace(0, outRemainder.length(),
                                    parameterDescriptor.substring(i + 2));

                            parameterDescriptor = parameterDescriptor.substring(0, i + 2);
                            break;
                        }
                    }
                }

                return getFieldType(parameterDescriptor, outImports);
            }
            default: throw new RuntimeException("type is invalid; class might be corrupted");
        }
    }


    /**
     *
     * @param fieldDescriptor Ljava/util/ArrayList<Ljava/lang/Integer;>;
     * @param outImports the return Set for imports
     * @return the Java valid type, java.util.ArrayList<Integer>
     */
    public static String getFieldType(String fieldDescriptor, Set<String> outImports) {
        assert !fieldDescriptor.contains("(") : "Must be a field or method return descriptor";

        char c = fieldDescriptor.charAt(0);
        switch (c) {
            case 'B': return "byte";
            case 'C': return "char";
            case 'D': return "double";
            case 'F': return "float";
            case 'I': return "int";
            case 'J': return "long";
            case 'S': return "short";
            case 'Z': return "boolean";
            case 'V': return "void";
            case 'L': {
                fieldDescriptor = fieldDescriptor.substring(1, fieldDescriptor.length() - 1)
                        .replaceAll("<L", "<")
                        .replaceAll(";L", ",")
                        .replaceAll(";>", ">")
                        .replaceAll("java/lang/", "")
                        .replaceAll("/", ".");

                if (outImports != null) {
                    // thanks @Fried Rice On Ice
                    Pattern pattern = Pattern.compile("(.*?)(?=<)(?:.+?[\\s<])?");
                    Matcher matcher = pattern.matcher(fieldDescriptor);
                    while (matcher.find()) {
                        String match = matcher.group(1);

                        if (match.isEmpty()) continue;

                        outImports.add(match);

                        fieldDescriptor = fieldDescriptor.replaceAll(
                                match.substring(
                                        0, match.lastIndexOf('.') + 1), "");
                    }

                    // test for '.'
                    int pd = fieldDescriptor.lastIndexOf('.');
                    if (pd != -1) {
                        int dm = fieldDescriptor.indexOf('<');
                        if (dm != -1)
                            outImports.add(fieldDescriptor.substring(0, dm));
                        else
                            outImports.add(fieldDescriptor);
                        fieldDescriptor = fieldDescriptor.substring(pd + 1);
                    }

                }



                return toValidTypeName(
                        fieldDescriptor.replaceAll(",", ", "));
            }
            default: {
                throw new RuntimeException("type " + c + " is invalid");
            }
        }
    }

    public static String toValidName(String s) {
        StringBuilder builder = new StringBuilder();
        for (int pt : s.codePoints().toArray()) {
            if ((pt >= '0' && pt <= '9') ||
                    (pt >= 'A' && pt <= 'Z') ||
                    (pt >= 'a' && pt <= 'z') ||
                    (pt == '_'))
            {
                // if starts with a number
                if (pt <= '9') {
                    builder.append("_");
                }
                builder.append((char)pt);
                continue;
            }
            // else, nuke that codepoint
            if (builder.length() == 0) builder.append("_");
            builder.append(Integer.toHexString(pt));
        }
        return builder.toString();
    }

    /*
        Returns a reformatted string:
            - i0:   a -> Z, _
            - i1->: a -> Z, 0 -> 9, <, >, _, ., ,
     */
    public static String toValidTypeName(String s) {
        StringBuilder builder = new StringBuilder();
        for (int pt : s.codePoints().toArray()) {
            if ((pt >= '0' && pt <= '9') ||
                    (pt >= 'A' && pt <= 'Z') ||
                    (pt >= 'a' && pt <= 'z') ||
                    (pt == '_') ||
                    (pt == '.') || (pt == '<') || (pt == '>') ||
                    (pt == ',')) // slash for unique package case
            {
                if (pt >= '0' && pt <= '9') {
                    builder.append("_");
                }
                builder.append((char)pt);
                continue;
            }
            // else, nuke that codepoint
            if (builder.length() == 0) builder.append("_");
            builder.append(Integer.toHexString(pt));
        }
        return builder.toString();
    }

    public static String beautify(ArrayList<String> lines) {
        StringBuilder builder = new StringBuilder();
        builder.ensureCapacity(lines.size()*20);
        int indentCount = 0;
        for (String s : lines) {
            String trimmed = s.trim();

            if (trimmed.isEmpty())
                continue;

            if (trimmed.charAt(trimmed.length() - 1) == '}') {
                indentCount -= 4;
            }

            builder.append(new String(new char[indentCount])
                            .replace('\0', ' '))
                    .append(trimmed).append("\n");

            if (trimmed.charAt(trimmed.length() - 1) == '{') {
                indentCount += 4;
            }
        }
        return builder.substring(0, builder.length() - 1);
    }

    public static String combine(ArrayList<String> lines) {
        StringBuilder builder = new StringBuilder();
        builder.ensureCapacity(lines.size()*20);
        for (String s : lines) {
            String trimmed = s.trim();

            builder.append(trimmed).append("\n");
        }
        return builder.substring(0, builder.length() - 1);
    }

}
