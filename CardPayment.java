public class CardPayment extends AbstractPaymentMethod {
    private final String cardLastDigits;

    public CardPayment(String cardLastDigits) {
        if (cardLastDigits == null || !cardLastDigits.matches("\\d{4}")) {
            throw new IllegalArgumentException("Потрібно рівно 4 останні цифри картки: " + cardLastDigits);
        }
        this.cardLastDigits = cardLastDigits;
    }

    @Override
    public double pay(double amount) {
        double valid = validateAmount(amount);
        System.out.printf("Знято %.2f грн з картки **** %s.%n", valid, cardLastDigits);
        return valid;
    }

    @Override
    public String getLabel() {
        return "банківська картка";
    }
    // printPaymentHeader(...) не перевизначається — береться default з інтерфейсу
}
