package com.sgc.common.util;

import com.alibaba.fastjson.JSONObject;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Pair;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubTooManyRequestsException;
import com.microsoft.azure.sdk.iot.service.jobs.JobClient;
import com.microsoft.azure.sdk.iot.service.jobs.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * 作业工具类
 *
 * @author Charlie
 * @date 2018/12/17
 */
public class AzureJobUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(AzureJobUtil.class);

	private static Long responseTimeoutInSeconds = 20L;
	private static Long connectTimeoutInSeconds = 20L;
	/**
	 * 作业延迟时间，单位s
	 */
	private static Integer delayTime = 10;

	/**
	 * @param iotHubConnectStr
	 *            iotHub连接字符串
	 * @param jobUUID
	 *            job的UUID
	 * @param deviceIds
	 *            要操作的设备id
	 * @param payloadMap
	 *            参数map
	 * @param startTime
	 *            开始时间
	 * @param overtime
	 *            超时时间
	 * @return com.microsoft.azure.sdk.iot.service.jobs.JobResult
	 * @author Charlie
	 * @date 2018/12/17 17:39
	 */
	public static JSONObject scheduleUpdateTwin(String iotHubConnectStr, String jobUUID, List<String> deviceIds,
			Map<String, Object> payloadMap, Date startTime, long overtime) {
		JobResult jobResult = null;
		JobClient jobClient = null;
		JSONObject result = new JSONObject();
		String message = "success";
		try {
			jobClient = JobClient.createFromConnectionString(iotHubConnectStr);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("初始化JobClient失败，connectionString为 " + iotHubConnectStr);
			message = "连接iot hub失败";
			result.put("message", message);
			return result;
		}

		String deviceStr = "[";
		for (String deviceId : deviceIds) {
			deviceStr += "'" + deviceId + "',";
		}
		deviceStr = deviceStr.substring(0, deviceStr.length() - 1);
		deviceStr += "]";
		// 设置其中一个imei
		DeviceTwinDevice deviceTwinDevice = new DeviceTwinDevice(deviceIds.get(0));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startTime);
		calendar.add(Calendar.SECOND, AzureJobUtil.delayTime);
		startTime = calendar.getTime();

		// 设置更新的数据
		Set<Pair> desiredProperties = new HashSet<Pair>();
		Set<Map.Entry<String, Object>> entrySet = payloadMap.entrySet();
		Iterator<Map.Entry<String, Object>> it = entrySet.iterator();
		Map.Entry<String, Object> entry = null;
		while (it.hasNext()) {
			entry = it.next();
			desiredProperties.add(new Pair(entry.getKey(), entry.getValue()));
		}
		deviceTwinDevice.setDesiredProperties(desiredProperties);
		deviceTwinDevice.setETag("*");

		LOGGER.info("Schedule job " + jobUUID + " for device " + deviceStr);
		try {
			jobResult = jobClient.scheduleUpdateTwin(jobUUID, "DeviceId IN " + deviceStr, deviceTwinDevice, startTime,
					overtime * 60);
		} catch (IotHubTooManyRequestsException e) {
			e.printStackTrace();
			message = "当前可安排作业已达上限";
		} catch (Exception e) {
			LOGGER.error("Exception scheduling desired properties job: " + jobUUID);
			e.printStackTrace();
			message = e.getMessage();
		}

		if (jobResult != null) {
			result.put("result", jobResult);
		}
		result.put("message", message);
		return result;
	}

	/**
	 * @param iotHubConnectStr
	 *            iotHub连接字符串
	 * @param jobUUID
	 *            job的UUID
	 * @param deviceIds
	 *            要操作的设备id
	 * @param methodName
	 *            方法名
	 * @param payload
	 *            参数map
	 * @param startTime
	 *            开始时间
	 * @param overtime
	 *            超时时间
	 * @return com.microsoft.azure.sdk.iot.service.jobs.JobResult
	 * @author Charlie
	 * @date 2018/12/18 9:46
	 */
	public static JSONObject scheduleDeviceMethod(String iotHubConnectStr, String jobUUID, List<String> deviceIds,
			String methodName, Map<String, Object> payload, Date startTime, long overtime) {
		JobResult jobResult = null;
		JobClient jobClient = null;
		JSONObject result = new JSONObject();
		String message = "success";
		try {
			jobClient = JobClient.createFromConnectionString(iotHubConnectStr);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("初始化JobClient失败，connectionString为 " + iotHubConnectStr);
			message = "连接iot hub失败";
			result.put("message", message);
			return result;
		}

		String deviceStr = "[";
		for (String deviceId : deviceIds) {
			deviceStr += "'" + deviceId + "',";
		}
		deviceStr = deviceStr.substring(0, deviceStr.length() - 1);
		deviceStr += "]";
		System.out.println("Schedule job " + jobUUID + " for device " + deviceStr);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startTime);
		calendar.add(Calendar.SECOND, AzureJobUtil.delayTime);
		startTime = calendar.getTime();

		try {
			jobResult = jobClient.scheduleDeviceMethod(jobUUID, "DeviceId IN " + deviceStr, methodName,
					AzureJobUtil.responseTimeoutInSeconds, AzureJobUtil.connectTimeoutInSeconds, payload, startTime,
					overtime * 60);
		} catch (IotHubTooManyRequestsException e) {
			e.printStackTrace();
			message = "当前可安排作业已达上限";
			result.put("message", message);
			return result;
		} catch (Exception e) {
			System.out.println("Exception scheduling direct method job: " + jobUUID);
			e.printStackTrace();
			message = e.getMessage();
			result.put("message", message);
			return result;
		}

		if (jobResult != null) {
			result.put("result", jobResult);
		}
		result.put("message", message);
		return result;
	}

	/*
	 * public static void main(String[] args) { System.out.println("开始执行。。。");
	 * String connectionStr =
	 * "HostName=iothub-aosmith-test.azure-devices.cn;SharedAccessKeyName=iothubowner;SharedAccessKey=+b6Es/a0X7pZXeG5dde1Dnocfpg4u8os+s1oCTc20sc=";
	 * String jobUUID = UUIDUtils.random().toString(); List<String> deviceIds = new
	 * ArrayList<>(); deviceIds.add("AABBCC"); deviceIds.add("DDEEFF"); String
	 * methodName = "Reboot"; Date startTime = new Date(); JobResult result=
	 * JobUtils.scheduleDeviceMethod(connectionStr,jobUUID, deviceIds, methodName,
	 * null, startTime, 5L); System.out.println("作业执行完毕");
	 * System.out.println(result.toString()); }
	 */

	/*
	 * public static void main(String[] args) { String iotHubConnectStr =
	 * "HostName=iothub-aosmith-test.azure-devices.cn;SharedAccessKeyName=iothubowner;SharedAccessKey=+b6Es/a0X7pZXeG5dde1Dnocfpg4u8os+s1oCTc20sc=";
	 * String jobUUID = UUID.randomUUID().toString(); JobResult jobResult = null;
	 * JobClient jobClient = null; JSONObject result = new JSONObject();
	 * System.out.println(jobUUID); Map payloadMap = new HashMap<>();
	 * payloadMap.put("interval", 10);
	 * 
	 * long localTimeInMillis = System.currentTimeMillis(); Calendar calendar=
	 * Calendar.getInstance();
	 * 
	 * calendar.setTimeInMillis(localTimeInMillis);
	 *//** 取得时间偏移量 */
	/*
	 * int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
	 *//** 取得夏令时差 */
	/*
	 * int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
	 *//** 从本地时间里扣除这些差量，即可以取得UTC时间 */
	/*
	 * calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
	 *//** 取得的时间就是UTC标准时间 *//*
							 * Date utcDate=new Date(calendar.getTimeInMillis());
							 * System.out.println(utcDate);
							 * 
							 * 
							 * Date startTime = utcDate; long overtime = 30 * 1000; String[] deviceIds =
							 * {"865933034694212","865933034684304"}; String message = "success"; try {
							 * jobClient = JobClient.createFromConnectionString(iotHubConnectStr); } catch
							 * (IOException e) { e.printStackTrace();
							 * System.out.println("初始化JobClient失败，connectionString为 " + iotHubConnectStr);
							 * message = "连接iot hub失败"; result.put("message", message); return; }
							 * 
							 * String deviceStr = "[";
							 * 
							 * for (String deviceId : deviceIds) { deviceStr += "'" + deviceId + "',"; }
							 * deviceStr = deviceStr.substring(0, deviceStr.length() - 1); deviceStr += "]";
							 * DeviceTwinDevice deviceTwinDevice = new DeviceTwinDevice(deviceIds[0]);
							 * 
							 * // 设置更新的数据 Set<Pair> desiredProperties = new HashSet<Pair>();
							 * 
							 * Set<Map.Entry<String, Object>> entrySet = payloadMap.entrySet();
							 * Iterator<Map.Entry<String, Object>> it = entrySet.iterator();
							 * Map.Entry<String, Object> entry = null; while (it.hasNext()) { entry =
							 * it.next(); desiredProperties.add(new Pair(entry.getKey(), entry.getValue()));
							 * } deviceTwinDevice.setDesiredProperties(desiredProperties);
							 * deviceTwinDevice.setETag("*"); System.out.println("Schedule job " + jobUUID +
							 * " for device " + deviceStr); try { jobResult =
							 * jobClient.scheduleUpdateTwin(jobUUID, "DeviceId IN " + deviceStr,
							 * deviceTwinDevice, startTime, overtime); } catch (Exception e) {
							 * System.out.println("Exception scheduling desired properties job: " +
							 * jobUUID); e.printStackTrace(); message = e.getMessage(); }
							 * 
							 * if(jobResult != null){ result.put("result", jobResult); }
							 * result.put("message", message); }
							 */
}
