package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class AddDao {
	/**
	 * 添加url
	 * 
	 * @param con
	 * @param url
	 * @throws Exception
	 */
	public static int Add(Connection con, String url, String table)
			throws Exception {
		String sql = "insert into " + table + " values('" + url + "',crc32('"
				+ url + "'),'0') ";
		System.out.println(sql);
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.addBatch(sql);
		JOptionPane.showMessageDialog(null, "添加成功");
		pstmt.executeBatch();
		pstmt.close();
		return 1;
	}
}
