package com.example.paint2;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {
    private Paint paint;
    private Path path;
    private List<Path> paths;
    private List<Integer> colors;
    private List<Float> strokeSizes;
    private List<Path> undonePaths;
    private int currentColor;
    private float strokeWidth;

    private boolean hasBackground;
    private boolean isEraserMode;
    private int eraserSize;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        strokeWidth = 5f; // Default brush size

        path = new Path();
        paths = new ArrayList<>();
        colors = new ArrayList<>();
        strokeSizes = new ArrayList<>();
        undonePaths = new ArrayList<>();
        currentColor = Color.BLACK;

        isEraserMode = false;
        eraserSize = 30;


    }

    public void setEraserMode(boolean isEraserMode) {
        this.isEraserMode = isEraserMode;

        if (isEraserMode) {
            paint.setColor(Color.WHITE); // Set eraser color to white
            paint.setStrokeWidth(eraserSize); // Set eraser size
        } else {
            paint.setColor(Color.BLACK); // Reset to default color
            paint.setStrokeWidth(10f); // Reset to default stroke width
        }
    }

    public void setEraserSize(int size) {
        eraserSize = size;

        if (isEraserMode) {
            paint.setStrokeWidth(eraserSize);
        }
    }





    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < paths.size(); i++) {
            paint.setColor(colors.get(i));
            paint.setStrokeWidth(strokeSizes.get(i));
            canvas.drawPath(paths.get(i), paint);
        }
        paint.setColor(currentColor);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    private void touchStart(float x, float y) {
        path = new Path();
        path.moveTo(x, y);
        colors.add(currentColor);
        strokeSizes.add(strokeWidth);
    }

    private void touchMove(float x, float y) {
        path.lineTo(x, y);
    }

    private void touchUp() {
        paths.add(path);
        path = new Path();
        undonePaths.clear();
    }

    public void undo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            colors.remove(colors.size() - 1);
            strokeSizes.remove(strokeSizes.size() - 1);
            invalidate();
        }
    }

    public void redo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            colors.add(currentColor);
            strokeSizes.add(strokeWidth);
            invalidate();
        }
    }

    public void clear() {
        paths.clear();
        colors.clear();
        strokeSizes.clear();
        undonePaths.clear();
        invalidate();
    }

    public void setColor(int color) {
        currentColor = color;
    }



    public float getStrokeWidth() {
        return strokeWidth;
    }
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
