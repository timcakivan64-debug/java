import java.util.Objects;

/** Третій рівень: Pizza -> MeatPizza -> PepperoniPizza. Додає поле spicy. */
public final class PepperoniPizza extends MeatPizza {
    public static final double BASE_PRICE = 185.00;

    private final boolean spicy;

    public PepperoniPizza() {
        this(DEFAULT_DIAMETER, false, false);
    }

    public PepperoniPizza(int diameter, boolean extraCheese, boolean spicy) {
        super("Пепероні", BASE_PRICE, diameter, extraCheese, 100);
        this.spicy = spicy;
        trace("[PepperoniPizza] конструктор третього рівня, гостра: " + spicy);
    }

    public boolean isSpicy() { return spicy; } // специфічний метод підкласу

    @Override
    public String getIngredients() {
        return "томатний соус, моцарела, пепероні" + (spicy ? ", перець чилі" : "");
    }

    @Override
    public int getBakingMinutes() {
        return super.getBakingMinutes() - 1;
    }

    @Override
    public String toString() {
        return super.toString() + ", гостра: " + (spicy ? "так" : "ні");
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        PepperoniPizza that = (PepperoniPizza) o;
        return spicy == that.spicy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), spicy);
    }
}
