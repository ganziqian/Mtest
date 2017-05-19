package com.zwg.wifilib.utils;

import java.util.Comparator;
import java.util.List;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

public class BaseWifiUtil {
	public static final String WPA2 = "WPA2";
	public static final String WPA = "WPA";
	public static final String WEP = "WEP";
	public static final String OPEN = "Open";
    public static final String WPA_EAP = "WPA-EAP";
    public static final String IEEE8021X = "IEEE8021X";
    public static final String[] EAP_METHOD = { "PEAP", "TLS", "TTLS" };
    public static final int WEP_PASSWORD_AUTO = 0;
    public static final int WEP_PASSWORD_ASCII = 1;
    public static final int WEP_PASSWORD_HEX = 2;
	private static final String TAG = "Wifi";
	private static final int MAX_PRIORITY = 99999;

	/**
	 * 重置密码后连接
	 * @param wifiMgr
	 * @param config
	 * @param newPassword
	 * @param numOpenNetworksKept
	 * @return
	 */
	public static boolean changePasswordAndConnect( final WifiManager wifiMgr, final WifiConfiguration config, final String newPassword, final int numOpenNetworksKept) {
		setupSecurity(config, getWifiConfigurationSecurity(config), newPassword);
		final int networkId = wifiMgr.updateNetwork(config);
		if(networkId == -1) {
			return false;
		}
		return connectToConfiguredNetwork(wifiMgr, config, true);
	}

	/**
	 * 连接网络
	 * @param wifiMgr
	 * @param scanResult
	 * @param password
	 * @return
	 */
	public static boolean connectToNewNetwork(final WifiManager wifiMgr, final ScanResult scanResult, final String password) {
		final String security = getScanResultSecurity(scanResult);
		if(security.equals(OPEN)) {
			final int numOpenNetworksKept = 10;
			checkForExcessOpenNetworkAndSave(wifiMgr, numOpenNetworksKept);
		}
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = convertToQuotedString(scanResult.SSID);
		config.BSSID = scanResult.BSSID;
		setupSecurity(config, security, password);
		int id = wifiMgr.addNetwork(config);
		if(id == -1) {
			return false;
		}
		if(!wifiMgr.saveConfiguration()) {
			return false;
		}
		config = getWifiConfiguration(wifiMgr, config, security);
		if(config == null) {
			return false;
		}
		return connectToConfiguredNetwork(wifiMgr, config, true);
	}

	/**
	 * 连接网络
	 * @param wifiMgr
	 * @param config
	 * @param reconnect
	 * @return
	 */
	public static boolean connectToConfiguredNetwork( final WifiManager wifiMgr, WifiConfiguration config, boolean reconnect) {
		final String security = getWifiConfigurationSecurity(config);
		int oldPri = config.priority;
		int newPri = getMaxPriority(wifiMgr) + 1;
		if(newPri > MAX_PRIORITY) {
			newPri = shiftPriorityAndSave(wifiMgr);
			config = getWifiConfiguration(wifiMgr, config, security);
			if(config == null) {
				return false;
			}
		}
		config.priority = newPri;
		int networkId = wifiMgr.updateNetwork(config);
		if(networkId == -1) {
			return false;
		}
		if(!wifiMgr.enableNetwork(networkId, false)) {
			config.priority = oldPri;
			return false;
		}
		if(!wifiMgr.saveConfiguration()) {
			config.priority = oldPri;
			return false;
		}
		config = getWifiConfiguration(wifiMgr, config, security);
		if(config == null) {
			return false;
		}
		if(!wifiMgr.enableNetwork(config.networkId, true)) {
			return false;
		}
		final boolean connect = reconnect ? wifiMgr.reconnect() : wifiMgr.reassociate();
		if(!connect) {
			return false;
		}
		return true;
	}

	/**
	 * 根据优先级重新排列
	 * @param configurations
	 */
	private static void sortByPriority(final List<WifiConfiguration> configurations) {
		java.util.Collections.sort(configurations, new Comparator<WifiConfiguration>() {

			@Override
			public int compare(WifiConfiguration object1,
					WifiConfiguration object2) {
				return object1.priority - object2.priority;
			}
		});
	}

