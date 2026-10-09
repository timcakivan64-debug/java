/** Стратегія "без знижки" — наприклад, для акційних позицій, де знижка вже врахована в ціні. */
public class NoDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double subtotal, int quantity) {
        return 0.0;
    }

    @Override
    public String getDescription() {
        return "без знижки";
    }
}
