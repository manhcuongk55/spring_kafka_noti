package vn.viettel.browser.config;

/**
 * Created by giang on 21/07/2017.
 */
public class ProductionConfig {
	public static final int REDIS_PORT = 3001;
	public static final String[] REDIS_HOST_STAGING = { "10.240.152.146", "10.240.152.147", "10.240.152.148",
			"10.240.152.149", "10.240.152.150", "10.240.152.151" };
	public static final String[] REDIS_HOST_PRODUCTION = { "10.240.152.63", "10.240.152.64", "10.240.152.65",
			"10.240.152.66", "10.240.152.67", "10.240.152.68" };
	public static final String[] ES_HOST_PRODUCTION = { "10.240.152.69", "10.240.152.70", "10.240.152.71",
			"10.240.152.72", "10.240.152.73", "10.240.152.74", "10.240.152.75", "10.240.152.76", "10.240.152.77",
			"10.240.152.78" };
	public static final String[] ES_HOST_STAGING = { "10.240.152.146", "10.240.152.147", "10.240.152.148",
			"10.240.152.149", "10.240.152.150", "10.240.152.151" };
	public static boolean PRODUCTION_ENV = false;
	// public static final String[] TestFireBase = {
	// "eseq1-O9WaU:APA91bENg9ifxedNRmuFcVZJCTubIfr0l14ACza51LX9u7UBOgYu1KkNMKze4axzOsHTq6rBpI7s3VJ9r7Z5wXhclOIViME835G3JPZLcswQ7nyKiHNF_xm-8nIFwI_6L7xlfkjbfX3V"};

	public static final String[] TestFireBase = {
			"fsIkfudPgKs:APA91bGfF5x7UL6xisMcGUFVeBu4x65qGJSNel76IgEnqrcVYwDhdlOszNxqwurQ-sRfaZxXmgwtyvMaAjjE3OpSqhCctoJo6DS6kfa6fdNg74Li-UPKnyN8ylI7FvTbmRrGojSD92of",
			"dDn3SS-yc0k:APA91bFCOIC4cPNs3p00bDKCGb2U9AEarNOC7XffmjKl5lACUEeJ3SYEFA3ULRPACK3mmFoKEcpA9cZ2OrwOxYniYuGEd-bgjRYDYGaCXz78k8ZvpSXcD1sOgTzvCxj3av7GPsjV-il8",
			"fjao5HqVKr0:APA91bHYww0UDlvNvPlR5px1WE2xAALD6T50vN4X_bLRDej7QXcRN5DxmKFrqYhzyLV20U2_XzE18tP2JNmzH8r8b4vnq9uyINpaUBtf93CdjkxUbd4SKtLRCfCyEwMdKr_BTSyrgJrY",
			"fXUjQ3VuxbI:APA91bEWSxkOcGeF6_q-QPHESLRR3TvqOBzpwXBVxJepbksBM_5pZ_2CB3hDwaJt9InRe2XTYKuaBnd5vZ11oSOZzRiBeEt0pHpZIdObU6YbbQVdd6mps6CH12K4pfHjwKJGnClCp0sy",
			"fkmc8tKT35Q:APA91bE6A1_pDkeR0NL7J5Stxlcd6P9PG1mQICmmGGWBW1b8aHndsZS9wNCSieOwKXI7sden08UWDDnX9wmhqggExpCb19koeMJKqbzZR0smVLzcHO5g_l9tOrvnrHYZ1frzBg63CGYE",
			"ei5oI90HWck:APA91bFsZyTV-lEyhkg7reD0-9yawEKal_m1kjEyARuZ8NGTjKTF-C8JNhK-4sUKf1D1Bg-K_o5TZIA3TgPYGGb2wH9sGy7imoWnZgPi9jzIU77S80FeSP7Scnn35MyGFv7_cC2ctPLw",
			"dTmmdYsIZl4:APA91bHf8rlf6UnglIqE6BSkPoyzpXU_T59N5rsG19-11lN7RgsqUmrY_GE_tfqds3bZK33KJiyPpkdi_BDwyNWimKGKhv4WW15unaL5htnc5RSWSp9f7_s3KqX6CdaW_GC1AMPkzkbj" };

}
