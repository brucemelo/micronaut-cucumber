package com.brucemelo.cucumber;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom formatter that generates a non-technical HTML report focused on features.
 * Enhanced with more information and using Java's modern multiline strings.
 */
public class NonTechnicalHtmlFormatter implements EventListener {
    private final File outputFile;
    private final List<Feature> features = new ArrayList<>();
    private final Map<String, String> featureDescriptions = new HashMap<>();
    private final Map<String, String> featureNames = new HashMap<>();
    private Feature currentFeature;
    private Rule currentRule;
    private Scenario currentScenario;
    private final List<Step> currentSteps = new ArrayList<>();
    private final LocalDateTime reportTime = LocalDateTime.now();

    public NonTechnicalHtmlFormatter(File outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestRunStarted.class, this::handleTestRunStarted);
        publisher.registerHandlerFor(TestRunFinished.class, this::handleTestRunFinished);
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::handleTestStepStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        publisher.registerHandlerFor(TestSourceRead.class, this::handleTestSourceRead);
    }

    private void handleTestRunStarted(TestRunStarted event) {
        // Initialize report
    }

    private void handleTestSourceRead(TestSourceRead event) {
        String source = event.getSource();
        String uri = event.getUri().toString();

        // Extract feature name and description from the source
        if (source.contains("Feature:")) {
            String[] lines = source.split("\n");
            StringBuilder description = new StringBuilder();
            boolean inDescription = false;
            String featureName = null;

            for (String line : lines) {
                if (line.trim().startsWith("Feature:")) {
                    // Extract feature name
                    featureName = line.trim().substring("Feature:".length()).trim();
                    inDescription = true;
                    continue;
                }

                if (inDescription) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                        continue;
                    }

                    if (trimmedLine.startsWith("Scenario:") || trimmedLine.startsWith("Rule:") || 
                        trimmedLine.startsWith("Background:") || trimmedLine.startsWith("Scenario Outline:")) {
                        break;
                    }

                    description.append(trimmedLine).append("\n");
                }
            }

            if (featureName != null) {
                featureNames.put(uri, featureName);
            }
            featureDescriptions.put(uri, description.toString().trim());
        }
    }

    private void handleTestRunFinished(TestRunFinished event) {
        try (Writer writer = new FileWriter(outputFile)) {
            generateReport(writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write report", e);
        }
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        TestCase testCase = event.getTestCase();
        String featureUri = testCase.getUri().toString();

        // Use the extracted feature name if available, otherwise fall back to extracting from URI
        String featureName = featureNames.getOrDefault(featureUri, extractFeatureName(featureUri));

        // Get the feature description if available
        String featureDescription = featureDescriptions.getOrDefault(featureUri, "");

        if (currentFeature == null || !currentFeature.uri.equals(featureUri)) {
            currentFeature = new Feature(featureUri, featureName, featureDescription);
            features.add(currentFeature);
        }

        String scenarioName = testCase.getName();
        currentScenario = new Scenario(scenarioName);
        currentFeature.scenarios.add(currentScenario);
        currentSteps.clear();
    }

    private String extractFeatureName(String uri) {
        // Extract the file name from the URI
        String fileName = uri.substring(uri.lastIndexOf('/') + 1);

        // Remove the file extension
        if (fileName.endsWith(".feature")) {
            fileName = fileName.substring(0, fileName.length() - 8);
        }

        // Convert snake_case to Title Case and replace underscores with spaces
        String[] words = fileName.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }

        return result.toString().trim();
    }

    private void handleTestStepStarted(TestStepStarted event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep pickleStep = (PickleStepTestStep) event.getTestStep();
            Step step = new Step(pickleStep.getStep().getKeyword(), pickleStep.getStep().getText());
            currentSteps.add(step);
            currentScenario.steps.add(step);
        }
    }

    private void handleTestStepFinished(TestStepFinished event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep pickleStep = (PickleStepTestStep) event.getTestStep();
            int index = currentSteps.size() - 1;
            if (index >= 0) {
                Status status = event.getResult().getStatus();
                currentSteps.get(index).status = status;

                // Also update the status in the currentScenario.steps list
                if (index < currentScenario.steps.size()) {
                    currentScenario.steps.get(index).status = status;
                }
            }
        }
    }

    private void generateReport(Writer writer) throws IOException {
        // Using Java's modern multiline strings (text blocks) for the HTML template
        String htmlTemplate = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <title>Feature Report</title>
              <style>
                body { 
                    font-family: Arial, sans-serif; 
                    margin: 20px; 
                    color: #333;
                    line-height: 1.6;
                }
                .header {
                    background-color: #f8f9fa;
                    padding: 20px;
                    border-radius: 5px;
                    margin-bottom: 30px;
                    border-left: 5px solid #007bff;
                }
                .report-title {
                    font-size: 28px;
                    color: #007bff;
                    margin: 0;
                }
                .report-time {
                    color: #6c757d;
                    font-size: 14px;
                    margin-top: 5px;
                }
                .feature { 
                    margin-bottom: 40px; 
                    border: 1px solid #e9ecef;
                    border-radius: 5px;
                    overflow: hidden;
                }
                .feature-header {
                    background-color: #e9ecef;
                    padding: 15px;
                    border-bottom: 1px solid #dee2e6;
                }
                .feature-name { 
                    font-size: 24px; 
                    color: #333; 
                    margin: 0;
                    font-weight: bold;
                }
                .feature-description {
                    margin-top: 10px;
                    color: #555;
                    white-space: pre-line;
                }
                .feature-content {
                    padding: 15px;
                }
                .scenario { 
                    margin: 15px 0; 
                    padding: 15px; 
                    background-color: #f9f9f9; 
                    border-radius: 5px; 
                    border-left: 3px solid #6c757d;
                }
                .scenario-name { 
                    font-size: 18px; 
                    color: #444; 
                    margin-bottom: 15px; 
                    font-weight: bold;
                }
                .step { 
                    margin: 8px 0; 
                    padding: 5px 10px;
                    border-radius: 3px;
                }
                .passed { 
                    color: #28a745; 
                    background-color: rgba(40, 167, 69, 0.1);
                }
                .failed { 
                    color: #dc3545; 
                    background-color: rgba(220, 53, 69, 0.1);
                }
                .skipped { 
                    color: #fd7e14; 
                    background-color: rgba(253, 126, 20, 0.1);
                }
                .rule { 
                    margin: 15px 0; 
                    padding: 15px; 
                    background-color: #e9f7fe; 
                    border-radius: 5px; 
                    border-left: 3px solid #0066cc;
                }
                .rule-name { 
                    font-size: 20px; 
                    color: #0066cc; 
                    margin-bottom: 10px; 
                    font-weight: bold;
                }
                .summary {
                    margin-top: 30px;
                    padding: 15px;
                    background-color: #f8f9fa;
                    border-radius: 5px;
                }
                .summary-title {
                    font-size: 20px;
                    color: #333;
                    margin-bottom: 10px;
                }
                .summary-item {
                    margin: 5px 0;
                }
              </style>
            </head>
            <body>
              <div class="header">
                <h1 class="report-title">Feature Report</h1>
                <div class="report-time">Generated on: %s</div>
              </div>
            """.formatted(reportTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        writer.write(htmlTemplate);

        // Generate feature sections
        for (Feature feature : features) {
            writer.write("""
              <div class="feature">
                <div class="feature-header">
                  <h2 class="feature-name">%s</h2>
                  <div class="feature-description">%s</div>
                </div>
                <div class="feature-content">
            """.formatted(feature.displayName, feature.description));

            // Generate scenario sections
            for (Scenario scenario : feature.scenarios) {
                writer.write("""
                  <div class="scenario">
                    <div class="scenario-name">%s</div>
                """.formatted(scenario.name));

                // Generate step sections
                for (Step step : scenario.steps) {
                    String statusClass = getStatusClass(step.status);
                    writer.write("""
                      <div class="step %s">%s %s</div>
                    """.formatted(statusClass, step.keyword, step.text));
                }

                writer.write("  </div>\n");
            }

            writer.write("  </div>\n");
            writer.write("</div>\n");
        }

        // Generate summary section
        int totalScenarios = features.stream().mapToInt(f -> f.scenarios.size()).sum();
        int totalSteps = features.stream().flatMap(f -> f.scenarios.stream()).flatMap(s -> s.steps.stream()).mapToInt(s -> 1).sum();

        writer.write("""
          <div class="summary">
            <h3 class="summary-title">Test Summary</h3>
            <div class="summary-item">Total Features: %d</div>
            <div class="summary-item">Total Scenarios: %d</div>
            <div class="summary-item">Total Steps: %d</div>
          </div>
        """.formatted(features.size(), totalScenarios, totalSteps));

        writer.write("</body>\n");
        writer.write("</html>\n");
    }

    private String getStatusClass(Status status) {
        if (status == null) return "";
        switch (status) {
            case PASSED: return "passed";
            case FAILED: return "failed";
            case SKIPPED: return "skipped";
            default: return "";
        }
    }

    // Data classes
    private static class Feature {
        final String uri;
        final String displayName;
        final String description;
        final List<Scenario> scenarios = new ArrayList<>();
        final List<Rule> rules = new ArrayList<>();

        Feature(String uri, String displayName, String description) {
            this.uri = uri;
            this.displayName = displayName;
            this.description = description;
        }
    }

    private static class Rule {
        final String name;
        final List<Scenario> scenarios = new ArrayList<>();

        Rule(String name) {
            this.name = name;
        }
    }

    private static class Scenario {
        final String name;
        final List<Step> steps = new ArrayList<>();

        Scenario(String name) {
            this.name = name;
        }
    }

    private static class Step {
        final String keyword;
        final String text;
        Status status;

        Step(String keyword, String text) {
            this.keyword = keyword;
            this.text = text;
        }
    }
}