	/**
	 * 确保不会存在超过numOpenNetworksKept个开放网络
	 * @param wifiMgr
	 * @param numOpenNetworksKept
	 * @return
	 */
	private static boolean checkForExcessOpenNetworkAndSave(final WifiManager wifiMgr, final int numOpenNetworksKept) {
		final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
		sortByPriority(configurations);
		boolean modified = false;
		int tempCount = 0;
		for(int i = configurations.size() - 1; i >= 0; i--) {
			final WifiConfiguration config = configurations.get(i);
			if(getWifiConfigurationSecurity(config).equals(OPEN)) {
				tempCount++;
				if(tempCount >= numOpenNetworksKept) {
					modified = true;
					wifiMgr.removeNetwork(config.networkId);
				}
			}
		}
		if(modified) {
			return wifiMgr.saveConfiguration();
		}
		return true;
	}

	/**
	 * 重新排列优先级
	 * @param wifiMgr
	 * @return
	 */
	private static int shiftPriorityAndSave(final WifiManager wifiMgr) {
		final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
		sortByPriority(configurations);
		final int size = configurations.size();
		for(int i = 0; i < size; i++) {
			final WifiConfiguration config = configurations.get(i);
			config.priority = i;
			wifiMgr.updateNetwork(config);
		}
		wifiMgr.saveConfiguration();
		return size;
	}

	/**
	 * 得到最大优先级
	 * @param wifiManager
	 * @return
	 */
	private static int getMaxPriority(final WifiManager wifiManager) {
		final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
		int pri = 0;
		for(final WifiConfiguration config : configurations) {
			if(config.priority > pri) {
				pri = config.priority;
			}
		}
		return pri;
	}

	/**
	 * 得到Wifi配置
	 * @param wifiMgr
	 * @param hotsopt
	 * @param hotspotSecurity
	 * @return
	 */
	public static WifiConfiguration getWifiConfiguration(final WifiManager wifiMgr,final ScanResult hotsopt, String hotspotSecurity) {
		final String ssid = convertToQuotedString(hotsopt.SSID);
		if(ssid.length() == 0) {
			return null;
		}
		final String bssid = hotsopt.BSSID;
		if(bssid == null) {
			return null;
		}
		if(hotspotSecurity == null) {
			hotspotSecurity = getScanResultSecurity(hotsopt);
		}
		final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
		for(final WifiConfiguration config : configurations) {
			if(config.SSID == null || !ssid.equals(config.SSID)) {
				continue;
			}
			if(config.BSSID == null || bssid.equals(config.BSSID)) {
				final String configSecurity = getWifiConfigurationSecurity(config);
				if(hotspotSecurity.equals(configSecurity)) {
					return config;
				}
			}
		}
		return null;
	}

	/**
	 * 得到Wifi配置
	 * @param wifiMgr
	 * @param configToFind
	 * @param security
	 * @return
	 */
	public static WifiConfiguration getWifiConfiguration(final WifiManager wifiMgr,final WifiConfiguration configToFind, String security) {
		final String ssid = configToFind.SSID;
		if(ssid.length() == 0) {
			return null;
		}
		final String bssid = configToFind.BSSID;
		if(security == null) {
			security = getWifiConfigurationSecurity(configToFind);
		}
		final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
		for(final WifiConfiguration config : configurations) {
			if(config.SSID == null || !ssid.equals(config.SSID)) {
				continue;
			}
			if(config.BSSID == null || bssid == null || bssid.equals(config.BSSID)) {
				final String configSecurity = getWifiConfigurationSecurity(config);
				if(security.equals(configSecurity)) {
					return config;
				}
			}
		}
		return null;
	}

