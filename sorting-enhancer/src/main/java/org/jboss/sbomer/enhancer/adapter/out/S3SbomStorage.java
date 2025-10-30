package org.jboss.sbomer.enhancer.adapter.out;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.enhancer.core.port.spi.SbomStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class S3SbomStorage implements SbomStorage {

    @Override
    public List<String> download(List<String> urls) {
        log.info("Simulating download of {} SBOM(s) from storage...", urls.size());
        // In a real implementation, you would use an S3 client to fetch content.
        // For the demo, we just return dummy content.
        return urls.stream()
                .map(url -> "{\"dummyContentFor\": \"" + url + "\", \"components\": [{\"name\": \"Z-component\"}, {\"name\": \"A-component\"}]}")
                .collect(Collectors.toList());
    }

    @Override
    public List<String> upload(List<String> sbomContents) {
        log.info("Simulating upload of {} new SBOM(s) to storage...", sbomContents.size());
        List<String> newUrls = new ArrayList<>();
        // In a real implementation, you would use an S3 client to upload.
        // For the demo, we generate fake S3 URLs.
        for (int i = 0; i < sbomContents.size(); i++) {
            String newUrl = String.format("s3://your-bucket/enhanced-sboms/%s.json", UUID.randomUUID());
            newUrls.add(newUrl);
            log.debug("Uploaded content to {}", newUrl);
        }
        return newUrls;
    }
}
