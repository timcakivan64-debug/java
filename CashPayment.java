public class CashPayment extends AbstractPaymentMethod {

    @Override
    public double pay(double amount) {
        double valid = validateAmount(amount);
        System.out.printf("Отримано готівкою %.2f грн. Чек роздруковано.%n", valid);
        return valid;
    }

    @Override
    public String getLabel() {
        return "готівка";
    }
    // printPaymentHeader(...) не перевизначається — береться default з інтерфейсу
}
