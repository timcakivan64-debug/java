/**
 * Підклас PizzeriaException: некоректні чайові
 * (доменне правило — чайові не можуть бути від'ємними).
 */
public class InvalidTipException extends PizzeriaException {
    private final double invalidTip;

    public InvalidTipException(String message, double invalidTip) {
        super(message);
        this.invalidTip = invalidTip;
    }

    public double getInvalidTip() {
        return invalidTip;
    }
}
