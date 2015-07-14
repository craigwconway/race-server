/**
 * 
 */
package com.bibsmobile.util;

import com.amazonaws.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.AmazonKinesisClient;

/**
 * Used for posting data to Amazon Kinesis
 * @author galen
 *
 */
public class KinesisUtil {
	//AmazonKinesisClient amazonKinesisClient = new AmazonKinesisClient();
	KinesisProducer kinesis = new KinesisProducer();
	
	void test() {
		//kinesis.addUserRecord(stream, partitionKey, data);
	}
}
