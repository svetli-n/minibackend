package backend.controller.validator;

public @interface ValidateWith {
    Class<Validator> validator();
}
