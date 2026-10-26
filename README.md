# ЛР 7–9. Типізований CRUD-сервіс: узагальнення, колекції, сортування

Предметна область — піцерія. Продовження проєкту ЛР1–6 (рівні 1, 2 і 3).

## Сутності

* `BaseEntity` (абстрактний): `final long id`, `String name`, гетери, `equals/hashCode` за id.
  Назва не може бути `null` (перевірка в конструкторі).
* `Pizza` та її підкласи (ЛР6) тепер `extends BaseEntity`: `id` і `name` винесено у базовий клас,
  а в підкласах лишились специфічні поля (`diameter`, `meatGrams`, `spicy`).
* `Customer extends BaseEntity` — друга сутність: `phone`, `bonusPoints`.

Нові id генерує `IdGenerator`; для тестів можна передати id явно (`new MargheritaPizza(1, 30, false)`).

## Структура

| Клас / інтерфейс | Роль |
| --- | --- |
| `CRUDService<T extends BaseEntity>` | `findById`, `save`, `delete`, `findAll`, `findAllSorted(Comparator<? super T>)`, default `findAllSortedByName()` |
| `InMemoryCrudService<T>` | універсальне сховище (`LinkedHashMap<Long, T>`) |
| `PizzaService` / `PizzaServiceImpl` | + `findAllSortedByNameAndPrice()` |
| `CustomerService` / `CustomerServiceImpl` | + `findAllSortedByNameAndBonus()` |
| `EntityNotFoundException` | виняток із полем `id` |
| `EntityComparators` | компаратор за іменем (Collator) |
| `EntityUtils.printAll(Collection<? extends BaseEntity>)` | wildcard-метод |
| `CrudDemo` | демонстрація |

## Запуск

Потрібна JDK 25.

```bash
javac *.java
java Main
```
Далі пройти сценарій замовлення (або ввести `Іван`, `2`, `35`, `3`, `так`, `ні`, `20`, `2`, `1`).
Демонстрації ЛР6 і ЛР7–9 друкуються наприкінці. Ввід для швидкої перевірки:
`printf 'Іван\n2\n35\n3\nтак\nні\n20\n2\n1\n' | java Main`.

## Рівень 1

**`EntityNotFoundException` — unchecked** (через `PizzeriaException` → `RuntimeException`).
Відсутній id — зазвичай помилка виклику чи даних, а не подія, яку треба обробляти після
кожного виклику. Checked-виняток змусив би обгортати кожен `findById` у `try/catch`, ускладнив
би лямбди та стріми, а при заміні сховища на БД довелося б міняти сигнатури. Хто хоче —
ловить виняток там, де має сенс (див. `CrudDemo`).

**`T extends BaseEntity`.** Без обмеження `T` — це просто `Object`, і всередині сервісу не можна
було б викликати `getId()` (потрібен у `save` і `findById`) та `getName()` (сортування за іменем).
Довелось би приводити типи вручну, втративши безпеку на етапі компіляції.

**Узагальнений клас.** `InMemoryCrudService<T>` не містить жодного коду, залежного від типу:
він працює і з `Pizza`, і з `Customer` без змін. Масивів немає (`new T[]` заборонено через
стирання типів) — сховище це `Map<Long, T>`.

**Демонстрація:** додавання, оновлення (той самий id — кількість записів не росте), успішний і
неуспішний `delete`, `findById(999)` з обробкою винятку, `save(null)`.

## Рівень 2

**Чому `findAllSortedByNameAndPrice()` в інтерфейсі сутності, а не в `CRUDService<T>`.**
Не в кожного `T` є ціна чи бонуси: це залежить від конкретної сутності. Метод «за іменем»
працює для будь-якого `BaseEntity` (в нього є `name`), а «ім'я + ціна» — лише для `Pizza`.
Тому він у `PizzaService extends CRUDService<Pizza>`, а клас `PizzaServiceImpl extends
InMemoryCrudService<Pizza> implements PizzaService` лише додає його. Програмування «на інтерфейс»
дає змогу пізніше замінити in-memory сховище на JDBC/Hibernate, не змінюючи код, що викликає сервіс.

