package example.com.gameapp.game;

import android.graphics.Bitmap;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import example.com.gameapp.ConstantsConfig;

public class GameBuilder {
    private CelebrityManager celebrityManager;

    public GameBuilder(CelebrityManager celebrityManager) {
        this.celebrityManager = celebrityManager;
    }

    public Game create(Difficulty level) {
        int questionTotal = ConstantsConfig.DIFFICULTY_EASY_TOTAL;
        int optionsCount = ConstantsConfig.DIFFICULTY_EASY_OPTIONS_COUNT;
        switch (level) {
            case EASY:
                questionTotal = ConstantsConfig.DIFFICULTY_EASY_TOTAL;
                optionsCount = ConstantsConfig.DIFFICULTY_EASY_OPTIONS_COUNT;
                break;
            case MEDIUM:
                questionTotal = ConstantsConfig.DIFFICULTY_MEDIUM_TOTAL;
                optionsCount = ConstantsConfig.DIFFICULTY_MEDIUM_OPTIONS_COUNT;
                break;
            case HARD:
                questionTotal = ConstantsConfig.DIFFICULTY_HARD_TOTAL;
                optionsCount = ConstantsConfig.DIFFICULTY_HARD_OPTIONS_COUNT;
                break;
            case EXPERT:
                questionTotal = ConstantsConfig.DIFFICULTY_EXPERT_TOTAL;
                optionsCount = ConstantsConfig.DIFFICULTY_EXPERT_OPTIONS_COUNT;
                break;
        }
        Question[] questions = new Question[questionTotal];
        //获取题库中questionTotal个不同的题目位置
        Set<Integer> positionSet = new HashSet<>();
        while (positionSet.size() < questionTotal) {
            int position = new Random().nextInt(celebrityManager.count());
            positionSet.add(position);
        }
        Integer[] positionArray = positionSet.toArray(new Integer[positionSet.size()]);
        //为每个位置下的题目出可能的答案
        for (int i = 0; i < questions.length; i++) {
            String name = celebrityManager.getName(positionArray[i]);
            Bitmap bitmap = celebrityManager.get(positionArray[i]);
            Set<String> answers = new HashSet<>();
            //先添加答案，避免随机结果中没有正确答案
            answers.add(name);
            while (answers.size() < optionsCount) {
                int j = new Random().nextInt(celebrityManager.count());
                String answerName = celebrityManager.getName(j);
                answers.add(answerName);
            }
            questions[i] = new Question(name, bitmap, answers.toArray(new String[answers.size()]));
        }
        return new Game(questions);
    }

}
