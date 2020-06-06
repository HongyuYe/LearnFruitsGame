package example.com.gameapp.game;

import android.graphics.Bitmap;

public class Question {
    private String celebrityName;
    private Bitmap celebrityImage;
    private String[] possibleNames;

    public Question(String celebrityName, Bitmap celebrityImage, String[] possibleNames) {
        this.celebrityName = celebrityName;
        this.celebrityImage = celebrityImage;
        this.possibleNames = possibleNames;
    }

    public boolean check(String guess) {
        return guess.equals(celebrityName);
    }

    public Bitmap getCelebrityImage() {
        return celebrityImage;
    }

    public String[] getPossibleNames() {
        return possibleNames;
    }
}
