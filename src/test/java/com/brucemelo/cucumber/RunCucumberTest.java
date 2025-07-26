package com.brucemelo.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.SNIPPET_TYPE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.brucemelo"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME, 
        value = "pretty," +
                "html:build/cucumber-reports/report.html," +
                "com.brucemelo.cucumber.NonTechnicalHtmlFormatter:build/cucumber-reports/non-technical-report.html"
)
@ConfigurationParameter(
        key = SNIPPET_TYPE_PROPERTY_NAME,
        value = "camelcase"
)
public class RunCucumberTest {
}
