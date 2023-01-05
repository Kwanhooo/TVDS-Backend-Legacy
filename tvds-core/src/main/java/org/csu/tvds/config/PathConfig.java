package org.csu.tvds.config;

public interface PathConfig {
    boolean DEV_MODE = true;
    String BASE = System.getProperty("user.dir") + "/blob/";
    String AI_BASE = System.getProperty("user.dir") + "/ai/";
    String UPLOAD_BASE = BASE + "origin/";
    String ALIGNED_BASE = BASE + "aligned/";
    String PARTS_BASE = BASE + "parts/";

    String URL_BASE = "http://127.0.0.1:8080/blob/";

    // TODO: MODEL_PATH 需要迁移
}
