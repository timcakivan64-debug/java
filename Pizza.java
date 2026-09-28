/**
 * Позиція електронного меню піцерії.
 */
public class Pizza {
    private String name;        // назва піци
    private int diameter;       // діаметр, см
    private double price;       // ціна, грн
    private boolean vegetarian; // чи вегетаріанська

    public Pizza(String name, int diameter, double price, boolean vegetarian) {
        this.name = name;
        this.diameter = diameter;
        this.price = price;
        this.vegetarian = vegetarian;
    }

    public String getName() {
        return name;
    }

    public int getDiameter() {
        return diameter;
    }

    public double getPrice() {
        return price;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    @Override
    public String toString() {
        return String.format("Піца \"%s\" | %d см | %.2f грн | %s",
                name, diameter, price, vegetarian ? "вегетаріанська" : "з м'ясом/рибою");
    }

    // Потрібно для рівня 3: пошук за цілим об'єктом ("рівність" за всіма полями)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pizza other = (Pizza) o;
        return name.equals(other.name)
                && diameter == other.diameter
                && Double.compare(price, other.price) == 0
                && vegetarian == other.vegetarian;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + diameter;
        result = 31 * result + Double.hashCode(price);
        result = 31 * result + (vegetarian ? 1 : 0);
        return result;
    }
}
