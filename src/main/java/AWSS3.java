import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.sun.xml.internal.ws.util.StreamUtils;
import org.apache.commons.io.IOUtils;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;


public class AWSS3 {

    public static void main(String[] args) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        try {
            S3Object o = s3.getObject("rulecode-config-stage", "vendors.json");
            S3ObjectInputStream s3is = o.getObjectContent();

//            byte[] read_buf = new byte[1024];
//            int read_len = 0;
//            while ((read_len = s3is.read(read_buf)) > 0) {
//                System.out.println(StreamUtils.copyToString(s3isis, StandardCharsets.UTF_8));
//            }

            StringWriter writer = new StringWriter();
            IOUtils.copy(s3is, writer, StandardCharsets.UTF_8);
            System.out.println(writer.toString());
            s3is.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }


    }
}

