package com.daxin.kafka010;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Date;

public class ProducerTopic {

    public static void main(String[] args) throws Exception{

        Producer<String, String> producer = KafkaUtil.getKafkaProducer();
        for(int i=0;i<50000;i++ ){
            //构建消息
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(KafkaUtil.TOPIC_NAME, String.valueOf(i), "this is message "+ new Date().getTime());
            producer.send(record, new Callback() {
                //消息发送成功后回调
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
//                    System.out.println("message send to partition " + metadata.partition() + ", offset: " + metadata.offset());
                }
            });
            Thread.sleep(100);
        }
    }
}
