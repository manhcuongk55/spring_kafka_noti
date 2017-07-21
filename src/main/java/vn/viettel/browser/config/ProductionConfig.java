package vn.viettel.browser.config;

/**
 * Created by giang on 21/07/2017.
 */
public class ProductionConfig {
    public static final int REDIS_PORT = 3001;
    public static final String[] REDIS_HOST_STAGING = {"10.240.152.146", "10.240.152.147", "10.240.152.148", "10.240.152.149",
            "10.240.152.150", "10.240.152.151"};
    public static final String[] REDIS_HOST_PRODUCTION = {"10.240.152.63", "10.240.152.64", "10.240.152.65",
            "10.240.152.66", "10.240.152.67", "10.240.152.68"};
    public static final String[] ES_HOST_PRODUCTION = {"10.240.152.69", "10.240.152.70", "10.240.152.71", "10.240.152.72",
            "10.240.152.73", "10.240.152.74", "10.240.152.75", "10.240.152.76", "10.240.152.77", "10.240.152.78"};
    public static final String[] ES_HOST_STAGING = {"10.240.152.146", "10.240.152.147", "10.240.152.148", "10.240.152.149",
            "10.240.152.150", "10.240.152.151"};
    public static boolean PRODUCTION_ENV = false;


}