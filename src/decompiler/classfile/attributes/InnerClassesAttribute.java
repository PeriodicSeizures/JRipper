package decompiler.classfile.attributes;

import decompiler.Result;
import decompiler.classfile.JavaItem;

import java.io.IOException;
import java.util.ArrayList;

public class InnerClassesAttribute extends JavaAttribute {

    private class InnerClassEntry extends JavaItem {



        public static final int ACC_PUBLIC =        0x0001;
        public static final int ACC_PRIVATE =       0x0002;
        public static final int ACC_PROTECTED =     0x0004;
        public static final int ACC_STATIC =        0x0008;
        public static final int ACC_FINAL =         0x0010;
        public static final int ACC_INTERFACE =     0x0200;
        public static final int ACC_ABSTRACT =      0x0400;
        public static final int ACC_SYNTHETIC =     0x1000;
        public static final int ACC_ANNOTATION =    0x2000;
        public static final int ACC_ENUM =          0x4000;



        private int inner_class_info_index;
        private int outer_class_info_index;
        private int inner_name_index;
        private int inner_class_access_flags;

        @Override
        public Result read() throws IOException {
            inner_class_info_index = bytes.readUnsignedShort();
            outer_class_info_index = bytes.readUnsignedShort();
            inner_name_index = bytes.readUnsignedShort();
            inner_class_access_flags = bytes.readUnsignedShort();

            return Result.OK;
        }
    }

    private ArrayList<InnerClassEntry> classes = new ArrayList<>();

    @Override
    public Result read() throws IOException {

        int number_of_classes = bytes.readUnsignedShort();
        for (; number_of_classes > 0; number_of_classes--) {
            InnerClassEntry entry = new InnerClassEntry();

            entry.read();

            classes.add(entry);
        }

        return Result.OK;
    }
}
