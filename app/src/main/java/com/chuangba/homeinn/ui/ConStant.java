package com.chuangba.homeinn.ui;

public class ConStant {
	public static String publicFilePath;
	// 授权码，由云从科技提供

    public static String sLicence= "MzQ0MTEwbm9kZXZpY2Vjd2F1dGhvcml6Zf3k5+bn5+Ti3+fg5efm5Of/5Obn4Obg5Yjm5uvl5ubrkeXm5uvl5uai6+Xm5uvl5uTm6+Xm5uDm1efr5+vn6+ep4OXl5+fi6/Xn6+fr5+fk++bn5+Y=";

    public static final String PACKAGE_NAME = "com.chuangba.homeinn";
	public static final String URL_SERVER = "http://118.31.38.73";
	public static final int HTTP_FAIL_MAX_COUNT = 6;//最大失败次数
	public static final class CARD_READER{
		// 读卡器类型
		public static final int TYPE_HS = 1; // 华视
		public static final int TYPE_HD = TYPE_HS + 1;// 华大
		public static final int TYPE_ARA = TYPE_HD +1; //亚略特
	}

	public static final class STORAGE{
		// 存储参数
		public static final String DEFAULT_VALUE = "default";
		public static final String HOTEL_ID = "hotel_id";
		public static final String MACHINE_ID = "machine_id";
		public static final String MACHINE_TOKEN = "machine_token";
		public static final String MACHINE_NAME = "machine_name";
	}
	public static final class BEEP{
		public static final int SUCCESS = 1;
		public static final int FAIL = 2;
		public static final int TIME_OUT = 3;
		public static final int BEEP = 4;
	}
    public static final class CAMERA_SET{
		// 相机预览参数
		public final static int PREVIEW_WIDTH = 1280;
		public final static int PREVIEW_HEIGHT = 720;
	}


}
