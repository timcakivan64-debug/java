public class CardPayment extends AbstractPaymentMethod {
    private final String cardLastDigits;

    public CardPayment(String cardLastDigits) {
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
    // printPaymentHeader(...) не перевизначається — використовується default з інтерфейсу
}
