package decompiler.linker;

import java.util.ArrayList;

public class OperandStack {

    private ArrayList<String> stack = new ArrayList<>();

    void push(String s) {
        stack.add(s);
    }

    String pop() {
        return stack.remove(stack.size() - 1);
    }

    String[] pop2() {
        String s1 = stack.remove(stack.size() - 1);
        String s2 = stack.remove(stack.size() - 1);
        return new String[] { s2, s1 };
    }

    void toByte() {
        String popped = this.pop();
        this.push("(byte)(" + popped + ")");
    }

    void toLong() {
        String popped = this.pop();
        this.push("(long)(" + popped + ")");
    }

    void toChar() {
        String popped = this.pop();
        this.push("(char)(" + popped + ")");
    }

    void toDouble() {
        String popped = this.pop();
        this.push("(double)(" + popped + ")");
    }

    void toFloat() {
        String popped = this.pop();
        this.push("(float)(" + popped + ")");
    }

    void toInt() {
        String popped = this.pop();
        this.push("(int)(" + popped + ")");
    }

    void neg() {
        String popped = this.pop();
        this.push("-" + popped);
    }

    void add() {
        String[] popped = this.pop2();
        this.push(popped[0] + " + " + popped[1]);
    }

    void sub() {
        String[] popped = this.pop2();
        this.push(popped[0] + " - " + popped[1]);
    }

    void mul() {
        String[] popped = this.pop2();
        this.push(popped[0] + " * " + popped[1]);
    }

    void div() {
        String[] popped = this.pop2();
        this.push(popped[0] + " / " + popped[1]);
    }




    //void executeNext(CodeAttribute codeAttribute, int index) {
    //
    //    if (codeAttribute.code.get(index))
    //
    //}

}