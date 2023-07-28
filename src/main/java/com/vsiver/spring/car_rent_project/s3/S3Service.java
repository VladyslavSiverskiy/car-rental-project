package com.vsiver.spring.car_rent_project.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3;

    public S3Service(@Autowired S3Client s3) {
        this.s3 = s3;
    }

    public void putObject(String bucketName, String key, byte[] file){
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.putObject(objectRequest, RequestBody .fromBytes(file));
    }

    public byte[] downloadObject(String bucketName, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        var res = s3.getObject(getObjectRequest);
        return res.readAllBytes(); //throws exception
    }


    public void deleteObject(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.deleteObject(deleteObjectRequest);
    }

    public void deleteFolder(String bucketName, String folderPath) {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderPath)
                .build();

        ListObjectsV2Response listObjectsResponse = s3.listObjectsV2(listObjectsRequest);

        for (S3Object s3Object : listObjectsResponse.contents()) {
            deleteObject(bucketName, s3Object.key());
        }
    }
}
