package com.levemus.gliderhud.Utils;

import android.os.Environment;

import com.levemus.gliderhud.Types.Waypoint;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by markcarter on 16-01-03.
 */
public class WaypointReader {
    public static ArrayList<Waypoint> load(String fileName) {
        ArrayList<Waypoint> wayPoints = new ArrayList<>();

        try {
            InputStream inputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = bufferedReader.readLine();
            while(line != null) {
                Waypoint wayPoint = new Waypoint();
                wayPoint.deserialize(line);
                wayPoints.add(wayPoint);
                line = bufferedReader.readLine();
            }

            inputStream.close();

        } catch(Exception e) {}
        return wayPoints;
    }
}
