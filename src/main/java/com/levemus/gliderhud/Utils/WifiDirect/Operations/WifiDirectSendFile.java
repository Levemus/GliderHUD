package com.levemus.gliderhud.Utils.WifiDirect.Operations;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectEvent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by markcarter on 16-01-04.
 */
public class WifiDirectSendFile implements IWifiDirectOperation {
    private final String TAG = this.getClass().getSimpleName();
    private final int READ_TIMEOUT = 1000 * 60 * 5; // min in ms
    public final int PORT = 7950;

    @Override
    public WifiDirectEvent.Event perform(WifiDirectOperationConfig config, HashMap<String, String> params) {
        WifiP2pInfo wifiInfo = config.mWifiInfo;

        if(!wifiInfo.isGroupOwner)
        {
            try {

                File file = new File(params.get("filename"));
                FileInputStream fileInputStream = new FileInputStream(file);

                // read the file off disk
                int fileLength = (int)file.length();
                byte[] buffer = new byte[(int)fileLength];
                fileInputStream.read(buffer, 0, buffer.length);
                fileInputStream.close();

                // get socket
                InetAddress targetIP = wifiInfo.groupOwnerAddress;
                Socket clientSocket = new Socket(targetIP, PORT);
                OutputStream outputStream = clientSocket.getOutputStream();

                // Send file length
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeLong(file.length());
                dataOutputStream.flush();
                dataOutputStream.write(buffer,0, fileLength);
                dataOutputStream.flush();
                dataOutputStream.close();
                outputStream.close();

                try {
                    InputStream inputStream = clientSocket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    // wait for client socket close and cause an exception
                    clientSocket.setSoTimeout(READ_TIMEOUT);
                    dataInputStream.readByte();
                    dataInputStream.close();
                    inputStream.close();
                } catch(Exception e) {}
                finally {
                    clientSocket.close();
                }


            } catch (Exception e) {
                Log.i(TAG, "Exception: " + e);
                return WifiDirectEvent.Event.OPERATION_FAIL;
            }
        }
        else
        {
            Log.i(TAG, "Device is group owner, send failed");
            return WifiDirectEvent.Event.OPERATION_FAIL;
        }

        return WifiDirectEvent.Event.OPERATION_SUCCESS;
    }
}
