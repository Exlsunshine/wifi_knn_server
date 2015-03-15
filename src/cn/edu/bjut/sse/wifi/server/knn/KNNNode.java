package cn.edu.bjut.sse.wifi.server.knn;

public class KNNNode {
    private int    index;    // 元组标号
    private double distance; // 与测试元组的距离
    private String c;        // 所属类别
                             
    public KNNNode(int index, double distance, String c) {
        super();
        this.setIndex(index);
        this.setDistance(distance);
        this.setC(c);
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public void setC(String c) {
        this.c = c;
    }
    
    public String getC() {
        return c;
    }
}
