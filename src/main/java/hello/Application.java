package hello;

import com.lightstep.tracer.shared.Options;
import io.opentracing.noop.NoopTracerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class Application {

	final org.slf4j.Logger log = LoggerFactory.getLogger(Application.class);

	@Bean
	public io.opentracing.Tracer lightstepTracer() {
		log.info("Configuring Lightstep tracer.");
		Map<String, String> env = System.getenv();

		try {
			String accessToken = env.get("LIGHTSTEP_ACCESS_TOKEN");
			String satelliteHost = env.get("LIGHTSTEP_HOST");
			Integer satellitePort = Integer.parseInt(env.get("LIGHTSTEP_PORT"));
			String satelliteProtocol = env.get("LIGHTSTEP_PROTOCOL");

			Options opts = new Options.OptionsBuilder()
					.withComponentName("spring-app")
					.withAccessToken(accessToken)
					.withCollectorHost(satelliteHost)
					.withCollectorPort(satellitePort)
					.withCollectorProtocol(satelliteProtocol)
					.withVerbosity(4)
					.build();

			return new com.lightstep.tracer.jre.JRETracer(opts);
		} catch (Exception e) {
			log.error("Couldn't create tracer, likely variable missing in environment or init failed.");
			io.opentracing.Tracer noopTracer = NoopTracerFactory.create();
			return noopTracer;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
