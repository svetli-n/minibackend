package backend.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ScoreList<K, V extends Comparable> {

    private final ConcurrentMap<K, ConcurrentMap<K, V>> scores = new ConcurrentHashMap<>();

    public void add(Score score) {
        K level = (K) score.getLevel();
        K userId = (K) score.getUserId();
        V scoreValue = (V) score.getScore();
        V oldScoreValue;
        do {
            oldScoreValue = scores
                .computeIfAbsent(level, k -> new ConcurrentHashMap<>())
                .putIfAbsent(userId, scoreValue);
        } while (
            oldScoreValue != null &&
            oldScoreValue.compareTo(scoreValue) < 0 &&
            !scores.get(level).replace(userId, oldScoreValue, scoreValue)
        );
    }

    public List<Map.Entry<K, V>> getSortedDesc(K level, int firstN) {
        return Collections.unmodifiableList(
                (List<Map.Entry<K, V>>)scores.get(level).entrySet().stream()
                        .sorted(Map.Entry.<K, V> comparingByValue().reversed())
                        .limit(firstN)
                        .collect(Collectors.toList())
        );
    }
}
