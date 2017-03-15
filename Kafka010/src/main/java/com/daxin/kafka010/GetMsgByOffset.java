package com.daxin.kafka010;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

public class GetMsgByOffset {
	public static void main(String[] args) throws Exception {

		KafkaConsumer<String, String> consumer = KafkaUtil.getKafkaConsumer();
		// 报错为 java.lang.IllegalStateException:Subscription to topics, partitions and pattern are mutually exclusive
		// It is not possible to combine topic subscription with group
		// management with manual partition assignment through
		// assign(Collection).
		//consumer.subscribe(Arrays.asList(KafkaUtil.TOPIC_NAME));// 打开注释错误，方法的说明说的非常清楚，subscribe不可以和assign一起使用
		TopicPartition tp = new TopicPartition(KafkaUtil.TOPIC_NAME, 0);

		consumer.assign(Arrays.asList(tp));
		consumer.seek(tp, 4141);

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);

			for (ConsumerRecord<String, String> record : records)

			{
				System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());

				Thread.sleep(1000);
			}
		}

	}
}
