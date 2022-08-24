package ucm.yifei.tooltfm.controller;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.stereotype.Component;

@Component
public class AppContainerCustomizer implements EmbeddedServletContainerCustomizer {
    public void customize(ConfigurableEmbeddedServletContainer container) {

        container.setPort(8080);
        container.setContextPath("/tooltfm");

    }
}