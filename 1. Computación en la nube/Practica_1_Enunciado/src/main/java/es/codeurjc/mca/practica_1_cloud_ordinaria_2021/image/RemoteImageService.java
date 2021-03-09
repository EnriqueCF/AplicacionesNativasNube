package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.image;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service("storageService")
@Profile("production")
public class RemoteImageService implements ImageService {

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;
    @Value("${amazon.s3.endpoint}")
    private String endpoint;
    @Value("${amazon.s3.region}")
    private String region;

    private static final AWSCredentials credentials;

    static {
        //put your accesskey and secretkey here
        credentials = new BasicAWSCredentials(
                "AKIA3AJAU3A7RMA3DB77",
                "7CDLxx/9ruYOK0uFbou97B/226Gv8R2xmeLkoFfQ"
        );
    }
    public static AmazonS3 s3;

    public RemoteImageService() {

        s3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    @Override
    public String createImage(MultipartFile multiPartFile) throws IOException {
        String fileName = multiPartFile.getOriginalFilename();
        File file = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multiPartFile.transferTo(file);
        s3.putObject(bucketName, fileName, file);
        return fileName;
    }

    @Override
    public void deleteImage(String image) {
        s3.deleteObject(bucketName, image);
    }
}
