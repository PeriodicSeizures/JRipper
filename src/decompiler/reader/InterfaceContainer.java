package decompiler.reader;

import java.io.IOException;
import java.util.ArrayList;

public class InterfaceContainer extends RItem {

    private ArrayList<Integer> interfaces = new ArrayList<>();

    @Override
    public void read() throws IOException {

        int interfaces_count = bytes.readUnsignedShort();

        for (; interfaces_count > 0; interfaces_count--)
            interfaces.add(bytes.readUnsignedShort());
    }

    String[] getInterfaces() {
        String[] arr = new String[interfaces.size()];
        for (int index = 0; index < interfaces.size(); index++) {
            arr[index] = (String) getEntry(interfaces.get(index)).getValue();
        }
        return arr;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{InterfaceContainer} ").append("\n");

        for (String s : getInterfaces()) {
            stringBuilder.append(s).append(", ");
        }

        if (interfaces.isEmpty())
            stringBuilder.append("  -  ");

        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
