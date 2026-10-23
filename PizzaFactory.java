import java.util.ArrayList;
import java.util.List;

/**
 * Замінює ланцюжок if/else за номером меню з Main (ЛР1–5): тип піци більше
 * не визначається рядком, а створюється відповідний підклас Pizza.
 */
public final class PizzaFactory {
    public static final int MENU_SIZE = 5;

    private PizzaFactory() { }

    public static void requireValidMenuNumber(int menuNumber) {
        if (menuNumber < 1 || menuNumber > MENU_SIZE) {
            throw new InvalidMenuItemException("У меню немає піци з номером " + menuNumber, menuNumber);
        }
    }

    public static Pizza create(int menuNumber, int diameter, boolean extraCheese) {
        requireValidMenuNumber(menuNumber);
        return switch (menuNumber) {
            case 1 -> new MargheritaPizza(diameter, extraCheese);
            case 2 -> new PepperoniPizza(diameter, extraCheese, false);
            case 3 -> new FourCheesePizza(diameter, extraCheese);
            case 4 -> new HawaiianPizza(diameter, extraCheese);
            default -> new MeatPizza(diameter, extraCheese);
        };
    }

    /** Меню для показу: піци стандартного розміру 30 см (поліморфний список). */
    public static List<Pizza> menu() {
        List<Pizza> items = new ArrayList<>();
        for (int i = 1; i <= MENU_SIZE; i++) {
            items.add(create(i, Pizza.DEFAULT_DIAMETER, false));
        }
        return items;
    }
}
