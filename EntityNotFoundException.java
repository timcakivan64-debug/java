/**
 * Сутність з таким id не знайдена. UNCHECKED (через PizzeriaException ->
 * RuntimeException): відсутній id — це здебільшого помилка виклику/даних, а не
 * ситуація, яку треба обробляти на кожному кроці; checked-виняток змусив би
 * писати try/catch навколо кожного findById, ламав би лямбди й стріми, а
 * сигнатуру інтерфейсу довелося б міняти при заміні сховища на БД.
 */
public class EntityNotFoundException extends PizzeriaException {
    private final long id;

    public EntityNotFoundException(long id) {
        super("Сутність з id=" + id + " не знайдена");
        this.id = id;
    }

    public long getId() { return id; }
}
