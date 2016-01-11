package com.levemus.gliderhud.FlightDisplay.Recon.Components;

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
import android.view.View;
import android.widget.ImageView;

import com.levemus.gliderhud.Types.Point;

/**
 * Created by mark@levemus on 15-11-30.
 */

public class DirectionDisplayImage extends DirectionDisplay {

    protected ImageView mImageView = null;

    Point location = new Point(-1,-1);
    public DirectionDisplayImage(final ImageView image)
    {
        mImageView = image;
    }

    public void init(Activity activity) {
        super.init(activity);
    }

    public void deInit(Activity activity) {
        mImageView = null;
        super.deInit(activity);
    }

    @Override
    public void display(Activity activity)
    {
        activity.runOnUiThread(new Runnable() {
            public void run() {

                double angle = (mImageOffset != null ? mImageOffset.getDirectionOffset(mCurrentDirection) : mCurrentDirection);
                double locationX = location.X();
                double screenLocationX = -(getScreenLocation(angle).X());
                if (locationX == screenLocationX)
                    return;
                Matrix matrix = new Matrix();
                matrix.reset();
                matrix.postTranslate(-428, 0);

                mImageView.setVisibility(View.VISIBLE);
                mImageView.setScaleType(ImageView.ScaleType.MATRIX);
                mImageView.setImageMatrix(matrix);
                location = new Point(screenLocationX, 0);
                mImageView.getImageMatrix().reset();
                mImageView.getImageMatrix().postTranslate((int) location.X(), 0);
                mImageView.postInvalidate();
            }
        });
    }
}
