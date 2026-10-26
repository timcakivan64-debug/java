import java.util.Comparator;
import java.util.List;

public class PizzaServiceImpl extends InMemoryCrudService<Pizza> implements PizzaService {
    @Override
    public List<Pizza> findAllSortedByNameAndPrice() {
        Comparator<Pizza> cmp = EntityComparators.<Pizza>byName()
                .thenComparingDouble(Pizza::getPrice); // при однаковому імені — за ціною
        return findAllSorted(cmp);
    }
}
