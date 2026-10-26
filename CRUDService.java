import java.util.Comparator;
import java.util.List;

/**
 * Типізований CRUD-сервіс. T extends BaseEntity: без обмеження T був би
 * «будь-яким Object», і всередині сервісу не можна було б викликати getId()
 * і getName() (потрібні для save/findById і сортування за іменем).
 */
public interface CRUDService<T extends BaseEntity> {

    /** @throws EntityNotFoundException якщо сутності з таким id немає */
    T findById(long id);

    /** Додає нову сутність або оновлює наявну (за id). Повертає збережену сутність. */
    T save(T entity);

    /** @return true, якщо сутність була видалена; false, якщо такого id не було */
    boolean delete(long id);

    /** Копія всіх записів у порядку зберігання. */
    List<T> findAll();

    /**
     * Нова відсортована колекція; сховище не змінюється.
     * Comparator<? super T>: підходить будь-який компаратор для T або його предка
     * (наприклад Comparator<BaseEntity> можна використати для списку Pizza).
     */
    List<T> findAllSorted(Comparator<? super T> comparator);

    /** Сортування за іменем реалізовано один раз для всіх сутностей. */
    default List<T> findAllSortedByName() {
        return findAllSorted(EntityComparators.byName());
    }
}
