# ЛР5. Рефакторинг із патернами GoF (Factory Method, Observer, Adapter)

Це **рефакторинг** наявного коду ЛР1–4 (піцерія), а не новий проєкт.
Поведінка програми не змінилась — змінилась лише структура коду в трьох
конкретних місцях, де вона була справді проблемною.

## Рівень 1. Factory Method

**Проблема "до":** у `Main.java` способи оплати створювались напряму через
`new` у масиві-літералі:
```java
PaymentMethod[] availablePayments = {
        new CardPayment("4521"), new CashPayment(), new WalletPayment(50.0)
};
```
а потім ще раз вибирались через `if/else` за номером, який ввів користувач.
Щоб додати новий спосіб оплати, довелось би правити одразу два місця.

**Рефакторинг:** усе створення винесене в `PaymentMethodFactory.create(int code)`.
`Main.java` тепер лише друкує підписи способів оплати (текст) і звертається
до фабрики:
```java
PaymentMethod chosenPayment = PaymentMethodFactory.create(paymentChoice);
```
**Яку проблему вирішено:** додавання нового способу оплати тепер вимагає
правки лише одного методу (`PaymentMethodFactory.create`) — `Main.java`
можна взагалі не чіпати (принцип OCP: клієнтський код закритий для
модифікації, відкритий для розширення).

## Рівень 2. Observer

**Ситуація:** зміна стану замовлення (підміна стратегії знижки, оплата)
логічно повинна сповіщати кілька незалежних частин системи — друк чека і
аудит-лог. Раніше ці дії (друк, повідомлення) були вручну розкидані по
`Main.java` після кожної операції.

**Рефакторинг:** `PizzaOrder` став Subject — тримає список `OrderObserver`
і сам розсилає подію через `notifyObservers(...)` всередині
`setDiscountStrategy(...)` та нового методу `applyPayment(...)`. Два
незалежні спостерігачі підписані на один і той самий об'єкт:
- `ReceiptPrinterObserver` — друкує оновлений чек;
- `AuditLogObserver` — пише подію в лог.

```java
order.addObserver(new ReceiptPrinterObserver());
order.addObserver(new AuditLogObserver());
order.setDiscountStrategy(new PromoCodeDiscount(20)); // обидва спостерігачі спрацюють автоматично
```

**Яку проблему вирішено:** `PizzaOrder` не знає і не повинен знати, ЩО
саме роблять підписники — друк чека й лог можна додавати/прибирати, не
чіпаючи сам `PizzaOrder`.

## Рівень 3. Adapter

**Ситуація (змодельована навмисно):** `LegacyBankGatewayApi` — уявна
"стороння бібліотека" з несумісним API: метод називається `chargeCard`
(не `pay`), сума передається в копійках як `long` (не в гривнях як
`double`), повертає `String`-статус замість `double`. Змінювати цей клас
не можна (так само, як не можна змінити чужу бібліотеку).

**Рефакторинг:** `LegacyBankGatewayAdapter implements PaymentMethod` —
перекладає виклики: `pay(double)` → `chargeCard(String, long)`, конвертує
гривні в копійки, повертає узгоджений тип. Тримає посилання на
`LegacyBankGatewayApi` через **композицію** (поле), не через успадкування
— бо спільної природи з `PaymentMethod` в оригінального класу немає,
і успадкування змусило б тягнути в наш код усе публічне API чужої бібліотеки.

```java
case 4 -> new LegacyBankGatewayAdapter(new LegacyBankGatewayApi(), "9931");
```

Завдяки цьому `LegacyBankGatewayAdapter` підставляється в `PaymentMethodFactory`
нарівні з `CardPayment`/`CashPayment`/`WalletPayment` — решта коду (`Main`,
`PizzaOrder`) взагалі не підозрює, що під капотом четвертого способу оплати
ховається стороння система з геть іншим API.

## Файли (нові порівняно з ЛР4)

| Файл | Патерн |
| --- | --- |
| `PaymentMethodFactory.java` | Factory Method |
| `OrderObserver.java`, `ReceiptPrinterObserver.java`, `AuditLogObserver.java` | Observer |
| `LegacyBankGatewayApi.java`, `LegacyBankGatewayAdapter.java` | Adapter |

`PizzaOrder.java` змінено: додано список спостерігачів, `addObserver(...)`,
`notifyObservers(...)`, метод `applyPayment(...)` (раніше оплата керувалась
вручну з `Main`). Решта файлів (винятки з ЛР3, `DiscountStrategy` та його
реалізації, `PaymentMethod` та `CardPayment`/`CashPayment`/`WalletPayment`)
— **без змін**.

## Компіляція та запуск

```bash
javac *.java
java Main
```

## Перевірка, що стара функціональність не зламалась

Усі сценарії з ЛР1–4 протестовано вручну після рефакторингу:
- повний успішний сценарій (меню → валідація → промокод → чайові → оплата) — працює, суми рахуються так само;
- усі 5 винятків з ЛР3 (`InputMismatchException`, `InvalidDiameterException`, `InvalidQuantityException`, `InvalidTipException`, `ArithmeticException`) — спрацьовують так само, як до рефакторингу;
- усі 4 способи оплати (картка/готівка/гаманець/стороння система) — працюють через фабрику;
- Strategy (зміна знижки на льоту) — працює, і тепер ще й сповіщає спостерігачів;
- сортування (`Comparable`) і демонстрація ієрархії винятків — не чіпались, працюють як раніше.

## Git-flow

```bash
git checkout -b lr5/pizzeria-patterns
git add *.java README.md
git commit -m "LR5: refactor with Factory Method, Observer, Adapter"
git push -u origin lr5/pizzeria-patterns
# після цього створити Pull Request у main
```
