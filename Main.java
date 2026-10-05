import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

/*
 * ЛР3. Обробка винятків та власні підкласи Exception.
 * Продовження предметної області ЛР №1 — «Електронне меню піцерії».
 *
 * Реалізовано:
 *  Рівень 1 (базовий):
 *    - дві ситуації з можливою помилкою виконання:
 *        1) InputMismatchException  — нечислове введення у Scanner;
 *        2) ArithmeticException     — ділення кількості піц на 0 осіб.
 *    - конкретні типи винятків у catch (не узагальнений Exception);
 *    - зрозумілі повідомлення в кожному catch (без e.printStackTrace());
 *    - блок finally — закриття Scanner і підсумкове повідомлення.
 *
 *  Рівень 2 (середній):
 *    - власний CHECKED-виняток InvalidQuantityException (успадкований
 *      від Exception) із конструктором super(message) і власним полем
 *      invalidQuantity; кидається у validateQuantity(...) і перехоплюється
 *      окремим catch-блоком.
 *
 *  Рівень 3 (високий):
 *    - множинна обробка: кілька catch-блоків у одному try,
 *      від найбільш специфічних до найбільш загальних;
 *    - повторне збудження (re-throw): метод readInt(...) перехоплює
 *      InputMismatchException, логує її й кидає далі (throw e;) —
 *      остаточно обробляється вже в main();
 *    - невелика ієрархія власних винятків: базовий PizzeriaException
 *      (unchecked) і два підкласи InvalidDiameterException,
 *      InvalidTipException; демонстрація, що catch(PizzeriaException e)
 *      перехоплює обидва підтипи (метод demoBaseClassCatch()).
 *
 * Масиви й додаткові явні класи (крім винятків — вони за своєю
 * природою мусять бути класами) у коді не використовуються.
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
    IO.println("Знижка: від 3 піц – 10%, від 5 піц – 15%");
    IO.println("==============================================");

    IO.print("Ваше ім'я: ");
    String customerName = scanner.nextLine();

    // ---------- Основний блок із множинною обробкою винятків ----------
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
            scanner.close();
            return;
        }

        int rawDiameter = readInt(scanner, "Діаметр піци, см (25/30/35/40): ");
        int diameter = validateDiameter(rawDiameter); // може кинути InvalidDiameterException

        double sizeCoefficient;
        if (diameter == 25) sizeCoefficient = 0.80;
        else if (diameter == 30) sizeCoefficient = 1.00;
        else if (diameter == 35) sizeCoefficient = 1.25;
        else sizeCoefficient = 1.50; // diameter == 40 (інше вже відсіяно валідацією)

        int rawQuantity = readInt(scanner, "Кількість піц: ");
        int quantity = validateQuantity(rawQuantity); // checked, може кинути InvalidQuantityException

        IO.print("Додатковий сир? (так/ні): ");
        String extraCheese = scanner.next();
        boolean withExtraCheese = extraCheese.equalsIgnoreCase("так");

        IO.print("Чайові, грн (наприклад, 20.50): ");
        double rawTip = scanner.nextDouble(); // теж може кинути InputMismatchException
        double tip = validateTip(rawTip);      // може кинути InvalidTipException

        IO.print("На скільки осіб ділите замовлення (0 — не ділити): ");
        int people = readInt(scanner, "");
        int pizzasPerPerson = quantity / people; // ArithmeticException, якщо people == 0

        double unitPrice = basePrice * sizeCoefficient;
        if (withExtraCheese) unitPrice += EXTRA_CHEESE_PRICE;
        double subtotal = unitPrice * quantity;

        int discountPercent;
        if (quantity >= 5) discountPercent = 15;
        else if (quantity >= 3) discountPercent = 10;
        else discountPercent = 0;
        double discountAmount = subtotal * discountPercent / 100;
        double total = subtotal - discountAmount + tip;

        IO.println();
        IO.println("==============================================");
        IO.println("                   ВАШ ЧЕК");
        IO.println("==============================================");
        System.out.printf("Клієнт:            %s%n", customerName);
        System.out.printf("Піца:              %s, %d см%n", pizzaName, diameter);
        System.out.printf("Додатковий сир:    %s%n", withExtraCheese ? "так" : "ні");
        System.out.printf("Ціна за 1 шт.:     %.2f грн%n", unitPrice);
        System.out.printf("Кількість:         %d%n", quantity);
        System.out.printf("Сума:              %.2f грн%n", subtotal);
        System.out.printf("Знижка (%d%%):      -%.2f грн%n", discountPercent, discountAmount);
        System.out.printf("Чайові:            %.2f грн%n", tip);
        IO.println("----------------------------------------------");
        System.out.printf("ДО СПЛАТИ:         %.2f грн%n", total);
        System.out.printf("На кожну з %d осіб припадає приблизно %d піц(и).%n", people, pizzasPerPerson);
        IO.println("==============================================");
        IO.println("Дякуємо за замовлення, " + customerName + "! Смачного!");

    } catch (InputMismatchException e) {
        // найспецифічніший тип — читання з консолі
        IO.println("Помилка: ви ввели текст там, де очікувалося число. " +
                "Перезапустіть програму і вводьте лише цифри.");
    } catch (InvalidQuantityException e) {
        // власний checked-виняток (рівень 2)
        IO.println("Помилка кількості: " + e.getMessage() +
                " (введено значення: " + e.getInvalidQuantity() + ")");
    } catch (InvalidDiameterException e) {
        // підклас ієрархії (рівень 3) — конкретніший за базовий PizzeriaException
        IO.println("Помилка діаметра: " + e.getMessage());
    } catch (InvalidTipException e) {
        // ще один підклас тієї самої ієрархії
        IO.println("Помилка чайових: " + e.getMessage());
    } catch (ArithmeticException e) {
        // стандартний виняток — ділення на нуль осіб
        IO.println("Помилка: неможливо розділити замовлення на 0 осіб.");
    } catch (PizzeriaException e) {
        // узагальнюючий catch для решти доменних помилок піцерії
        // (має стояти ПІСЛЯ своїх підкласів — інакше компілятор видасть помилку)
        IO.println("Загальна помилка піцерії: " + e.getMessage());
    } finally {
        // гарантоване завершення: закриваємо Scanner і друкуємо підсумок
        IO.println("----------------------------------------------");
        IO.println("Роботу з електронним меню завершено.");
        scanner.close();
    }

    // ---------- Демонстрація: catch(PizzeriaException) ловить різні підтипи ----------
    demoBaseClassCatch();
}

/**
 * Зчитує int зі Scanner. У разі помилки формату ЛОГУЄ подію і ПОВТОРНО
 * ЗБУДЖУЄ виняток (throw e;) — обробка завершується вже в main()
 * (демонстрація re-throw, рівень 3).
 */
