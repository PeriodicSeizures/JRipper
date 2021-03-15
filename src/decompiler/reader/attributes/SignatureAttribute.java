package decompiler.reader.attributes;

import decompiler.Result;

import java.io.IOException;

public class SignatureAttribute extends RawAttribute {

    private int signature_index;

    @Override
    public Result read() throws IOException {

        signature_index = bytes.readUnsignedShort();

        return Result.OK;
    }

    @Override
    public String toString() {
        return "{Signature} " + signature_index + " (" + getEntry(signature_index).getValue() + ")";
    }

    //@Override
    //public String toJavaSourceCode(Object context) {
    //    String signature = getEntry(signature_index).toJavaSourceCode(context);
    //    return signature;
    //}
}
