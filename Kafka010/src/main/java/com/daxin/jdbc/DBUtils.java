package com.daxin.jdbc;

import java.sql.*;

/**
 * JDBC代码随便写的比较不规范，只为了应付一下
 * @author Daxin
 *
 */
public class DBUtils {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static Connection con = null;

	public static void insert(int id) {

		try {

			if (con == null) {
				con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "root"); // 链接本地MYSQL
			}

			// 创建声明

			con.setAutoCommit(false);

			Statement stmt = con.createStatement();

			// 新增一条数据
			stmt.executeUpdate("INSERT INTO OffSetTable (id) VALUES (" + id + ")");
			con.commit();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static int getMaxId() throws Exception {

		if (con == null) {
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "root"); // 链接本地MYSQL
		}

		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery("select max(id) as max from OffSetTable");

		int max=Integer.MIN_VALUE;
		while (rs.next()) {
			max =rs.getInt(1);
		}
		return max;
	}

	public static void main(String[] args) throws Exception {

	System.out.println(	getMaxId());

	}
}
