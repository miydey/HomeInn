package cn.face.sdk;



public class FaceVersion {

	static FaceVersion version = null;
	
	public FaceVersion() {
		loadLibrarys();
	}

	public static FaceVersion getInstance() {

		if (null == version) {
			version = new FaceVersion();
		}
		return version;
	}

	private static void loadLibrary(String libraryName) {
		System.loadLibrary(libraryName);
	}

	private static void loadLibrarys() {
		loadLibrary("CWFaceIDCardDet");
		loadLibrary("CloudWalkRecog");
		loadLibrary("CWFaceSDK");
		loadLibrary("CWFaceSDKJni");
	}

	static public native String cwGetFaceSDKVersion();

	static public native int cwGetMaxHandlesNum(String pLicence);
	
	static public native String cwGetDeviceInfo();
	
	static public native String cwGetLicence(String pCusName, String pCusCode);

}
