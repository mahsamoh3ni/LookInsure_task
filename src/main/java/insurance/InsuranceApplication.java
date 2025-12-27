package insurance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.TimeZone;


@SpringBootApplication
@Slf4j
@EnableCaching
public class InsuranceApplication {

    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+03:30"));

        SpringApplication.run(InsuranceApplication.class, args);
    }
}
