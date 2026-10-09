# ЛР3. Обробка винятків та власні підкласи Exception

Консольний застосунок мовою Java — продовження предметної області ЛР №1
(«Електронне меню піцерії»). До логіки оформлення замовлення додано
обробку виняткових ситуацій: try-catch-finally, власні checked- та
unchecked-винятки, повторне збудження (re-throw) і невелику ієрархію
власних класів винятків.

## Файли

| Файл | Призначення |
| --- | --- |
| `Main.java` | основна логіка меню піцерії + обробка винятків |
| `PizzeriaException.java` | базовий **unchecked** виняток домену (`extends RuntimeException`) |
| `InvalidDiameterException.java` | підклас `PizzeriaException` — некоректний діаметр піци |
| `InvalidTipException.java` | підклас `PizzeriaException` — від'ємні чайові |
| `InvalidQuantityException.java` | власний **checked** виняток (`extends Exception`) — некоректна кількість піц |

## Що саме реалізовано

### Рівень 1. Базовий
- Дві ситуації з можливою помилкою виконання:
  1. `InputMismatchException` — нечислове введення у `Scanner`;
  2. `ArithmeticException` — ділення кількості піц на 0 осіб при розподілі рахунку.
- У кожному `catch` — конкретний тип винятку (без узагальненого `catch (Exception e)`) і зрозуміле повідомлення користувачу.
- Блок `finally` — гарантоване закриття `Scanner` та підсумкове повідомлення.

### Рівень 2. Середній
- Власний **checked**-виняток `InvalidQuantityException` (`extends Exception`), конструктор `super(message)` + власне поле `invalidQuantity`.
- Кидається явно (`throw new ...`) у методі `validateQuantity(...)`, коли кількість піц поза межами 1–50.
- Перехоплюється окремим `catch (InvalidQuantityException e)`.

### Рівень 3. Високий
- Множинна обробка: 6 `catch`-блоків в одному `try`, від специфічних до загального (`PizzeriaException` — останній, бо є батьківським для двох підкласів).
- Повторне збудження (re-throw): метод `readInt(...)` перехоплює `InputMismatchException`, логує подію (`[LOG] ...`) і кидає її далі (`throw e;`) — остаточно обробляється вже в `main()`.
- Невелика ієрархія власних винятків: `PizzeriaException` (базовий, unchecked) → `InvalidDiameterException`, `InvalidTipException` (підкласи). Метод `demoBaseClassCatch()` демонструє, що `catch (PizzeriaException e)` перехоплює обидва підтипи.

## Вимоги

- JDK 25 або новіший (у проєкті використовується компактний файл-джерело з `void main()`).

## Компіляція та запуск

```bash
javac *.java
java Main
```

Якщо кирилиця в консолі Windows відображається некоректно:

```bat
chcp 65001
java -Dstdout.encoding=UTF-8 -Dstdin.encoding=UTF-8 Main
```

## Приклад успішного запуску

Ввід: `Іван`, піца `2`, діаметр `35`, кількість `3`, сир `так`, чайові `20.50`, осіб `2`

```
ДО СПЛАТИ:         739.38 грн
На кожну з 2 осіб припадає приблизно 1 піц(и).
```

## Приклади обробки помилок

| Що вводить користувач | Який виняток спрацьовує | Повідомлення |
| --- | --- | --- |
| `абв` замість номера піци | `InputMismatchException` (re-throw з `readInt`) | «ви ввели текст там, де очікувалося число» |
| діаметр `33` | `InvalidDiameterException` | «Непідтримуваний діаметр 33 см...» |
| кількість `0` | `InvalidQuantityException` (checked) | «Кількість піц має бути від 1 до 50...» |
| чайові `-10` | `InvalidTipException` | «Чайові не можуть бути від'ємними...» |
| осіб `0` | `ArithmeticException` | «неможливо розділити замовлення на 0 осіб» |

Усі п'ять сценаріїв протестовано вручну — кожен виводить коректне повідомлення, після чого незалежно від результату виконується блок `finally`.

## Git-flow

```bash
git checkout -b lr3/pizzeria-exceptions
git add Main.java PizzeriaException.java InvalidDiameterException.java InvalidTipException.java InvalidQuantityException.java README.md
git commit -m "LR3: exception handling for pizzeria menu"
git push -u origin lr3/pizzeria-exceptions
# після цього створити Pull Request у main
```
