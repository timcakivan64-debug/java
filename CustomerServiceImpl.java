import java.util.Comparator;
import java.util.List;

public class CustomerServiceImpl extends InMemoryCrudService<Customer> implements CustomerService {
    @Override
    public List<Customer> findAllSortedByNameAndBonus() {
        Comparator<Customer> cmp = EntityComparators.<Customer>byName()
                .thenComparingInt(Customer::getBonusPoints);
        return findAllSorted(cmp);
    }
}
