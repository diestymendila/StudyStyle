package com.example.studystyle.models;

public class Result {
    private int id;
    private int userId;
    private int visualScore;
    private int auditoryScore;
    private int kinestetikScore;
    private String resultType;
    private String date;

    public Result() {}

    public Result(int userId, int visualScore, int auditoryScore, int kinestetikScore,
                  String resultType, String date) {
        this.userId = userId;
        this.visualScore = visualScore;
        this.auditoryScore = auditoryScore;
        this.kinestetikScore = kinestetikScore;
        this.resultType = resultType;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getVisualScore() { return visualScore; }
    public void setVisualScore(int visualScore) { this.visualScore = visualScore; }

    public int getAuditoryScore() { return auditoryScore; }
    public void setAuditoryScore(int auditoryScore) { this.auditoryScore = auditoryScore; }

    public int getKinestetikScore() { return kinestetikScore; }
    public void setKinestetikScore(int kinestetikScore) { this.kinestetikScore = kinestetikScore; }

    public String getResultType() { return resultType; }
    public void setResultType(String resultType) { this.resultType = resultType; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