private static int readInt(Scanner scanner, String prompt) {
    if (!prompt.isEmpty()) {
        IO.print(prompt);
    }
    try {
        return scanner.nextInt();
    } catch (InputMismatchException e) {
        System.out.println("[LOG] Некоректний формат числа при введенні.");
        scanner.nextLine(); // прибираємо некоректний токен з буфера
        throw e;            // повторне збудження для обробки на вищому рівні
    }
}

/** Доменна перевірка діаметра. Кидає підклас ієрархії PizzeriaException. */
private static int validateDiameter(int diameter) {
    if (diameter != 25 && diameter != 30 && diameter != 35 && diameter != 40) {
        throw new InvalidDiameterException(
                "Непідтримуваний діаметр " + diameter + " см. Доступні: 25, 30, 35, 40.",
                diameter);
    }
    return diameter;
}

/** Доменна перевірка чайових. Кидає інший підклас тієї самої ієрархії. */
private static double validateTip(double tip) {
    if (tip < 0) {
        throw new InvalidTipException("Чайові не можуть бути від'ємними: " + tip, tip);
    }
    return tip;
}

/**
 * Доменна перевірка кількості піц. CHECKED-виняток (рівень 2):
 * компілятор вимагає обробити його на кожному виклику.
 */
private static int validateQuantity(int quantity) throws InvalidQuantityException {
    if (quantity <= 0 || quantity > 50) {
        throw new InvalidQuantityException(
                "Кількість піц має бути від 1 до 50, отримано: " + quantity,
                quantity);
    }
    return quantity;
}

/**
 * Демонстрація того, що catch(PizzeriaException e) перехоплює ОБИДВА
 * підкласи — InvalidDiameterException і InvalidTipException (рівень 3).
 */
private static void demoBaseClassCatch() {
    IO.println();
    IO.println("=== Демонстрація: базовий catch(PizzeriaException) ловить різні підтипи ===");
    try {
        validateDiameter(99); // свідомо некоректне значення
    } catch (PizzeriaException e) {
        IO.println("Спіймано через базовий клас: " + e.getClass().getSimpleName() +
                " -> " + e.getMessage());
    }
    try {
        validateTip(-5.0); // свідомо некоректне значення
    } catch (PizzeriaException e) {
        IO.println("Спіймано через базовий клас: " + e.getClass().getSimpleName() +
                " -> " + e.getMessage());
    }
}
