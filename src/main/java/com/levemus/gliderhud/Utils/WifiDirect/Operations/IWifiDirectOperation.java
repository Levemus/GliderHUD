package com.levemus.gliderhud.Utils.WifiDirect.Operations;


import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectEvent;

import java.util.HashMap;

/**
 * Created by markcarter on 16-01-05.
 */
public interface IWifiDirectOperation {
    WifiDirectEvent.Event perform(WifiDirectOperationConfig config, HashMap<String, String> params);
}
