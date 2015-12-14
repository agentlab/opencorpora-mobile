package org.opencorpora.data;

public class BaseTask {
    private int mId;
    private TaskType mType;
    BaseTask(int id, TaskType type){
        mId = id;
        mType = type;
    }

    public int getId(){
        return mId;
    }

    public TaskType getType(){
        return mType;
    }
}
