public class Question {
    private int id;
    private String category;
    private String questionText;
    private String optA, optB, optC, optD;
    private String correctOpt;

    public Question(int id, String category, String questionText, String optA, String optB, String optC, String optD, String correctOpt) {
        this.id = id;
        this.category = category;
        this.questionText = questionText;
        this.optA = optA;
        this.optB = optB;
        this.optC = optC;
        this.optD = optD;
        this.correctOpt = correctOpt;
    }

    public String getQuestionText() { return questionText; }
    public String getOptA() { return optA; }
    public String getOptB() { return optB; }
    public String getOptC() { return optC; }
    public String getOptD() { return optD; }
    public String getCorrectOpt() { return correctOpt; }
}