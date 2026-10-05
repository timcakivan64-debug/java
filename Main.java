import java.util.Arrays;
import java.util.Locale;

/*
 * ЛР4. Інтерфейси, абстрактні класи, Strategy — продовження домену піцерії.
 *
 *  Рівень 1 (базовий):
 *    - поведінка, що відрізняється: СПОСІБ ОПЛАТИ замовлення;
 *    - інтерфейс PaymentMethod з двома+ реалізаціями
 *      (CardPayment, CashPayment, WalletPayment);
 *    - масив PaymentMethod[] + цикл -> поліморфний виклик pay(...).
 *
 *  Рівень 2 (середній):
 *    - default-метод printPaymentHeader(...) в PaymentMethod
 *      (успадкований Card/Cash, перевизначений у Wallet);
 *    - абстрактний клас AbstractPaymentMethod з спільною валідацією
 *      (використовують Card і Cash; Wallet свідомо — ні, "чистий" інтерфейс);
 *    - PizzaOrder реалізує ОДРАЗУ два інтерфейси: Comparable (сортування)
 *      і Receiptable (бізнес-поведінка — формування рядка чека).
 *
 *  Рівень 3 (високий) — паттерн Strategy:
 *    - DiscountStrategy — інтерфейс-стратегія з трьома реалізаціями;
 *    - PizzaOrder (контекст) приймає стратегію через конструктор
 *      і дозволяє ЗМІНИТИ її під час виконання через setDiscountStrategy(...).
 */
void main() {
    Locale.setDefault(Locale.US);

    IO.println("==============================================");
    IO.println("   ЛР4. Інтерфейси, абстрактні класи, Strategy");
    IO.println("==============================================");

    // ---------------- Рівень 1: масив об'єктів інтерфейсу + поліморфізм ----------------
    IO.println();
    IO.println("--- Оплата замовлення різними способами (PaymentMethod) ---");
    PaymentMethod[] payments = {
            new CardPayment("4521"),
            new CashPayment(),
            new WalletPayment(50.0)
    };
    double orderTotalForPaymentDemo = 739.38;
    for (PaymentMethod method : payments) {
        method.printPaymentHeader("Іван");     // default-метод (у Wallet — перевизначений)
        method.pay(orderTotalForPaymentDemo);  // у кожного класу — своя реалізація
        IO.println();
    }

    // ---------------- Рівень 2: клас із двома інтерфейсами одночасно ----------------
    IO.println("--- Замовлення: Receiptable (бізнес-поведінка) ---");
    PizzaOrder order1 = new PizzaOrder("Іван", "Пепероні", 231.25, 3, new TieredQuantityDiscount());
    PizzaOrder order2 = new PizzaOrder("Олена", "Маргарита", 120.00, 1, new NoDiscount());
    PizzaOrder order3 = new PizzaOrder("Петро", "Чотири сири", 262.50, 6, new TieredQuantityDiscount());
    PizzaOrder[] orders = { order1, order2, order3 };

    for (PizzaOrder o : orders) {
        IO.println(o.toReceiptLine());
    }

    IO.println();
    IO.println("--- Ті самі замовлення: Comparable (поведінка сортування) ---");
    Arrays.sort(orders); // використовує compareTo(...) з PizzaOrder
    for (PizzaOrder o : orders) {
        IO.println(o.toReceiptLine());
    }

    // ---------------- Рівень 3: Strategy — зміна поведінки під час виконання ----------------
    IO.println();
    IO.println("--- Strategy: зміна стратегії знижки БЕЗ створення нового об'єкта ---");
    PizzaOrder promoOrder = new PizzaOrder("Марія", "Гавайська", 218.75, 2, new TieredQuantityDiscount());
    IO.println("До зміни стратегії:                              " + promoOrder.toReceiptLine());

    promoOrder.setDiscountStrategy(new PromoCodeDiscount(20));
    IO.println("Після setDiscountStrategy(PromoCodeDiscount 20%): " + promoOrder.toReceiptLine());

    promoOrder.setDiscountStrategy(new NoDiscount());
    IO.println("Після setDiscountStrategy(NoDiscount):            " + promoOrder.toReceiptLine());
}
