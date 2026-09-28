import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    // Кодування консолі: на Windows програма сама перемикає її на UTF-8,
    // щоб кирилиця (і, ї, є) виводилась і читалась правильно
    static {
        try {
            if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "chcp 65001 >nul")
                        .inheritIO().start().waitFor();
            }
        } catch (Exception ignored) {
            // якщо не вийшло — програма все одно працює
        }
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    // ...і для вводу
    private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

    // ---------- допоміжні методи введення ----------
    private static String readString(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Некоректне ціле число, спробуйте ще раз.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readString(prompt).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Некоректне число, спробуйте ще раз.");
            }
        }
    }

    private static boolean readBoolean(String prompt) {
        while (true) {
            String s = readString(prompt).toLowerCase();
            if (s.equals("так") || s.equals("т") || s.equals("y")
                    || s.equals("yes") || s.equals("1")) {
                return true;
            }
            if (s.equals("ні") || s.equals("н") || s.equals("n")
                    || s.equals("no") || s.equals("0")) {
                return false;
            }
            System.out.println("Введіть так/ні (або y/n, 1/0).");
        }
    }

    private static Pizza readPizza() {
        String name = readString("  Назва: ");
        int diameter = readInt("  Діаметр (см): ");
        double price = readDouble("  Ціна (грн): ");
        boolean veg = readBoolean("  Вегетаріанська? (так/ні, y/n): ");
        return new Pizza(name, diameter, price, veg);
    }

    // ---------- Рівень 1 ----------
    private static void printArray(String title, Pizza[] arr) {
        System.out.println(title);
        for (Pizza p : arr) {          // цикл for-each
            System.out.println("  " + p);
        }
    }

    private static int countCheaperThan(Pizza[] arr, double limit) {
        int count = 0;
        for (Pizza p : arr) {
            if (p.getPrice() < limit) {
                count++;
            }
        }
        return count;
    }

    // ---------- Рівень 2 ----------
    /** Сортування методом простого обміну (bubble sort) за ціною, за зростанням. */
    private static void bubbleSortByPrice(Pizza[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j].getPrice() > arr[j + 1].getPrice()) {
                    Pizza tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {   // масив уже відсортований
                break;
            }
        }
    }

    private static Pizza[] copyArray(Pizza[] src) {
        Pizza[] copy = new Pizza[src.length];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i];
        }
        return copy;
    }

    // ---------- Рівень 3 ----------
    /** Лінійний пошук за цілим об'єктом. Повертає індекс або -1, якщо не знайдено. */
    private static int linearSearch(Pizza[] arr, Pizza sample) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(sample)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        // ----- Рівень 1: заповнення масиву з клавіатури -----
        int n = 0;
        while (n <= 0) {
            n = readInt("Скільки піц у меню? ");
        }

        Pizza[] menu = new Pizza[n];
        for (int i = 0; i < menu.length; i++) {
            System.out.println("Піца #" + (i + 1) + ":");
            menu[i] = readPizza();
        }

        System.out.println();
        printArray("=== Меню піцерії ===", menu);

        double limit = readDouble("\nПорахувати піци дешевші за (грн): ");
        System.out.println("Кількість піц дешевших за " + limit + " грн: "
                + countCheaperThan(menu, limit));

        // ----- Рівень 2: сортування -----
        Pizza[] sorted = copyArray(menu);
        bubbleSortByPrice(sorted);

        System.out.println();
        printArray("=== До сортування ===", menu);
        printArray("=== Після сортування за ціною (зростання) ===", sorted);

        // ----- Рівень 3: пошук за об'єктом -----
        System.out.println("\nВведіть піцу-зразок для пошуку:");
        Pizza sample = readPizza();
        int index = linearSearch(menu, sample);
        if (index >= 0) {
            System.out.println("Знайдено на позиції " + (index + 1) + ": " + menu[index]);
        } else {
            System.out.println("Такої піци в меню немає.");
        }
    }
}
