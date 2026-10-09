/**
 * Власний CHECKED-виняток (успадкований від Exception, а не RuntimeException).
 * Доменне правило: кількість піц у замовленні має бути від 1 до 50.
 * Обраний саме checked-варіант, бо кількість піц — це ключове
 * поле замовлення, і компілятор має примусово нагадати будь-якому,
 * хто викликає validateQuantity(...), що цю помилку треба обробити
 * (на відміну від InvalidDiameterException/InvalidTipException,
 * які є допоміжними перевірками в тій самій операції).
 */
public class InvalidQuantityException extends Exception {
    private final int invalidQuantity;

    public InvalidQuantityException(String message, int invalidQuantity) {
        super(message);
        this.invalidQuantity = invalidQuantity;
    }

    public int getInvalidQuantity() {
        return invalidQuantity;
    }
}
