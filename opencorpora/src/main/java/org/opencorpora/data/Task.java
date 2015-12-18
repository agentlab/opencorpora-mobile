package org.opencorpora.data;

import java.util.HashMap;

public class Task extends BaseTask {
    private String mTarget;
    private String mLeftContext;
    private String mRightContext;
    private boolean mHasInstruction;
    private HashMap<Integer, String> mChoices;

    public Task(int id, TaskType type) {
        super(id, type);
    }

    public Task(int id,
                TaskType type,
                String target,
                String leftContext,
                String rightContext,
                boolean hasInstruction){
        super(id, type);
        mTarget = target;
        mLeftContext = leftContext;
        mRightContext = rightContext;
        mHasInstruction = hasInstruction;
        mChoices = new HashMap<>();
    }

    public String getTarget() {
        return mTarget;
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

    public HashMap<Integer, String> getChoices(){
        return mChoices;
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

    public void setTarget(String target) {
        mTarget = target;
    }
}
