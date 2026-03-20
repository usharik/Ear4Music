package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by macbook on 03.07.17.
 */
public class StatisticsStorage implements Serializable {

    private record Answer(
            NotesEnum actualNote,
            NotesEnum answeredNote,
            long delay) {
        boolean isCorrect() {
            return actualNote == answeredNote;
        }

        boolean isMissed() {
            return answeredNote == null;
        }
    }

    public static class Result {
        public int correct = 0;
        public int wrong = 0;
        public int missed = 0;
    }

    private ConcurrentHashMap<Long, Answer> answers;
    private int correctCount = 0;
    private int wrongCount = 0;
    private int missedCount= 0;

    public StatisticsStorage() {
        reset();
    }

    public void reset() {
        answers = new ConcurrentHashMap<>();
        correctCount = 0;
        wrongCount = 0;
        missedCount= 0;
    }

    public void submitAnswer(NoteInfo noteInfo) {
        submitAnswer(noteInfo.num, noteInfo.note, noteInfo.pressedNote, noteInfo.time);
    }

    public void submitAnswer(JudgedAnswer answer) {
        submitAnswer(answer.noteNumber, answer.expectedNote, answer.pressedNote, answer.noteTime);
    }

    public void submitAnswer(long noteNumber, NotesEnum actualNote, NotesEnum answeredNote, Date noteTime) {
        if (actualNote == null) {
            return;
        }
        answers.computeIfAbsent(noteNumber, k -> {
            Date answerTime = Calendar.getInstance().getTime();
            Answer answer = new Answer(actualNote, answeredNote, answerTime.getTime() - noteTime.getTime());
            if (answer.isCorrect()) {
                correctCount++;
            } else if (answer.isMissed()) {
                missedCount++;
            } else {
                wrongCount++;
            }
            return answer;
        });
    }

    public long getAvgAnswerTime() {
        return Math.round(answers.values().stream()
                .filter(answer -> !answer.isMissed())
                .mapToLong(answer -> answer.delay)
                .summaryStatistics()
                .getAverage());
    }

    public HashMap<NotesEnum, Result> calcFinalResult() {
        HashMap<NotesEnum, Result> result = new HashMap<>();
        for (long key : answers.keySet()) {
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

    public int getCorrectPercent() {
        int overallCount = getOverallCount();
        if (overallCount == 0) {
            return 0;
        }
        return (int) (correctCount / (double) overallCount * 100.0);
    }
}
