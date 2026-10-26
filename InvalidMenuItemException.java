/** Підклас PizzeriaException: у меню немає піци з таким номером. */
public class InvalidMenuItemException extends PizzeriaException {
    private final int menuNumber;

    public InvalidMenuItemException(String message, int menuNumber) {
        super(message);
        this.menuNumber = menuNumber;
    }

    public int getMenuNumber() {
        return menuNumber;
    }
}
