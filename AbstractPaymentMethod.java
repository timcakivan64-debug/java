/**
 * Абстрактний клас (рівень 2): виносить СПІЛЬНУ логіку (перевірку суми),
 * яку недоцільно дублювати в кожному класі-реалізації. Сюди виносимо лише
 * ті способи оплати, яким дійсно потрібна ця спільна перевірка —
 * CardPayment і CashPayment. WalletPayment навмисно НЕ успадковує цей
 * клас: йому потрібна інша поведінка (списання бонусів), спільного коду
 * мало, тож для нього природніше реалізувати «чистий» інтерфейс напряму
 * (демонстрація того, коли доцільний абстрактний клас, а коли — ні).
 */
public abstract class AbstractPaymentMethod implements PaymentMethod {

    protected final double validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сума до оплати має бути додатною: " + amount);
        }
        return amount;
    }
}
