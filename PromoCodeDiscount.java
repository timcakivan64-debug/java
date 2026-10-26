/** Стратегія фіксованого відсотка знижки за промокодом (не залежить від кількості). */
public class PromoCodeDiscount implements DiscountStrategy {
    private final double percent;

    public PromoCodeDiscount(double percent) {
        this.percent = percent;
    }

    @Override
    public double applyDiscount(double subtotal, int quantity) {
        return subtotal * percent / 100;
    }

    @Override
    public String getDescription() {
        return "промокод на " + percent + "% знижки";
    }
}
