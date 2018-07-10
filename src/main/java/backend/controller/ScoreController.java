package backend.controller;

import backend.model.Score;
import backend.controller.validator.GetRequestValidator;
import backend.controller.validator.PositiveIntegerValidator;
import backend.controller.validator.PostRequestValidator;
import backend.controller.validator.ValidationException;
import backend.model.ScoreList;
import backend.view.ScoreListView;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class ScoreController <K, V extends Comparable> extends BaseController {

    private ScoreList<K, V> scoreList;
    private ScoreListView<K, V> scoreListView = new ScoreListView<>();

    public ScoreController() {
        this.scoreList = new ScoreList<>();
    }

    public void getScores(HttpExchange httpExchange, String[] path, int firstN) {
        if (path.length < 3) {
            throw new ControllerException();
        }
        String levelIdString = path[path.length-2];
        try {
            GetRequestValidator.validate(httpExchange);
            K levelId = (K)PositiveIntegerValidator.validate(levelIdString);
            List<Map.Entry<K, V>> maxScoreList = scoreList.getSortedDesc(levelId, firstN);
            String responseText = scoreListView.format(maxScoreList);
            sendResponse(httpExchange, 200, responseText);
        }
        catch (ValidationException e) {
            throw new ControllerException();
        }
    }

    public void addScore(HttpExchange httpExchange, String[] path, K userId) {
        if (path.length < 3) {
            throw new ControllerException();
        }
        String scoreString = getScoreString(httpExchange);
        if (scoreString.isEmpty()) {
            throw new ControllerException();
        }
        String levelString = path[path.length-2];
        try {
            PostRequestValidator.validate(httpExchange);
            Integer scoreValue = PositiveIntegerValidator.validate(scoreString);
            Integer level = PositiveIntegerValidator.validate(levelString);
            Score score = new Score(userId, level, scoreValue);
            scoreList.add(score);
            sendResponse(httpExchange, 200, "");
        }
        catch (ValidationException e) {
            throw new ControllerException();
        }
    }

    private String getScoreString(HttpExchange httpExchange) throws ControllerException{
        String scoreString;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), "UTF-8"))) {
            scoreString = br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
           throw new ControllerException();
        }
        return scoreString;
    }

}
