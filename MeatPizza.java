import java.util.Objects;

/**
 * Другий рівень ієрархії: Pizza -> MeatPizza -> PepperoniPizza.
 * Це КОНКРЕТНИЙ клас («М'ясна» з меню) і водночас базовий для піци «Пепероні»:
 * пепероні — різновид м'ясної піци (is-a).
 */
public sealed class MeatPizza extends Pizza permits PepperoniPizza {
    public static final double BASE_PRICE = 220.00;

    private final int meatGrams;

    public MeatPizza() {
        this(DEFAULT_DIAMETER, false);
    }

    public MeatPizza(int diameter, boolean extraCheese) {
        this("М'ясна", BASE_PRICE, diameter, extraCheese, 150);
    }

    /** Захищений конструктор для підкласів, які задають власну назву й ціну. */
    protected MeatPizza(String name, double basePrice, int diameter, boolean extraCheese, int meatGrams) {
        super(name, basePrice, diameter, extraCheese);
        if (meatGrams <= 0) {
            throw new IllegalArgumentException("Вага м'яса має бути додатною: " + meatGrams);
        }
        this.meatGrams = meatGrams;
        trace("[MeatPizza] конструктор другого рівня, м'яса: " + meatGrams + " г");
    }

    public int getMeatGrams() { return meatGrams; }

    @Override
    public String getIngredients() {
        return "томатний соус, моцарела, бекон, шинка, ковбаски";
    }

    @Override
    public int getBakingMinutes() {
        return 15;
    }

    @Override
    public String toString() {
        return super.toString() + ", м'яса: " + meatGrams + " г";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false; // getClass() перевірено в Pizza
        MeatPizza that = (MeatPizza) o;
        return meatGrams == that.meatGrams;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), meatGrams);
    }
}
