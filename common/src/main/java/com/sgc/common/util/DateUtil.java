package com.sgc.common.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {

	/**
	 * 取得北京当前时间
	 * @return
	 */
	public static Date getBeijingDate() {
        
        return getDateByZoneId("Asia/Shanghai");
	}
	
	/**
	 * 取得UTC当前时间
	 * @return
	 */
	public static Date getUTCDate() {
		return getDateByZoneId("UTC");
	}
	
	/**
	 * 根据时区取得当前时间
	 * @param zoneId
	 * @return
	 */
	public static Date getDateByZoneId(String zoneId) {
		ZoneId shanghaiZoneId = ZoneId.of(zoneId);
        ZonedDateTime shanghaiZonedDateTime = ZonedDateTime.now(shanghaiZoneId);
        
        return Date.from(shanghaiZonedDateTime.toInstant());
	}
	
	
}
