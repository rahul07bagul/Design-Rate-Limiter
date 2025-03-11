package com.example.ratelimiter.parser;

import com.example.ratelimiter.model.ApiRule;
import com.example.ratelimiter.model.Algorithm;
import com.example.ratelimiter.model.Rule;
import com.example.ratelimiter.exception.RuleParsingException;

import org.apache.poi.hwpf.*;
import org.apache.poi.hwpf.extractor.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DocumentRuleParser {
    private static final Pattern RULE_PATTERN = Pattern.compile(
            "API:\\s*([^\\n]+)\\s*" +
                    "METHOD:\\s*([^\\n]+)\\s*" +
                    "ALGORITHM:\\s*([^\\n]+)\\s*" +
                    "LIMIT:\\s*(\\d+)\\s*" +
                    "PERIOD:\\s*(\\d+)\\s*" +
                    "TIME_UNIT:\\s*([^\\n]+)");

    public List<Rule> parseRulesFromDoc(File docFile) throws RuleParsingException {
        try (FileInputStream fis = new FileInputStream(docFile)) {
            return parseRulesFromInputStream(fis);
        } catch (IOException e) {
            throw new RuleParsingException("Failed to read rules file");
        }
    }

    public List<Rule> parseRulesFromInputStream(InputStream inputStream) throws RuleParsingException {
        try {
            // Create XWPFDocument to extract content from the .docx file
            XWPFDocument extractor = new XWPFDocument(inputStream);

            // Extract text from paragraphs
            List<XWPFParagraph> paragraphs = extractor.getParagraphs();
            StringBuilder text = new StringBuilder();

            // Loop through each paragraph and append text to the StringBuilder
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }

            // Now you have the full text from the document
            return parseRulesFromText(text.toString());
        } catch (IOException e) {
            throw new RuleParsingException("Failed to parse rules from document");
        }
    }

    public List<Rule> parseRulesFromText(String text) throws RuleParsingException {
        List<Rule> rules = new ArrayList<>();
        Matcher matcher = RULE_PATTERN.matcher(text);

        int id = 1;
        while (matcher.find()) {
            String apiPath = matcher.group(1).trim();
            String method = matcher.group(2).trim();
            String algorithmStr = matcher.group(3).trim();
            long limit = Long.parseLong(matcher.group(4).trim());
            long period = Long.parseLong(matcher.group(5).trim());
            String timeUnitStr = matcher.group(6).trim();

            // Parse algorithm
            Algorithm algorithm;
            try {
                algorithm = Algorithm.valueOf(algorithmStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuleParsingException("Invalid algorithm: " + algorithmStr);
            }

            // Parse time unit
            ChronoUnit timeUnit;
            try {
                timeUnit = ChronoUnit.valueOf(timeUnitStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuleParsingException("Invalid time unit: " + timeUnitStr);
            }

            // Create the rule
            String ruleId = "rule-" + id++;
            String resourceId = apiPath + ":" + method;

            ApiRule rule = new ApiRule(
                    ruleId,
                    resourceId,
                    algorithm,
                    limit,
                    period,
                    timeUnit,
                    apiPath,
                    method
            );

            rules.add(rule);
        }

        return rules;
    }
}
