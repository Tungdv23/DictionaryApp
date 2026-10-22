
package com.example.mydict.model;

public class QuizResult {
    private int id;
    private int topicId;
    private int score;
    private int total;
    private String date;

    public QuizResult(int id, int topicId, int score,
                      int total, String date) {
        this.id = id;
        this.topicId = topicId;
        this.score = score;
        this.total = total;
        this.date = date;
    }

    public QuizResult(int topicId, int score,
                      int total, String date) {
        this.topicId = topicId;
        this.score = score;
        this.total = total;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
