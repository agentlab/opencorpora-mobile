package org.opencorpora.data;

import java.util.HashMap;

public class Task extends BaseTask {
    private String mLeftContext;
    private String mRightContext;
    private boolean mHasInstruction;
    private HashMap<Integer, String> mChoices;

    public Task(int id, int type) {
        super(id, type);
    }

    public Task(int id,
                int type,
                String leftContext,
                String rightContext,
                boolean hasInstruction){
        super(id, type);
        mLeftContext = leftContext;
        mRightContext = rightContext;
        mHasInstruction = hasInstruction;
        mChoices = new HashMap<>();
    }

    public String getLeftContext() {
        return mLeftContext;
    }

    public String getRightContext() {
        return mRightContext;
    }

    public boolean hasInstruction(){
        return mHasInstruction;
    }

    public String getChoiceByNumber(int choiceNum){
        return mChoices.get(choiceNum);
    }

    public void setLeftContext(String value){
        mLeftContext = value;
    }

    public void setRightContext(String value){
        mRightContext = value;
    }

    public void setHasInstruction(boolean value){
        mHasInstruction = value;
    }

    public void setChoiceByNumber(int number, String value){
        mChoices.put(number, value);
    }
}
