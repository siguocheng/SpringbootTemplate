package com.sgc.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.azure.sdk.iot.service.devicetwin.*;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.jobs.JobResult;
import com.microsoft.azure.sdk.iot.service.jobs.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AzureUtils {
    private static final long WAIT_1_SECOND_TO_CANCEL_IN_MILLISECONDS = 1000L;
    private static final long GIVE_100_MILLISECONDS_TO_IOTHUB = 100L;
    private static final long ADD_10_SECONDS_IN_MILLISECONDS = 10000L;
    private static final long ADD_10_MINUTES_IN_MILLISECONDS = 600000L;
    private static final long MAX_EXECUTION_TIME_IN_SECONDS = 100L;

    private static final Long responseTimeout = TimeUnit.SECONDS.toSeconds(200);
    private static final Long connectTimeout = TimeUnit.SECONDS.toSeconds(5);

    /**
     * Directly invoke method on remote device.
     *
     * @throws Exception Throws Exception if sample fails
     */

    private static MethodResult invokeMethod(DeviceMethod methodClient, String deviceId, String methodName,
                                             Map<String, Object> payload) throws Exception {
        MethodResult result = null;
        try {
            result = methodClient.invoke(deviceId, methodName, responseTimeout, connectTimeout, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 调度调用方法
     *
     * @param methodClient
     * @param deviceId
     * @param methodName
     * @param payload
     * @throws IotHubException
     * @throws IOException
     * @throws InterruptedException
     * @author :
     * @date 创建时间：2018年6月25日
     * @info
     */
    @Autowired(required = false)
    private static void scheduleInvokeMethod(DeviceMethod methodClient, String deviceId, String methodName,
                                             Map<String, Object> payload) throws IotHubException, IOException, InterruptedException {
        // query condition that defines the list of device to invoke
        String queryCondition = "DeviceId IN ['" + deviceId + "']";

        // date when the invoke shall be executed
        Date invokeDateInFuture = new Date(new Date().getTime() + ADD_10_SECONDS_IN_MILLISECONDS); // 10
        // seconds
        // in
        // the
        // future.

        // System.out.println("Schedule invoke method on the Device in 10
        // seconds");
        Job job = methodClient.scheduleDeviceMethod(queryCondition, methodName, responseTimeout, connectTimeout,
                payload, invokeDateInFuture, MAX_EXECUTION_TIME_IN_SECONDS);

        // System.out.println("Wait for job completed...");
        JobResult jobResult = job.get();
        while (jobResult.getJobStatus() != JobStatus.completed) {
            Thread.sleep(GIVE_100_MILLISECONDS_TO_IOTHUB);
            jobResult = job.get();
        }
        // System.out.println("job completed");
    }

    /**
     * 取消调度调用方法
     *
     * @param methodClient
     * @param deviceId
     * @param methodName
     * @param payload
     * @throws IotHubException
     * @throws IOException
     * @throws InterruptedException
     * @author :Jimmy
     * @date 创建时间：2018年6月25日
     * @info
     */
    @Autowired(required = false)
    private static void cancelScheduleInvokeMethod(DeviceMethod methodClient, String deviceId, String methodName,
                                                   Map<String, Object> payload) throws IotHubException, IOException, InterruptedException {
        // query condition that defines the list of device to invoke
        String queryCondition = "DeviceId IN ['" + deviceId + "']";

        // date when the invoke shall be executed
        Date invokeDateInFuture = new Date(new Date().getTime() + ADD_10_MINUTES_IN_MILLISECONDS); // 10
        // minutes
        // in
        // the
        // future.
        // System.out.println("Schedule invoke method on the Device in 10
        // minutes");
        Job job = methodClient.scheduleDeviceMethod(queryCondition, methodName, responseTimeout, connectTimeout,
                payload, invokeDateInFuture, MAX_EXECUTION_TIME_IN_SECONDS);

        Thread.sleep(WAIT_1_SECOND_TO_CANCEL_IN_MILLISECONDS);
        // System.out.println("Cancel job after 1 second");
        job.cancel();

        // System.out.println("Wait for job cancelled...");
        JobResult jobResult = job.get();
        while (jobResult.getJobStatus() != JobStatus.cancelled) {
            Thread.sleep(GIVE_100_MILLISECONDS_TO_IOTHUB);
            jobResult = job.get();
        }
        // System.out.println("job cancelled");
    }

    /**
     * 发送到设备方法
     *
     * @param connectionString
     * @param deviceId
     * @param methodName
     * @param payload
     * @return
     * @throws Exception
     * @author :xuzhen
     * @date 创建时间：2018年6月25日
     * @info
     */
    public static MethodResult sendToDeviceMethod(String connectionString, String deviceId, String methodName,
                                                  Map<String, Object> payload) {
        MethodResult result = null;
        DeviceMethod methodClient;
        try {
            methodClient = DeviceMethod.createFromConnectionString(connectionString);
            result = invokeMethod(methodClient, deviceId, methodName, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void sendToDeviceTwin(String connectionString, String deviceId, Map<String, Object> payload) {
        DeviceTwin deviceTwin = null;
        try {
            deviceTwin = DeviceTwin.createFromConnectionString(connectionString);
            DeviceTwinDevice deviceTwinDevice = new DeviceTwinDevice(deviceId);
            deviceTwin.getTwin(deviceTwinDevice);

            Set<Pair> desiredProperties = deviceTwinDevice.getDesiredProperties();
            Set<Map.Entry<String, Object>> entrySet = payload.entrySet();
            Iterator<Map.Entry<String, Object>> it = entrySet.iterator();
            Map.Entry<String, Object> entry;
            Pair pair;
            while (it.hasNext()) {
                entry = it.next();
                boolean containsKey = false;
                Iterator<Pair> pIt = desiredProperties.iterator();
                while (pIt.hasNext()) {
                    pair = pIt.next();
                    if (entry.getKey().equals(pair.getKey())) {
                        containsKey = true;
                        pair.setValue(entry.getValue());
                        break;
                    }
                }
                if (!containsKey) {
                    desiredProperties.add(new Pair(entry.getKey(), entry.getValue()));
                }
            }
            deviceTwinDevice.setDesiredProperties(desiredProperties);
            //deviceTwinDevice.setETag("*");
            deviceTwin.updateTwin(deviceTwinDevice);
        } catch (IOException | IotHubException e) {
            e.printStackTrace();
        }
    }

    public static String getIccidFromDeviceTwin(String connectionString, String deviceId) {
	    String iccid = null;
        DeviceTwin deviceTwin = null;
        try {
            deviceTwin = DeviceTwin.createFromConnectionString(connectionString);
            DeviceTwinDevice deviceTwinDevice = new DeviceTwinDevice(deviceId);
            deviceTwin.getTwin(deviceTwinDevice);
            Set<Pair> desiredProperties = deviceTwinDevice.getReportedProperties();
            Pair pair;
            for (Pair desiredProperty : desiredProperties) {
                pair = desiredProperty;
                if ("System".equals(pair.getKey())) {
                    JSONObject json = JSON.parseObject(JSONObject.toJSONString(pair.getValue()));
                    if (json.get("ICCID") != null) {
                        iccid = (String) json.get("ICCID");
                        break;
                    }
                }
            }
        } catch (IOException | IotHubException e) {
            e.printStackTrace();
        }
        return iccid;
    }

}
