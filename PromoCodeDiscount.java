/** Стратегія фіксованого відсотка знижки за промокодом (не залежить від кількості). */
public class PromoCodeDiscount implements DiscountStrategy {
    private final double percent;

    public PromoCodeDiscount(double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Відсоток знижки має бути в межах 0..100: " + percent);
        }
        this.percent = percent;
    }

    @Override
    public double applyDiscount(double subtotal, int quantity) {
        return subtotal * percent / 100;
    }

    @Override
    public String getDescription() {
        return String.format("промокод на %.0f%% знижки", percent);
    }
}
