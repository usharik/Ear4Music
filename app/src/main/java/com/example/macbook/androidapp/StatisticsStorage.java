package com.example.macbook.androidapp;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by macbook on 03.07.17.
 */
public class StatisticsStorage {
    private static class Answer {
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

    private ConcurrentHashMap<Integer, Answer> answers;
    private int correctCount;
    private int wrongCount;
    private int missedCount;

    public StatisticsStorage() {
        answers = new ConcurrentHashMap<>();
    }

    public void submitAnswer(int noteNumber, NotesEnum actualNote, NotesEnum answeredNote) {
        if (!answers.containsKey(noteNumber)) {
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

    public int getCorrectCount() {
        return correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public int getMissedCount() {
        return missedCount;
    }
}
