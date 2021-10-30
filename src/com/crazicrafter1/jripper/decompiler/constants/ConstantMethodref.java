package com.crazicrafter1.jripper.decompiler.constants;

import com.crazicrafter1.jripper.decompiler.ByteReader;
import com.crazicrafter1.jripper.decompiler.DisassembledClass;
import com.crazicrafter1.jripper.decompiler.IDisassembled;

import java.io.IOException;

public class ConstantMethodref extends IDisassembled implements IConstant, IMethodRef {
    private int class_index;
    private int name_and_type_index;

    public ConstantMethodref(DisassembledClass belongingClass) {
        super(belongingClass);
    }

    @Override
    public void read(ByteReader bytes) throws IOException {
        class_index = bytes.readUnsignedShort();
        name_and_type_index = bytes.readUnsignedShort();
    }

    @Override
    public String GUID() {
        return getMainClass().getClassName() + "::" + getMethodName() + getMethodDescriptor();
    }

    @Override
    public ConstantClass getPointingClass() {
        return (ConstantClass) getEntry(class_index);
    }

    @Override
    public String getMethodName() {
        return ((ConstantNameAndType) getEntry(name_and_type_index)).getName();
    }

    @Override
    public String getMethodDescriptor() {
        return ((ConstantNameAndType) getEntry(name_and_type_index)).getDescriptor();
    }

    @Override
    public String toString() {
        return "{Methodref} \tclass: " + getPointingClass().get() + ", " + getMethodDescriptor() + " " + getMethodName();
    }
}