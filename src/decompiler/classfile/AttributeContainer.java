package decompiler.classfile;

import decompiler.Result;
import decompiler.classfile.attributes.JavaAttribute;
import decompiler.classfile.pool.ConstantUtf8;

import java.io.IOException;
import java.util.ArrayList;

public class AttributeContainer extends JavaItem {

    private ArrayList<JavaAttribute> attributes = new ArrayList<>();

    @Override
    public Result read() throws IOException {

        // read the count first
        int attributes_count = bytes.readUnsignedShort();

        for (; attributes_count > 0; attributes_count--) {

            int attribute_name_index = bytes.readUnsignedShort();
            long attribute_length = bytes.readUnsignedInt();



            try {
                String s = getEntry(attribute_name_index).toString();
                JavaAttribute.Attribute atr = JavaAttribute.Attribute.valueOf(s);

                float class_version = Float.parseFloat(currentClassInstance.getClassVersion());
                float atr_version = Float.parseFloat(atr.major + "." + atr.minor);

                if (class_version >= atr_version)
                {

                    // then is valid and should read
                    //Class.forName("")
                    JavaAttribute javaAttribute = (JavaAttribute) atr.clazz.newInstance();

                    javaAttribute.read();

                    attributes.add(javaAttribute);

                } else {
                    // do nothing
                    System.out.println("attribute being ignored due to class version: " + s);
                }

            } catch (Exception e) {
                 //skip ahead in length bytes
                bytes.skip(attribute_length);
            }

        }

        return Result.OK;
    }
}
