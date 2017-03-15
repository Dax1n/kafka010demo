package com.daxin.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import com.daxin.kafka010.KafkaUtil;

/**
 * 手动指定offset进行消费
 * 
 * @author Daxin
 *
 */
public class ConsumerExactlyOnce {
	static Connection con = null;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "root"); // 链接本地MYSQL
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		KafkaConsumer<String, String> consumer = KafkaUtil.getKafkaConsumer();
		TopicPartition tp = new TopicPartition(KafkaUtil.TOPIC_NAME, 0);
		consumer.assign(Arrays.asList(tp));

		//获取目前消费的最大偏移
		int currentOffset = DBUtils.getMaxId();

		System.out.println("max currentOffset = " + currentOffset);

		//定位到最大偏移
		consumer.seek(tp, currentOffset);//
		
		
		
		while (true) {

			// consumer.poll(100)是每一100ms获取一次数据放到records中，如果没有可获取的数据的话返回empty
			ConsumerRecords<String, String> records = consumer.poll(100);
			System.out.println("start");
			// records传入到process方法中，只要consumer.poll能持续不断向records缓存数据的话，就会一直在process的for循环中处理
			process(records);// 带有事务的处理逻辑
			System.out.println("end");
		} // while

	}

	/**
	 * 
	 * @param records
	 */
	public static void process(ConsumerRecords<String, String> records) {
		
		
		System.err.println("records.count()= " + records.count());// 当我一共有14724条时候，一次就全部取出来了。一会测试一下最多一次能取多少数据
		for (ConsumerRecord<String, String> record : records) {
			try {

				con.setAutoCommit(false);

				Statement stmt = con.createStatement();

				stmt.executeUpdate("INSERT INTO OffSetTable (id) VALUES (" + record.offset() + ")");

				// System.err.println(record.offset() + " " + record.value());

				if (record.offset() == 800) {
					// 模拟计算offset的消息时候抛出异常 事务回滚，最后查询数据库select * from OffSetTable where id =800 还是不存在的
					throw new Exception();
				}

				con.commit();

			} catch (Exception e) {
				e.printStackTrace();
				//也可以在此处进行存储到一个数据库表，用来记录丢失的数据offset
				
				try {
					con.rollback();// 事务回滚，最后查询数据库select * from OffSetTable where id =800 还是不存在的
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	
	
	

	/**
	 * 
	 * 实现错误版本：异常捕获出问题 在我测试程序时候由于我只有14724条记录，一次consumer.poll(100);就全取出来了
	 * 然后我由于for循环在try语句块中实现，一旦抛出异常就停止了for循环遍历其他records中的记录，因为导致直接跳出循环
	 * 最后又consumer.poll(100);取数据，但是由于第一次去消费数据已经把数据全部消费完毕，所以以后再也取不到数据
	 * 
	 * @param 错误实现方式
	 */
	public static void processError(ConsumerRecords<String, String> records) {
		System.err.println("records.count()= " + records.count());
		try {
			for (ConsumerRecord<String, String> record : records) {

				con.setAutoCommit(false);

				Statement stmt = con.createStatement();

				stmt.executeUpdate("INSERT INTO OffSetTable (id) VALUES (" + record.offset() + ")");

				System.err.println(record.offset() + "   " + record.value());

				if (record.offset() == 500) {
					throw new Exception();
				}

				con.commit();

			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

	}

}
