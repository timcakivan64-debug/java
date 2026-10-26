import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/** Демонстрація ЛР7–9: CRUD, сортування, wildcard. */
public class CrudDemo {

    public static void run() {
        IO.println();
        IO.println("################ ЛР7–9: ТИПІЗОВАНИЙ CRUD-СЕРВІС ################");
        PizzaServiceImpl pizzas = new PizzaServiceImpl();
        demoCrud(pizzas);
        demoSorting(pizzas);
        demoSameServiceOtherEntity();
        demoWildcards(pizzas);
    }

    private static void demoCrud(PizzaService service) {
        IO.println();
        IO.println("=== CRUD: додавання, оновлення, видалення, пошук ===");
        service.save(new PepperoniPizza(2, 40, false, false));
        service.save(new MargheritaPizza(1, 30, false));
        service.save(new PepperoniPizza(3, 30, false, false)); // те саме ім'я, інша ціна
        service.save(new HawaiianPizza(4, 30, false));
        IO.println("Після додавання 4 піц: записів = " + service.findAll().size());

        service.save(new MargheritaPizza(1, 35, true)); // той самий id -> оновлення
        IO.println("Після оновлення id=1: записів = " + service.findAll().size()
                + ", нова версія: " + service.findById(1));

        IO.println("delete(4) = " + service.delete(4));
        IO.println("delete(4) ще раз = " + service.delete(4) + " (такого id вже немає)");
        IO.println("Записів = " + service.findAll().size());

        try {
            service.findById(999);
        } catch (EntityNotFoundException e) {
            IO.println("Спіймано EntityNotFoundException: " + e.getMessage() + " (id=" + e.getId() + ")");
        }
        try {
            service.save(null);
        } catch (NullPointerException e) {
            IO.println("save(null) -> NullPointerException: " + e.getMessage());
        }
        service.save(new HawaiianPizza(4, 25, false)); // повертаємо для тесту сортування
    }

    private static void demoSorting(PizzaServiceImpl service) {
        IO.println();
        IO.println("=== Сортування (сховище не змінюється) ===");
        List<Pizza> before = service.findAll();
        IO.println("До сортування (порядок збереження):");
        EntityUtils.printAll(before);

        IO.println("findAllSortedByName() (за іменем):");
        EntityUtils.printAll(service.findAllSortedByName());

        IO.println("findAllSortedByNameAndPrice() (ім'я, потім ціна):");
        EntityUtils.printAll(service.findAllSortedByNameAndPrice());

        IO.println("findAllSorted(за ціною спаданням) — довільний компаратор:");
        EntityUtils.printAll(service.findAllSorted(Comparator.comparingDouble(Pizza::getPrice).reversed()));

        IO.println("Comparator<BaseEntity> (? super T) підходить для Pizza:");
        Comparator<BaseEntity> byId = Comparator.comparingLong(BaseEntity::getId);
        EntityUtils.printAll(service.findAllSorted(byId));

        List<Pizza> after = service.findAll();
        IO.println("Порядок і склад сховища не змінились: " + before.equals(after));

        List<Pizza> sorted = service.findAllSortedByName();
        sorted.clear(); // зміна результату не впливає на сховище
        IO.println("Після очищення копії у сховищі записів: " + service.findAll().size());
    }

    private static void demoSameServiceOtherEntity() {
        IO.println();
        IO.println("=== Той самий InMemoryCrudService працює з Customer ===");
        CustomerServiceImpl customers = new CustomerServiceImpl();
        customers.save(new Customer(1, "Іван", "+380501111111", 120));
        customers.save(new Customer(2, "Олена", "+380502222222", 40));
        customers.save(new Customer(3, "Іван", "+380503333333", 15));
        IO.println("Сортування за іменем і бонусами:");
        EntityUtils.printAll(customers.findAllSortedByNameAndBonus());

        // Без жодних змін у класі — працює і напряму:
        CRUDService<Customer> plain = new InMemoryCrudService<>();
        plain.save(new Customer(10, "Петро", "+380504444444", 0));
        IO.println("InMemoryCrudService<Customer>: " + plain.findById(10));
    }

    private static void demoWildcards(PizzaService service) {
        IO.println();
        IO.println("=== Wildcard: printAll(Collection<? extends BaseEntity>) ===");
        List<Pizza> pizzas = service.findAll();
        EntityUtils.printAll(pizzas); // List<Pizza> приймається завдяки ? extends
        // List<BaseEntity> entities = pizzas; // ПОМИЛКА КОМПІЛЯЦІЇ: List<Pizza> не є List<BaseEntity>
        // Якби це було дозволено, entities.add(new Customer(...)) поклав би клієнта в список піц.
        Collection<? extends BaseEntity> view = pizzas;
        IO.println("Читати через wildcard можна, елементів: " + view.size()
                + "; додати (крім null) не можна");
        List<BaseEntity> mixed = new ArrayList<>(pizzas);
        mixed.add(new Customer(77, "Марія", "+380505555555", 5));
        IO.println("Змішаний List<BaseEntity>:");
        EntityUtils.printAll(mixed);
    }
}
