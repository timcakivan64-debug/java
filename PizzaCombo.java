import java.util.List;

/**
 * КОМПОЗИЦІЯ замість наслідування (ЛР6, рівень 3).
 * Комбо-набір «кілька піц + напій» НЕ є піцею (is-a хибне), він МАЄ піци (has-a).
 * Тому PizzaCombo НЕ extends Pizza, а містить List<Pizza>.
 */
public class PizzaCombo {
    private final List<Pizza> pizzas;
    private final String drinkName;
    private final double drinkPrice;

    public PizzaCombo(List<Pizza> pizzas, String drinkName, double drinkPrice) {
        if (pizzas.isEmpty()) {
            throw new IllegalArgumentException("У комбо має бути хоча б одна піца");
        }
        this.pizzas = List.copyOf(pizzas);
        this.drinkName = drinkName;
        this.drinkPrice = drinkPrice;
    }

    public double getTotalPrice() {
        double sum = drinkPrice;
        for (Pizza p : pizzas) {
            sum += p.getPrice();
        }
        return sum;
    }

    public int getTotalBakingMinutes() {
        int max = 0;
        for (Pizza p : pizzas) {
            max = Math.max(max, p.getBakingMinutes()); // печуться паралельно
        }
        return max;
    }

    @Override
    public String toString() {
        return String.format("Комбо: %d піц(и) + %s, разом %.2f грн", pizzas.size(), drinkName, getTotalPrice());
    }
}
