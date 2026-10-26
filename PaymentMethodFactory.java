/**
 * Паттерн Factory Method / Simple Factory (ЛР5, рівень 1).
 *
 * ДО рефакторингу (ЛР4): у Main.java способи оплати створювались напряму
 * через new у масиві-літералі:
 *     PaymentMethod[] availablePayments = {
 *             new CardPayment("4521"), new CashPayment(), new WalletPayment(50.0)
 *     };
 * а вибір конкретного об'єкта для реальної оплати залежав від if/else за
 * номером, який ввів користувач. Якщо додати новий спосіб оплати — довелось
 * би правити одразу два місця (масив і if/else), а якщо new розкидані по
 * кількох місцях програми — ще більше.
 *
 * ПІСЛЯ рефакторингу: усе створення зосереджене в одному методі. Додати
 * новий спосіб оплати — значить додати один new case/case сюди; решта
 * клієнтського коду (Main) не змінюється. Клієнтський код більше не знає,
 * ЯКИЙ саме конструктор викликати для кожного коду — лише знає інтерфейс
 * PaymentMethod, який отримає у відповідь (відповідає принципу OCP —
 * Main "закритий для модифікації, відкритий для розширення").
 */
public class PaymentMethodFactory {

    private PaymentMethodFactory() {
        // утилітний клас — екземпляри не потрібні
    }

    public static PaymentMethod create(int code) {
        return switch (code) {
            case 2 -> new CashPayment();
            case 3 -> new WalletPayment(50.0);
            case 4 -> new LegacyBankGatewayAdapter(new LegacyBankGatewayApi(), "9931"); // ЛР5, рівень 3 (Adapter)
            default -> new CardPayment("4521"); // код 1 і будь-який нерозпізнаний код — картка за замовчуванням
        };
    }
}
