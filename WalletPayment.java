/**
 * Реалізує PaymentMethod НАПРЯМУ (без абстрактного класу): має власний стан
 * (залишок бонусів) і власну логіку — часткове покриття суми.
 */
public class WalletPayment implements PaymentMethod {
    private double bonusBalance;

    public WalletPayment(double bonusBalance) {
        if (bonusBalance < 0) {
            throw new IllegalArgumentException("Баланс бонусів не може бути від'ємним: " + bonusBalance);
        }
        this.bonusBalance = bonusBalance;
    }

    @Override
    public double pay(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сума до оплати має бути додатною: " + amount);
        }
        double fromBonus = Math.min(bonusBalance, amount);
        bonusBalance -= fromBonus;
        System.out.printf("Списано %.2f грн бонусами; залишилось сплатити іншим способом: %.2f грн.%n",
                fromBonus, amount - fromBonus);
        return fromBonus; // покрито лише частину, якщо бонусів не вистачило
    }

    @Override
    public String getLabel() {
        return "бонусний гаманець";
    }

    /** Перевизначення default-методу: додаємо залишок бонусів. */
    @Override
    public void printPaymentHeader(String customerName) {
        System.out.printf("Оплата для %s через: %s (залишок бонусів: %.2f грн)%n",
                customerName, getLabel(), bonusBalance);
    }
}
