package com.demo.parkinglot.config;

import com.demo.parkinglot.enums.AllocationStrategyType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for slot allocation strategies
 */
@Configuration
@ConfigurationProperties(prefix = "parking.allocation")
public class AllocationStrategyConfig {

    private String strategy = "NEAREST_SLOT";
    private boolean enableStrategySwitching = true;
    private boolean logStrategyUsage = false;

    /**
     * Get current strategy type
     */
    public AllocationStrategyType getStrategyType() {
        return AllocationStrategyType.fromString(strategy);
    }

    /**
     * Set strategy type
     */
    public void setStrategyType(AllocationStrategyType strategyType) {
        this.strategy = strategyType.name();
    }

    /**
     * Validate if strategy is valid
     */
    public boolean isValidStrategy() {
        return AllocationStrategyType.isValidStrategy(strategy);
    }

    // Getters and Setters
    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public boolean isEnableStrategySwitching() {
        return enableStrategySwitching;
    }

    public void setEnableStrategySwitching(boolean enableStrategySwitching) {
        this.enableStrategySwitching = enableStrategySwitching;
    }

    public boolean isLogStrategyUsage() {
        return logStrategyUsage;
    }

    public void setLogStrategyUsage(boolean logStrategyUsage) {
        this.logStrategyUsage = logStrategyUsage;
    }
}
