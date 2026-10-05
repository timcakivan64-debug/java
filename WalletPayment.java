/**
 * Реалізує PaymentMethod НАПРЯМУ (без абстрактного класу) — «чистий інтерфейс»,
 * бо тут немає спільної логіки з CardPayment/CashPayment, зате є власний стан
 * (залишок бонусів), який логічно тримати саме тут.
 */
public class WalletPayment implements PaymentMethod {
    private double bonusBalance;

    public WalletPayment(double bonusBalance) {
        this.bonusBalance = bonusBalance;
    }

    @Override
    public double pay(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сума до оплати має бути додатною: " + amount);
        }
        double fromBonus = Math.min(bonusBalance, amount);
        bonusBalance -= fromBonus;
        double remaining = amount - fromBonus;
        System.out.printf("Списано %.2f грн бонусами, доплата %.2f грн іншим способом.%n",
                fromBonus, remaining);
        return amount;
    }

    @Override
    public String getLabel() {
        return "бонусний гаманець";
    }

    @Override
    public void printPaymentHeader(String customerName) {
        // перевизначення default-методу: додаємо інформацію про залишок бонусів
        System.out.println("Оплата для " + customerName + " через: " + getLabel() +
                " (залишок бонусів: " + String.format("%.2f", bonusBalance) + " грн)");
    }
}
