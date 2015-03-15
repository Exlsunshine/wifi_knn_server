package cn.edu.bjut.sse.wifi.server.matching.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.bjut.sse.wifi.server.dao.DataBaseOption;
import cn.edu.bjut.sse.wifi.server.knn.KNN;
import cn.edu.bjut.sse.wifi.server.model.LocationDetails;
import cn.edu.bjut.sse.wifi.server.model.WifiDetail;


/**
 * 这个类是用来处理指纹匹配过程中的一些数据的处理。
 * 
 * 在接收到手机传过来的匹配请求之后，@see com.wifi.server.matching.MatchingServlet
 * 将接收到的json字符串转化成   @see com.wifi.server.model.LocationDetails,然后使用其中
 * 部分有效的wifi信号值与指纹库的中的信号值进行匹配，然后返回一个匹配结果。
 *
 */
public class MatchingService {
	
	private DataBaseOption dbo = null;
	
	private KNN knn = null;
	
	private static final int K = 8;                      //k值
	
	private static final int WIFI_LENGTH = 5;            //参与匹配的wifi信号的个数
	
	
	
	
	
	/**
	 * 用knn算法进行匹配
	 * @param ld
	 * @return
	 */
	public LocationDetails matching(LocationDetails ld){
		
		LocationDetails opsition = null;
		knn = new KNN();
		
		List<WifiDetail> wifi = this.getWifiInfo(ld);
		List<LocationDetails> ldList = this.getWifisFingerPrint();
		
		for(int i = 0;i < ldList.size();i++){   //对从库里取出来的数据进行处理，每个ap点只取一个值
			ldList.get(i).setWifiDetails(getUniqueAp(ldList.get(i).getWifiDetails()));
		}
		
		String type = knn.wifiKnn(ldList, wifi, K);
		
		//Long lId = Long.parseLong(locationId);
		
		for(int i = 0;i < ldList.size();i++){
			
			if(type.equals(ldList.get(i).getType())){
				opsition = ldList.get(i);
				break;
			}
		}
		
		
		return opsition;
	}
	
	
	
	/**
	 * 这个比较器的实现是为了比较wifi信号的强度值，从而确定哪些信号可以参与匹配
	 */
	
	private final Comparator<WifiDetail> wifiComparator = new Comparator<WifiDetail>() {

		@Override
		public int compare(WifiDetail o1, WifiDetail o2) {

			if(o1.getRSS() >= o2.getRSS()){
				return -1;
			}else{
				return 1;
			}
				
		}
		
	};
	
	
	
	/**
	 * 将指纹库中的信号取出来，匹配使用
	 * 
	 * 注意：已提取出来，代码需要重构
	 * @return
	 */
	
	
	public List<LocationDetails> getWifisFingerPrint(){
		
		List<LocationDetails> fingerPrintLists = null;
		
		dbo  = new DataBaseOption();
		
		Connection connection = dbo.getConnection();
		
		fingerPrintLists = dbo.selectLocationDetails(connection);
		
		/*
		for(int i = 0;i < fingerPrintLists.size();i++){
			Collections.sort(fingerPrintLists.get(i).getWifiDetails(), wifiComparator);
		}*/
		
		
		dbo.closeConnection();
		
		return fingerPrintLists;
		
	}
	
	
	/**
	 *  将接收到的wifi信号转化成有效的信号值，待匹配。
	 * @param ld 接收的json串转化成的对象
	 * @return
	 */
	public List<WifiDetail> getWifiInfo(LocationDetails ld){
		List<WifiDetail> wifiInfos = null;
		
		wifiInfos = ld.getWifiDetails();
				
		wifiInfos = this.getUniqueAp(wifiInfos);   //一个ap点只取一个信号值来计算距离
		
		Collections.sort(wifiInfos, wifiComparator);
		
		
		for(int i = 0;i < wifiInfos.size();i++){
			System.out.println(wifiInfos.get(i));
		}
		
		return wifiInfos;
	}
	
	/**
	 * 正常情况下，一个ap会虚拟出好几个mac地址，而这个这个mac地址的rss强度基本相等，
	 * 而几个强度相等的信号值也会参与到计算距离中来，这样参与计算的ap点的个数就会减少，
	 * 这样相邻点之间的差距就会减少。
	 * 这个方法将对这些rss进行处理，对于同一个ap点发出的信号，只取那个信号强度最强的进行计算，
	 * 这样就能覆盖更多的ap，从而使得每个点与其他点的差异性更大，这样就更能方便进行定位。
	 * 
	 * 问题：是否将数据存进库之前就进行以下处理
	 * @param wifis
	 * @return
	 */
	public List<WifiDetail> getUniqueAp(List<WifiDetail> wifis){
		List<WifiDetail> uniqueList = new ArrayList<WifiDetail>();
		String apUniqueBssid = "";
		boolean isExit = false;
		
		for(int i = 0;i < wifis.size();i++){
			
			apUniqueBssid = wifis.get(i).getBSSID().substring(0, 14);
			
			for(int j = 0;j < uniqueList.size();j++){
				
				if(apUniqueBssid.equals(uniqueList.get(j).getBSSID().substring(0, 14))){
					isExit = true;
				}
				
			}
			
			if(isExit){
				isExit = false;
			}else {
				uniqueList.add(wifis.get(i));
			}
		}
		
		
		
		return uniqueList;
	}
	
	
	
	
}
