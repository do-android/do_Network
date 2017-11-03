package doext.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import core.helper.DoSingletonModuleHelper;
import core.interfaces.DoIAppDelegate;

/**
 * APP启动的时候会执行onCreate方法；
 * 
 */
public class do_Network_App implements DoIAppDelegate {

	private static do_Network_App instance;
	private String moduleTypeID;

	private do_Network_App() {

	}

	public static do_Network_App getInstance() {
		if (instance == null) {
			instance = new do_Network_App();
		}
		return instance;
	}

	private NetWorkChangedListener netWorkChangedListener;
	private MyReceiver receiver;
	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 网络变化的时候会发送通知
			if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
				String networkType = DoSingletonModuleHelper.getAPNType(context);
				if (netWorkChangedListener != null) {
					netWorkChangedListener.changed(networkType);
				}
			}
		}
	}

	@Override
	public void onCreate(Context context) {
		// /注册监听网络广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(CONNECTIVITY_CHANGE_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		receiver = new MyReceiver();
		context.registerReceiver(receiver, filter);
	}

	public void setNetWorkChangedListener(NetWorkChangedListener netWorkChangedListener) {
		this.netWorkChangedListener = netWorkChangedListener;
	}

	public String getModuleTypeID() {
		return moduleTypeID;
	}

	public void setModuleTypeID(String moduleTypeID) {
		this.moduleTypeID = moduleTypeID;
	}

	@Override
	public String getTypeID() {
		// TODO Auto-generated method stub
		return getModuleTypeID();
	}

}
