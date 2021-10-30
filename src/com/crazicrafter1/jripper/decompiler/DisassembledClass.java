package com.crazicrafter1.jripper.decompiler;

import com.crazicrafter1.jripper.Util;

import java.io.IOException;

public class DisassembledClass extends IDisassembled {

    private static final int ACC_PUBLIC =        0x0001;
    private static final int ACC_FINAL =         0x0010;
    private static final int ACC_SUPER =         0x0020; // exists for backward compatibility with code compiled by older compilers
    private static final int ACC_INTERFACE =     0x0200;
    private static final int ACC_ABSTRACT =      0x0400;
    private static final int ACC_SYNTHETIC =     0x1000; /* not really needed */
    private static final int ACC_ANNOTATION =    0x2000;
    private static final int ACC_ENUM =          0x4000;

    /*
     * The stuff that makes up a given class file
     */
    private String class_version;

    public ConstantContainer constantPoolContainer = new ConstantContainer(this);
    private int access_flags;
    public int this_class;
    private int super_class;
    public InterfaceContainer interfaceContainer = new InterfaceContainer(this);
    public FieldContainer fieldContainer = new FieldContainer(this);
    public MethodContainer methodContainer = new MethodContainer(this);
    public AttributeContainer attributeContainer = new AttributeContainer(this);

    public DisassembledClass() {
        super(null);
    }

    public void read(ByteReader bytes) throws IOException {
        if (!bytes.compareNext(4, new byte[] {(byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE}))
            throw new RuntimeException("No magic header found");

        int minor_version = bytes.readUnsignedShort();
        int major_version = bytes.readUnsignedShort();

        class_version = major_version + "." + minor_version;

        constantPoolContainer.read(bytes);
        access_flags = bytes.readUnsignedShort();
        this_class = bytes.readUnsignedShort();
        super_class = bytes.readUnsignedShort();
        interfaceContainer.read(bytes);
        fieldContainer.read(bytes);
        methodContainer.read(bytes);
        attributeContainer.read(bytes);
    }

    public boolean isPublic() {
        return (access_flags & ACC_PUBLIC) == ACC_PUBLIC;
    }

    public boolean isInterface() {
        return (access_flags & ACC_INTERFACE) == ACC_INTERFACE;
    }

    public boolean isAbstractClass() {
        return (access_flags & ACC_ABSTRACT) == ACC_ABSTRACT && !isInterface();
    }

    public boolean isAnnotation() {
        return (access_flags & ACC_ANNOTATION) == ACC_ANNOTATION;
    }

    public boolean isEnum() {
        return (access_flags & ACC_ENUM) == ACC_ENUM;
    }

    public boolean isFinal() {
        return (access_flags & ACC_FINAL) == ACC_FINAL;
    }

    public boolean isSuper() {
        return (access_flags & ACC_SUPER) == ACC_SUPER;
    }

    public String getAccessFlags() {
        StringBuilder s = new StringBuilder();
        if (isPublic())
            s.append("public ");

        if (isFinal())
            s.append("final ");

        if (isAbstractClass())
            s.append("abstract class");
        else if (isInterface())
            s.append("interface ");
        else if (isAnnotation())
            s.append("@interface ");
        else if (isEnum())
            s.append("enum ");
        else
            s.append("class ");

        return s.toString().trim();
    }

    public String getClassVersion() {
        return class_version;
    }

    /**
     * @return the fully qualified name (package.etc.clazz)
     */
    public String getPackageAndName() {
        return (String) getEntry(this_class).get();
    }

    public String getClassName() {
        String[] pk = Util.getPackageAndClass(getPackageAndName());
        return pk[1];
    }

    /**
     * Get the super class name
     * @return Return 'java.lang.Object' or 'com.my.package.MyClazz'
     */
    public String getSuperBinaryName() {
        if (super_class != 0)
            return (String) getEntry(super_class).get();
        return "Ljava/lang/Object"; // must then directly inherit from Object
    }

    public String getSuperClassName() {
        String[] pk =  Util.getPackageAndClass((getSuperBinaryName()));
        return pk[1];
    }

    public String[] getInterfaces() {
        return interfaceContainer.getInterfaces();
    }

    @Override
    public String toString() {
        return  "class version: " + class_version + "\n" +
                "class: " + getPackageAndName() + "\n" +
                "super class: " + getSuperBinaryName() + "\n" +
                interfaceContainer + "\n" +
                constantPoolContainer + "\n" +
                fieldContainer + "\n" +
                methodContainer + "\n" +
                attributeContainer + "\n";
    }
}