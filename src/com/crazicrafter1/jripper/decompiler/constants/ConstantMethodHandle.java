package com.crazicrafter1.jripper.decompiler.constants;

import com.crazicrafter1.jripper.decompiler.ByteReader;
import com.crazicrafter1.jripper.decompiler.DisassembledClass;
import com.crazicrafter1.jripper.decompiler.IDisassembled;

import java.io.IOException;

public class ConstantMethodHandle extends IDisassembled implements IConstant {

    public static final int REF_getField = 1;
    public static final int REF_getStatic = 2;
    public static final int REF_putField = 3;
    public static final int REF_putStatic = 4;
    public static final int REF_invokeVirtual = 5;
    public static final int REF_invokeStatic = 6;
    public static final int REF_invokeSpecial = 7;
    public static final int REF_newInvokeSpecial = 8;
    public static final int REF_invokeInterface = 9;

    private int reference_kind;
    private int reference_index;

    public ConstantMethodHandle(DisassembledClass belongingClass) {
        super(belongingClass);
    }

    @Override
    public void read(ByteReader bytes) throws IOException {
        reference_kind = bytes.readUnsignedByte();
        reference_index = bytes.readUnsignedShort();
    }

    public int getHandleKind() {
        return reference_kind;
    }

    public IConstant getRef() {
        return getEntry(reference_index);
    }

    @Override
    public String toString() {
        return "{MethodHandle} \treference_kind: " + reference_kind;
    }
}