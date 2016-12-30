package com.example.testvideo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import x1.Studio.Core.IVideoDataCallBack;
import x1.Studio.Core.OnlineService;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testvideo.UI.DevInfo;

public class ViewOfSettingWifi extends Activity implements IVideoDataCallBack {

	
	
	private DevInfo devInfo;
	private OnlineService ons;
	private int open = 1;
	
	private EditText pwdEdit;
	private TextView ssidTv;;
	
	private String ssid,wifiMac,wifiSatype,wifiEntype;
	private List<WifiInfo> wifiInfoList;

	

	
	private ProgressBar getWifiPb;
	private ListView wifiListView;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.function_setting_wifi);
		
		initView();
		
		devInfo = (DevInfo)this.getIntent().getSerializableExtra("devInfo");
		ons = OnlineService.getInstance();
		ons.setCallBackData(this);
		
	
		
	}
	
	
	
	private void initView(){
		pwdEdit = (EditText)findViewById(R.id.setting_wifi_pwd_edit);
		ssidTv = (TextView)findViewById(R.id.setting_wifi_SSID_text);
		wifiListView = (ListView)findViewById(R.id.wifi_list);
		wifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				wifiSatype = wifiInfoList.get(position).getSafeType();
				wifiEntype = wifiInfoList.get(position).getEncryType();
				ssid =  wifiInfoList.get(position).getSSID();
				ssidTv.setText(ssid);
			}
			
		});
	}
	
	
	/**
	 * 获取当前WIFI信息,取到mac用来搜索列表
	 * @param v
	 */
	public void getWifiMac(View v) {
		
		
		String wifiInfo = ons.getLanSysInfo(204, devInfo.getDevid());
		
		System.out.println("WifiInfo： "+wifiInfo);
		
		String[] data = wifiInfo.split(";");
		for(int i=0;i<data.length;i++){
			
			String key = data[i].substring(0, data[i].indexOf("="));
			String value = data[i].substring(data[i].indexOf("=") + 1, data[i].length());
			
			if(key.equals("mac")){
				wifiMac =  value;
			}
			if(key.equals("open")){
				open = Integer.parseInt(value);
			}
			
			if(key.equals("sid")){
				ssid = value;
			}
			if(key.equals("satype")){
				wifiSatype = value;
			}
			if(key.equals("entype")){
				wifiEntype = value;
			}
			
			
		}
	   
	}
		
	
	/**
	 * 搜索WIFI列表，由OnCallbackFunForLanDate返回 
	 * @param v
	 */
	public void SearchWifi(View v){
		
			ons.getLanGetWifiSid(devInfo.getHkid(), wifiMac, 0);
			
	}
		
	


	/**
	 * 设置WIFI
	 * @param v
	 */
		public void savedata(View v){
			
			//open = 0 ,关闭wifi,即AP模式
				   //1 ,开启wifi
			
			if(ons.setLanWifi(1, open, processData(pwdEdit.getText().toString()))!=-1){
				Toast.makeText(this, "设置成功", Toast.LENGTH_LONG).show();
			}
			
		}
	

		
	
	public void goBack(View v){
		finish();
	}
	
	
	
	
	
	private void getData(String[] data){
		 wifiInfoList = new ArrayList<WifiInfo>();
		if(data.length>1){
			for(int i=0;i<data.length;i++){
				String[] info = data[i].split(";");
				WifiInfo wifiInfo = new WifiInfo();
				if(!info[0].equals("")){
					wifiInfo.setSSID(info[0]);
					wifiInfo.setSafeType(info[1]);
					wifiInfo.setEncryType(info[2]);
					wifiInfoList.add(wifiInfo);
				
				}
				
			}
			buildWifiList(wifiInfoList);
		
			
		}
	}
	
	private void buildWifiList(List<WifiInfo> wifiInfoList){
		
		final xWifiListAdapter adapter = new xWifiListAdapter(this, wifiInfoList);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try{
					wifiListView.setAdapter(adapter) ;
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});
	
	}
	

	
	

	private String processData(String pwd){
		
		String s = "sid="+ssid+
				";pswd="+pwd+
				";mac="+wifiMac +
				";satype="+wifiSatype+
				";entype="+wifiEntype+";";
		
		
		return s;
	}
	
	
	
	@Override
	public void OnCallbackFunForDataServer(String CallId, ByteBuffer Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnCallbackFunForLanDate(String devid, String devType, int hkid,
			int channal, int stats, String audioType) {
		if(devid.equals("301")){
		
			
			String[] data = devType.split(":");
			getData(data);
		}
	}

	@Override
	public void OnCallbackFunForRegionMonServer(int iFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnCallbackFunForComData(int Type, int Result,
			int AttachValueBufSize, String AttachValueBuf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnCallbackFunForGetItem(byte[] byteArray, int result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnCallbackFunForUnDecodeDataServer(String arg0, byte[] arg1,
			int arg2, int arg3, int arg4, int arg5, int arg6) {
		// TODO Auto-generated method stub
		
	}
	
	class WifiInfo{
		
		private String SSID;
		private String safeType;
		private String encryType;
		
		public String getSSID() {
			return SSID;
		}
		public void setSSID(String sSID) {
			SSID = sSID;
		}
		public String getSafeType() {
			return safeType;
		}
		public void setSafeType(String safeType) {
			this.safeType = safeType;
		}
		public String getEncryType() {
			return encryType;
		}
		public void setEncryType(String encryType) {
			this.encryType = encryType;
		}
		
	}
	
	
 class xWifiListAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater mGroupInflater;
		public List<WifiInfo> DataSource;

		public int selectPosition = 0;
		
		public xWifiListAdapter(Context c, List<WifiInfo> List) {
			this.context = c;
			
			this.mGroupInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.DataSource = List;
		}

		
		public class FileListItemHolder {
			public ImageView I;
			public TextView N;
		};



		@Override
		public int getCount() {
			int V = 0;
			if(this.DataSource != null)V = this.DataSource.size();
			return V;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			FileListItemHolder holder = null;
			try{
				if (view == null) { 
					view = mGroupInflater.inflate(R.layout.item_wifi_list, null);
					holder = new FileListItemHolder();
					AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					view.setLayoutParams(lp);
					view.setTag(holder);
					holder.N = (TextView) view.findViewById(R.id.textView);
					
					
				} else {
					holder = (FileListItemHolder) view.getTag();
				}
				
				String info = null;
				if (this.DataSource != null)
					info = this.DataSource.get(position).getSSID();
				if (info != null && holder != null) {
					// holder.N.setText(info.Name);
					holder.N.setText(info);
					
					
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			return view;
			
		
			
		}
 }


@Override
public void OnCallbackFunForIPRateData(String Ip) {
	// TODO Auto-generated method stub
	
}
}
