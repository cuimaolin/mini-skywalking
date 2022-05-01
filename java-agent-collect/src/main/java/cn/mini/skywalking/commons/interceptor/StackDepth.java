package cn.mini.skywalking.commons.interceptor;

public class StackDepth {
    private int stackDepth;

    public StackDepth() {
        this.stackDepth = 0;
    }

    public int depth() {
        return this.stackDepth;
    }

    public StackDepth increment() {
        this.stackDepth++;
        return this;
    }

    public StackDepth decrement() {
        this.stackDepth--;
        return this;
    }
}
