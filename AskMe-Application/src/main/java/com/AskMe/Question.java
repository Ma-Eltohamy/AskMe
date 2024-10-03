package com.AskMe;

import java.util.ArrayList;

public class Question {
    private int id;
    private int qid;
    private int from;
    private int to;
    private boolean isAnonymous;
    private String questionText;
    private String answerText;
    private ArrayList<Question> threads;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
            this.questionText = questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public ArrayList<Question> getThreads() {
        return threads;
    }

    public void setThreads(ArrayList<Question> threads) {
        this.threads = threads;
    }
}
