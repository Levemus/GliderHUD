package com.levemus.gliderhud.Utils;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 Original implementation (c) 2010 Michael Doube
    https://github.com/mdoube/BoneJ/blob/master/src/org/doube/geometry/FitCircle.java

 Modifications (c) 2015 Levemus Software, Inc.
 */
import java.util.ArrayList;
import java.util.Iterator;

import com.levemus.gliderhud.Types.Point;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Types.OffsetCircle;

/**
 * Created by flyinorange on 15-11-23.
 *
 */
public class TaubinNewtonFitCircle {

    private static Point getCentroid(ArrayList<Vector> points) {
        double sumX = 0;
        double sumY = 0;

        Iterator<Vector> pointIter = points.iterator();
        while (pointIter.hasNext()) {
            Vector current = pointIter.next();
            sumX += current.X();
            sumY += current.Y();
        }

        return new Point(sumX / points.size(), sumY / points.size());
    }

    public static OffsetCircle FitCircle(ArrayList<Vector> points) {
        int nPoints = points.size();
        if (nPoints < 3)
            return null;

        Point centroid = getCentroid(points);
        double Mxx = 0, Myy = 0, Mxy = 0, Mxz = 0, Myz = 0, Mzz = 0;
        Iterator<Vector> pointIter = points.iterator();
        while (pointIter.hasNext()) {
            Vector current = pointIter.next();
            double Xi = current.X() - centroid.X();
            double Yi = current.Y() - centroid.Y();
            double Zi = Xi * Xi + Yi * Yi;
            Mxy += Xi * Yi;
            Mxx += Xi * Xi;
            Myy += Yi * Yi;
            Mxz += Xi * Zi;
            Myz += Yi * Zi;
            Mzz += Zi * Zi;

        }
        Mxx /= nPoints;
        Myy /= nPoints;
        Mxy /= nPoints;
        Mxz /= nPoints;
        Myz /= nPoints;
        Mzz /= nPoints;

        double Mz = Mxx + Myy;
        double Cov_xy = Mxx * Myy - Mxy * Mxy;
        double A3 = 4 * Mz;
        double A2 = -3 * Mz * Mz - Mzz;
        double A1 = Mzz * Mz + 4 * Cov_xy * Mz - Mxz * Mxz - Myz * Myz - Mz
                * Mz * Mz;
        double A0 = Mxz * Mxz * Myy + Myz * Myz * Mxx - Mzz * Cov_xy - 2 * Mxz
                * Myz * Mxy + Mz * Mz * Cov_xy;
        double A22 = A2 + A2;
        double A33 = A3 + A3 + A3;

        double xnew = 0;
        double ynew = 1e+20;
        double epsilon = 1e-12;
        double iterMax = 20;

        for (int iter = 0; iter < iterMax; iter++) {
            double yold = ynew;
            ynew = A0 + xnew * (A1 + xnew * (A2 + xnew * A3));
            if (Math.abs(ynew) > Math.abs(yold)) {
                xnew = 0;
                break;
            }
            double Dy = A1 + xnew * (A22 + xnew * A33);
            double xold = xnew;
            xnew = xold - ynew / Dy;
            if (Math.abs((xnew - xold) / xnew) < epsilon) {
                break;
            }
            if (iter >= iterMax) {
                xnew = 0;
            }
            if (xnew < 0.) {
                xnew = 0;
            }
        }
        double det = xnew * xnew - xnew * Mz + Cov_xy;
        double x = (Mxz * (Myy - xnew) - Myz * Mxy) / (det * 2);
        double y = (Myz * (Mxx - xnew) - Mxz * Mxy) / (det * 2);
        OffsetCircle result = new OffsetCircle();
        result.mCenterOffset = new Vector(x + centroid.X(),y + centroid.Y());
        result.mRadius = Math.sqrt(x * x + y * y + Mz);
        return(result);
    }
}
