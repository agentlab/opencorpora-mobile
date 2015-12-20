package org.opencorpora.data;

public class TaskType {
    private int mId;
    private String mName;
    private int mComplexity;

    public TaskType(int id, String name, int complexity) {
        mComplexity = complexity;
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getComplexity() {
        return mComplexity;
    }
}
