package net.freenode.xenomorph.xenomat;

public class CheckSentence {

    private String wrongSentence;
    private String correctSentence;

    public CheckSentence(String wrong, String correct) {
        wrongSentence = wrong;
        correctSentence = correct;
    }

    /**
     * @return the wrongSentence
     */
    public String getWrongSentence() {
        return wrongSentence;
    }

    /**
     * @param wrongSentence the wrongSentence to set
     */
    public void setWrongSentence(String wrongSentence) {
        this.wrongSentence = wrongSentence;
    }

    /**
     * @return the correctSentence
     */
    public String getCorrectSentence() {
        return correctSentence;
    }

    /**
     * @param correctSentence the correctSentence to set
     */
    public void setCorrectSentence(String correctSentence) {
        this.correctSentence = correctSentence;
    }
}
