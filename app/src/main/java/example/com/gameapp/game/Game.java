package example.com.gameapp.game;

public class Game {
    private int questionNumber;
    private int score;
    private Question[] questions;

    public Game(Question[] questions) {
        this.questions = questions;
        this.questionNumber = -1;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return questionNumber + 1 >= questions.length;
    }

    public Question next() {
        if (questionNumber + 1 < questions.length) {
            questionNumber++;
            return questions[questionNumber];
        } else {
            return null;
        }
    }

    public void updateScore(boolean correct) {
        if (correct) {
            score++;
        }
    }

    public int count() {
        return questions.length;
    }

    public int optionsCount() {
        return questions[0].getPossibleNames().length;
    }
}
