package com.example.testmoderation.datamodels;

public class PropositionModel {

    private  String proposition;
    private String score;
    private String result;
    private String date;

    public PropositionModel(String proposition) {
        this.proposition = proposition;
    }

    public PropositionModel(String proposition, String score, String result, String date) {
        this.proposition=proposition;
        this.score=score;
        this.result=result;
        this.date=date;
    }

    public String getProposition() {
        return proposition;
    }

    public String getScore() {
        return score;
    }

    public String getResult() {
        return result;
    }

    public void setProposition(String proposition) {
        this.proposition = proposition;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
