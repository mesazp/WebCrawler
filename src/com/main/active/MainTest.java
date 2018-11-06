package com.main.active;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.Configuration;
import dao.SearchDao;
import util.DbUtil;


public class MainTest {
	
	public static List getValidConfigurationSet() throws Exception {
	// TODO Auto-generated method stub
	DbUtil dbUtil = new DbUtil();
	List list = new ArrayList();  
	ResultSet rs=SearchDao.Search(dbUtil.getCon(), "1", "configuration");
    while (rs.next()) {  
     	 Configuration rec=new Configuration ();
         rec.setId(rs.getInt(1));
         rec.setUrl(rs.getString(2));
         rec.setFlag(rs.getString(3));
         list.add(rec);
    }
    return list;
}
	
	
	
	
	public static void main(String[] args) {
		
		
		DownLoadImg img = new DownLoadImg();
		DownLoadHTML html = new DownLoadHTML();
		try {
			List list=getValidConfigurationSet();
			for(int i=0;i<list.size();i++)
			{
				Configuration rec=(Configuration) list.get(i);
				img.start(rec);
			    html.start(rec);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}
