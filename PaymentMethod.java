/**
 * Поведінка, яка відрізняється залежно від способу оплати замовлення
 * (рівень 1 — поведінка, що варіюється між сутностями одного домену).
 */
public interface PaymentMethod {

    /** Власне оплата суми. У кожної реалізації — своя логіка. */
    double pay(double amount);

    /** Коротка назва способу оплати (для заголовка та повідомлень). */
    String getLabel();

    /**
     * Default-метод (рівень 2): реалізація «за замовчуванням», яку успадковують
     * усі класи. CardPayment і CashPayment користуються нею без змін,
     * а WalletPayment перевизначає (бо хоче показати ще й залишок бонусів).
     */
    default void printPaymentHeader(String customerName) {
        System.out.println("Оплата для " + customerName + " через: " + getLabel());
    }
}
