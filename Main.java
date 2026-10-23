import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

/*
 * ПІДСУМКОВА ПРОГРАМА: ЛР1 (меню й розрахунок) + ЛР3 (обробка винятків) +
 * ЛР4 (інтерфейси, абстрактний клас, Strategy) — ОДНА програма, де все
 * реально використовується в єдиному потоці замовлення, а не окремими
 * ізольованими демонстраціями.
 *
 *  З ЛР1: меню піци, розрахунок ціни за розміром, чек.
 *
 *  З ЛР3 (винятки реально ловлять помилки РЕАЛЬНОГО введення користувача):
 *    - InputMismatchException — нечислове введення (re-throw з readInt);
 *    - InvalidDiameterException, InvalidTipException — підкласи PizzeriaException;
 *    - InvalidQuantityException — власний CHECKED-виняток;
 *    - ArithmeticException — ділення кількості піц на 0 осіб;
 *    - один try з 6 catch-блоками (специфічні -> загальний PizzeriaException);
 *    - finally — гарантоване закриття Scanner.
 *
 *  З ЛР6: ієрархія Pizza (sealed) замінила рядок pizzaName та if/else за номером меню.
 *
 *  З ЛР4 (інтерфейси реально визначають поведінку РЕАЛЬНОГО замовлення):
 *    - PaymentMethod[] — масив інтерфейсного типу, цикл, поліморфізм;
 *    - PizzaOrder (Comparable + Receiptable) — контекст паттерна Strategy;
 *    - DiscountStrategy — реальна знижка замовлення МІНЯЄТЬСЯ "на льоту"
 *      (setDiscountStrategy), якщо клієнт вводить промокод;
 *    - реальна оплата замовлення відбувається через обраний PaymentMethod.
 */
void main() {
    Locale.setDefault(Locale.US);

    Scanner scanner = new Scanner(System.in);

    IO.println("==============================================");
    IO.println("        ПІЦЕРІЯ «ПРОСТО СМАЧНО» – МЕНЮ");
    IO.println("==============================================");
    // ЛР6: меню будується поліморфно зі списку Pizza, а не з окремих констант
    int menuIndex = 1;
    for (Pizza item : PizzaFactory.menu()) {
        System.out.printf("%d. %-16s %7.2f грн (30 см)%n", menuIndex++, item.getName(), item.getPrice());
    }
    IO.println("----------------------------------------------");
    IO.println("Розміри: 25 см (-20%), 30 см, 35 см (+25%), 40 см (+50%)");
    System.out.printf("Додатковий сир: +%.2f грн до кожної піци%n", Pizza.EXTRA_CHEESE_PRICE);
    IO.println("Знижка: від 3 піц – 10%, від 5 піц – 15% (якщо без промокоду)");
    IO.println("==============================================");

    IO.print("Ваше ім'я: ");
    String customerName = scanner.nextLine();

    // ---------- ЛР5, рівень 1 (Factory Method): жодного new тут більше немає ----------
    // Раніше (ЛР4) тут стояв масив PaymentMethod[] з трьома "new ...()" напряму.
    // Тепер меню лише ПОКАЗУЄ підписи, а створення об'єкта — відповідальність
    // PaymentMethodFactory (дивись цей клас і коментар у ньому).
    IO.println();
    IO.println("--- Доступні способи оплати ---");
    IO.println("1) банківська картка");
    IO.println("2) готівка");
    IO.println("3) бонусний гаманець");
    IO.println("4) стороння банківська система (демонстрація Adapter)");

    // ---------- ЛР3: один try з кількома catch — реальна обробка вводу ----------
    try {
        int menuNumber = readInt(scanner, "Номер піци з меню (1-5): ");

        PizzaFactory.requireValidMenuNumber(menuNumber); // InvalidMenuItemException

        int rawDiameter = readInt(scanner, "Діаметр піци, см (25/30/35/40): ");
        int diameter = Pizza.validateDiameter(rawDiameter); // може кинути InvalidDiameterException

        int rawQuantity = readInt(scanner, "Кількість піц: ");
        int quantity = validateQuantity(rawQuantity); // checked: InvalidQuantityException

        IO.print("Додатковий сир? (так/ні): ");
        String extraCheeseAnswer = scanner.next();
        boolean withExtraCheese = extraCheeseAnswer.equalsIgnoreCase("так");

        // ЛР6: if/else за номером замінено фабрикою, яка створює підклас Pizza
        Pizza pizza = PizzaFactory.create(menuNumber, diameter, withExtraCheese);
        IO.println("Обрано: " + pizza + " (склад: " + pizza.getIngredients() + ")");

        // ---------- ЛР4, рівень 3: Strategy — PizzaOrder як контекст ----------
        PizzaOrder order = new PizzaOrder(customerName, pizza, quantity,
                new TieredQuantityDiscount());

        // ---------- ЛР5, рівень 2 (Observer): підписуємо два незалежних спостерігачі ----------
        order.addObserver(new ReceiptPrinterObserver());
        order.addObserver(new AuditLogObserver());

        IO.println();
        IO.println("Проміжний чек (стандартна знижка за кількістю):");
        IO.println(order.toReceiptLine());

        IO.print("Є промокод? (так/ні): ");
        String hasPromo = scanner.next();
        if (hasPromo.equalsIgnoreCase("так")) {
            IO.print("Відсоток знижки за промокодом: ");
            double promoPercent = scanner.nextDouble();
            // Заміна стратегії "на льоту" (Strategy, ЛР4) — ТЕПЕР цей виклик ще й
            // автоматично розсилає подію обом спостерігачам (Observer, ЛР5):
            // ReceiptPrinterObserver одразу надрукує новий чек, AuditLogObserver — запише подію в лог.
            order.setDiscountStrategy(new PromoCodeDiscount(promoPercent));
        }

        IO.print("Чайові, грн (наприклад, 20.50): ");
        double rawTip = scanner.nextDouble();
        double tip = validateTip(rawTip); // може кинути InvalidTipException

        double totalWithTip = order.getTotal() + tip;

        IO.print("На скільки осіб ділите замовлення (0 — не ділити): ");
        int people = readInt(scanner, "");
        int pizzasPerPerson = quantity / people; // ArithmeticException, якщо people == 0

        IO.println();
        IO.println("==============================================");
        IO.println("                   ВАШ ЧЕК");
        IO.println("==============================================");
        IO.println(order.toReceiptLine());
        System.out.printf("Чайові:                 %.2f грн%n", tip);
        System.out.printf("ДО СПЛАТИ З ЧАЙОВИМИ:   %.2f грн%n", totalWithTip);
        System.out.printf("На кожну з %d осіб припадає приблизно %d піц(и).%n", people, pizzasPerPerson);

        // ---------- ЛР5: Factory (створення) + Observer (сповіщення після оплати) ----------
        int paymentChoice = readInt(scanner,
                "Яким способом оплачуєте? (1-картка, 2-готівка, 3-гаманець, 4-стороння система): ");
        PaymentMethod chosenPayment = PaymentMethodFactory.create(paymentChoice); // Factory Method

        // applyPayment(...) сам друкує заголовок, викликає pay(...) і розсилає подію "ОПЛАЧЕНО"
        // обом спостерігачам — Main більше не керує цим напряму (раніше викликав pay() сам).
        order.applyPayment(chosenPayment, tip);

        IO.println("==============================================");
        IO.println("Дякуємо за замовлення, " + customerName + "! Смачного!");

    } catch (InputMismatchException e) {
        IO.println("Помилка: ви ввели текст там, де очікувалося число. " +
                "Перезапустіть програму і вводьте лише цифри.");
    } catch (InvalidQuantityException e) {
        IO.println("Помилка кількості: " + e.getMessage() +
                " (введено значення: " + e.getInvalidQuantity() + ")");
    } catch (InvalidMenuItemException e) {
        IO.println("Помилка: у меню немає піци з таким номером.");
    } catch (InvalidDiameterException e) {
        IO.println("Помилка діаметра: " + e.getMessage());
    } catch (InvalidTipException e) {
        IO.println("Помилка чайових: " + e.getMessage());
    } catch (ArithmeticException e) {
        IO.println("Помилка: неможливо розділити замовлення на 0 осіб.");
    } catch (PizzeriaException e) {
        IO.println("Загальна помилка піцерії: " + e.getMessage());
    } finally {
        IO.println("----------------------------------------------");
        IO.println("Роботу з електронним меню завершено.");
        scanner.close();
    }

    // ---------- Бонус-демонстрація: Comparable — сортування кількох замовлень ----------
    demoSortingOrders();

    // ---------- Бонус-демонстрація: catch(PizzeriaException) ловить різні підтипи ----------
    demoBaseClassCatch();

    // ---------- ЛР6: демонстрація наслідування, equals, sealed, композиції ----------
    InheritanceDemo.run();
}

