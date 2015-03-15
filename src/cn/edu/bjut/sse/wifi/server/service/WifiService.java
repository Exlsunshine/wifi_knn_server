package cn.edu.bjut.sse.wifi.server.service;

import java.sql.Connection;

import cn.edu.bjut.sse.wifi.server.dao.DataBaseOption;
import cn.edu.bjut.sse.wifi.server.model.LocationDetails;

public class WifiService {
	
	private DataBaseOption dbo;
	private Connection connection;
	public int saveLocationInfo(LocationDetails ld){
		
		if (null != ld) {
			
			dbo = new DataBaseOption();

			connection = dbo.getConnection();
			
			int ldId = dbo.saveLocationDetails(ld,connection);
			
			if(ldId > 0){
				dbo.saveWifiInfo(ld.getWifiDetails(),connection,ldId);
			}
			
			dbo.closeConnection();
			return ldId;
		}
		
		
		return -1;
	}
	
}
