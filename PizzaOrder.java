/**
 * Контекст паттерна Strategy (рівень 3) і водночас приклад класу,
 * що реалізує ДВА інтерфейси одночасно (рівень 2): Comparable — поведінка
 * сортування, Receiptable — бізнес-поведінка формування чека.
 */
public class PizzaOrder implements Comparable<PizzaOrder>, Receiptable {
    private final String customerName;
    private final String pizzaName;
    private final double unitPrice;
    private final int quantity;
    private DiscountStrategy discountStrategy;

    public PizzaOrder(String customerName, String pizzaName, double unitPrice, int quantity,
                       DiscountStrategy discountStrategy) {
        this.customerName = customerName;
        this.pizzaName = pizzaName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discountStrategy = discountStrategy; // початкова стратегія — через конструктор
    }

    /** Дозволяє змінити стратегію ПІД ЧАС виконання, без створення нового об'єкта. */
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public double getTotal() {
        double subtotal = getSubtotal();
        double discount = discountStrategy.applyDiscount(subtotal, quantity);
        return subtotal - discount;
    }

    @Override
    public int compareTo(PizzaOrder other) {
        // поведінка СОРТУВАННЯ — перша з двох незалежних реалізованих поведінок
        return Double.compare(this.getTotal(), other.getTotal());
    }

    @Override
    public String toReceiptLine() {
        // поведінка БІЗНЕС-ЛОГІКИ — друга незалежна реалізована поведінка
        return String.format("%-7s | %-12s x%d | %-45s | ДО СПЛАТИ: %7.2f грн",
                customerName, pizzaName, quantity, discountStrategy.getDescription(), getTotal());
    }
}
