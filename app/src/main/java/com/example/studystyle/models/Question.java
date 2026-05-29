package com.example.studystyle.models;

public class Question {
    private int id;
    private String questionText;
    private String optionA; // Visual
    private String optionB; // Auditori
    private String optionC; // Kinestetik
    private int selectedOption; // 0=none, 1=A, 2=B, 3=C

    public Question() {}

    public Question(int id, String questionText,
                    String optionA, String optionB, String optionC) {
        this.id           = id;
        this.questionText = questionText;
        this.optionA      = optionA;
        this.optionB      = optionB;
        this.optionC      = optionC;
        this.selectedOption = 0;
    }

    public int    getId()             { return id; }
    public void   setId(int id)       { this.id = id; }
    public String getQuestionText()   { return questionText; }
    public void   setQuestionText(String q) { this.questionText = q; }
    public String getOptionA()        { return optionA; }
    public void   setOptionA(String a){ this.optionA = a; }
    public String getOptionB()        { return optionB; }
    public void   setOptionB(String b){ this.optionB = b; }
    public String getOptionC()        { return optionC; }
    public void   setOptionC(String c){ this.optionC = c; }
    public int    getSelectedOption() { return selectedOption; }
    public void   setSelectedOption(int v) { this.selectedOption = v; }
}
