package com.daxin.kafka010;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
/**
 * 
 * @author Daxin
 *
 */
public class ConsumerTopic {
	public static void main(String[] args) throws Exception {
		KafkaConsumer<String, String> consumer = KafkaUtil.getKafkaConsumer();
		consumer.subscribe(Arrays.asList(KafkaUtil.TOPIC_NAME));

		while (true) {
			/**
			 * KafkaConsumer的poll方法即是从Broker拉取消息，
			 * 在poll之前首先要用subscribe方法订阅一个Topic。
			 * poll方法的入参是拉取超时毫秒数，如果没有新的消息可供拉取，consumer会等待指定的毫秒数，
			 * 到达超时时间后会直接返回一个空的结果集
			 */
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records)
			{
				System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
			}
		}
	}
}
