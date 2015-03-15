package cn.edu.bjut.sse.wifi.server.knn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import cn.edu.bjut.sse.wifi.server.model.LocationDetails;
import cn.edu.bjut.sse.wifi.server.model.WifiDetail;

public class KNN {

	
	private final Comparator<KNNNode> comparator = new Comparator<KNNNode>() {

		@Override
		public int compare(KNNNode o1, KNNNode o2) {
			if (o1.getDistance() >= o2.getDistance()) {
				return -1;
			} else {
				return 1;
			}
		}
	};

	/**
	 * 获取K个不同的随机数
	 * 
	 * @param k
	 *            随机数的个数
	 * @param max
	 *            随机数最大的范围
	 * @return 生成的随机数数组
	 */
    public List<Integer> getRandKNum(int k, int max) {
        List<Integer> rand = new ArrayList<Integer>(k);
        for (int i = 0; i < k; i++) {
            int temp = (int) (Math.random() * max);
            if (!rand.contains(temp)) {
                rand.add(temp);
            } else {
                i--;
            }
        }
        return rand;
    }
    
    /**
	 * 计算测试元组与训练元组之前的距离
	 * @param d1 测试元组
	 * @param d2 训练元组
	 * @return 距离值
	 */
    public double calDistance(List<Double> d1, List<Double> d2) {
        double distance = 0.00;
        for (int i = 0; i < d1.size(); i++) {
            distance += (d1.get(i) - d2.get(i)) * (d1.get(i) - d2.get(i));
        }
        return distance;
    }
    
    
    //问题：在计算这个距离的时候，将数据库里面的数据都进行一次过滤，计算的时候，只考虑一个ap点所发出的一个信号
    
    public double wifiCalDistance(List<WifiDetail> d1,List<WifiDetail> d2){
    	double distance = 0.00;
    	int wifiLength = 0;
    	int d1Length = d1.size();
    	int d2Length = d2.size();
    	//if(d1Length >= 10 && d2Length >= 10){
    		
    		//wifiLength = 10;
    	//}else{
			wifiLength = (int) (d1Length < d2Length ? (d1Length*0.6):(d2Length*0.6));
		//}
    	
    	for(int i = 0;i < wifiLength;i++){
    		
    		String d1BSSID = d1.get(i).getBSSID();
    		
    		d1BSSID = d1BSSID.substring(0, 14);
    		
    		for(int j = 0; j < d2.size();j++){
    			
    			if(d1BSSID.equals(d2.get(j).getBSSID().substring(0, 14))){  //最后两位不需要比较,与@see MatchingService.getUnqiueAp()
    				
    				distance += ((double)d1.get(i).getRSS() - (double)d2.get(j).getRSS())*((double)d1.get(i).getRSS() - (double)d2.get(j).getRSS());
    			}
    		}
    		
    		
    	}
    	
    	return distance;
    }
    
    /**
	 * 执行KNN算法，获取测试元组的类别
	 * @param datas 训练数据集
	 * @param testData 测试元组
	 * @param k 设定的K值
	 * @return 测试元组的类别
	 */
    public String knn(List<List<Double>> datas, List<Double> testData, int k) {
        PriorityQueue<KNNNode> pq = new PriorityQueue<KNNNode>(k, comparator);
        List<Integer> randNum = getRandKNum(k, datas.size());
        for (int i = 0; i < k; i++) {
            int index = randNum.get(i);
            List<Double> currData = datas.get(index);
            String c = currData.get(currData.size() - 1).toString();
            KNNNode node = new KNNNode(index, calDistance(testData, currData), c);
            pq.add(node);
        }
        for (int i = 0; i < datas.size(); i++) {
            List<Double> t = datas.get(i);
            double distance = calDistance(testData, t);
            KNNNode top = pq.peek();
            if (top.getDistance() > distance) {
                pq.remove();
                pq.add(new KNNNode(i, distance, t.get(t.size() - 1).toString()));
            }
        }
        return getMostClass(pq);
    }
    
    
    
    /**
     * 适合wifi数据定位的算法，将原knn算法修改了一下
     * @param ld
     * @param wifi
     * @param k
     * @return
     */
    public String wifiKnn(List<LocationDetails> ld,List<WifiDetail> wifi,int k){
    	
    	PriorityQueue<KNNNode> pq = new PriorityQueue<KNNNode>(k, comparator);
    	
    	List<Integer> randNum = getRandKNum(k, ld.size());
    	
    	
    	for(int i = 0;i < k;i++){
    		int index = randNum.get(i);
    		List<WifiDetail> currData = ld.get(index).getWifiDetails();
    		String c = ""+ld.get(index).getType();
    		KNNNode node = new KNNNode(index, wifiCalDistance(wifi, currData), c);
    		pq.add(node);
    	}
    	for(int i = 0;i < ld.size();i++){
    		List<WifiDetail> t = ld.get(i).getWifiDetails();
    		double distance = wifiCalDistance(wifi, t);
    		KNNNode top = pq.peek();
    		
    		if(top.getDistance() > distance){
    			pq.remove();
    			pq.add(new KNNNode(i, distance, ""+ld.get(i).getType()));
    		}
    	}
    	
    	
    	return getMostClass(pq);
    	
    }
    
    
    /**
	 * 获取所得到的k个最近邻元组的多数类
	 * @param pq 存储k个最近近邻元组的优先级队列
	 * @return 多数类的名称
	 */
    private String getMostClass(PriorityQueue<KNNNode> pq) {
        Map<String, Integer> classCount = new HashMap<String, Integer>();
        int pqsize = pq.size();
        for (int i = 0; i < pqsize; i++) {
            KNNNode node = pq.remove();
            String c = node.getC();
            if (classCount.containsKey(c)) {
                classCount.put(c, classCount.get(c) + 1);//增加距离变量
            } else {
                classCount.put(c, 1);
            }
        }
        
        /**
         * 1、采集不用均值，用概率分布算法。
         * 2、下回测试去一层。
         */
        //个数一样的时候取距离小的那一类
        
        
        int maxIndex = -1;
        int maxCount = 0;
        Object[] classes = classCount.keySet().toArray();
        for (int i = 0; i < classes.length; i++) {
            if (classCount.get(classes[i]) > maxCount) {
                maxIndex = i;
                maxCount = classCount.get(classes[i]);
            }
        }
        return classes[maxIndex].toString();
    }
}
