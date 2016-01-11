package com.levemus.gliderhud.Utils.WifiDirect.Operations;

import android.util.Log;

import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectEvent;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by markcarter on 16-01-04.
 */
public class WifiDirectReceiveFile implements IWifiDirectOperation {
    private final String TAG = this.getClass().getSimpleName();
    private final int READ_TIMEOUT = 1000 * 60 * 5; // min in ms
    public final int PORT = 7950;

    @Override
    public WifiDirectEvent.Event perform(WifiDirectOperationConfig config, HashMap<String, String> params) {

        ServerSocket welcomeSocket = null;
        try {
            welcomeSocket = new ServerSocket(PORT);
            Socket socket = welcomeSocket.accept();
            socket.setSoTimeout(READ_TIMEOUT);
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            // Get filelength
            long fileLength = dataInputStream.readLong();
            byte[] buffer = new byte[(int)fileLength];

            // read the data
            dataInputStream.readFully(buffer, 0, (int)fileLength);
            dataInputStream.close();
            socket.close();
            welcomeSocket.close();

            // write to file
            File file = new File(params.get("filename"));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buffer, 0, (int)fileLength);
            fileOutputStream.getFD().sync();
            fileOutputStream.close();

        } catch (Exception e) {
                Log.i(TAG, "Exception: " + e);
                return WifiDirectEvent.Event.OPERATION_FAIL;
        }

        return (WifiDirectEvent.Event.OPERATION_SUCCESS);
    }
}
