package personal.batcherhelper;

public class BatchItem {

    private String mItem;
    private boolean mIsQC;
    private int mCount;
    private int mIndexInArray;

    public int getmIndexInArray() {
        return mIndexInArray;
    }

    public void setmIndexInArray(int mIndexInArray) {
        this.mIndexInArray = mIndexInArray;
    }

    public boolean getmIsQC() {
        return mIsQC;
    }

    public void setmIsQC(boolean mIsQC) {
        this.mIsQC = mIsQC;
    }

    public String getmItem() {
        return mItem;
    }

    public void setmItem(String mItem) {
        this.mItem = mItem;
    }

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }
}
