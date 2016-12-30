package com.example.testvideo;

import java.io.UnsupportedEncodingException;
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
import android.widget.ListView;

import com.example.testvideo.UI.DevInfo;
import com.example.testvideo.UI.xDeviceListAdapter;





public class WanDevListActivity extends Activity implements IVideoDataCallBack{

	private OnlineService ons;
	private List<DevInfo> devInfoList;
	private xDeviceListAdapter lanListAdapter = null; 
	private ListView devListView;
	private Handler handler;;  
	private DevInfo TerminalObjectForPlayActivity;
	private String Tag = "WanDevListAvtivity";
    private String[] CacheCallbackFunForGetItem = new String[100];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devlist);
		ons =  OnlineService.getInstance();
		ons.setCallBackData(this);
		
		handler = new Handler();
		ons.getWanListItemEX(); //获取互联网设备列表，数据由OnCallbackFunForGetItem返回;
		
		devListView= (ListView)findViewById(R.id.devList);
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
			 ons.refreshLan(); //刷新局域网列表
			
		}
	 
 }
	
	class ListViewItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int postion, long id) {
		 
			TerminalObjectForPlayActivity =   devInfoList.get(postion);
			if(TerminalObjectForPlayActivity.getStats()==2||TerminalObjectForPlayActivity.getStats()==1){
				Intent intent = new Intent();
				intent.setClass(WanDevListActivity.this, VideoActivity.class);
				Bundle mBundle = new Bundle();
				//mBundle.putBoolean("NetType", this.isOnline);
				mBundle.putSerializable("devInfo",
						WanDevListActivity.this.TerminalObjectForPlayActivity);
				intent.putExtras(mBundle);
				startActivity(intent);
				//this.TerminalObjectForPlayActivity = null;
			}else{
				Log.v(Tag,"设备不在线");
			}
			
		}
		
	}

	

	
	private void createList(DevInfo devInfo){
		
		if( devInfoList==null){
			 devInfoList = new ArrayList<DevInfo>();
		}
		
		//ons.getState(devInfo.getDevid());
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
	
	private class updateListView implements Runnable{
		
		String[] dev;
		
		public updateListView(String[] CacheCallbackFunForGetItem){
			this.dev = CacheCallbackFunForGetItem;
		}
		@Override
		public void run() {
			Log.v(Tag,"获取互联网设备列表...");
			Doing(dev);
		
		}
	}
	
	

	@Override
		public void OnCallbackFunForLanDate(String devid, String videoType, int hkid,
				int channal, int stats, String audioType) {
		
			
		
	 }

	@Override
	public void OnCallbackFunForRegionMonServer(int iFlag) {}


	
	@Override
	/**
	 *  @param  byteArray：包含一个设备的所有信息，可用new String(byteArray)解析
	 *								解析出来的格式为 DevFlag%equal%***%comma%formid%equal%***%comma%alias%equal%***%comma%devid%equal%***%comma%Count%equal%***%comma%type%equal%***%comma%audio%equal%***%comma%admin%equal%***%comma%status%equal%***%comma%popedom%equal%***
									设备信息每个key与value之前用%equal%分开，如devid%equal%***，每个信息用%comma%分开，如devid%equal%***%comma%Count%equal%***
		@param result：设备个数
	*/
	public void OnCallbackFunForGetItem(byte[] byteArray, int result) {
		  
		try {
			if (CacheCallbackFunForGetItem != null) {
				CacheCallbackFunForGetItem[result] = new String(byteArray,
						"gbk"); //编码防止乱码
				System.out.println(CacheCallbackFunForGetItem[result]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
			
	    
	    if(result==0){
	    	handler.post(new updateListView(CacheCallbackFunForGetItem));
	     }
	}
		
	   
	private void Doing(String[] CacheCallbackFunForGetItem){
	//	devInfoList = new ArrayList<DevInfo>();
		for(int i = 0;i<CacheCallbackFunForGetItem.length;i++){
	        try{
			String devList[] = CacheCallbackFunForGetItem[i].split("%comma%");  //设备信息
			DevInfo devInfo = new DevInfo();
			for(int j = 0;j<devList.length;j++){
				String dev[] = devList[j].split("%equal%");
				if(dev.length==2){
					if(dev[0].equals("devid")){
						devInfo.setDevid(dev[1]);
					}
					if(dev[0].equals("DevFlag")){
						devInfo.setVideoType(dev[1]);
					}
					if(dev[0].equals("audio")){
						devInfo.setAudioType(dev[1]);
					}
					if(dev[0].equals("alias")){
						devInfo.setAlias(dev[1]);
					}
					if(dev[0].equals("status")){
						devInfo.setStats(Integer.parseInt(dev[1]));
					}
					if(dev[0].equals("Count")){
						devInfo.setChannal(Integer.parseInt(dev[1]));
					}
				}
				/*devInfoList.add(devInfo)*/;
			}
			    devInfo.setType(1);//1:互联网设备0：局域网设备
			    createList(devInfo);
	        }catch(Exception E){
	        	
	        	CacheCallbackFunForGetItem[i]="";
	        }
		}
		
		
	
	}
	    	
	  
	    	
	    
	    


	@Override
	public void OnCallbackFunForDataServer(String CallId, ByteBuffer Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime) {}  
	

	@Override
	public void OnCallbackFunForComData(int Type, int Result,
			int AttachValueBufSize, String AttachValueBuf) {
		
		
	}

	@Override
	public void OnCallbackFunForUnDecodeDataServer(String CallId, byte[] Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime,int mFream) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy(){
		ons.quitSysm();//注销
		
		
		super.onDestroy();
		
	}

	@Override
	public void OnCallbackFunForIPRateData(String Ip) {
		// TODO Auto-generated method stub
		
	}
	    




}
