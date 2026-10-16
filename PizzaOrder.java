import java.util.ArrayList;
import java.util.List;

/**
 * Контекст паттерна Strategy (ЛР4) і водночас приклад класу,
 * що реалізує ДВА інтерфейси одночасно: Comparable — поведінка
 * сортування, Receiptable — бізнес-поведінка формування чека.
 *
 * ЛР5, рівень 2 (Observer): PizzaOrder тепер є Subject — тримає список
 * підписників OrderObserver і сповіщає їх при зміні стратегії знижки
 * та при оплаті. Сам PizzaOrder НІЧОГО не знає про те, ЩО саме роблять
 * спостерігачі (друкують чек, пишуть лог тощо) — лише розсилає подію.
 */
public class PizzaOrder implements Comparable<PizzaOrder>, Receiptable {
    private final String customerName;
    private final String pizzaName;
    private final double unitPrice;
    private final int quantity;
    private DiscountStrategy discountStrategy;
    private final List<OrderObserver> observers = new ArrayList<>();

    public PizzaOrder(String customerName, String pizzaName, double unitPrice, int quantity,
                       DiscountStrategy discountStrategy) {
        this.customerName = customerName;
        this.pizzaName = pizzaName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discountStrategy = discountStrategy; // початкова стратегія — через конструктор
    }

    public String getCustomerName() {
        return customerName;
    }

    /** Підписати нового спостерігача. Суб'єкт не знає про конкретний тип підписника. */
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    /** Розсилає подію всім підписникам по черзі. */
    private void notifyObservers(String event) {
        for (OrderObserver observer : observers) {
            observer.onOrderChanged(this, event);
        }
    }

    /** Дозволяє змінити стратегію ПІД ЧАС виконання, без створення нового об'єкта. */
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
        notifyObservers("ЗМІНЕНО ЗНИЖКУ"); // Observer: сповіщаємо про зміну стану
    }

    /**
     * Оплата замовлення обраним способом (поліморфізм PaymentMethod з ЛР4).
     * Раніше виклик pay(...) та обчислення суми з чайовими жили прямо в Main —
     * тепер це відповідальність самого замовлення, а Main лише делегує.
     */
    public double applyPayment(PaymentMethod paymentMethod, double tip) {
        double totalWithTip = getTotal() + tip;
        paymentMethod.printPaymentHeader(customerName);
        paymentMethod.pay(totalWithTip);
        notifyObservers("ОПЛАЧЕНО"); // Observer: сповіщаємо про оплату
        return totalWithTip;
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
