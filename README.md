# ЛР4. Інтерфейси, абстрактні класи, Strategy

Консольний застосунок мовою Java — продовження предметної області
«Електронне меню піцерії» (ЛР №1–3). Реалізовано поліморфну поведінку
через інтерфейси, абстрактний клас зі спільною логікою та паттерн Strategy.

## Файли

| Файл | Призначення |
| --- | --- |
| `Main.java` | демонстрація всіх трьох рівнів |
| `PaymentMethod.java` | інтерфейс способу оплати (+ default-метод) |
| `AbstractPaymentMethod.java` | абстрактний клас зі спільною валідацією суми |
| `CardPayment.java`, `CashPayment.java` | реалізації через абстрактний клас |
| `WalletPayment.java` | «чиста» реалізація інтерфейсу, перевизначає default-метод |
| `DiscountStrategy.java` | інтерфейс-стратегія розрахунку знижки |
| `TieredQuantityDiscount.java` | стратегія: знижка за кількістю (як у ЛР №1) |
| `NoDiscount.java` | стратегія: без знижки |
| `PromoCodeDiscount.java` | стратегія: фіксований % за промокодом |
| `Receiptable.java` | інтерфейс бізнес-поведінки (рядок чека) |
| `PizzaOrder.java` | контекст Strategy; реалізує `Comparable` + `Receiptable` одночасно |

## Що саме реалізовано

### Рівень 1. Базовий
- Поведінка, що відрізняється: **спосіб оплати замовлення**.
- Інтерфейс `PaymentMethod` і три реалізації: `CardPayment`, `CashPayment`, `WalletPayment`.
- Масив `PaymentMethod[]` + цикл `for` → поліморфний виклик `pay(...)`: кожен клас списує гроші по-своєму.

### Рівень 2. Середній
- **Default-метод** `printPaymentHeader(...)` в `PaymentMethod` — `CardPayment`/`CashPayment` використовують його без змін, `WalletPayment` перевизначає (додає інформацію про бонуси).
- **Абстрактний клас** `AbstractPaymentMethod` виносить спільну перевірку суми для `CardPayment`/`CashPayment`; `WalletPayment` свідомо реалізує інтерфейс напряму — показано обидва підходи й коментарем пояснено, чому саме так.
- **Клас із двома інтерфейсами одночасно**: `PizzaOrder implements Comparable<PizzaOrder>, Receiptable` — окремо поведінка сортування (`compareTo`) і окремо бізнес-поведінка (`toReceiptLine`).

### Рівень 3. Високий — паттерн Strategy
- `DiscountStrategy` — інтерфейс-стратегія з трьома реалізаціями.
- `PizzaOrder` (контекст) приймає стратегію через конструктор і дозволяє **змінити її під час виконання** методом `setDiscountStrategy(...)` — без створення нового об'єкта. У `Main.java` це показано на одному й тому ж об'єкті `promoOrder`: спочатку знижка за кількістю, потім промокод 20%, потім без знижки.

## Вимоги

JDK 25 або новіший (компактний файл-джерело `Main.java` з `void main()`).

## Компіляція та запуск

```bash
javac *.java
java Main
```

Якщо кирилиця в консолі Windows відображається некоректно:

```bat
chcp 65001
java -Dstdout.encoding=UTF-8 Main
```

## Приклад виводу (фрагмент Strategy-демонстрації)

```
До зміни стратегії:                              Марія | Гавайська x2 | знижка за кількістю ... | ДО СПЛАТИ:  437.50 грн
Після setDiscountStrategy(PromoCodeDiscount 20%): Марія | Гавайська x2 | промокод на 20.0% знижки | ДО СПЛАТИ:  350.00 грн
Після setDiscountStrategy(NoDiscount):            Марія | Гавайська x2 | без знижки               | ДО СПЛАТИ:  437.50 грн
```

Той самий об'єкт `promoOrder`, три різні результати — завдяки Strategy.

## Git-flow

```bash
git checkout -b lr4/pizzeria-interfaces
git add *.java README.md
git commit -m "LR4: interfaces, abstract class, Strategy pattern for pizzeria"
git push -u origin lr4/pizzeria-interfaces
# після цього створити Pull Request у main
```
