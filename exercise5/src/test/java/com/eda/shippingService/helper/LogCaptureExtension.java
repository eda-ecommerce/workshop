package com.eda.shippingService.helper;

import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.extension.*;
import org.slf4j.LoggerFactory;

public class LogCaptureExtension implements ParameterResolver, AfterTestExecutionCallback, BeforeEachCallback {

    private Logger logger;
    private LogCapture logCapture;
    private String loggerName;

    public LogCaptureExtension(String loggerName) {
        this.loggerName = loggerName;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == LogCapture.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        logCapture = new LogCapture();
        setup();
        return logCapture;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        teardown();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        logger = (Logger) LoggerFactory.getLogger(loggerName);
    }

    private void setup() {
        logger.addAppender(logCapture.getListAppender());
        logCapture.start();
    }

    private void teardown() {
        if (logCapture == null || logger == null) {
            return;
        }
        logger.detachAndStopAllAppenders();
        logCapture.stop();
    }
}