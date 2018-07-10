package backend.model;

public class Score<T> {

    private final T userId, level, score;

    public Score(T userId, T levelId, T score) {
        this.userId = userId;
        this.level = levelId;
        this.score = score;
    }

    T getUserId() {
        return userId;
    }

    T getLevel() { return level; }

    T getScore() {
        return score;
    }

}