**Comparable чи Comparator — обрано Comparator.** Comparable задає один «природний» порядок,
а у сутності їх кілька (за іменем, ціною, бонусами, id). Код порівняння в Comparator лежить окремо
від класу, тому нові порядки додаються без змін у сутності, а вторинне сортування легко будується
через `thenComparing...`. До того ж `compareTo` за іменем був би неузгоджений з `equals` (за id).
Вторинні поля: для піци — **ціна** (піци з однаковою назвою відрізняються розміром, а отже ціною);
для клієнта — **бонуси**.

**Ім'я порівнюється через `Collator`** (мовно-залежне порівняння, коректніше за `compareTo`
для кирилиці); `Comparator.nullsLast` та перевірка `name != null` у конструкторі захищають від `null`.

**Сортування не змінює сховище.** `findAllSorted` створює копію (`new ArrayList<>(values())`) і сортує її.
У демо є дані з однаковим іменем «Пепероні» (40 см і 30 см), друк до й після, перевірка
`before.equals(after)` та `clear()` на результаті сортування.

### Вибір колекції для сховища

| Колекція | findById | save (додати/оновити) | delete | відсортований список | Примітки |
| --- | --- | --- | --- | --- | --- |
| `ArrayList<T>` | O(n) — пошук перебором | O(n) (щоб знайти існуючий), O(1) додати в кінець | O(n) | O(n log n) | дублікати id треба перевіряти вручну |
| `HashMap<Long,T>` | O(1) | O(1) | O(1) | O(n log n) | порядок обходу не гарантований |
| `LinkedHashMap<Long,T>` | O(1) | O(1) | O(1) | O(n log n) | порядок вставки зберігається |
| `TreeMap<Long,T>` | O(log n) | O(log n) | O(log n) | O(n log n) (відсортований лише за id) | порядок за ключем (id), а не за іменем |

**Обрано `LinkedHashMap`**: усі головні операції (за id) за O(1), `put` сам розрізняє нове і наявне
значення за ключем, а порядок вставки стабільний (зручно для виводу «до сортування»). `TreeMap` не
допомагає, бо впорядковує за ключем, а не за іменем; відсортований за іменем список все одно
будується копією. При повторному `put` для наявного ключа порядок у `LinkedHashMap` не змінюється
(у режимі insertion-order).

**equals/hashCode і compareTo.** `BaseEntity.equals/hashCode` — за id (з `getClass()`, щоб
зберегти симетрію між різними типами). Компаратор за іменем/ціною **не узгоджений з equals**:
дві різні сутності з однаковим іменем і ціною для компаратора «рівні» (результат 0), а за
`equals` — різні (різні id). Наслідок: такі компаратори не можна використовувати для `TreeSet/TreeMap`
як основне сховище — другий об'єкт з тим самим іменем вважався б дублікатом і зник би. Тому сховище —
`Map` за id, а сортування — лише на етапі виводу.

## Рівень 3

**`findAllSorted(Comparator<? super T> comparator)`** — обидва методи сортування реалізовані через
нього; `findAllSortedByName()` — **default-метод** інтерфейсу (працює для будь-якої сутності).

**Чому `? super T`, а не `Comparator<T>`.** Компаратор, що вміє порівнювати предків `T`, вміє
порівнювати і `T` (контраваріантність). Наприклад, `Comparator<BaseEntity>` (за id або за іменем)
можна застосувати до списку `Pizza` — у `CrudDemo` це показано. З `Comparator<T>` довелося б
писати окремий компаратор для кожної сутності.

**`printAll(Collection<? extends BaseEntity>)`.** `List<Pizza>` **не є** підтипом
`List<BaseEntity>` (узагальнення інваріантні). Якби це дозволили, через змінну `List<BaseEntity>`
можна було б додати `Customer` у список піц і зламати типобезпеку. `? extends BaseEntity` означає
«колекція якогось підтипу `BaseEntity`»: читати елементи як `BaseEntity` безпечно, а додавати
(крім `null`) заборонено — тому `printAll(List<Pizza>)` працює, і це безпечно.

## Git-flow

```bash
git checkout -b lr7/crud-service
git add *.java README.md
git commit -m "LR7-9: generic CRUD service, collections, sorting"
git push -u origin lr7/crud-service
# створити Pull Request у main до дедлайну
```
