/** Спостерігач №1: при будь-якій зміні замовлення друкує оновлений чек. */
public class ReceiptPrinterObserver implements OrderObserver {
    @Override
    public void onOrderChanged(PizzaOrder order, String event) {
        IO.println("[ЧЕК] (" + event + ") " + order.toReceiptLine());
    }
}
