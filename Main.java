import java.util.Scanner;


void main() {

   
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

    IO.print("Номер піци з меню (1-5): ");
    int menuNumber = scanner.nextInt();

    IO.print("Діаметр піци, см (25/30/35/40): ");
    int diameter = scanner.nextInt();

    IO.print("Кількість піц: ");
    int quantity = scanner.nextInt();

    IO.print("Додатковий сир? (так/ні): ");
    String extraCheese = scanner.next();

    IO.print("Чайові, грн (наприклад, 20.50): ");
    double tip = scanner.nextDouble();

    scanner.close();

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
        return;
    }


    double sizeCoefficient;
    if (diameter == 25) {
        sizeCoefficient = 0.80;
    } else if (diameter == 30) {
        sizeCoefficient = 1.00;
    } else if (diameter == 35) {
        sizeCoefficient = 1.25;
    } else if (diameter == 40) {
        sizeCoefficient = 1.50;
    } else {
        IO.println("Помилка: доступні розміри 25, 30, 35 або 40 см.");
        return;
    }

    
    if (quantity <= 0) {
        IO.println("Помилка: кількість піц має бути більшою за нуль.");
        return;
    }
    if (tip < 0) {
        IO.println("Помилка: чайові не можуть бути від'ємними.");
        return;
    }

   
    double unitPrice = basePrice * sizeCoefficient; 
    boolean withExtraCheese = extraCheese.equalsIgnoreCase("так");
    if (withExtraCheese) {
        unitPrice = unitPrice + EXTRA_CHEESE_PRICE;
    }

    double subtotal = unitPrice * quantity; 

    int discountPercent;
    if (quantity >= 5) {
        discountPercent = 15;
    } else if (quantity >= 3) {
        discountPercent = 10;
    } else {
        discountPercent = 0;
    }
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
    IO.println("==============================================");
    IO.println("Дякуємо за замовлення, " + customerName + "! Смачного!");
}
