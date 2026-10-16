/**
 * Спостерігач №2: незалежний від першого, веде власний аудит-лог подій.
 * PizzaOrder (Subject) не знає про існування жодного з двох спостерігачів
 * окремо — просто розсилає подію всім підписаним.
 */
public class AuditLogObserver implements OrderObserver {
    @Override
    public void onOrderChanged(PizzaOrder order, String event) {
        IO.println("[LOG] Подія «" + event + "» для замовлення клієнта " + order.getCustomerName());
    }
}
