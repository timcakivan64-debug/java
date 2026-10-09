/** Та сама логіка знижки, що й у ЛР №1: 10% від 3 піц, 15% від 5 піц. */
public class TieredQuantityDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double subtotal, int quantity) {
        int percent;
        if (quantity >= 5) percent = 15;
        else if (quantity >= 3) percent = 10;
        else percent = 0;
        return subtotal * percent / 100;
    }

    @Override
    public String getDescription() {
        return "знижка за кількістю (10% від 3 піц, 15% від 5 піц)";
    }
}
