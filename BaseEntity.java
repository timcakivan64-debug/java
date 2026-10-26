import java.util.Objects;

/**
 * Базова сутність (ЛР7–9). Усі сутності предметної області (Pizza, Customer)
 * успадковують її. id незмінний (final, сеттера немає).
 *
 * equals/hashCode — за id: два об'єкти одного класу з однаковим id — це той самий
 * запис. getClass() (а не instanceof) зберігає симетрію між різними типами.
 */
public abstract class BaseEntity {
    private final long id;
    private final String name;

    protected BaseEntity(long id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name не може бути null"); // захист від null у сортуванні
    }

    public long getId() { return id; }

    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((BaseEntity) o).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id + " " + name;
    }
}
