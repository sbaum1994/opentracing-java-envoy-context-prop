package hello;

import com.lightstep.tracer.shared.EnvoyPropagator;
import com.lightstep.tracer.shared.Propagator;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.BinaryAdapters;
import io.opentracing.propagation.Format;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

@RestController
public class Controller {

    final org.slf4j.Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Tracer tracer;

    @RequestMapping("/")
    public String home(@RequestHeader Map<String, String> headers) {
        // in theory you'd do this in an interceptor somewhere
        // good examples here that our friends at indeed built
        // https://github.com/opentracing-contrib/java-spring-web/tree/master/opentracing-spring-web/src/main/java/io/opentracing/contrib/spring/web/webfilter

        String incomingCtx = headers.getOrDefault("x-ot-span-context", "");

        Span span;
        if (incomingCtx != "") {
            byte[] bytes = Base64.decodeBase64(incomingCtx);
            ByteBuffer buff = ByteBuffer.wrap(bytes);
            Propagator envoyPropagator = new EnvoyPropagator();
            SpanContext ctx = envoyPropagator.extract(BinaryAdapters.extractionCarrier(buff));
            span = tracer.buildSpan("home").asChildOf(ctx).start();
        } else {
            span = tracer.buildSpan("home").start();
        }

        sleep(); // inject a little time

        span.finish();

        return "Hello Docker World";
    }

    public void sleep() {
        try {
            Thread.sleep(100);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return;
    }

}