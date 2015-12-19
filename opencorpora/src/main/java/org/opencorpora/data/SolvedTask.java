package org.opencorpora.data;

public class SolvedTask extends BaseTask {
    private int mAnswer;
    private int mSecondsBeforeAnswer;
    private boolean mIsLeftContextShowed;
    private boolean mIsRightContextShowed;
    private boolean mIsCommented;
    private String mComment;

    public SolvedTask(int id, TaskType type) {
        super(id, type);
    }

    public int getAnswer() {
        return mAnswer;
    }

    public void setAnswer(int value) {
        mAnswer = value;
    }

    public int getSecondsBeforeAnswer() {
        return mSecondsBeforeAnswer;
    }

    public void setSecondsBeforeAnswer(int value) {
        mSecondsBeforeAnswer = value;
    }

    public boolean isLeftContextShowed() {
        return mIsLeftContextShowed;
    }

    public void setIsLeftContextShowed(boolean value) {
        mIsLeftContextShowed = value;
    }

    public boolean isRightContextShowed() {
        return mIsRightContextShowed;
    }

    public void setIsRightContextShowed(boolean value) {
        mIsRightContextShowed = value;
    }

    public boolean isCommented() {
        return mIsCommented;
    }

    public void setIsCommented(boolean value) {
        mIsCommented = value;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String value) {
        mComment = value;
    }
}
