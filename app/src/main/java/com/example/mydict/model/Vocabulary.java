
package com.example.mydict.model;

public class Vocabulary {
    private int id;
    private int topicId;
    private String word;
    private String meaning;
    private String partOfSpeech;
    private String example;

    public Vocabulary(int id, int topicId, String word,
                      String meaning, String partOfSpeech,
                      String example) {
        this.id = id;
        this.topicId = topicId;
        this.word = word;
        this.meaning = meaning;
        this.partOfSpeech = partOfSpeech;
        this.example = example;
    }

    public Vocabulary(int topicId, String word,
                      String meaning, String partOfSpeech,
                      String example) {
        this.topicId = topicId;
        this.word = word;
        this.meaning = meaning;
        this.partOfSpeech = partOfSpeech;
        this.example = example;
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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
