/**
 * Базовий (unchecked) виняток усієї предметної області «піцерія».
 * Від нього успадковуються конкретні доменні помилки нижче.
 * Успадкування саме від RuntimeException обрано тому, що це —
 * помилки введених даних клієнтом, які ми самі перевіряємо й кидаємо
 * в межах власного коду, а не зовнішні/відновлювані ситуації,
 * які обов'язково мусив би обробити кожен виклик методу (на відміну
 * від checked-винятку InvalidQuantityException, див. рівень 2).
 */
public class PizzeriaException extends RuntimeException {
    public PizzeriaException(String message) {
        super(message);
    }
}
