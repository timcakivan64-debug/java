public final class FourCheesePizza extends Pizza {
    public static final double BASE_PRICE = 210.00;

    public FourCheesePizza() {
        this(DEFAULT_DIAMETER, false);
    }

    public FourCheesePizza(int diameter, boolean extraCheese) {
        this(IdGenerator.next(), diameter, extraCheese);
    }

    public FourCheesePizza(long id, int diameter, boolean extraCheese) {
        super(id, "Чотири сири", BASE_PRICE, diameter, extraCheese);
        trace("[FourCheesePizza] конструктор підкласу");
    }

    @Override
    public String getIngredients() {
        return "моцарела, горгонзола, пармезан, чеддер";
    }

    @Override
    public int getBakingMinutes() {
        return 11;
    }
}
