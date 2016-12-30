package com.example.testvideo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import x1.Studio.Core.IVideoDataCallBack;
import x1.Studio.Core.OnlineService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.testvideo.UI.DevInfo;
import com.example.testvideo.UI.Global;
import com.example.testvideo.UI.xDeviceListAdapter;




public class LanDevListActivity extends Activity implements IVideoDataCallBack{

	private OnlineService ons;
	private List<DevInfo> devInfoList;
	private xDeviceListAdapter lanListAdapter = null; 
	private ListView devListView;
	private Handler handler;;  
	private DevInfo TerminalObjectForPlayActivity;
	private DevInfo devInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devlist);
		
		ons = OnlineService.getInstance();
		ons.setCallBackData(this);
		
		if(!Global.isInitLan){
			Global.isInitLan = true;
			ons.initLan(); //初始化局域网;
		
		}
		
		handler = new Handler();
		
		devListView = (ListView)findViewById(R.id.devList);
		
		ImageButton c = (ImageButton) findViewById(R.id.refresh_btn);
		c.setOnClickListener(new ButtonListener());
		
		devListView.setOnItemClickListener(new ListViewItemClickListener());
		
	
		
	
	}
	
	
	@Override
	protected void onResume() {
		ons.setCallBackData(this);
		super.onResume();
	}

	class ButtonListener implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			devInfoList = null;
			System.out.println( ons.refreshLan()); //刷新局域网列表
			
		}
	 
 }
	
	class ListViewItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int postion, long id) {
			/*TerminalObjectForPlayActivity =   devInfoList.get(postion);
			System.out.println(ons.getLanSysInfo(208, TerminalObjectForPlayActivity.getDevid()));*/
			TerminalObjectForPlayActivity =   devInfoList.get(postion);
			Intent intent = new Intent();
			intent.setClass(LanDevListActivity.this, VideoActivity.class);
			Bundle mBundle = new Bundle();
			//mBundle.putBoolean("NetType", this.isOnline);
			mBundle.putSerializable("devInfo",
					LanDevListActivity.this.TerminalObjectForPlayActivity);
			intent.putExtras(mBundle);
			startActivity(intent);
			//this.TerminalObjectForPlayActivity = null;
			
		}
		
	}

	

	@Override
	/**
	 * 局域网列表回调
	 * Devid：设备ID
		DevType:设备类型 "H264","MJEPG","MPGE4"
		hkid:局域网标识ID
		ChannelCount：通道数,普通设备0
		State：在线状态 :1和2 在线，其他不在线
		AudioType：音频类型 "G711","G726","PCM"
 */
	public void OnCallbackFunForLanDate(String devid, String videoType, int hkid,
			int channal, int status, String audioType) {
		
		if(devid.equals("302")) //无关的回调，直接return
			return;
		
	//	System.out.println(ons.doGetFtpInfo(devid, hkid);
		handler.post(new updateListView(devid, videoType, hkid, channal, status, audioType));
  
	
 }
	
	private class updateListView implements Runnable{
		
		String devid,devType,audioType;
		int hkid,channal,status;
		public updateListView(String devid,String devType,int hkid,int channal ,int status, String audioType){
			this.devid = devid;
			this.devType = devType;
			this.hkid = hkid;
			this.channal = channal;
			this.status = status;
			this.audioType = audioType;
		}
		@Override
		public void run() {
			setDevList(devid,  devType, hkid, channal, status, audioType);
			
		}
		
	}
	
	private void setDevList(String devid, String devType, int hkid,
			int channal, int stats, String audioType){
		
		DevInfo devInfo = new DevInfo();
		
		devInfo.setDevid(devid);
		devInfo.setVideoType(devType);
		devInfo.setHkid(hkid);
		devInfo.setChannal(channal);
		devInfo.setStats(stats);
		devInfo.setAudioType(audioType);
		devInfo.setType(0);//1互联网，0局域网
		createList(devInfo);
		
		
		
	}

	
	private void createList(DevInfo devInfo){
		
		if( devInfoList==null){
			 devInfoList = new ArrayList<DevInfo>();
		}
		
		devInfoList.add(devInfo);
		
		if (this.lanListAdapter == null) {
			
			this.lanListAdapter = new xDeviceListAdapter(this, devInfoList);
			this.devListView.setAdapter(lanListAdapter);
			
		} else {
			
			try{
				
				this.lanListAdapter.DataSource = devInfoList;
				this.lanListAdapter.notifyDataSetChanged();
				
				Log.v("DevList","notifyDataSetChanged..."+	this.lanListAdapter.DataSource.size());
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	

	@Override
	public void OnCallbackFunForRegionMonServer(int iFlag) {}


	@Override
	public void OnCallbackFunForGetItem(byte[] byteArray, int result) {}


	@Override
	public void OnCallbackFunForDataServer(String CallId, ByteBuffer Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime) {}  
	

	@Override
	public void OnCallbackFunForComData(int Type, int Result,
			int AttachValueBufSize, String AttachValueBuf) {}


	@Override
	public void OnCallbackFunForUnDecodeDataServer(String CallId, byte[] Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime,int mFream) {
		// TODO Auto-generated method stub
		
	}

	
	protected void onDestroy(){
		//ons.exitLan();
		super.onDestroy();
	}


	@Override
	public void OnCallbackFunForIPRateData(String Ip) {
		// TODO Auto-generated method stub
		
	}



}
