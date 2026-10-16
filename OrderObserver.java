/**
 * Паттерн Observer (ЛР5, рівень 2). Контракт для будь-кого, хто хоче
 * дізнаватись про зміни стану PizzaOrder (Subject), не знаючи деталей
 * його внутрішньої реалізації.
 */
public interface OrderObserver {
    void onOrderChanged(PizzaOrder order, String event);
}
