package com.sgc.common.util;

public class Constants {

	public static enum Dict {
		PROSTA("PROSTA", "产品状态"), COUNTRY("COUNTRY", "国家");

		private Dict(String value, String name) {
			this.value = value;
			this.name = name;
		}

		private final String value;
		private final String name;

		public String getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
	}
}
