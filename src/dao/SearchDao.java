package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class SearchDao {
	/**
	 * 校验是否存在url
	 * 
	 * @param con
	 * @param url
	 * @throws Exception
	 */

	public static ResultSet Search(Connection con, String flag, String table)
			throws Exception {
		String sql = "select * from " + table + " where flag='" + flag+ "'";
		PreparedStatement pstmt = con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	/**
	 * 校验是否存在url
	 * 
	 * @param con
	 * @param url
	 * @throws Exception
	 */

	public static int SearchExist(Connection con, String url, String log)
			throws Exception {
		String sql = "select * from " + log + " where url='" + url
				+ "' and url_crc=CRC32('" + url + "')";
		// String
		// sql="select * from "+log+" where url='www.baidu.com' and url_crc = crc32('www.baidu.com')";

		PreparedStatement pstmt = con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			rs.close();
			pstmt.close();
			return 1;
		} else {
			rs.close();
			pstmt.close();
			return 0;
		}
	}
}
