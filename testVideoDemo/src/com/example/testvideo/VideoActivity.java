package com.example.testvideo;

import java.io.File;
import java.nio.ByteBuffer;

import x1.Studio.Core.IVideoDataCallBack;
import x1.Studio.Core.OnlineService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testvideo.UI.DevInfo;

public class VideoActivity extends Activity implements SurfaceHolder.Callback,
		IVideoDataCallBack {

	private OnlineService ons;

	private Bitmap VideoBit;
	private boolean VideoInited = false;
	private Rect RectOfRegion;// 原始影像的区域
	private RectF RectOfScale;// used while in the landscape mode to scale the
	private int Width;
	private int Height;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private DevInfo devInfo;
	private String callID, audioCallID, audioSayID;
	private Button upPtzBtn, downPtzBtn, rigthPztBtn, leftPtzBtn, stopPtzBtn,sendDataBtn;
	private EditText dataEt;
	private int type;
	private String tag = "VideoActivity";
	private TextView queueText;
	private boolean isRecord = false, isListen = false, isSay = false;

	private Button sayBtn, listenBtn,settingWifiBtn;
	

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ons = OnlineService.getInstance();
		ons.setCallBackData(this);

		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView_video);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);

		ons.regionAudioDataServer();// 注册音频回调，必须调用才能收到音频数据
		ons.regionVideoDataServer();// 注册视频数据回调
		
		devInfo = (DevInfo) getIntent().getSerializableExtra("devInfo");
		type = devInfo.getType();// 1互联网，0局域网
		
		dataEt = (EditText)findViewById(R.id.data_et);
		
		if (devInfo != null) {
			if (type == 0) {
				callID = ons.callLanVideo(devInfo.getDevid(),
						devInfo.getHkid(), devInfo.getVideoType(),
						devInfo.getChannal(), 0);// 呼叫局域网视频

			} else {
				callID = ons.callWanVideo(devInfo.getDevid(),
						devInfo.getVideoType(), devInfo.getChannal(), 0);// 呼叫互联网视频

			}

		}

		sayBtn = (Button) findViewById(R.id.say_btn);
		sayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSay) {
					playAudioSay();
				} else {
					stopAudioSay();
				}

			}

		});

		listenBtn = (Button) findViewById(R.id.listen_btn);
		listenBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isListen) {

					playAudio();

				} else {
					stopAudio();
				}

			}
		});

		Button captureBtn = (Button) findViewById(R.id.capture_btn);
		captureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Button btn = (Button) v;
				if (!isRecord) {
					String path = Environment.getExternalStorageDirectory()
							+ "/videoDemo";
					String fileName = "test.avi";

					File dir = new File(path);
					if (!dir.exists()) {
						dir.mkdirs();
					}

					int m_audioType = 3;
					int m_videoType = 0;

					if (devInfo.getVideoType().equals("MJPEG")) {
						m_videoType = 1;
					}
					if (devInfo.getAudioType().equals("G711")) {
						m_audioType = 4;
					}

					if (ons.initAviInfo(m_videoType, 5, m_audioType, path,
							fileName) != -1) {
						isRecord = true;
						ons.aviRecordVideo();
						ons.aviRecordAudio();
						btn.setText("停止录像");
					}
				} else {
					btn.setText("开始录像");
					ons.stopRecordClose();
					isRecord = false;
				}
			}
		});

		upPtzBtn = (Button) findViewById(R.id.up_btn);
		upPtzBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (type == 1) {
					ons.setWanPTZ(devInfo.getDevid(), 3, 0);
				} else {
					ons.setLanPTZ(devInfo.getHkid(), 3, 0);
				}

			}

		});

		downPtzBtn = (Button) findViewById(R.id.down_btn);
		downPtzBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (type == 1) {
					ons.setWanPTZ(devInfo.getDevid(), 4, 0);
				} else {
					ons.setLanPTZ(devInfo.getHkid(), 4, 0);
				}

			}

		});

		rigthPztBtn = (Button) findViewById(R.id.right_btn);
		rigthPztBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (type == 1) {
					ons.setWanPTZ(devInfo.getDevid(), 2, 0);
				} else {
					ons.setLanPTZ(devInfo.getHkid(), 2, 0);
				}

			}

		});
		leftPtzBtn = (Button) findViewById(R.id.left_btn);
		leftPtzBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (type == 1) {
					ons.setWanPTZ(devInfo.getDevid(), 1, 0);
				} else {
					ons.setLanPTZ(devInfo.getHkid(), 1, 0);
				}

			}

		});

		stopPtzBtn = (Button) findViewById(R.id.stop_btn);
		stopPtzBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (type == 1) {
					ons.setWanPTZ(devInfo.getDevid(), 0, 0);
				} else {
					ons.setLanPTZ(devInfo.getHkid(), 0, 0);
				}

			}

		});
		
		settingWifiBtn = (Button)findViewById(R.id.settingwifi_btn);
		 
		settingWifiBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent  i = new Intent(VideoActivity.this, ViewOfSettingWifi.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("devInfo", devInfo);
				i.putExtras(mBundle);
				startActivity(i);				
			}
		});
		
		sendDataBtn = (Button)findViewById(R.id.senddata_btn);
		
		sendDataBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String data = dataEt.getText().toString();
				
				if (type == 1) {
					System.out.println("wan data");
					ons.sendWanData(devInfo.getDevid(), data);
				}else{
					System.out.println("lan data");
					ons.sendLanData(devInfo.getHkid(), data);
					
				}
				
		}
		});
	}


	/**
	 * 初始化播放画面区域
	 * 
	 * @param mFrameWidth
	 * @param mFrameHeight
	 */
	private void initPlayer(int mFrameWidth, int mFrameHeight) {

		VideoInited = true;

		VideoBit = Bitmap.createBitmap(mFrameWidth, mFrameHeight,
				Bitmap.Config.RGB_565);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.Width = dm.widthPixels;
		this.Height = dm.heightPixels;

		// int Left = (this.Width - this.mFrameWidth) / 2;
		int Top = 0;
		int myHight = this.Height;
		int myWidth = (int) (mFrameWidth * (Double.valueOf(this.Height) / Double
				.valueOf(mFrameHeight)));
		int Left = (this.Width - myWidth) / 2;
		this.RectOfRegion = null;
		// this.RectOfScale = new RectF(Left, Top, Left + myWidth, myHight);
		this.RectOfScale = new RectF(0, 0, mSurfaceView.getWidth(),
				mSurfaceView.getHeight());
	}

	/**
	 * 视频数据回调
	 * 
	 * @param CallId
	 *            呼叫ID
	 * @param Buf
	 * @param mFrameWidth
	 * @param mFrameHeight
	 * @param mEncode
	 *            视频类型 1,MPEG4 2,MJPEG 5,H264
	 * @param mTime
	 *            时间戳
	 * 
	 */
	@Override
	public void OnCallbackFunForDataServer(String CallId, ByteBuffer Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime) {
		
		if (!VideoInited) {
			initPlayer(mFrameWidth, mFrameHeight);

		}

		this.VideoBit.copyPixelsFromBuffer(Buf);
		try {
			Canvas canvas = mSurfaceHolder.lockCanvas(null);

			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(this.VideoBit, this.RectOfRegion,
					this.RectOfScale, null);

			this.mSurfaceHolder.unlockCanvasAndPost(canvas);
		} catch (Exception e) {

		}

	}

	private void playAudioSay() {
		if (type == 0)
			audioSayID = ons.callLanAudioSay(devInfo.getHkid(),
					devInfo.getAudioType());// 局域网说音频
		else
			audioSayID = ons.callWanAudioSay(devInfo.getDevid(),
					devInfo.getAudioType());

		sayBtn.setText("停止对讲");

		isSay = true;
	}

	private void stopAudioSay() {
		if (audioSayID != null)
			if (type == 0)
				ons.closeLanAudioSay(audioSayID);// 关闭局域网音频说
			else
				ons.closeWanAudioSay(devInfo.getDevid(), audioSayID);// 关闭互联网音频说

		sayBtn.setText("开始对讲");
		isSay = true;
		audioSayID = null;

	}

	private void playAudio() {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		if (type == 0)
			audioCallID = ons.callLanAudioListen(devInfo.getHkid(), am,
					devInfo.getAudioType());// 局域网听音频
		else
			audioCallID = ons.callWanAudioListen(devInfo.getDevid(), am,
					devInfo.getAudioType());

		listenBtn.setText("停止收听");
		isListen = true;
	}

	private void stopAudio() {

		if (audioCallID != null)
			if (type == 0)
				ons.closeLanAudio(audioCallID);// 关闭局域网音频听
			else
				ons.closeWanAudio(devInfo.getDevid(), audioCallID);// 关闭互联网音频听

		listenBtn.setText("开始收听");
		isListen = false;
		audioCallID = null;

	}

	@Override
	protected void onPause() {
		System.out.println("onPause");
		
		super.onPause();

	}
	
	@Override
	protected void onResume() {
		ons.setCallBackData(this);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (callID != null)
			if (type == 0)
				ons.closeLanVideo(callID);// 关闭局域网视频
			else {
				ons.closeWanVideo(devInfo.getDevid(), callID);// 关闭互联网视频
				System.out.println(devInfo.getDevid() + "...." + callID);

			}

		// ons.closeWanAudio();

		if (audioCallID != null)
			stopAudio();

		if (audioSayID != null)
			if (type == 0)
				ons.closeLanAudioSay(audioSayID);// 关闭局域网音频说
			else
				ons.closeWanAudioSay(devInfo.getDevid(), audioSayID);// 关闭互联网音频说

		// ons.quitSysm();

		super.onDestroy();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void OnCallbackFunForRegionMonServer(int iFlag) {
		Log.v(tag, "OnCallbackFunForRegionMonServer");
	}

	@Override
	public void OnCallbackFunForGetItem(byte[] byteArray, int result) {
		Log.v(tag, "OnCallbackFunForGetItem");

	}

	@Override
	public void OnCallbackFunForLanDate(final String devid,
			final String devType, final int hkid, final int channal, int stats,
			final String auioType) {
			if(devid.equals("200")){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(VideoActivity.this, "从设备返回："+auioType, Toast.LENGTH_LONG).show();
						
					}
				});
				
			}

	}

	@Override
	public void OnCallbackFunForComData(int Type, int Result,
			int AttachValueBufSize, final String AttachValueBuf) {
		Log.v(tag, "OnCallbackFunForComData");

		if (Type == 105) {
			if (Result == 2) {
				Log.v(tag, "互联网设备连接成功...");
			} else if (Result == 4) {
				Log.v(tag, "互联网设备连接失败...");
			}

		}

		if (Type == 106) {
			if (Result == 1) {
				Log.v(tag, "互联网设备呼叫成功...");
			} else if (Result == 2) {
				Log.v(tag, "互联网设备呼叫失败...");
			}
		}
		
		if(Type == 200){
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
				
					Toast.makeText(VideoActivity.this, "从设备返回："+AttachValueBuf, Toast.LENGTH_LONG).show();
				}
			});
			
		}
	}

	@Override
	public void OnCallbackFunForUnDecodeDataServer(String CallId, byte[] Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime,
			int mFream) {
		Log.v(tag, "OnCallbackFunForUnDecodeDataServer");

	}

	@Override
	public void OnCallbackFunForIPRateData(String Ip) {
		// TODO Auto-generated method stub

	}
}
