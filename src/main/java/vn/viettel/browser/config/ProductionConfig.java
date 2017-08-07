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
    public static boolean PRODUCTION_ENV = true;
    //public static final String[] TestFireBase = {
	//		"eseq1-O9WaU:APA91bENg9ifxedNRmuFcVZJCTubIfr0l14ACza51LX9u7UBOgYu1KkNMKze4axzOsHTq6rBpI7s3VJ9r7Z5wXhclOIViME835G3JPZLcswQ7nyKiHNF_xm-8nIFwI_6L7xlfkjbfX3V"};

    public static final String[] TestFireBase = {
            "f29x_cRGuVo:APA91bGobltDdb9_NNzz4gG8nO-gW26lCTaY-8eK6-8MhgQRjP34KUXZHaHnRhkw7Jwb6vR4UZ--iF8OzOpE-RJ4qREZuU34ZjvhZEFS4ax1F4E6mhDPnblrEftoX6hf4m5-7F0v89_R"
    };

}
