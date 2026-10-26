import java.util.concurrent.atomic.AtomicLong;

/** Генератор унікальних id для нових сутностей (потокобезпечний). */
public final class IdGenerator {
    private static final AtomicLong COUNTER = new AtomicLong(1000);

    private IdGenerator() { }

    public static long next() {
        return COUNTER.incrementAndGet();
    }
}
