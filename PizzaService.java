import java.util.List;

/**
 * Інтерфейс саме для піци: сортування «за іменем, потім за ціною» залежить від
 * поля price, якого немає в BaseEntity, тому в загальному CRUDService<T> цього
 * методу бути не може.
 */
public interface PizzaService extends CRUDService<Pizza> {
    List<Pizza> findAllSortedByNameAndPrice();
}
