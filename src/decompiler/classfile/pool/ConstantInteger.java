package decompiler.classfile.pool;

import decompiler.Result;

import java.io.IOException;

public class ConstantInteger extends JavaPoolEntry {

    private int value;

    @Override
    public Result read() throws IOException {
        value = bytes.readInt();
        return Result.OK;
    }
}
