import java.util.Set;

/**
 * Замовлення піци. Об'єднує ДВІ лабораторні:
 *  - ЛР №3: доменні правила, що порушуються через власні винятки
 *    (кількість -> checked InvalidQuantityException; діаметр і чайові ->
 *    unchecked підкласи PizzeriaException);
 *  - ЛР №4: контекст паттерна Strategy (знижка) + реалізація ДВОХ інтерфейсів
 *    одночасно: Comparable (сортування) і Receiptable (чек).
 */
public class PizzaOrder implements Comparable<PizzaOrder>, Receiptable {
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 50;
    private static final Set<Integer> ALLOWED_DIAMETERS = Set.of(25, 30, 35, 40);

    private final String customerName;
    private final String pizzaName;
    private final double unitPrice;
    private final int diameter;
    private final int quantity;
    private double tip = 0.0;
    private DiscountStrategy discountStrategy;

    /**
     * @throws InvalidQuantityException checked: кількість поза межами 1..50
     * @throws InvalidDiameterException unchecked: діаметр не з {25, 30, 35, 40}
     * @throws IllegalArgumentException порожнє ім'я, неположна ціна, null-стратегія
     */
    public PizzaOrder(String customerName, String pizzaName, double unitPrice, int diameter,
                      int quantity, DiscountStrategy discountStrategy) throws InvalidQuantityException {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Ім'я клієнта не може бути порожнім");
        }
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Ціна має бути додатною: " + unitPrice);
        }
        if (!ALLOWED_DIAMETERS.contains(diameter)) {
            throw new InvalidDiameterException(
                    "Недопустимий діаметр " + diameter + " см. Дозволені: 25, 30, 35, 40", diameter);
        }
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new InvalidQuantityException(
                    "Кількість піц має бути від " + MIN_QUANTITY + " до " + MAX_QUANTITY, quantity);
        }
        this.customerName = customerName;
        this.pizzaName = pizzaName;
        this.unitPrice = unitPrice;
        this.diameter = diameter;
        this.quantity = quantity;
        setDiscountStrategy(discountStrategy); // початкова стратегія — через конструктор
    }

    /** Додає чайові. @throws InvalidTipException якщо чайові від'ємні */
    public void addTip(double tip) {
        if (tip < 0) {
            throw new InvalidTipException("Чайові не можуть бути від'ємними: " + tip, tip);
        }
        this.tip = tip;
    }

    /** Strategy: стратегію можна ЗМІНИТИ під час виконання, без створення нового об'єкта. */
    public final void setDiscountStrategy(DiscountStrategy discountStrategy) {
        if (discountStrategy == null) {
            throw new IllegalArgumentException("Стратегія знижки не може бути null");
        }
        this.discountStrategy = discountStrategy;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    /** Підсумок: сума мінус знижка (делегується стратегії) плюс чайові. */
    public double getTotal() {
        return getSubtotal() - discountStrategy.applyDiscount(getSubtotal(), quantity) + tip;
    }

    /**
     * Приймає інтерфейс (а не конкретний клас) -> працює з будь-яким
     * способом оплати, у тому числі з тими, що додадуть пізніше.
     */
    public double checkout(PaymentMethod method) {
        method.printPaymentHeader(customerName);
        return method.pay(getTotal());
    }

    @Override
    public int compareTo(PizzaOrder other) {
        // поведінка СОРТУВАННЯ (за сумою до сплати)
        return Double.compare(this.getTotal(), other.getTotal());
    }

    @Override
    public String toReceiptLine() {
        // бізнес-поведінка: формування рядка чека
        return String.format("%-6s | %-12s %dсм x%-2d | %-52s | ДО СПЛАТИ: %8.2f грн",
                customerName, pizzaName, diameter, quantity,
                discountStrategy.getDescription(), getTotal());
    }
}
