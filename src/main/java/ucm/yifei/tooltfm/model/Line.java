package ucm.yifei.tooltfm.model;

public class Line {
	public String fromNode = "";
	public String toNode = "";

	public String label = "";
	
	/**
	 * 
	 * @param fromNode 起始点
	 * @param toNode 指向的点
	 * @param label 线的内容(代码)
	 */
	public Line(String fromNode, String toNode, String label) {
		this.fromNode = fromNode;
        this.toNode = toNode;
        this.label = label;
	}
	
	@Override
    public String toString() {
        String labelText =" ,label: {\n}";
        return "{" +
                "source:'" + fromNode  + '\'' +
                ", target:'" + toNode + '\'' +
				" ,label: '" + label + '\'' +
				//labelText +
                '}';
    }
}
