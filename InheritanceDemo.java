import java.util.List;

/** Демонстрація вимог ЛР6 (рівні 1–3) на класах піцерії. */
public class InheritanceDemo {

    public static void run() {
        IO.println();
        IO.println("################ ЛР6: ДЕМОНСТРАЦІЯ НАСЛІДУВАННЯ ################");
        demoPolymorphism();
        demoConstructorChain();
        demoStaticVsDynamicType();
        demoEqualsSymmetry();
        demoSealedSwitch();
        demoComposition();
    }

    /** Рівень 1: список базового типу з різними підкласами, виклик перевизначених методів. */
    private static void demoPolymorphism() {
        IO.println();
        IO.println("=== Поліморфізм: List<Pizza> ===");
        // new Pizza(...) — помилка компіляції: клас абстрактний
        List<Pizza> pizzas = List.of(
                new MargheritaPizza(),
                new FourCheesePizza(35, true),
                new HawaiianPizza(25, false),
                new MeatPizza(),
                new PepperoniPizza(40, false, true));
        for (Pizza p : pizzas) {
            // p.toString(), getIngredients(), getBakingMinutes() — динамічна диспетчеризація
            IO.println(p + " | склад: " + p.getIngredients() + " | випікання: " + p.getBakingMinutes() + " хв");
        }
    }

    /** Рівень 2: ланцюжок конструкторів. */
    private static void demoConstructorChain() {
        IO.println();
        IO.println("=== Ланцюжок конструкторів: new PepperoniPizza() ===");
        Pizza.setTraceConstructors(true);
        new PepperoniPizza();
        Pizza.setTraceConstructors(false);
        IO.println("(порядок: Pizza -> MeatPizza -> PepperoniPizza: спершу завершується конструктор предка)");
    }

    /** Рівень 2: статичний і динамічний тип, instanceof з pattern matching. */
    private static void demoStaticVsDynamicType() {
        IO.println();
        IO.println("=== Статичний і динамічний тип ===");
        Pizza p = new PepperoniPizza(30, false, true); // статичний тип — Pizza, динамічний — PepperoniPizza
        IO.println("Статичний тип: Pizza, динамічний: " + p.getClass().getSimpleName());
        // p.isSpicy(); — не компілюється: компілятор бачить лише методи Pizza
        if (p instanceof PepperoniPizza pepperoni) {
            IO.println("instanceof-pattern: гостра? " + pepperoni.isSpicy());
        }
        Pizza other = new MargheritaPizza();
        if (!(other instanceof PepperoniPizza)) {
            IO.println("Маргарита не є PepperoniPizza — приведення було б ClassCastException");
        }
    }

    /** Рівень 3: equals/hashCode і симетрія. */
    private static void demoEqualsSymmetry() {
        IO.println();
        IO.println("=== equals(): симетрія ===");
        MeatPizza meat = new MeatPizza(30, false);
        PepperoniPizza pepperoni = new PepperoniPizza(30, false, false);
        IO.println("Реалізація з getClass():");
        IO.println("  meat.equals(pepperoni) = " + meat.equals(pepperoni));
        IO.println("  pepperoni.equals(meat) = " + pepperoni.equals(meat));
        PepperoniPizza same = new PepperoniPizza(30, false, false);
        IO.println("  pepperoni.equals(такий самий) = " + pepperoni.equals(same)
                + ", hashCode однакові: " + (pepperoni.hashCode() == same.hashCode()));
        IO.println("  різна гострота: " + pepperoni.equals(new PepperoniPizza(30, false, true)));

        BadBase base = new BadBase(30);
        BadSub sub = new BadSub(30, true);
        IO.println("Хибна реалізація з instanceof (демонстраційні класи):");
        IO.println("  base.equals(sub) = " + base.equals(sub));
        IO.println("  sub.equals(base) = " + sub.equals(base) + "  <- порушено симетрію!");
    }

    /** Демонстраційний базовий клас з equals через instanceof. */
    static class BadBase {
        final int diameter;
        BadBase(int diameter) { this.diameter = diameter; }
        @Override public boolean equals(Object o) {
            return o instanceof BadBase b && diameter == b.diameter;
        }
        @Override public int hashCode() { return diameter; }
    }

    /** Підклас додає поле і порівнює його — симетрія ламається. */
    static class BadSub extends BadBase {
        final boolean spicy;
        BadSub(int diameter, boolean spicy) { super(diameter); this.spicy = spicy; }
        @Override public boolean equals(Object o) {
            return o instanceof BadSub s && super.equals(o) && spicy == s.spicy;
        }
        @Override public int hashCode() { return 31 * super.hashCode() + (spicy ? 1 : 0); }
    }

    /** Самостійне опрацювання: switch із pattern matching над sealed-ієрархією. */
    private static String category(Pizza p) {
        // default не потрібен: компілятор знає всі дозволені підтипи (sealed).
        // Порядок важливий: PepperoniPizza має стояти перед MeatPizza (dominance).
        return switch (p) {
            case PepperoniPizza pp -> pp.isSpicy() ? "гостра м'ясна" : "м'ясна (пепероні)";
            case MeatPizza m -> "м'ясна, " + m.getMeatGrams() + " г м'яса";
            case MargheritaPizza m -> "вегетаріанська класика";
            case FourCheesePizza f -> "сирна";
            case HawaiianPizza h -> "з фруктами";
        };
    }

    private static void demoSealedSwitch() {
        IO.println();
        IO.println("=== sealed + switch з pattern matching ===");
        for (Pizza p : List.of(new MargheritaPizza(), new MeatPizza(), new PepperoniPizza(30, false, true),
                new FourCheesePizza(), new HawaiianPizza())) {
            IO.println(p.getName() + " -> " + category(p));
        }
    }

    /** Рівень 3: композиція замість наслідування. */
    private static void demoComposition() {
        IO.println();
        IO.println("=== Композиція: PizzaCombo (has-a, а не is-a) ===");
        PizzaCombo combo = new PizzaCombo(
                List.of(new MargheritaPizza(), new PepperoniPizza(35, false, false)), "лимонад", 45.00);
        IO.println(combo);
        IO.println("Час випікання (паралельно): " + combo.getTotalBakingMinutes() + " хв");
    }
}
