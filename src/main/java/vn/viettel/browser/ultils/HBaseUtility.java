package vn.viettel.browser.ultils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vn.viettel.browser.global.Variables;

public class HBaseUtility {

	private byte[] genRowKeyFromUserIdAndDeviceId(long userId, long deviceId) {
		byte[] userIdBytes = Bytes.toBytes(userId);
		byte[] deviceIdBytes = Bytes.toBytes(deviceId);
		ByteBuffer bb = ByteBuffer.allocate(userIdBytes.length + deviceIdBytes.length);
		bb.put(userIdBytes);
		bb.put(deviceIdBytes);
		return bb.array();
	}

	private byte[] genRowKeyFromUserIdAndDeviceIdAndTimeStamp(long userId, long deviceId, long timeStamp) {
		byte[] userIdBytes = Bytes.toBytes(userId);
		byte[] deviceIdBytes = Bytes.toBytes(deviceId);
		byte[] timeStampBytes = Bytes.toBytes(timeStamp);
		ByteBuffer bb = ByteBuffer.allocate(userIdBytes.length + timeStampBytes.length + deviceIdBytes.length);
		bb.put(userIdBytes);
		bb.put(deviceIdBytes);
		bb.put(timeStampBytes);
		return bb.array();
	}

	private byte[] genRowKeyFromUserIdAndDeviceIdAndTimeStampAndUrl(long userId, long deviceId, long timeStamp,
			String md5Url) {
		byte[] userIdBytes = Bytes.toBytes(userId);
		byte[] timeStampBytes = Bytes.toBytes(timeStamp);
		byte[] md5UrlBytes = Bytes.toBytes(md5Url);
		byte[] deviceIdBytes = Bytes.toBytes(deviceId);
		ByteBuffer bb = ByteBuffer
				.allocate(userIdBytes.length + timeStampBytes.length + md5UrlBytes.length + deviceIdBytes.length);
		bb.put(userIdBytes);
		bb.put(deviceIdBytes);
		bb.put(timeStampBytes);
		bb.put(md5UrlBytes);
		return bb.array();
	}

	private byte[] genRowKeyFromUserIdAndTimeStampAndUrl(long userId, long timeStamp, String md5Url) {
		byte[] userIdBytes = Bytes.toBytes(userId);
		byte[] md5UrlBytes = Bytes.toBytes(md5Url);
		byte[] timeStampBytes = Bytes.toBytes(timeStamp);
		ByteBuffer bb = ByteBuffer.allocate(userIdBytes.length + timeStampBytes.length + md5UrlBytes.length);
		bb.put(md5UrlBytes);
		bb.put(userIdBytes);
		bb.put(timeStampBytes);
		return bb.array();
	}

	public void pushHistory(JSONArray history, long idUser, long deviceId) throws IOException, InterruptedException {
		List<Row> batch = new ArrayList<>();
		for (Object aHistory : history) {
			JSONObject data = (JSONObject) aHistory;
			Long timestamp = Long.valueOf(data.get("time").toString());
			String url = data.get("des").toString();
			MD5 md5 = new MD5();
			String md5Url = md5.getMD5(url);
			byte[] rowKey = genRowKeyFromUserIdAndDeviceIdAndTimeStampAndUrl(idUser, deviceId, timestamp, md5Url);
			Put p = new Put(rowKey);
			p.addColumn(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("history"), Bytes.toBytes(data.toString()));
			byte[] rowKeyUserId = genRowKeyFromUserIdAndTimeStampAndUrl(idUser, timestamp, md5Url);
			Put pUserId = new Put(rowKeyUserId);
			pUserId.addColumn(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("history"),
					Bytes.toBytes(data.toString()));
			batch.add(p);
			batch.add(pUserId);
		}
		Object[] results = new Object[batch.size()];
		Variables.table.batch(batch, results);
	}

	public void pushOtherStatus(String speedDial, String statusNews, String bookmark, long userId, long deviceId,
			ArrayList<String> selections) throws IOException, InterruptedException {
		List<Row> batch = new ArrayList<>();
		byte[] rowKey = genRowKeyFromUserIdAndDeviceId(userId, deviceId);
		Put p = new Put(rowKey);
		if (selections.contains("speed_dial"))
			p.addColumn(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("speedDial"), Bytes.toBytes(speedDial));
		if (selections.contains("status_news"))
			p.addColumn(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("statusNews"), Bytes.toBytes(statusNews));
		if (selections.contains("bookmark"))
			p.addColumn(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("bookmark"), Bytes.toBytes(bookmark));
		batch.add(p);
		Object[] results = new Object[batch.size()];
		Variables.table.batch(batch, results);
	}

	public JSONObject getHistoryFromHBase(long idUser, long deviceId, long timeEnd, long duration)
			throws IOException, ParseException {
		JSONObject response = new JSONObject();
		JSONArray historyList = new JSONArray();
		JSONParser parser = new JSONParser();
		Long timeStart = timeEnd - duration;
		timeEnd++;
		byte[] rowStart = genRowKeyFromUserIdAndDeviceIdAndTimeStamp(idUser, deviceId, timeStart);
		byte[] rowEnd = genRowKeyFromUserIdAndDeviceIdAndTimeStamp(idUser, deviceId, timeEnd);
		Scan scan = new Scan(rowStart, rowEnd);
		ResultScanner scanner = Variables.table.getScanner(scan);
		for (Result result : scanner) {
			String hBaseData = new String(
					result.getValue(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("history")));
			JSONObject jsonData = (JSONObject) parser.parse(hBaseData);
			historyList.add(jsonData);
		}
		response.put("list", historyList);
		return response;
	}

	public JSONObject getOtherStatus(long userId, long deviceId, ArrayList<String> selections) {
		JSONObject dataResponse = new JSONObject();
		String speedDial = "";
		String statusNews = "";
		String bookmark = "";
		byte[] rowKey = genRowKeyFromUserIdAndDeviceId(userId, deviceId);
		Scan scan = new Scan(rowKey, rowKey);
		try {
			ResultScanner scanner = Variables.table.getScanner(scan);
			for (Result result : scanner) {
				if (selections.contains("speed_dial"))
					speedDial = new String(
							result.getValue(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("speedDial")));
				if (selections.contains("status_news"))
					statusNews = new String(
							result.getValue(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("statusNews")));
				if (selections.contains("bookmark"))
					bookmark = new String(
							result.getValue(Bytes.toBytes(Variables.nameFamily), Bytes.toBytes("bookmark")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataResponse.put("speed_dial", speedDial);
		dataResponse.put("status_news", statusNews);
		dataResponse.put("bookmark", bookmark);
		return dataResponse;
	}
}