/** Re-throw (ЛР3): логує помилку формату і кидає її далі — обробка завершується в main(). */
private static int readInt(Scanner scanner, String prompt) {
    if (!prompt.isEmpty()) {
        IO.print(prompt);
    }
    try {
        return scanner.nextInt();
    } catch (InputMismatchException e) {
        System.out.println("[LOG] Некоректний формат числа при введенні.");
        scanner.nextLine();
        throw e;
    }
}

/** Доменна перевірка (ЛР3): інший підклас тієї самої ієрархії. */
private static double validateTip(double tip) {
    if (tip < 0) {
        throw new InvalidTipException("Чайові не можуть бути від'ємними: " + tip, tip);
    }
    return tip;
}

/** Доменна перевірка (ЛР3): власний CHECKED-виняток. */
private static int validateQuantity(int quantity) throws InvalidQuantityException {
    if (quantity <= 0 || quantity > 50) {
        throw new InvalidQuantityException(
                "Кількість піц має бути від 1 до 50, отримано: " + quantity,
                quantity);
    }
    return quantity;
}

/** ЛР4, рівень 2: Comparable — сортування об'єктів PizzaOrder за сумою. */
private static void demoSortingOrders() {
    IO.println();
    IO.println("=== Comparable: сортування кількох замовлень за сумою ===");
    PizzaOrder o1 = new PizzaOrder("Іван", new PepperoniPizza(35, false, false), 3, new TieredQuantityDiscount());
    PizzaOrder o2 = new PizzaOrder("Олена", new MargheritaPizza(25, false), 1, new NoDiscount());
    PizzaOrder o3 = new PizzaOrder("Петро", new FourCheesePizza(35, false), 6, new TieredQuantityDiscount());
    PizzaOrder[] orders = { o1, o2, o3 };
    Arrays.sort(orders); // використовує compareTo(...) з PizzaOrder
    for (PizzaOrder o : orders) {
        IO.println(o.toReceiptLine());
    }
}

/** ЛР3, рівень 3: один і той самий catch(PizzeriaException) ловить різні підтипи. */
private static void demoBaseClassCatch() {
    IO.println();
    IO.println("=== catch(PizzeriaException) ловить різні підтипи ===");
    try {
        Pizza.validateDiameter(99);
    } catch (PizzeriaException e) {
        IO.println("Спіймано через базовий клас: " + e.getClass().getSimpleName() +
                " -> " + e.getMessage());
    }
    try {
        validateTip(-5.0);
    } catch (PizzeriaException e) {
        IO.println("Спіймано через базовий клас: " + e.getClass().getSimpleName() +
                " -> " + e.getMessage());
    }
}
