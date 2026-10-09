# ЛР3 + ЛР4. Винятки, інтерфейси, абстрактні класи, Strategy

Консольний застосунок мовою Java — предметна область **«Електронне меню піцерії»**.
Дві лабораторні виконано як один проєкт навколо спільної сутності.

**Сутність:** `PizzaOrder` — замовлення піци (клієнт, піца, ціна, діаметр, кількість, чайові).
- ЛР3: доменні правила замовлення порушуються через власні винятки.
- ЛР4: способи оплати (`PaymentMethod`) і розрахунок знижки (`DiscountStrategy`) — варіативна поведінка.

## Файли

| Файл | Призначення |
| --- | --- |
| `Main.java` | точка входу: запускає демонстрацію ЛР3, потім ЛР4 |
| `ExceptionsDemo.java` | **ЛР3**, рівні 1–3 |
| `PizzeriaException.java` | базовий unchecked-виняток (RuntimeException) |
| `InvalidDiameterException.java`, `InvalidTipException.java` | підкласи `PizzeriaException` |
| `InvalidQuantityException.java` | checked-виняток (extends Exception) з полем `invalidQuantity` |
| `PizzaOrder.java` | сутність; валідація (ЛР3); контекст Strategy, `Comparable` + `Receiptable` (ЛР4) |
| `PaymentMethod.java` | інтерфейс оплати + default-метод |
| `AbstractPaymentMethod.java` | абстрактний клас зі спільною валідацією суми |
| `CardPayment.java`, `CashPayment.java` | реалізації через абстрактний клас |
| `WalletPayment.java` | «чиста» реалізація інтерфейсу, перевизначає default-метод |
| `DiscountStrategy.java` | інтерфейс-стратегія знижки |
| `TieredQuantityDiscount.java`, `NoDiscount.java`, `PromoCodeDiscount.java` | три стратегії |
| `Receiptable.java` | інтерфейс бізнес-поведінки (рядок чека) |

## ЛР3. Що реалізовано

**Рівень 1.** Три ситуації з конкретними `catch` і зрозумілими повідомленнями:
`ArithmeticException` (середнє по порожньому списку), `ArrayIndexOutOfBoundsException` (позиція меню),
`InputMismatchException` (текст замість числа в `Scanner`). Блок `finally` закриває `Scanner`.

**Рівень 2.** Checked-виняток `InvalidQuantityException` (`super(message)` + поле `invalidQuantity`),
кидається в конструкторі `PizzaOrder` (кількість поза 1..50) і ловиться окремим `catch`.

**Рівень 3.**
- кілька `catch` в одному `try` у порядку від специфічних до загальних (`tryOrder`);
- re-throw: `createWithLogging` логує й робить `throw e;`, остаточна обробка — на верхньому рівні;
- ієрархія: `PizzeriaException` → `InvalidDiameterException`, `InvalidTipException`;
  один `catch (PizzeriaException e)` ловить обидва підтипи.

## ЛР4. Що реалізовано

**Рівень 1.** Інтерфейс `PaymentMethod` і три реалізації; масив `PaymentMethod[]` + цикл із поліморфним викликом.

**Рівень 2.** Default-метод `printPaymentHeader` (Card/Cash успадковують, Wallet перевизначає);
абстрактний клас для Card/Cash, Wallet — «чистий» інтерфейс; `PizzaOrder implements Comparable<PizzaOrder>, Receiptable`.

**Рівень 3 — Strategy.** `PizzaOrder` приймає `DiscountStrategy` через конструктор і дозволяє
змінити її під час виконання через `setDiscountStrategy(...)` на тому самому об'єкті.

## Запуск

Потрібен JDK 17+.

```bash
javac -encoding UTF-8 *.java
java Main
```

Якщо кирилиця в консолі Windows відображається некоректно:

```bat
chcp 65001
java -Dstdout.encoding=UTF-8 Main
```

## Git-flow

```bash
git checkout -b lr4/pizzeria-exceptions-interfaces
git add *.java README.md
git commit -m "LR3+LR4: exceptions, interfaces, abstract class, Strategy for pizzeria"
git push -u origin lr4/pizzeria-exceptions-interfaces
# створити Pull Request у main
```
