package cn.downrice.graduation_discuss.async;

public enum EventType {
    LIKE(0),
    LOGIN(1);

    private int  value;
    EventType(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}
