package ucm.yifei.tooltfm.model;

public class GraStack
{

    public String id;
    public String key; //节点关键字 if, else, while, for, switch
    public String inWhichStruct;
    public boolean isFirst = true;

    public String content = ""; // 具体内容

    public GraStack(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public GraStack(String id, String key, String inWhichStruct) {
        this.id = id;
        this.key = key;
        this.inWhichStruct = inWhichStruct;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
