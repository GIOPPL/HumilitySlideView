package com.gioppl.humiditysliderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class SlideView extends View {
    private float width, height;

    private int maxNum, minNum;
    private int divideNum;
    private int colorLineStart, colorLineEnd;
    private int colorMark;
    private int colorText,colorTextSelect;
    private int orientation;
    private int markWidth;

    private int colorButton;
    private Context context;

    private Paint mPaintButton;
    private Paint mPaintLine;
    private Paint mPaintTest;
    private Paint mPaintMark;
    private Paint mPaintText;

    private Path mPathLine;

    private float touchX;
    private float touchY;
    private float originalY;

    private int touchStatus = 0;//0为禁止，1为上滑动，2为下滑动

    private CircleBean btnCircle = new CircleBean(0, 0);

    public SlideView(Context context) {
        super(context);
    }

    public SlideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
    }

    private void drawLinePaths(Canvas canvas) {
        mPathLine = new Path();
        mPathLine.moveTo(btnCircle.x, 0);
        mPathLine.lineTo(btnCircle.x, btnCircle.y - btnCircle.r - btnCircle.r * 2);
        mPathLine.quadTo(btnCircle.x - btnCircle.r * 0.2f, btnCircle.y - btnCircle.r * 1.9f, btnCircle.x - btnCircle.r, btnCircle.y - btnCircle.r * 1.5f);
        mPathLine.quadTo(btnCircle.x - 2 * btnCircle.r, btnCircle.y - btnCircle.r * 0.9f, btnCircle.x - btnCircle.r * 2, btnCircle.y);
        mPathLine.quadTo(btnCircle.x - 2 * btnCircle.r, btnCircle.y + btnCircle.r * 0.9f, btnCircle.x - btnCircle.r, btnCircle.y + btnCircle.r * 1.5f);
        mPathLine.quadTo(btnCircle.x - btnCircle.r * 0.2f, btnCircle.y + btnCircle.r * 1.9f, btnCircle.x, btnCircle.y + btnCircle.r + btnCircle.r * 2);
        mPathLine.lineTo(btnCircle.x, height);
        canvas.drawPath(mPathLine, mPaintLine);
    }

    private void initPaints() {
        mPaintButton = new Paint();
        mPaintButton.setColor(colorButton);
        mPaintButton.setAntiAlias(true);
        mPaintButton.setDither(true);
        mPaintButton.setStyle(Paint.Style.FILL);
        mPaintButton.setStrokeWidth(5);
        mPaintButton.setPathEffect(new CornerPathEffect(10f));

        mPaintLine = new Paint();
        mPaintLine.setColor(colorButton);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setDither(true);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(15);
        mPaintLine.setPathEffect(new CornerPathEffect(10f));
        Shader shader = new LinearGradient(0, 0, btnCircle.x, btnCircle.y, colorLineStart, colorLineEnd, Shader.TileMode.MIRROR);
        mPaintLine.setShader(shader);

        mPaintTest = new Paint();
        mPaintTest.setColor(Color.RED);
        mPaintTest.setAntiAlias(true);
        mPaintTest.setDither(true);
        mPaintTest.setStyle(Paint.Style.STROKE);
        mPaintTest.setStrokeWidth(5);
        mPaintTest.setPathEffect(new CornerPathEffect(30f));

        mPaintMark = new Paint();
        mPaintMark.setColor(colorMark);
        mPaintMark.setAntiAlias(true);
        mPaintMark.setDither(true);
        mPaintMark.setStyle(Paint.Style.STROKE);
        mPaintMark.setStrokeWidth(2);


        mPaintText = new Paint();
        mPaintText.setColor(colorText);
        mPaintText.setAntiAlias(true);
        mPaintText.setDither(true);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setStrokeWidth(5);
        mPaintText.setTextSize(50);
    }

    public SlideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SlideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.save();
//        canvas.rotate(-90);
//        canvas.translate(-getHeight(), 0);

        canvas.drawCircle(btnCircle.x, btnCircle.y, btnCircle.r, mPaintButton);
        drawLinePaths(canvas);
        drawMark(canvas);
        drawText(canvas);

