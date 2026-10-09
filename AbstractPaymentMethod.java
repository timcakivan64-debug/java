/**
 * Рівень 2: абстрактний клас, що ЧАСТКОВО реалізує PaymentMethod.
 * Виносить спільну логіку (перевірку суми), щоб не дублювати її
 * в CardPayment і CashPayment.
 *
 * Коли абстрактний клас, а коли «чистий» інтерфейс:
 *  - Card і Cash мають спільний код (validateAmount) -> абстрактний клас;
 *  - WalletPayment має власний стан (залишок бонусів) і майже нічого
 *    спільного -> реалізує інтерфейс напряму (клас може успадкувати лише
 *    один клас, тож не варто «витрачати» це успадкування без потреби).
 */
public abstract class AbstractPaymentMethod implements PaymentMethod {

    protected final double validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сума до оплати має бути додатною: " + amount);
        }
        return amount;
    }
}
