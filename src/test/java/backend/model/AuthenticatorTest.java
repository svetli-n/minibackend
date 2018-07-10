package backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticatorTest {

    @Test
    void newSessionIsCreated() {
        Authenticator authenticator = new Authenticator(2);
        String sessionId = authenticator.login(42);
        assertTrue(sessionId.length() > 0);
    }

    @Test
    void newSessionIsRecreatedWhenNeeded() throws InterruptedException {
        Authenticator authenticator = new Authenticator(2);
        String firstSessionId = authenticator.login(42);
        System.out.println(firstSessionId);
        assertTrue(firstSessionId.length() > 0);

        Thread.sleep(1000);

        String secondSessionId = authenticator.login(42);
        System.out.println(secondSessionId);
        assertTrue(secondSessionId.length() > 0);
        assertTrue(firstSessionId.compareTo(secondSessionId) == 0);

        Thread.sleep(2000);

        String thirdSessionId = authenticator.login(42);
        System.out.println(thirdSessionId);
        assertTrue(thirdSessionId.length() > 0);
        assertTrue(secondSessionId.compareTo(thirdSessionId) != 0);
    }
}
