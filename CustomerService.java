import java.util.List;

public interface CustomerService extends CRUDService<Customer> {
    List<Customer> findAllSortedByNameAndBonus();
}
