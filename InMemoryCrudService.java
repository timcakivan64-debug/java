import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Універсальне сховище в пам'яті для будь-якої сутності. Не містить коду,
 * що залежить від конкретного типу, тому працює і з Pizza, і з Customer.
 * Сховище — LinkedHashMap<Long, T>: пошук/запис/видалення за id за O(1),
 * порядок додавання зберігається. new T[...] не потрібен — масивів немає.
 */
public class InMemoryCrudService<T extends BaseEntity> implements CRUDService<T> {

    private final Map<Long, T> storage = new LinkedHashMap<>();

    @Override
    public T findById(long id) {
        T entity = storage.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        return entity;
    }

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "entity не може бути null");
        storage.put(entity.getId(), entity); // новий id — додає, наявний — замінює значення
        return entity;
    }

    @Override
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator не може бути null");
        List<T> copy = new ArrayList<>(storage.values()); // нова колекція
        copy.sort(comparator);                              // сортуємо копію
        return copy;
    }
}
