import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Частина ЛР №3: обробка винятків у предметній області «піцерія».
 *
 *  Рівень 1: try-catch з КОНКРЕТНИМИ типами, зрозумілі повідомлення, finally.
 *  Рівень 2: власний checked-виняток InvalidQuantityException (повідомлення + поле),
 *            throw у PizzaOrder, окремий catch.
 *  Рівень 3: кілька catch у правильному порядку, re-throw, ієрархія винятків
 *            (PizzeriaException -> InvalidDiameterException, InvalidTipException).
 */
public class ExceptionsDemo {

    public static void run() {
        level1();
        level2();
        level3();
    }

    // ===================== Рівень 1 =====================
    private static void level1() {
        System.out.println("\n===== ЛР3, рівень 1: try-catch-finally =====");

        // Ситуація 1: ділення на нуль (середня кількість піц у порожньому списку замовлень)
        try {
            System.out.println("Середня кількість піц у замовленні: " + averageQuantity(new int[] {2, 4, 6}));
            System.out.println("Середня кількість піц у замовленні: " + averageQuantity(new int[] {}));
        } catch (ArithmeticException e) {
            System.out.println("Помилка: неможливо порахувати середнє — список замовлень порожній (ділення на нуль).");
        }

        // Ситуація 2: вихід за межі масиву (номер позиції меню)
        String[] menu = {"Маргарита", "Пепероні", "Гавайська"};
        int requested = 5;
        try {
            System.out.println("Позиція меню №" + requested + ": " + menu[requested]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Помилка: у меню лише " + menu.length + " позиції, а запитано індекс " + requested + ".");
        }

        // Ситуація 3: некоректне введення з Scanner (замість числа — текст)
        // try-with-resources: Scanner закривається автоматично, а finally лишається для підсумкової дії
        try (Scanner scanner = new Scanner("три")) {   // імітація введення користувача
            System.out.print("Введіть кількість піц (число): ");
            int qty = scanner.nextInt();
            System.out.println("Прочитано: " + qty);
        } catch (InputMismatchException e) {
            System.out.println("\nПомилка: кількість треба вводити цифрами, а не словами.");
        } finally {
            System.out.println("[finally] Обробку введення завершено (Scanner закрито автоматично).");
        }
    }

    /** Ціле ділення: на порожньому масиві кидає ArithmeticException. */
    private static int averageQuantity(int[] quantities) {
        int sum = 0;
        for (int q : quantities) sum += q;
        return sum / quantities.length;
    }

    // ===================== Рівень 2 =====================
    private static void level2() {
        System.out.println("\n===== ЛР3, рівень 2: власний checked-виняток =====");
        try {
            PizzaOrder order = new PizzaOrder("Іван", "Пепероні", 231.25, 30, 70, new NoDiscount());
            System.out.println("Замовлення створено: " + order.toReceiptLine());
        } catch (InvalidQuantityException e) {
            System.out.println("Порушено доменне правило: " + e.getMessage());
            System.out.println("Некоректне значення, що спричинило виняток: " + e.getInvalidQuantity());
        }
    }

    // ===================== Рівень 3 =====================
    private static void level3() {
        System.out.println("\n===== ЛР3, рівень 3: кілька catch, ієрархія, re-throw =====");

        System.out.println("\n-- Кілька catch в одному try (від специфічних до загальних) --");
        tryOrder("Валідне замовлення", 30, 2, 20.0);
        tryOrder("Поганий діаметр", 28, 2, 0.0);
        tryOrder("Погана кількість", 30, 0, 0.0);
        tryOrder("Від'ємні чайові", 30, 2, -10.0);
        tryOrder("Порожнє ім'я клієнта", 30, 2, 0.0, "  ");

        System.out.println("\n-- Ієрархія: catch БАЗОВОГО класу ловить обидва підтипи --");
        int[] diameters = {28, 30};
        double[] tips = {0.0, -5.0};
        for (int i = 0; i < 2; i++) {
            try {
                PizzaOrder o = new PizzaOrder("Олена", "Маргарита", 120.0, diameters[i], 1, new NoDiscount());
                o.addTip(tips[i]);
                System.out.println("OK");
            } catch (PizzeriaException e) { // базовий клас: InvalidDiameterException і InvalidTipException
                System.out.println("PizzeriaException перехопив " + e.getClass().getSimpleName()
                        + ": " + e.getMessage());
            } catch (InvalidQuantityException e) {
                System.out.println("Кількість: " + e.getMessage());
            }
        }

        System.out.println("\n-- Повторне збудження (re-throw) --");
        try {
            createWithLogging("Петро", "Чотири сири", 262.50, 33, 6);
        } catch (InvalidQuantityException e) {
            System.out.println("[верхній рівень] кількість: " + e.getMessage());
        } catch (PizzeriaException e) {
            System.out.println("[верхній рівень] остаточна обробка: " + e.getMessage()
                    + " -> замовлення скасовано.");
        }
    }

    /** Кілька catch: порядок — від найспецифічніших до найзагальніших. */
    private static void tryOrder(String title, int diameter, int quantity, double tip) {
        tryOrder(title, diameter, quantity, tip, "Клієнт");
    }

    private static void tryOrder(String title, int diameter, int quantity, double tip, String customer) {
        System.out.print(title + ": ");
        try {
            PizzaOrder o = new PizzaOrder(customer, "Пепероні", 231.25, diameter, quantity, new NoDiscount());
            o.addTip(tip);
            System.out.println("замовлення прийнято, сума " + String.format("%.2f", o.getTotal()) + " грн");
        } catch (InvalidDiameterException e) {                 // 1) найспецифічніший unchecked
            System.out.println("діаметр " + e.getInvalidDiameter() + " недопустимий.");
        } catch (InvalidTipException e) {                      // 2) інший специфічний підтип
            System.out.println("чайові " + e.getInvalidTip() + " недопустимі.");
        } catch (InvalidQuantityException e) {                 // 3) власний checked
            System.out.println("кількість " + e.getInvalidQuantity() + " недопустима.");
        } catch (PizzeriaException e) {                        // 4) базовий доменний
            System.out.println("доменна помилка: " + e.getMessage());
        } catch (IllegalArgumentException e) {                 // 5) стандартний виняток валідації
            System.out.println("некоректні дані: " + e.getMessage());
        } catch (Exception e) {                                // 6) найзагальніший — останнім
            System.out.println("непередбачена помилка: " + e);
        }
        // Якби catch (Exception) стояв першим, решта блоків стали б недосяжними — помилка компіляції.
    }

    /** Ловить виняток, частково обробляє (лог) і кидає ДАЛІ на вищий рівень. */
    private static PizzaOrder createWithLogging(String customer, String pizza, double price,
                                                int diameter, int quantity) throws InvalidQuantityException {
        try {
            return new PizzaOrder(customer, pizza, price, diameter, quantity, new NoDiscount());
        } catch (InvalidDiameterException e) {
            System.out.println("[LOG] Нижній рівень: діаметр " + e.getInvalidDiameter()
                    + " відхилено, передаю виняток вище.");
            throw e; // re-throw
        }
    }
}
