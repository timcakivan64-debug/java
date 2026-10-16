/**
 * ЛР5, рівень 3 (Adapter): "перекладає" виклики очікуваного інтерфейсу
 * PaymentMethod (з ЛР4) у виклики несумісного LegacyBankGatewayApi,
 * НЕ змінюючи сам LegacyBankGatewayApi.
 *
 * Що саме перекладається:
 *   - назва методу:      pay(double)        -> chargeCard(String, long)
 *   - одиниця суми:      гривні (double)    -> копійки (long)
 *   - тип результату:    double (сума)      <- String (текстовий статус)
 *
 * Композиція, а не успадкування: адаптер ТРИМАЄ посилання на
 * LegacyBankGatewayApi (поле legacyApi), а не успадковує його. Успадкування
 * тут було б недоречним — LegacyBankGatewayApi не має спільної з
 * PaymentMethod природи, це просто стороння утиліта, і екстендити її
 * заради однієї делегованої логіки означало б тягнути в PaymentMethod
 * усе публічне API сторонньої бібліотеки.
 */
public class LegacyBankGatewayAdapter implements PaymentMethod {
    private final LegacyBankGatewayApi legacyApi;
    private final String cardNumber;

    public LegacyBankGatewayAdapter(LegacyBankGatewayApi legacyApi, String cardNumber) {
        this.legacyApi = legacyApi;
        this.cardNumber = cardNumber;
    }

    @Override
    public double pay(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сума до оплати має бути додатною: " + amount);
        }
        long amountInCents = Math.round(amount * 100);
        String legacyResult = legacyApi.chargeCard(cardNumber, amountInCents);
        System.out.println("[LegacyBankGatewayAdapter] " + legacyResult);
        return amount;
    }

    @Override
    public String getLabel() {
        return "стороння банківська система (через адаптер)";
    }
}
