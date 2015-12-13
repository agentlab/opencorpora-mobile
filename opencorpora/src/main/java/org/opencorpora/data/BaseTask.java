package org.opencorpora.data;

public class BaseTask {
    private int mId;
    private int mType;
    BaseTask(int id, int type){
        mId = id;
        mType = type;
    }

    public int getId(){
        return mId;
    }

    public int getType(){
        return mType;
    }
}
