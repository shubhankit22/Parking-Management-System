package com.demo.parkinglot.enums;

import com.demo.parkinglot.strategy.SlotAllocationStrategy;
import com.demo.parkinglot.strategy.NearestSlotStrategy;
import com.demo.parkinglot.strategy.FirstAvailableStrategy;
import com.demo.parkinglot.strategy.LevelWiseStrategy;

/**
 * Enum for slot allocation strategy types with factory method
 */
public enum AllocationStrategyType {
    
    NEAREST_SLOT("Nearest Slot", "Assigns the closest available slot to entry gate") {
        @Override
        public SlotAllocationStrategy createStrategy() {
            return new NearestSlotStrategy();
        }
    },
    
    FIRST_AVAILABLE("First Available", "Assigns the first available slot in the list") {
        @Override
        public SlotAllocationStrategy createStrategy() {
            return new FirstAvailableStrategy();
        }
    },
    
    LEVEL_WISE("Level Wise", "Prioritizes lower floors first, then by slot ID") {
        @Override
        public SlotAllocationStrategy createStrategy() {
            return new LevelWiseStrategy();
        }
    };
    
    private final String displayName;
    private final String description;
    
    AllocationStrategyType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Factory method to create strategy instance
     * @return Concrete strategy implementation
     */
    public abstract SlotAllocationStrategy createStrategy();
    
    /**
     * Get display name for UI
     * @return Human-readable name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get strategy description
     * @return Strategy description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get strategy type from string value
     * @param strategyName String representation of strategy
     * @return AllocationStrategyType enum
     * @throws IllegalArgumentException if strategy name is invalid
     */
    public static AllocationStrategyType fromString(String strategyName) {
        if (strategyName == null || strategyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Strategy name cannot be null or empty");
        }
        
        try {
            return AllocationStrategyType.valueOf(strategyName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            String validStrategies = getAllStrategyNames();
            throw new IllegalArgumentException("Invalid allocation strategy: " + strategyName + 
                ". Valid strategies are: " + validStrategies);
        }
    }
    
    /**
     * Get all available strategy names
     * @return Comma-separated list of valid strategy names
     */
    public static String getAllStrategyNames() {
        StringBuilder sb = new StringBuilder();
        for (AllocationStrategyType type : AllocationStrategyType.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(type.name());
        }
        return sb.toString();
    }
    
    /**
     * Get all strategy types as array
     * @return Array of all strategy types
     */
    public static AllocationStrategyType[] getAllStrategies() {
        return AllocationStrategyType.values();
    }
    
    /**
     * Check if strategy is valid
     * @param strategyName Strategy name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidStrategy(String strategyName) {
        try {
            fromString(strategyName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
