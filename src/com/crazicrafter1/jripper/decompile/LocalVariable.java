package com.crazicrafter1.jripper.decompile;

public class LocalVariable  {

    private final String type;
    private final String name;
    private String value;

    public LocalVariable(String type, String name, String value) {

        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    //public static LocalVariable[] from(CodeAttr atr) {
    //    ArrayList<LocalVariableTableAttribute.Var> _locals = atr.getLocalVariableTable();

    //    LocalVariable[] localVariables = new LocalVariable[_locals.size()];

    //    for (LocalVariableTableAttribute.Var entry : _locals) {
    //        localVariables[entry.getIndex()] = new LocalVariable(
    //                JavaUtil.getUnqualifiedName(entry.getDescriptor()), entry.getName(), "");
    //    }
    //    return localVariables;
    //}
}
