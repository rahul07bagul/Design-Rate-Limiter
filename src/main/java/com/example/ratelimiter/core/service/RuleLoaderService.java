package com.example.ratelimiter.core.service;

import com.example.ratelimiter.model.Rule;
import com.example.ratelimiter.parser.DocumentRuleParser;
import com.example.ratelimiter.exception.RuleParsingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuleLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(RuleLoaderService.class);

    private final DocumentRuleParser ruleParser;

    @Value("${ratelimiter.rules.path}")
    private String rulesFilePath;

    // In-memory cache of rules
    private volatile Map<String, Rule> rulesCache = new ConcurrentHashMap<>();

    @Autowired
    public RuleLoaderService(DocumentRuleParser ruleParser) {
        this.ruleParser = ruleParser;
    }

    @PostConstruct
    public void init() {
        loadRules();
    }

    /**
     * Schedule rule reloading every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void loadRules() {
        try {
            logger.info("Loading rate limit rules from {}", rulesFilePath);
            File rulesFile = new File(rulesFilePath);

            if (!rulesFile.exists() || !rulesFile.isFile()) {
                logger.error("Rules file not found at {}", rulesFilePath);
                return;
            }

            List<Rule> rules = ruleParser.parseRulesFromDoc(rulesFile);

            // Update the cache
            Map<String, Rule> newCache = new ConcurrentHashMap<>();
            for (Rule rule : rules) {
                newCache.put(rule.getResourceId(), rule);
            }

            rulesCache = newCache;
            logger.info("Successfully loaded {} rate limit rules", rules.size());
        } catch (RuleParsingException e) {
            logger.error("Failed to load rate limit rules", e);
        }
    }

    public Rule getRule(String resourceId) {
        return rulesCache.get(resourceId);
    }

    public List<Rule> getAllRules() {
        return rulesCache.values().stream().toList();
    }
}
