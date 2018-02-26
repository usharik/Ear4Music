package com.example.macbook.ear4music.service;

import com.example.macbook.ear4music.NoteInfo;
import com.example.macbook.ear4music.NotesEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by macbook on 03.07.17.
 */
public class StatisticsStorage implements Serializable {
    private static class Answer implements Serializable {
        NotesEnum actualNote;
        NotesEnum answeredNote;
        long delay;

        Answer(NotesEnum actualNote, NotesEnum answeredNote, long delay) {
            this.actualNote = actualNote;
            this.answeredNote = answeredNote;
            this.delay = delay;
        }

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
    private ArrayList<NoteInfo> noteInfos;
    private int correctCount = 0;
    private int wrongCount = 0;
    private int missedCount= 0;

    public StatisticsStorage() {
        reset();
    }

    public void reset() {
        answers = new ConcurrentHashMap<>();
        noteInfos = new ArrayList<>();
        correctCount = 0;
        wrongCount = 0;
        missedCount= 0;
    }

    public void calculate() {
        for (NoteInfo noteInfo : noteInfos) {
            submitAnswer(noteInfo.num, noteInfo.note, noteInfo.pressedNote, noteInfo.time);
        }
    }

    public void submitAnswer(NoteInfo noteInfo) {
        submitAnswer(noteInfo.num, noteInfo.note, noteInfo.pressedNote, noteInfo.time);
    }

    public void submitAnswer(long noteNumber, NotesEnum actualNote, NotesEnum answeredNote, Date noteTime) {
        if (!answers.containsKey(noteNumber) && actualNote != null) {
            Date answerTime = Calendar.getInstance().getTime();
            Answer answer = new Answer(actualNote, answeredNote, answerTime.getTime() - noteTime.getTime());
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

    public long getAvgAnswerTime() {
        long cnt=0;
        long sum=0;
        for (Answer answer : answers.values()) {
            if (!answer.isMissed()) {
                sum+=answer.delay;
                cnt++;
            }
        }
        if (cnt == 0) {
            return 0;
        }
        return sum/cnt;
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
