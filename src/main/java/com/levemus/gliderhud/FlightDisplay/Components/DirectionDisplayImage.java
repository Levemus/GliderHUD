package com.levemus.gliderhud.FlightDisplay.Components;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.app.Activity;
import android.graphics.Matrix;
import android.widget.ImageView;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.Types.Point;

/**
 * Created by markcarter on 15-11-30.
 */
public class DirectionDisplayImage extends DirectionDisplay {

    protected ImageView mImageView = null;

    protected DirectionDisplayImage() {}
    public void init(Activity activity, IFlightDataBroadcaster broadcaster) {}

    Point location = new Point(-1,-1);
    public DirectionDisplayImage(ImageView image)
    {
        mImageView = image;
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postTranslate(-428, 0);

        mImageView.setScaleType(ImageView.ScaleType.MATRIX);
        mImageView.setImageMatrix(matrix);
    }

    @Override
    public void init(Activity activity) {}

    @Override
    public void display()
    {
        double angle = (mImageOffset != null ? mImageOffset.determineOffset(mCurrentDirection) : mCurrentDirection);
        if(location.X() == -(getScreenLocation(angle).X()))
            return;
        location = new Point(-(getScreenLocation(angle).X()), 0);
        mImageView.getImageMatrix().reset();
        mImageView.getImageMatrix().postTranslate((int)location.X(), 0);
        mImageView.postInvalidate();
    }
}
