import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Компаратори. Порядок за іменем зроблено Comparator'ом, а не Comparable:
 * сутність має кілька порядків (за іменем, за ціною, за id), а Comparable
 * задає лише один «природний»; до того ж compareTo за іменем був би
 * неузгоджений з equals (за id).
 */
public final class EntityComparators {
    private EntityComparators() { }

    /** Ім'я порівнюється через Collator (правила української мови), null-safe. */
    public static <T extends BaseEntity> Comparator<T> byName() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("uk-UA"));
        return Comparator.comparing(BaseEntity::getName, Comparator.nullsLast(collator));
    }
}
