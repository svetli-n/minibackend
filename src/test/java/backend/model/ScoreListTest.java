package backend.model;

import backend.controller.validator.ValidateWith;
import backend.controller.validator.Validator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;


// TODO Add tests
public class ScoreListTest {

    @Test
    public void ttest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
//        Session session = null;
        Session session = new Session(1, 3);
        Class<? extends Session> clazz = session.getClass();
        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(ValidateWith.class)) {
                ValidateWith validateWith = field.getAnnotation(ValidateWith.class);
                Class<Validator> piv = validateWith.validator();
                Method validateMethod = piv.getMethod("validate", String.class);
                assertEquals(1, validateMethod.invoke(session.getUserId()));
            }
        }
    }
}