	/**
	 * 得到加密方式
	 * @param wifiConfig
	 * @return
	 */
    static public String getWifiConfigurationSecurity(WifiConfiguration wifiConfig) {

        if (wifiConfig.allowedKeyManagement.get(KeyMgmt.NONE)) {
            if (!wifiConfig.allowedGroupCiphers.get(GroupCipher.CCMP)
                    && (wifiConfig.allowedGroupCiphers.get(GroupCipher.WEP40)
                            || wifiConfig.allowedGroupCiphers.get(GroupCipher.WEP104))) {
                return WEP;
            } else {
                return OPEN;
            }
        } else if (wifiConfig.allowedProtocols.get(Protocol.RSN)) {
            return WPA2;
        } else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_EAP)) {
            return WPA_EAP;
        } else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return IEEE8021X;
        } else if (wifiConfig.allowedProtocols.get(Protocol.WPA)) {
            return WPA;
        } else {
            Log.w(TAG, "Unknown security type from WifiConfiguration, falling back on open.");
            return OPEN;
        }
    }

	/**
	 * 设置加密方式
	 * @param config
	 * @param security
	 * @param password
	 */
	static private void setupSecurity(WifiConfiguration config, String security, final String password) {
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        if (TextUtils.isEmpty(security)) {
            security = OPEN;
            Log.w(TAG, "Empty security, assuming open");
        }
        if (security.equals(WEP)) {
        	 int wepPasswordType = WEP_PASSWORD_AUTO;
            if (!TextUtils.isEmpty(password)) {
				if (wepPasswordType == WEP_PASSWORD_AUTO) {
                    if (isHexWepKey(password)) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = convertToQuotedString(password);
                    }
                } else {
                    config.wepKeys[0] = wepPasswordType == WEP_PASSWORD_ASCII
                            ? convertToQuotedString(password)
                            : password;
                }
            }
            config.wepTxKeyIndex = 0;
            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.allowedGroupCiphers.set(GroupCipher.WEP40);
            config.allowedGroupCiphers.set(GroupCipher.WEP104);
        } else if (security.equals(WPA) || security.equals(WPA2)){
            config.allowedGroupCiphers.set(GroupCipher.TKIP);
            config.allowedGroupCiphers.set(GroupCipher.CCMP);
            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(PairwiseCipher.TKIP);
            config.allowedProtocols.set(security.equals(WPA2) ? Protocol.RSN : Protocol.WPA);
            if (!TextUtils.isEmpty(password)) {
                if (password.length() == 64 && isHex(password)) {
                    config.preSharedKey = password;
                } else {
                    config.preSharedKey = convertToQuotedString(password);
                }
            }
        } else if (security.equals(OPEN)) {
            config.allowedKeyManagement.set(KeyMgmt.NONE);
        } else if (security.equals(WPA_EAP) || security.equals(IEEE8021X)) {
            config.allowedGroupCiphers.set(GroupCipher.TKIP);
            config.allowedGroupCiphers.set(GroupCipher.CCMP);
            if (security.equals(WPA_EAP)) {
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
            } else {
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
            }
            if (!TextUtils.isEmpty(password)) {
                config.preSharedKey = convertToQuotedString(password);
            }
        }
    }

	/**
	 * 是否是WEP
	 * @param wepKey
	 * @return
	 */
	private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }
        return isHex(wepKey);
    }

	/**
	 * 是否是Hex字符串
	 * @param key
	 * @return
	 */
    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    static final String[] SECURITY_MODES = { WEP, WPA, WPA2, WPA_EAP, IEEE8021X };

	/**
	 * 得到加密方式
	 * @param scanResult
	 * @return
	 */
    public static String getScanResultSecurity(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
            if (cap.contains(SECURITY_MODES[i])) {
                return SECURITY_MODES[i];
            }
        }
        return OPEN;
    }

	/**
	 * 添加双引号
	 * @param string
	 * @return
	 */
	public static String convertToQuotedString(String string) {
		if (TextUtils.isEmpty(string)) {
			return "";
		}
		final int lastPos = string.length() - 1;
		if (lastPos < 0 || (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
			return string;
		}
		return "\"" + string + "\"";
	}
}