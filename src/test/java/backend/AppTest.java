package backend;

import com.sun.net.httpserver.HttpServer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO Add concurrent tests
class AppTest {

    private HttpServer httpServer;
    private int port = 8081;
    private String hostname = "localhost";

    @BeforeEach
    void startServer() throws IOException {
        httpServer = new Server<Integer>().getServer(hostname, port);
        httpServer.start();
    }

    @AfterEach
    void stopServer() {
        httpServer.stop(0);
    }

    @Test
    void loginRequestTest() throws IOException {
        // Given
        HttpUriRequest request = new HttpGet( "http://localhost:8081/42/login/");
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
        String sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        assertTrue(sessionId.length() > 0);
    }

    @Test
    void unsupportedActionTest() throws IOException {
        // Given
        HttpUriRequest request = new HttpGet( "http://localhost:8081/4224/users");
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
        assertEquals("", EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
    }

    @Test
    void tooShortURITest() throws IOException {
        // Given
        HttpUriRequest request = new HttpGet( "http://localhost:8081/4224");
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
        assertEquals(
            "",
            EntityUtils.toString(httpResponse.getEntity(), "UTF-8")
        );
    }

    @Test
    void postRequestOkTest() throws IOException {
        // Given
        HttpUriRequest request = new HttpGet( "http://localhost:8081/42/login/");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

        request = new HttpPost(String.format("http://localhost:8081/442/score?sessionkey=%s", sessionId));
        String score = "1500";
        HttpEntity entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }


    @Test
    void postRequestInvalidUriTest() throws IOException {
        // Given
        HttpUriRequest request = new HttpPost("http://localhost:8081/score?sessionkey=abc");
        String score = "1500";
        HttpEntity entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    void postRequestMissingSessionKeyTest() throws IOException {
        // Given
        HttpUriRequest request = new HttpPost("http://localhost:8081/score?user=me");
        String score = "1500";
        HttpEntity entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    void postRequestNotLoggedInTest() throws IOException {
        // Given
        HttpUriRequest request = new HttpPost("http://localhost:8081/42/score?sessionkey=abc");
        String score = "1500";
        HttpEntity entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    void postRequestInvalidLevelTest() throws IOException {
        // Given
        HttpUriRequest request = new HttpGet( "http://localhost:8081/42/login/");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

        request = new HttpPost(String.format("http://localhost:8081/-1/score?sessionkey=%s", sessionId));
        String score = "1500";
        HttpEntity entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    void postRequestInvalidScoreTest() throws IOException {
        // Given
        HttpUriRequest request = new HttpGet( "http://localhost:8081/42/login/");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

        request = new HttpPost(String.format("http://localhost:8081/442/score?sessionkey=%s", sessionId));
        String score = "15000000000000000000";
        HttpEntity entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
    }

    // TODO Refactor using common method to auth and post
    @Test
    void highScoreListTest() throws IOException {
        HttpUriRequest request = new HttpGet( "http://localhost:8081/42/login/");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        request = new HttpPost(String.format("http://localhost:8081/111/score?sessionkey=%s", sessionId));
        String score = "1";
        HttpEntity entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

        request = new HttpGet( "http://localhost:8081/442/login/");
        httpResponse = HttpClientBuilder.create().build().execute(request);
        sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        request = new HttpPost(String.format("http://localhost:8081/111/score?sessionkey=%s", sessionId));
        score = "2";
        entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

        request = new HttpGet( "http://localhost:8081/999/login/");
        httpResponse = HttpClientBuilder.create().build().execute(request);
        sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        request = new HttpPost(String.format("http://localhost:8081/111/score?sessionkey=%s", sessionId));
        score = "3";
        entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

        request = new HttpGet( "http://localhost:8081/999/login/");
        httpResponse = HttpClientBuilder.create().build().execute(request);
        sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        request = new HttpPost(String.format("http://localhost:8081/111/score?sessionkey=%s", sessionId));
        score = "5";
        entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

        request = new HttpGet( "http://localhost:8081/999/login/");
        httpResponse = HttpClientBuilder.create().build().execute(request);
        sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        request = new HttpPost(String.format("http://localhost:8081/111/score?sessionkey=%s", sessionId));
        score = "4";
        entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

        request = new HttpGet( "http://localhost:8081/4442/login/");
        httpResponse = HttpClientBuilder.create().build().execute(request);
        sessionId = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        request = new HttpPost(String.format("http://localhost:8081/111/score?sessionkey=%s", sessionId));
        score = "2";
        entity = new ByteArrayEntity(score.getBytes("UTF-8"));
        ((HttpPost) request).setEntity(entity);
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

        request = new HttpGet( "http://localhost:8081/111/highscorelist");
        // When
        httpResponse = HttpClientBuilder.create().build().execute(request);
        String ranking = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        // Then
        assertEquals("5=999,2=442,2=4442,1=42", ranking);

    }
}
