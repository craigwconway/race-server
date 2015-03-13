package com.bibsmobile.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.StringInputStream;

public class S3Util {

    private static AWSCredentials awsS3Credentials;
    private static String awsS3AccessKey="AKIAJGFZDMCYWJZZAL2Q";
    private static String awsS3SecretKey="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";
    private static String awsS3Bucket="bibstest";
    private static String awsS3UrlPrefix="http://bibstest.s3.amazonaws.com/";
    private static String awsS3WaiverBucket="galen-shennanigans";
    private static String awsS3WaiverPrefix="http://galen-shennanigans.s3.amazonaws.com/";
    
    public String getAwsS3UrlPrefix(){
    	return awsS3UrlPrefix;
    }
    
    public static String getAwsS3WaiverPrefix(){
    	return awsS3WaiverPrefix;
    }
    
    @PostConstruct
    public void init() {
        S3Util.awsS3Credentials = new BasicAWSCredentials(this.awsS3AccessKey, this.awsS3SecretKey);
    }
    
    public PutObjectResult uploadFileToS3(MultipartFile file, String fileName) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(S3Util.awsS3Credentials);
        ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3Bucket, fileName, stream, new ObjectMetadata());
        return s3Client.putObject(putObjectRequest);
    }
    
    public static PutObjectResult uploadWaiverToS3(String str, String fileName) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey));
        InputStream stream = new StringInputStream(str);
        System.out.println("uploading to: " + awsS3WaiverBucket);
        PutObjectRequest putObjectRequest = new PutObjectRequest(awsS3WaiverBucket, fileName, stream, new ObjectMetadata());
        return s3Client.putObject(putObjectRequest);
    }
    
    public PutObjectResult uploadToS3(String str, String fileName) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(S3Util.awsS3Credentials);
        InputStream stream = new StringInputStream(str);
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3Bucket, fileName, stream, new ObjectMetadata());
        return s3Client.putObject(putObjectRequest);
    }

    public void deleteFileFromS3(String fileName) {
        AmazonS3 s3Client = new AmazonS3Client(S3Util.awsS3Credentials);
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.awsS3Bucket, fileName);
        s3Client.deleteObject(deleteObjectRequest);
    }
    
    public String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }
}
