/**
 * Підклас PizzeriaException: некоректний діаметр піци
 * (доменне правило — дозволені лише 25, 30, 35, 40 см).
 */
public class InvalidDiameterException extends PizzeriaException {
    private final int invalidDiameter;

    public InvalidDiameterException(String message, int invalidDiameter) {
        super(message);
        this.invalidDiameter = invalidDiameter;
    }

    public int getInvalidDiameter() {
        return invalidDiameter;
    }
}
