package nserve.delivery_application_backend;

import com.cloudinary.Cloudinary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DeliveryApplicationBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(DeliveryApplicationBackendApplication.class, args);
	}

}
