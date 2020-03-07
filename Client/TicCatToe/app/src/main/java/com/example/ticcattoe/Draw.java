package com.example.ticcattoe;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import static android.graphics.Paint.Style.STROKE;
import static com.example.ticcattoe.Constants.CIRCLE;
import static com.example.ticcattoe.Constants.DRAFT;
import static com.example.ticcattoe.Constants.ENEMY;
import static com.example.ticcattoe.Constants.ME;
import static com.example.ticcattoe.Constants.NONE;
import static com.example.ticcattoe.Constants.X;

public class Draw {
    private boolean isDrawing, done;
    public static final int BRUSH_SIZE_IN_DP = 24;
    public static final int SHAPE_SIZE_IN_DP = 200;
    public static final int LINE_SIZE_IN_DP = 16;
    public static final int HORIZONTAL_LINE_MARGIN_IN_DP = 16;
    public static final int DRAW_ANIMATION_SPEED = 500;
    public static final int AI_TURN_DELAY = 300;
    private int horizontalLineMargin;
    private int shapeSizeInPixels;

    private int MY_SHAPE = CIRCLE;
    private int AI_SHAPE = Constants.X;

    private Shape shape;
    private float v;

    private int width, height;
    private int topLeft;
    private int lineSize;
    private Paint line;
    private Point currentPoint = new Point();

    private int currentAnimationShape;

    private int[][] field = new int[3][3];

    private Rect zeroZero, zeroOne, zeroTwo, oneZero, oneOne, oneTwo, twoZero, twoOne, twoTwo;

    public Draw(Context context) {
        lineSize = (int) (getResources().getDisplayMetrics().density * LINE_SIZE_IN_DP);
        horizontalLineMargin = (int) (getResources().getDisplayMetrics().density * HORIZONTAL_LINE_MARGIN_IN_DP);
        shape = new Shape(context);
    }

}
