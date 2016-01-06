/**
 * 
 */
package com.bibsmobile.util;

import java.nio.ByteBuffer;

import com.amazonaws.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.AmazonKinesisClient;

/**
 * Used for posting data to Amazon Kinesis
 * @author galen
 *
 */
public final class KinesisUtil {
	//AmazonKinesisClient amazonKinesisClient = new AmazonKinesisClient();
	private static KinesisProducer kinesis = new KinesisProducer();
	
	private KinesisUtil() {
		super();
	}
	
	public static void test() {
		//kinesis.addUserRecord(stream, partitionKey, data);
		String sampleData = "{\"value\":\"test\"}";
		ByteBuffer buffer = ByteBuffer.allocate(sampleData.length());
		buffer.put(sampleData.getBytes());
		kinesis.addUserRecord("bibs1", "bibs1", buffer);
	}
}