//        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        float resolutionRation = 100;
        int totalTextNum = divideNum;
        float everyTextHeight = height / totalTextNum;//每个十的整数的高度
        float everyMarkHeight = everyTextHeight/10;//每个小的刻度的高度
        for (int i = 0; i <= totalTextNum; i++) {
            if (touchStatus == 0) {
                canvas.drawText(i + "0%", 30, i * everyTextHeight, mPaintText);
            } else if (touchStatus == 1) {//上滑动
                if ((i * everyTextHeight) > btnCircle.y - resolutionRation && (i * everyTextHeight) < btnCircle.y + resolutionRation) {//正常绘制
                    //放大绘制
                    setTextPaintStyle(true);
                    int g= (int) (btnCircle.y/everyMarkHeight);
                    canvas.drawText(g+"%", 30, btnCircle.y, mPaintText);
                } else {
                    //正常绘制
                    setTextPaintStyle(false);
                    canvas.drawText(i + "0%", 30, i * everyTextHeight, mPaintText);
                }
            } else {//下滑动
                if ((i * everyTextHeight) > btnCircle.y - resolutionRation && (i * everyTextHeight) < btnCircle.y + resolutionRation) {//正常绘制
                    //放大绘制
                    setTextPaintStyle(true);
                    int g= (int) (btnCircle.y/everyMarkHeight);
                    canvas.drawText(g+"%", 30, btnCircle.y, mPaintText);
                } else {
                    //正常绘制
                    setTextPaintStyle(false);
                    canvas.drawText(i + "0%", 30, i * everyTextHeight, mPaintText);
                }
            }

        }
    }
    private void drawMark(Canvas canvas) {
        canvas.
        int markX = 100;//刻度和圆心的x距离
        int totalMarkNum = divideNum*10;//刻度总个数
        float everyMarkHeight = height / totalMarkNum;//每个的高度
        int a = 0;
        for (int i = 0; i < totalMarkNum; i++, a++) {
            PathMeasure pathMeasure = new PathMeasure(mPathLine, false);
            float[] pos = new float[2];
            float[] tan = new float[2];
            pathMeasure.getPosTan(pathMeasure.getLength() / totalMarkNum * i, pos, tan);
            float x = pos[0];
            if (a != 5 && a != 10) {//一般的刻度
                canvas.drawLine(x - markX, i * everyMarkHeight, x - markX + markWidth, i * everyMarkHeight, mPaintMark);
            } else if (a == 5) {
                canvas.drawLine(x - markX, i * everyMarkHeight, x - markX + markWidth, i * everyMarkHeight, mPaintMark);
            } else {//=10
                canvas.drawLine(x - markX - 50, i * everyMarkHeight, x - markX + markWidth, i * everyMarkHeight, mPaintMark);
                a = 0;
            }
        }
    }
    private void setTextPaintStyle(boolean isEnlarge){
        if (isEnlarge){
            mPaintText.setColor(colorTextSelect);
            mPaintText.setTextSize(80);
        }else {
            mPaintText.setColor(colorText);
            mPaintText.setTextSize(50);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        if ((touchY - originalY) > 0) {
            touchStatus = 2;//上滑动
        } else {
            touchStatus = 1;//下滑动
        }
        originalY = touchY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                btnCircle.y = touchY;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideView);
        maxNum = typedArray.getInt(R.styleable.SlideView_maxNum, 100);
        minNum = typedArray.getInt(R.styleable.SlideView_minNum, 0);
        divideNum = typedArray.getInt(R.styleable.SlideView_divideNum, 8);
        colorLineStart = typedArray.getColor(R.styleable.SlideView_lineStartColor, Color.WHITE);
        colorLineEnd = typedArray.getColor(R.styleable.SlideView_lineEndColor, Color.WHITE);
        colorButton = typedArray.getColor(R.styleable.SlideView_buttonColor, Color.WHITE);
        btnCircle.r = typedArray.getInt(R.styleable.SlideView_circleR, 100);
        markWidth = typedArray.getInt(R.styleable.SlideView_markWidth, 50);
        colorMark = typedArray.getColor(R.styleable.SlideView_markColor, Color.WHITE);
        colorText = typedArray.getColor(R.styleable.SlideView_textColor, Color.WHITE);
        colorTextSelect = typedArray.getColor(R.styleable.SlideView_colorTextSelect, Color.BLUE);
        orientation = 1;
        typedArray.recycle();

    }

    public interface ScrollCallBack {
        void scrollBack(int num);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right;
        height = bottom;
        btnCircle.x = (left + right) / 2.0f;
        btnCircle.y = (top + bottom) / 2.0f;
        initPaints();
        touchX = width / 2;
        touchY = 0;
        originalY = 0;
    }

    public static class CircleBean {
        float x, y, r;

        public CircleBean(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private void log(String msg) {
        Log.e("HELLO", msg);
    }
}
