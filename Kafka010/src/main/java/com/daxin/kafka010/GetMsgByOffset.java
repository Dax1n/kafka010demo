package com.daxin.kafka010;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

/**
 * 手动指定offset进行消费
 * 
 * @author Daxin
 *
 */
public class GetMsgByOffset {
	public static void main(String[] args) throws Exception {

		KafkaConsumer<String, String> consumer = KafkaUtil.getKafkaConsumer();
		// 报错为 java.lang.IllegalStateException:Subscription to topics,
		// partitions and pattern are mutually exclusive
		// 方法注释： It is not possible to combine topic subscription with group
		// management with manual partition assignment through
		// assign(Collection).
		 //consumer.subscribe(Arrays.asList(KafkaUtil.TOPIC_NAME));// 打开注释错误，方法的说明说的非常清楚，subscribe不可以和assign一起使用

		// 指定消费的topic和该topic的分区编号
		TopicPartition tp = new TopicPartition(KafkaUtil.TOPIC_NAME, 0);
		consumer.assign(Arrays.asList(tp));

		// 指定消费的offset
		consumer.seek(tp, 13844);//

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);

			for (ConsumerRecord<String, String> record : records) {
				//exactly once 开启事务
				
				//模拟处理数据
				System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
				//exactly once 提交事务
//				Thread.sleep(1000);
			}
		}

	}
}
