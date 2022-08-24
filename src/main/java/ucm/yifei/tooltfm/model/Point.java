package ucm.yifei.tooltfm.model;

public class Point {

	public String node = "0";//NodeId: 0,1,2,3
	public String value = "";//具体是什么
	public String type = "";//节点类型

	public String msg = "";//节点信息, 如return

	/**
	 * 
	 * @param node 值: 0,1,2,3
	 * @param value 具体是什么: while,for,if...
	 */
	public Point(String node, String value) {
        this.node = node;
        this.value = value;
	}

	/**
	 *
	 * @param node node id
	 * @param type
	 * @param value 具体是什么: while,for,if...
	 */
	public Point(String node, String type, String value) {
		this.node = node;
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
        return "{" +
                "node: '" + node + '\'' +
                ", value: '" + value + "\'" +
				", msg: '" + msg + "\'" +
                "}";
    }

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
