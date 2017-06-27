package vn.viettel.browser.ultils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class FireBaseUtils {
	private static final String FIREBASE_URL = "https://fcm.googleapis.com/fcm/send";
	private static final String FIREBASE_API_KEY = "key=AAAAYdEO07Y:APA91bHVj_2t-wSrNj6E372nSYj4YstqraLfXKrSP0Tt_AJbe2A_zY975VZ9X85c6-7pdjfZcO6XvHMrGJS4ONLV_eO00baSjNgh5uJjC2SBoBZPGhEC7LvufC3jAWvxIXIRFrSjCIsG";
	public static final String NOTIFICATION_CLICK_FUNCTION = "getArticleByNotification";

	public static String pushNotificationToTopic(JSONObject mess) throws Exception {
		URL obj = new URL(FIREBASE_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", FIREBASE_API_KEY);
		con.setRequestProperty("Content-Type", "application/json");
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		System.out.println("@mess.toString() : " + mess.toString());
		byte[] ptext = mess.toString().getBytes("UTF8");
		wr.write(ptext);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + FIREBASE_URL);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
		return response.toString();
	}

	public static String pushNotificationToSingleDevice(JSONObject mess) throws Exception {
		URL obj = new URL(FIREBASE_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", FIREBASE_API_KEY);
		con.setRequestProperty("Content-Type", "application/json");
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		System.out.println("@mess.toString() : " + mess.toString());
		byte[] ptext = mess.toString().getBytes("UTF8");
		wr.write(ptext);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + FIREBASE_URL);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
		return response.toString();
	}

}
