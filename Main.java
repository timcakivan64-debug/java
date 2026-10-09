import java.util.Arrays;
import java.util.Locale;

/**
 * Об'єднана ЛР №3 + ЛР №4 — предметна область «піцерія».
 *
 *  ЛР3: обробка винятків (ExceptionsDemo, PizzeriaException та підкласи,
 *       InvalidQuantityException).
 *  ЛР4: інтерфейси, абстрактний клас, default-метод, Strategy
 *       (PaymentMethod, DiscountStrategy, Receiptable, Comparable).
 */
public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        System.out.println("==============================================");
        System.out.println("  ЛР3 + ЛР4. Винятки, інтерфейси, Strategy");
        System.out.println("==============================================");

        // ---------------- ЛР3 ----------------
        ExceptionsDemo.run();

        // ---------------- ЛР4 ----------------
        System.out.println("\n\n==============================================");
        System.out.println("  ЛР4. Інтерфейси, абстрактні класи, Strategy");
        System.out.println("==============================================");
        try {
            level1and2();
            level3();
        } catch (InvalidQuantityException e) {
            System.out.println("Помилка замовлення: " + e.getMessage()
                    + " (отримано: " + e.getInvalidQuantity() + ")");
        }
    }

    private static void level1and2() throws InvalidQuantityException {
        // ---------- Рівень 1: масив типу інтерфейсу + поліморфний виклик ----------
        System.out.println("\n--- Рівень 1: оплата різними способами (PaymentMethod[]) ---");
        PaymentMethod[] payments = {
                new CardPayment("4521"),
                new CashPayment(),
                new WalletPayment(50.0)
        };
        PizzaOrder demo = new PizzaOrder("Іван", "Пепероні", 231.25, 30, 3, new TieredQuantityDiscount());
        for (PaymentMethod method : payments) {
            double paid = demo.checkout(method); // default-метод + pay() у кожного свій
            System.out.printf("   покрито цим способом: %.2f з %.2f грн%n%n", paid, demo.getTotal());
        }

        // ---------- Рівень 2: два інтерфейси в одному класі ----------
        System.out.println("--- Рівень 2: Receiptable (бізнес-поведінка) ---");
        PizzaOrder[] orders = {
                demo,
                new PizzaOrder("Олена", "Маргарита", 120.00, 25, 1, new NoDiscount()),
                new PizzaOrder("Петро", "Чотири сири", 262.50, 40, 6, new TieredQuantityDiscount())
        };
        for (PizzaOrder o : orders) {
            System.out.println(o.toReceiptLine());
        }

        System.out.println("\n--- Рівень 2: Comparable (сортування за сумою) ---");
        Arrays.sort(orders);
        for (PizzaOrder o : orders) {
            System.out.println(o.toReceiptLine());
        }
    }

    private static void level3() throws InvalidQuantityException {
        // ---------- Рівень 3: Strategy, зміна поведінки під час виконання ----------
        System.out.println("\n--- Рівень 3: Strategy — зміна стратегії на ТОМУ САМОМУ об'єкті ---");
        PizzaOrder order = new PizzaOrder("Марія", "Гавайська", 218.75, 35, 2, new TieredQuantityDiscount());
        System.out.println("Початкова (через конструктор): " + order.toReceiptLine());

        DiscountStrategy[] strategies = {
                new PromoCodeDiscount(20),
                new NoDiscount(),
                new TieredQuantityDiscount()
        };
        for (DiscountStrategy s : strategies) {
            order.setDiscountStrategy(s); // runtime-заміна, новий PizzaOrder не створюється
            System.out.println("setDiscountStrategy(" + s.getClass().getSimpleName() + "): "
                    + order.toReceiptLine());
        }
    }
}
