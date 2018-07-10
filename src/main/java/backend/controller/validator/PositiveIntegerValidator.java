package backend.controller.validator;

public class PositiveIntegerValidator implements Validator {

    public static Integer validate(Object obj) {
        String intStr = (String) obj;
        Integer i;
        try {
            i = Integer.valueOf(intStr);
        }
        catch (NumberFormatException e) {
            throw new ValidationException();
        }
        if (i < 0) {
            throw new ValidationException();
        }
        return i;
    }
}
