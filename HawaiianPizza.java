public final class HawaiianPizza extends Pizza {
    public static final double BASE_PRICE = 175.00;

    public HawaiianPizza() {
        this(DEFAULT_DIAMETER, false);
    }

    public HawaiianPizza(int diameter, boolean extraCheese) {
        this(IdGenerator.next(), diameter, extraCheese);
    }

    public HawaiianPizza(long id, int diameter, boolean extraCheese) {
        super(id, "Гавайська", BASE_PRICE, diameter, extraCheese);
        trace("[HawaiianPizza] конструктор підкласу");
    }

    @Override
    public String getIngredients() {
        return "томатний соус, моцарела, шинка, ананас";
    }
}
