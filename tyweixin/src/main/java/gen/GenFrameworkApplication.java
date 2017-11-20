package gen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy
public class GenFrameworkApplication {

	public static void main(String[] args) {



        SpringApplication.run(new Object[]{GenFrameworkApplication.class}, args);
		
		
	}
}
