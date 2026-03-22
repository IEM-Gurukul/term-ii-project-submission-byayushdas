package util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique investment IDs.
 */
public class IdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1000);

    private IdGenerator() {}

    public static String generate(String prefix) {
        return prefix.toUpperCase() + "-" + counter.getAndIncrement();
    }
}
