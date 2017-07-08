package com.example.macbook.ear4music;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by macbook on 03.07.17.
 */
public class StatisticsStorage implements Serializable {
    private static class Answer implements Serializable {
        NotesEnum actualNote;
        NotesEnum answeredNote;

        Answer(NotesEnum actualNote, NotesEnum answeredNote) {
            this.actualNote = actualNote;
            this.answeredNote = answeredNote;
        }

        boolean isCorrect() {
            return actualNote == answeredNote;
        }

        boolean isMissed() {
            return answeredNote == null;
        }
    }

    public static class Result {
        int correct = 0;
        int wrong = 0;
        int missed = 0;
    }

    private ConcurrentHashMap<Integer, Answer> answers;
    private int correctCount;
    private int wrongCount;
    private int missedCount;

    public StatisticsStorage() {
        answers = new ConcurrentHashMap<>();
    }

    public void submitAnswer(int noteNumber, NotesEnum actualNote, NotesEnum answeredNote) {
        if (!answers.containsKey(noteNumber) && actualNote != null) {
            Answer answer = new Answer(actualNote, answeredNote);
            answers.put(noteNumber, answer);
            if (answer.isCorrect()) {
                correctCount++;
            } else {
                if (answer.isMissed()) {
                    missedCount++;
                } else {
                    wrongCount++;
                }
            }
        }
    }

    public HashMap<NotesEnum, Result> calcFinalResult() {
        HashMap<NotesEnum, Result> result = new HashMap<>();
        for (int key : answers.keySet()) {
            Answer answer = answers.get(key);
            Result res = result.get(answer.actualNote);
            if (res == null) {
                res = new Result();
                result.put(answer.actualNote, res);
            }
            if (answer.isCorrect()) res.correct++;
            else if (answer.isMissed()) res.missed++;
            else res.wrong++;
        }
        return result;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public int getMissedCount() {
        return missedCount;
    }

    public int getOverallCount() {
        return correctCount + wrongCount + missedCount;
    }
}
