/** Друга сутність: клієнт піцерії. Спільне (id, name) — у BaseEntity. */
public class Customer extends BaseEntity {
    private final String phone;
    private final int bonusPoints;

    public Customer(long id, String name, String phone, int bonusPoints) {
        super(id, name);
        this.phone = phone;
        this.bonusPoints = bonusPoints;
    }

    public String getPhone() { return phone; }

    public int getBonusPoints() { return bonusPoints; }

    @Override
    public String toString() {
        return super.toString() + ", тел. " + phone + ", бонуси: " + bonusPoints;
    }
}
