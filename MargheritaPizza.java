public final class MargheritaPizza extends Pizza {
    public static final double BASE_PRICE = 150.00;

    public MargheritaPizza() {
        this(DEFAULT_DIAMETER, false); // делегування, без дублювання
    }

    public MargheritaPizza(int diameter, boolean extraCheese) {
        this(IdGenerator.next(), diameter, extraCheese);
    }

    public MargheritaPizza(long id, int diameter, boolean extraCheese) {
        super(id, "Маргарита", BASE_PRICE, diameter, extraCheese);
        trace("[MargheritaPizza] конструктор підкласу");
    }

    @Override
    public String getIngredients() {
        return "томатний соус, моцарела, базилік";
    }

    @Override
    public int getBakingMinutes() {
        return 10;
    }
}
