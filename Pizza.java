/**
 * Базовий клас ієрархії піц (ЛР6). Рефакторинг: раніше піца в проєкті була
 * лише парою «рядок pizzaName + число unitPrice», а тип визначався ланцюжком
 * if/else за номером меню в Main. Тепер кожен вид піци — окремий підклас.
 *
 * Клас АБСТРАКТНИЙ: «просто піца» без назви й складу в піцерії не існує.
 * Клас SEALED: набір видів піци обмежений меню (див. permits).
 * Поля private; підкласи звертаються до них через гетери.
 */
public abstract sealed class Pizza extends BaseEntity permits MargheritaPizza, FourCheesePizza, HawaiianPizza, MeatPizza {

    public static final double EXTRA_CHEESE_PRICE = 35.00;
    public static final int DEFAULT_DIAMETER = 30;

    /** Увімкнення виводу ланцюжка конструкторів (лише для демонстрації). */
    private static boolean traceConstructors = false;

    private final double basePrice; // ціна для 30 см
    private final int diameter;
    private final boolean extraCheese;

    // ---- Перевантаження конструкторів із делегуванням через this(...) ----
    protected Pizza(String name, double basePrice) {
        this(name, basePrice, DEFAULT_DIAMETER);
    }

    protected Pizza(String name, double basePrice, int diameter) {
        this(name, basePrice, diameter, false);
    }

    protected Pizza(String name, double basePrice, int diameter, boolean extraCheese) {
        this(IdGenerator.next(), name, basePrice, diameter, extraCheese);
    }

    /** Головний конструктор: id передається в BaseEntity. */
    protected Pizza(long id, String name, double basePrice, int diameter, boolean extraCheese) {
        super(id, name);
        this.basePrice = basePrice;
        this.diameter = validateDiameter(diameter);
        this.extraCheese = extraCheese;
        trace("[Pizza] конструктор базового класу: " + getName());
    }

    /** Доменна перевірка діаметра (перенесена з Main, ЛР3). */
    public static int validateDiameter(int diameter) {
        if (diameter != 25 && diameter != 30 && diameter != 35 && diameter != 40) {
            throw new InvalidDiameterException(
                    "Непідтримуваний діаметр " + diameter + " см. Доступні: 25, 30, 35, 40.",
                    diameter);
        }
        return diameter;
    }

    public static void setTraceConstructors(boolean enabled) {
        traceConstructors = enabled;
    }

    protected static void trace(String message) {
        if (traceConstructors) {
            IO.println(message);
        }
    }

    /**
     * АБСТРАКТНИЙ метод: склад унікальний для кожного виду піци, розумної
     * реалізації «за замовчуванням» не існує. Якби метод був звичайним, довелось
     * би повертати вигадане значення, а підклас міг би про нього забути —
     * абстрактний метод змушує компілятор вимагати реалізацію.
     */
    public abstract String getIngredients();

    /** Метод із реалізацією за замовчуванням — підкласи можуть його перевизначити. */
    public int getBakingMinutes() {
        return 12;
    }

    /**
     * FINAL: правило «коефіцієнт ціни залежить від діаметра» — бізнес-правило
     * піцерії (25 см: -20%, 35 см: +25%, 40 см: +50%). Підклас не повинен
     * мати змоги його змінити, інакше чеки за однаковий розмір різнилися б.
     */
    public final double getSizeCoefficient() {
        return switch (diameter) {
            case 25 -> 0.80;
            case 35 -> 1.25;
            case 40 -> 1.50;
            default -> 1.00;
        };
    }

    /** Ціна однієї піци з урахуванням розміру та додаткового сиру. */
    public final double getPrice() {
        double price = basePrice * getSizeCoefficient();
        return extraCheese ? price + EXTRA_CHEESE_PRICE : price;
    }

    public double getBasePrice() { return basePrice; }
    public int getDiameter() { return diameter; }
    public boolean hasExtraCheese() { return extraCheese; }

    @Override
    public String toString() {
        return String.format("%s %d см%s, %.2f грн", getName(), diameter,
                extraCheese ? " + сир" : "", getPrice());
    }
}
