package cn.face.sdk;

public class FaceInfo {
	public FaceInfo()
	{

	}

	public int detected; // 1: 检测到的人脸,.0：跟踪到的人脸.
	                     // 注： 跟踪到的仅ID和人脸框数据有效

	public int trackId;  // 人脸ID（ID<0表示没有进入跟踪）
	
	// face rect人脸框
	public int x;        // 左上角x坐标
	public int y;        // 坐上角y坐标
	public int width;    // 人脸宽
	public int height;   // 人脸高

	
	// face_point关键点，最多68个关键点，目前使用9点关键点模型
	public float[] keypt_x;      // 关键点x坐标
	public float[] keypt_y;      // 关键点y坐标
	public float keyptScore;    // 关键点得分
	
	// face_aligned人脸对齐数据，用于提特征
	public byte[] alignedData;  // 图像数据，空间分配128*128
	public int alignedW;        // 宽
	public int alignedH;        // 高
	public int nChannels;       // 图像通道
	
	// face_quality人脸质量分
	public int   errcode;		// 质量分析错误码
	public float faceScore;     // 总分，0~1.0之间，越大则人脸质量越好.
	public float brightness;    // 亮度
	public float clearness;     // 清晰度
	public float symmetry;      // 对称性
	public float glassness;     // 眼镜: 返回值0~1，值越大，越有可能没戴眼镜.
	public float skiness;       // 肤色：返回值0~1, 肤色面积占人脸面积的比例
	public float mouthness;     // 嘴部: 自行设定阈值Thres，约在(0,1)区间，大于Thres为闭嘴.
	public float eyeLeft;       // 左眼
	public float eyeRight;      // 右眼
	public float occlusion;     // 眼睛被遮挡的置信度，推荐阈值为0.5

	// head_pose头部姿态
	public float pitch;         // 抬头、低头,范围-90到90，越大表示越抬头
	public float yaw;           // 左右转头
	public float roll;          // 平面内偏头

}
