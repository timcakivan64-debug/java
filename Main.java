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
 *  З ЛР4 (інтерфейси реально визначають поведінку РЕАЛЬНОГО замовлення):
 *    - PaymentMethod[] — масив інтерфейсного типу, цикл, поліморфізм;
 *    - PizzaOrder (Comparable + Receiptable) — контекст паттерна Strategy;
 *    - DiscountStrategy — реальна знижка замовлення МІНЯЄТЬСЯ "на льоту"
 *      (setDiscountStrategy), якщо клієнт вводить промокод;
 *    - реальна оплата замовлення відбувається через обраний PaymentMethod.
 */
void main() {
    Locale.setDefault(Locale.US);

    final double PRICE_MARGARITA = 150.00;
    final double PRICE_PEPPERONI = 185.00;
    final double PRICE_FOUR_CHEESE = 210.00;
    final double PRICE_HAWAIIAN = 175.00;
    final double PRICE_MEAT = 220.00;
    final double EXTRA_CHEESE_PRICE = 35.00;

    Scanner scanner = new Scanner(System.in);

    IO.println("==============================================");
    IO.println("        ПІЦЕРІЯ «ПРОСТО СМАЧНО» – МЕНЮ");
    IO.println("==============================================");
    System.out.printf("1. Маргарита       %7.2f грн (30 см)%n", PRICE_MARGARITA);
    System.out.printf("2. Пепероні        %7.2f грн (30 см)%n", PRICE_PEPPERONI);
    System.out.printf("3. Чотири сири     %7.2f грн (30 см)%n", PRICE_FOUR_CHEESE);
    System.out.printf("4. Гавайська       %7.2f грн (30 см)%n", PRICE_HAWAIIAN);
    System.out.printf("5. М'ясна          %7.2f грн (30 см)%n", PRICE_MEAT);
    IO.println("----------------------------------------------");
    IO.println("Розміри: 25 см (-20%), 30 см, 35 см (+25%), 40 см (+50%)");
    System.out.printf("Додатковий сир: +%.2f грн до кожної піци%n", EXTRA_CHEESE_PRICE);
    IO.println("Знижка: від 3 піц – 10%, від 5 піц – 15% (якщо без промокоду)");
    IO.println("==============================================");

    IO.print("Ваше ім'я: ");
    String customerName = scanner.nextLine();

    // ---------- ЛР4, рівень 1: масив об'єктів інтерфейсу + поліморфізм ----------
    IO.println();
    IO.println("--- Доступні способи оплати (PaymentMethod) ---");
    PaymentMethod[] availablePayments = {
            new CardPayment("4521"),
            new CashPayment(),
            new WalletPayment(50.0)
    };
    for (int i = 0; i < availablePayments.length; i++) {
        IO.println((i + 1) + ") " + availablePayments[i].getLabel());
    }

    // ---------- ЛР3: один try з кількома catch — реальна обробка вводу ----------
    try {
        int menuNumber = readInt(scanner, "Номер піци з меню (1-5): ");

        String pizzaName;
        double basePrice;
        if (menuNumber == 1) {
            pizzaName = "Маргарита";
            basePrice = PRICE_MARGARITA;
        } else if (menuNumber == 2) {
            pizzaName = "Пепероні";
            basePrice = PRICE_PEPPERONI;
        } else if (menuNumber == 3) {
            pizzaName = "Чотири сири";
            basePrice = PRICE_FOUR_CHEESE;
        } else if (menuNumber == 4) {
            pizzaName = "Гавайська";
            basePrice = PRICE_HAWAIIAN;
        } else if (menuNumber == 5) {
            pizzaName = "М'ясна";
            basePrice = PRICE_MEAT;
        } else {
            IO.println("Помилка: у меню немає піци з таким номером.");
            return; // finally все одно виконається і закриє scanner
        }

        int rawDiameter = readInt(scanner, "Діаметр піци, см (25/30/35/40): ");
        int diameter = validateDiameter(rawDiameter); // може кинути InvalidDiameterException

        double sizeCoefficient;
        if (diameter == 25) sizeCoefficient = 0.80;
        else if (diameter == 30) sizeCoefficient = 1.00;
        else if (diameter == 35) sizeCoefficient = 1.25;
        else sizeCoefficient = 1.50; // diameter == 40

        int rawQuantity = readInt(scanner, "Кількість піц: ");
        int quantity = validateQuantity(rawQuantity); // checked: InvalidQuantityException

        IO.print("Додатковий сир? (так/ні): ");
        String extraCheeseAnswer = scanner.next();
        boolean withExtraCheese = extraCheeseAnswer.equalsIgnoreCase("так");

        double unitPrice = basePrice * sizeCoefficient;
        if (withExtraCheese) unitPrice += EXTRA_CHEESE_PRICE;

        // ---------- ЛР4, рівень 3: Strategy — PizzaOrder як контекст ----------
        PizzaOrder order = new PizzaOrder(customerName, pizzaName, unitPrice, quantity,
                new TieredQuantityDiscount());
        IO.println();
        IO.println("Проміжний чек (стандартна знижка за кількістю):");
        IO.println(order.toReceiptLine());

        IO.print("Є промокод? (так/ні): ");
        String hasPromo = scanner.next();
        if (hasPromo.equalsIgnoreCase("так")) {
            IO.print("Відсоток знижки за промокодом: ");
            double promoPercent = scanner.nextDouble();
            order.setDiscountStrategy(new PromoCodeDiscount(promoPercent)); // заміна стратегії "на льоту"
            IO.println("Чек після застосування промокоду:");
            IO.println(order.toReceiptLine());
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

        // ---------- Оплата РЕАЛЬНОГО замовлення обраним способом ----------
        int paymentChoice = readInt(scanner, "Яким способом оплачуєте? (1-картка, 2-готівка, 3-гаманець): ");
        PaymentMethod chosenPayment;
        if (paymentChoice == 2) chosenPayment = availablePayments[1];
        else if (paymentChoice == 3) chosenPayment = availablePayments[2];
        else chosenPayment = availablePayments[0];

        chosenPayment.printPaymentHeader(customerName); // default-метод (у Wallet — перевизначений)
        chosenPayment.pay(totalWithTip);                // своя реалізація в кожному класі

        IO.println("==============================================");
        IO.println("Дякуємо за замовлення, " + customerName + "! Смачного!");

    } catch (InputMismatchException e) {
        IO.println("Помилка: ви ввели текст там, де очікувалося число. " +
                "Перезапустіть програму і вводьте лише цифри.");
    } catch (InvalidQuantityException e) {
        IO.println("Помилка кількості: " + e.getMessage() +
                " (введено значення: " + e.getInvalidQuantity() + ")");
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

/** Доменна перевірка (ЛР3): кидає підклас ієрархії PizzeriaException. */
private static int validateDiameter(int diameter) {
    if (diameter != 25 && diameter != 30 && diameter != 35 && diameter != 40) {
        throw new InvalidDiameterException(
                "Непідтримуваний діаметр " + diameter + " см. Доступні: 25, 30, 35, 40.",
                diameter);
    }
    return diameter;
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
    PizzaOrder o1 = new PizzaOrder("Іван", "Пепероні", 231.25, 3, new TieredQuantityDiscount());
    PizzaOrder o2 = new PizzaOrder("Олена", "Маргарита", 120.00, 1, new NoDiscount());
    PizzaOrder o3 = new PizzaOrder("Петро", "Чотири сири", 262.50, 6, new TieredQuantityDiscount());
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
        validateDiameter(99);
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
