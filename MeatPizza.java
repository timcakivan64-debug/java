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
        this(IdGenerator.next(), diameter, extraCheese);
    }

    public MeatPizza(long id, int diameter, boolean extraCheese) {
        this(id, "М'ясна", BASE_PRICE, diameter, extraCheese, 150);
    }

    /** Захищений конструктор для підкласів, які задають власну назву й ціну. */
    protected MeatPizza(long id, String name, double basePrice, int diameter, boolean extraCheese, int meatGrams) {
        super(id, name, basePrice, diameter, extraCheese);
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
}
