package com.oplus.content;

public class OplusFeatureConfigManager {
    
    private static final OplusFeatureConfigManager INSTANCE = new OplusFeatureConfigManager();

    public OplusFeatureConfigManager() {
    }

    public static OplusFeatureConfigManager getInstance() {
        return INSTANCE;
    }

    public boolean hasFeature(String featureName) {
        // Luôn trả về true để bypass kiểm tra phần cứng của app
        return true;
    }
}
