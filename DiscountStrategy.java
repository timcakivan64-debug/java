/**
 * Інтерфейс-стратегія (паттерн Strategy, рівень 3): визначає, ЯК саме
 * рахується знижка. PizzaOrder (контекст) отримує конкретну реалізацію
 * через конструктор або setDiscountStrategy(...) і делегує їй обчислення,
 * не знаючи деталей конкретного алгоритму.
 */
public interface DiscountStrategy {
    double applyDiscount(double subtotal, int quantity);

    String getDescription();
}
