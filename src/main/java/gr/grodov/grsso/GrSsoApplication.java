package gr.grodov.grsso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GrSsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrSsoApplication.class, args);
    }

}
