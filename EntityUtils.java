import java.util.Collection;

public final class EntityUtils {
    private EntityUtils() { }

    /**
     * ? extends BaseEntity: можна передати List<Pizza>, Set<Customer> тощо.
     * Читати елементи як BaseEntity безпечно. Додавати (крім null) не можна.
     */
    public static void printAll(Collection<? extends BaseEntity> items) {
        for (BaseEntity item : items) {
            IO.println("  " + item);
        }
    }
}
