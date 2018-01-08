package doext.implement;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import core.DoServiceContainer;
import core.helper.DoSingletonModuleHelper;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import core.object.DoSingletonModule;
import doext.app.NetWorkChangedListener;
import doext.app.do_Network_App;
import doext.define.do_Network_IMethod;
import doext.implement.do_Network_Model;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现do_Network_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.getUniqueKey());
 */
public class do_Network_Model extends DoSingletonModule implements do_Network_IMethod, NetWorkChangedListener {

	public do_Network_Model() throws Exception {
		super();
		do_Network_App.getInstance().setModuleTypeID(getTypeID());
		do_Network_App.getInstance().setNetWorkChangedListener(this);
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		if ("getStatus".equals(_methodName)) {
			this.getStatus(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("getIP".equals(_methodName)) {
			this.getIP(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("getOperators".equals(_methodName)) {
			this.getOperators(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("getMACAddress".equals(_methodName)) {
			this.getMACAddress(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("openWifiSetting".equals(_methodName)) {
			this.openWifiSetting(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("isProxyUsed".equals(_methodName)) {
			this.isProxyUsed(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	// 打开无线网络连接界面
	private void openWifiSetting(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) {
		DoServiceContainer.getPageViewFactory().getAppContext().startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		// ...do something
		if ("getWifiInfo".equals(_methodName)) {
			this.getWifiInfo(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 获取移动终端ip地址；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void getIP(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		_invokeResult.setResultText(getLocalIpAddress());
	}

	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取设备的运营商；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void getOperators(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		_invokeResult.setResultText(getProvidersName(DoServiceContainer.getPageViewFactory().getAppContext()));
	}

	/**
	 * 获取手机服务商信息 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 */
	public String getProvidersName(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String providersName = "none";
		try {
			// 返回唯一的用户ID;就是这张卡的编号神马的
			String IMSI = telephonyManager.getSubscriberId();
			// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				providersName = "中国移动";
			} else if (IMSI.startsWith("46001")) {
				providersName = "中国联通";
			} else if (IMSI.startsWith("46003")) {
				providersName = "中国电信";
			}
		} catch (Exception e) {

		}
		return providersName;
	}

	/**
	 * 获取当前网络状态；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void getStatus(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String netWorkType = DoSingletonModuleHelper.getAPNType(DoServiceContainer.getPageViewFactory().getAppContext());
		_invokeResult.setResultText(netWorkType);
	}

	@Override
	public void changed(String networkType) {
		DoInvokeResult _invokeResult = new DoInvokeResult(this.getUniqueKey());
		_invokeResult.setResultText(networkType);
		this.getEventCenter().fireEvent("changed", _invokeResult);
	}

	/**
	 * 获取当前IP Mac地址；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void getMACAddress(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		_invokeResult.setResultText(getLocalMacAddress());
	}

	private String getLocalMacAddress() {
		/*
		 * 获取mac地址有一点需要注意的就是android
		 * 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"
		 * 这个默认的mac地址，这是googel官方为了加强权限管理而禁用了getSYstemService
		 * (Context.WIFI_SERVICE)方法来获得mac地址。
		 */
		// String macAddress= "";
		// WifiManager wifiManager = (WifiManager)
		// MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
		// WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// macAddress = wifiInfo.getMacAddress();
		// return macAddress;

		String macAddress = "02:00:00:00:00:00";
		StringBuffer buf = new StringBuffer();
		NetworkInterface networkInterface = null;
		try {
			networkInterface = NetworkInterface.getByName("wlan0");
			if (networkInterface == null) {
				networkInterface = NetworkInterface.getByName("eth1");
			}
			if (networkInterface == null) {
				return "02:00:00:00:00:00";
			}
			byte[] addr = networkInterface.getHardwareAddress();
			if (addr == null || addr.length < 1) {
				return "02:00:00:00:00:00";
			}
			for (byte b : addr) {
				buf.append(String.format("%02x:", b));
			}
			if (buf.length() > 0) {
				buf.deleteCharAt(buf.length() - 1);
			}
			macAddress = buf.toString();
		} catch (SocketException e) {
			e.printStackTrace();
			return "02:00:00:00:00:00";
		}
		return macAddress;
	}

	private WifiManager wifiManager;

	@Override
	public void getWifiInfo(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		DoInvokeResult _invokeResult = new DoInvokeResult(do_Network_Model.this.getUniqueKey());
		wifiManager = (WifiManager) DoServiceContainer.getPageViewFactory().getAppContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		String _currentWifiName = info != null ? info.getSSID() : null;
		JSONObject _obj0 = new JSONObject();
		_obj0.put("currentWifiName", _currentWifiName.replaceAll("\"", ""));
		JSONArray _array = new JSONArray();
		_obj0.putOpt("wifiNameList", _array);
		_obj0.putOpt("routerMacAddress", info.getBSSID());
		List<ScanResult> scanResults = wifiManager.getScanResults();
		for (ScanResult scanResult : scanResults) {
			JSONObject _obj = new JSONObject();
			_obj.put("wifiName", scanResult.SSID);
			_array.put(_obj);
		}
		_invokeResult.setResultNode(_obj0);
		_scriptEngine.callback(_callbackFuncName, _invokeResult);
	}

	@Override
	public void isProxyUsed(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
		boolean _isProxyUsed = false;
		if (niList != null) {
			for (NetworkInterface intf : Collections.list(niList)) {
				if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
					_isProxyUsed = true;
				}
			}
		}
		_invokeResult.setResultBoolean(_isProxyUsed);
	}
}