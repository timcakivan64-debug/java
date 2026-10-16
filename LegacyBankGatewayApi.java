/**
 * ЛР5, рівень 3 (Adapter) — УЯВА, ЩО ЦЕ СТОРОННЯ БІБЛІОТЕКА.
 *
 * Навмисно написаний із несумісним API: інша назва методу (chargeCard,
 * не pay), інший тип суми (копійки типу long, не грн типу double) і
 * повертає String-статус, а не double. Ми НЕ маємо права змінювати цей
 * клас (так само, як не можна змінити код чужої бібліотеки) — тому для
 * нього й потрібен окремий Adapter, а не правка напряму.
 */
public class LegacyBankGatewayApi {
    public String chargeCard(String cardNumber, long amountInCents) {
        return "OK: charged " + amountInCents + " cents from card " + cardNumber;
    }
}
