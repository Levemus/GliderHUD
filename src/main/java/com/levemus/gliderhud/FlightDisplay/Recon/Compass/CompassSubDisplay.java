package com.levemus.gliderhud.FlightDisplay.Recon.Compass;

import android.app.Fragment;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.Types.Point;
import com.levemus.gliderhud.Utils.Angle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by markcarter on 16-01-22.
 */
public abstract class CompassSubDisplay {

    private final String TAG = this.getClass().getSimpleName();

    public abstract HashSet<UUID> processorIDs();

    public abstract void display(Fragment parent, HashMap<UUID, Object> results);

    protected ImageView mImageView = null;

    private Matrix mMatrix;

    protected void displayImage(Fragment parent, double angle) {
        if(mImageView == null)
            return;

        if(mMatrix == null) {
            mMatrix = new Matrix();
            mMatrix.reset();
            mMatrix.postTranslate(-428, 0);
            mImageView.setScaleType(ImageView.ScaleType.MATRIX);
            mImageView.setImageMatrix(mMatrix);
        }

        mImageView.getImageMatrix().reset();
        angle = (int)Angle.delta(angle, mHeading);
        mImageView.getImageMatrix().postTranslate(-(float)getScreenLocation(angle), 0);
        mImageView.invalidate();
    }

    protected TextView mTextView = null;
    protected void displayText(Fragment parent, double angle, String text) {
        if(mTextView == null)
            return;

        mTextView.setText(text);
        mTextView.setVisibility(View.VISIBLE);
        int color = mTextView.getCurrentTextColor();
        color &= 0x00FFFFFF;
        color |= mAlpha << 24;
        mTextView.setTextColor(color);

        final float densityMultiplier = parent.getActivity().getResources().getDisplayMetrics().density;
        final float scaledPx = mTextView.getTextSize() * densityMultiplier;
        Paint paint = new Paint();
        paint.setTextSize(scaledPx);
        final float size = paint.measureText(mTextView.getText().toString());
        angle = (int)Angle.delta(mHeading, angle);
        mTextView.setX((float) getScreenLocation(angle) - size / 2 + 411);
    }

    protected static final double PIXELS_PER_45_DEGREES = 190.0;
    protected double getScreenLocation(double angle)
    {
        double offset = -200;
        int x = (int) ((angle / 360.0 * (8.0 * PIXELS_PER_45_DEGREES)) + offset);
        return x;
    }

    protected int mAlpha = 0xFF;
    public void setAlpha(int alpha) { mAlpha = alpha; }

    public int getPosition() {
        if(mImageView != null)
            return((int)mImageView.getX());

        if(mTextView != null)
            return((int)mTextView.getX());

        return 0;
    }

    public int getWidth() {
        int width = 0;
        if(mImageView != null)
            width = Math.max(mImageView.getWidth(), width);

        if(mTextView != null)
            width = Math.max(mTextView.getWidth(), width);

        return width;
    }

    public boolean canDisplay(HashMap<UUID, Object> results) {return true;}

    protected double mHeading = 0.0;
    public void setHeading(double heading) { mHeading = heading; }
}
