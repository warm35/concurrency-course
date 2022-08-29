package course.concurrency.m3_shared.intro;

import java.util.HashSet;
import java.util.Set;

public class SimpleCode {
    private static Set<Integer> set = new HashSet<Integer>();

    public static volatile String a;

    public static void main(String[] args) throws InterruptedException {
        final int size = 50_000_000;
        Object[] objects = new Object[size];
        for (int i = 0; i < size; ++i) {
            objects[i] = new Object();
        }
    }
}
