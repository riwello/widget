package com.example.widget.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.widget.R;

/**
 * author   : 肖波
 * e-mail   : xiaoboabc168@163.com
 * date     :  2019/1/3.
 */

public class CalibrationView extends View {
    private int bgColor = Color.parseColor("#ECEFF4"); //背景颜色
    //渐变颜色
    private int[] gradientColors = new int[]{
        Color.parseColor("#3fe0d0"),
        Color.parseColor("#3b3b43"),
        Color.parseColor("#505a78"),
        Color.parseColor("#ff676b"),
        Color.RED
    };
    private int progress = 0;//在动画中接收实时的进度，在ondraw中用
    private int showProGress = 50;//要显示的进度
    private boolean isFromZero = false;//设置是否每次都从0开始计算
    private boolean shouldShowAnim=true;

    private int maxProgress = 100;//默认总进度为100
    private int pointerColor = Color.parseColor("#505a78");//指针颜色
    private int calibCircleColor=Color.WHITE;

    private Paint p_bc;//画背景的画笔
    private Paint p_real;//画实际颜色的画笔
    private Paint p_pointer;//画指针的画笔
    private Paint p_circle;//画小圆点的画笔

    private int DEF_VIEW_SIZE = 300;//默认view大小

    private int w_r = 300, n_r = 50, p_r = 10, c_r = 8;

    private int centerX = 500;//中心点x
    private int centerY = 500;//中心点y

    private int arcWidth = 200;//弧形的宽度，通过paint的setStrokeWidth来设置
    private int animDuration=2000;//默认整个进度画完需要执行动画的时间

    public void setCalibCircleColor(int calibCircleColor) {
        this.calibCircleColor = calibCircleColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    public void setShouldShowAnim(boolean shouldShowAnim) {
        this.shouldShowAnim = shouldShowAnim;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setPointerColor(int pointerColor) {
        this.pointerColor = pointerColor;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

    public void setFromZero(boolean fromZero) {
        isFromZero = fromZero;
    }

    public void setShowProGress(int showProGress) {
        int v1 = this.showProGress;
        this.showProGress = showProGress;

        if (shouldShowAnim){
            if (isFromZero) {
                StartAnim(0, showProGress);
            } else
                StartAnim(v1, showProGress);
        }else {
            progress=showProGress;
            invalidate();
        }



    }

    public CalibrationView(Context context) {
        this(context, null);

    }

    public CalibrationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalibrationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        p_bc = new Paint();
        p_real = new Paint();
        p_pointer = new Paint();
        p_circle = new Paint();

        //根据自定义属性获取相关的属性值，可以不要，根据对应的setter方法进行设置或者不设置都行，都与默认值
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.CalibrationView);
        maxProgress=array.getInt(R.styleable.CalibrationView_MaxProgress,100);
        showProGress=array.getInt(R.styleable.CalibrationView_DefaultShow,0);
        isFromZero=array.getBoolean(R.styleable.CalibrationView_ShowFromZero,false);
        shouldShowAnim=array.getBoolean(R.styleable.CalibrationView_ShowAnim,true);
        animDuration=array.getInt(R.styleable.CalibrationView_AnimDuration,2000);

        pointerColor=array.getColor(R.styleable.CalibrationView_PointerColor,Color.parseColor("#505a78"));
        bgColor=array.getColor(R.styleable.CalibrationView_CalibBgColor,Color.parseColor("#ECEFF4"));
        calibCircleColor=array.getColor(R.styleable.CalibrationView_CalibCircleColor,Color.WHITE);
        //根据自定义属性获取相关的属性值，可以不要，根据对应的setter方法进行设置或者不设置都行，都与默认值

        p_bc.setColor(bgColor);
        p_bc.setStyle(Paint.Style.STROKE);
        p_bc.setAntiAlias(true);//去噪

        p_pointer.setColor(pointerColor);
        p_pointer.setStyle(Paint.Style.STROKE);
        p_pointer.setAntiAlias(true);//去噪

        p_circle.setColor(calibCircleColor);
        p_circle.setStyle(Paint.Style.STROKE);
        p_circle.setStrokeWidth(p_r);
        p_circle.setAntiAlias(true);//去噪
        StartAnim(0, showProGress);
    }

    int viewH = 300;
    int viewW = 300;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewH = h;
        viewW = w;

        centerX = w / 2;//中心点x坐标
        centerY = h;//中心点y坐标
        arcWidth = h / 2;//圆弧宽带

        w_r = h - arcWidth - 20; //外圈半径 = 高度 - 圆弧宽度 - 20（去掉一点，一面刚好顶大view的边上）
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = 0;
        int heightSize = 0;

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_VIEW_SIZE, getResources().getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        } else {
            widthSize = getMeasuredWidth();
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_VIEW_SIZE, getResources().getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else {
            heightSize = getMeasuredHeight();
        }

        if (widthSize != 0 && heightSize != 0 && widthSize / 2 <heightSize) {//画的是上半圆弧，所以需要宽度超过高度的2倍保证画出来的图像不留空隙
            heightSize = widthSize / 2;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.i("TestView","onDraw");
//        RectF rf = new RectF(0, 0, viewW, viewH);
        RectF rf1 = new RectF(centerX - w_r - arcWidth / 2, centerY - w_r - arcWidth / 2, centerX + w_r + arcWidth / 2, centerY - (-w_r - arcWidth / 2));
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        canvas.drawRect(rf1, p);
        drawBg(canvas, rf1);//画背景弧形和圆点
        canvas.save();
        drawRealProgress(canvas, rf1);//画实际进度
        canvas.save();
        drawPointer(canvas, rf1);//画指针

    }

    /**
     * 画指针
     *
     * @param canvas
     * @param rf1
     */
    private void drawPointer(Canvas canvas, RectF rf1) {
        /**
         * 此处同样根据当前的角度计算出对应圆弧上的点，
         * 在圆心出划出指针的粗的部分（一个）小圆
         * 以小圆直径外的俩个点以及计算出的圆弧上的点通过路径画出图形
         * 即为指针
         */
        int mr = 16;//小圆的半径
        int mx = centerX;//小圆的圆心坐标x
        int my = centerY - 16;//小圆的圆心坐标y
        p_pointer.setStrokeWidth(mr);//以圆半径画的圈为中心平分，内8外8
        //画圆心在(mx,my)的小圆
        canvas.drawCircle(mx, my, mr / 2, p_pointer);

        float x = 0.0f;//某个进度值对应的外圆弧上的点的x坐标
        float y = 0.0f;//某个进度值对应的外圆弧上的点的y坐标
        int count = (int) (dgree_total % 90 / 18);
        Point p1 = new Point(mx + mr, my);//初始进度值是0的时候的小圆上的点1
        Point p2 = new Point(mx - mr, my);//初始进度值是0的时候的小圆上的点2
        if (dgree_total > 90) {//某个值对应的角度大于90度的时候，计算对应的x与y的所对应的三角函数不同，所以以90度作为界点分别计算
            float md = 180 - dgree_total;
            x = (float) (centerX + (w_r + arcWidth ) * Math.cos(Math.PI * md / 180.0f));
            y = (float) (centerY - (w_r + arcWidth ) * Math.sin(Math.PI * md / 180.0f));
            p1.x = (int) (mx - mr * Math.cos(Math.PI * (90 - md) / 180.0f));
            p1.y = (int) (my - mr * Math.sin(Math.PI * (90 - md) / 180.0f));
            p2.x = (int) (mx + mr * Math.cos(Math.PI * (90 - md) / 180.0f));
            p2.y = (int) (my + mr * Math.sin(Math.PI * (90 - md) / 180.0f));
        } else {

            x = (float) (centerX - (w_r + arcWidth ) * Math.cos(Math.PI * dgree_total / 180.0f));
            y = (float) (centerY - (w_r + arcWidth ) * Math.sin(Math.PI * dgree_total / 180.0f));
            p1.x = (int) (mx - mr * Math.sin(Math.PI * dgree_total / 180.0f));
            p1.y = (int) (my + mr * Math.cos(Math.PI * dgree_total / 180.0f));
            p2.x = (int) (mx + mr * Math.sin(Math.PI * dgree_total / 180.0f));
            p2.y = (int) (my - mr * Math.cos(Math.PI * dgree_total / 180.0f));

        }

        if (dgree_total == 0) {
            x = mx - w_r - arcWidth / 2;
            y = mx;
            p1.x = mx;
            p1.y = my - mr;
            p2.x = mx;
            p2.y = my + mr;
        }
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(p1.x, p1.y);

        path.lineTo(p2.x, p2.y);
        Paint p = new Paint();
        p.setColor(pointerColor);
        p.setAntiAlias(true);
        p.setStrokeWidth(12);
        p.setStyle(Paint.Style.FILL);
        path.close();
        canvas.drawPath(path, p);
    }

    /**
     * 画刻度点
     *
     * @param rf
     * @param canvas
     */
    private void drawPoint(RectF rf, Canvas canvas) {
        /**
         * 画180度的上弧形作为刻度盘
         * 分成10分，每份18度
         * 刻度点只需要画1-9个，即最边上的两个不需要画
         *
         * 已知圆弧的半径，可以根据知道直角三角形斜边及角C，计算出每个刻度的的位置，然后画点
         *
         * ps  直角三角形已知斜边c和对应角，其他两边分别为c*sin(C)与c*cos(C)
         */
        for (int i = 1; i < 10; i++) {
        int d = i * 18;
        if (d > 90) {
            d = 180 - d;
        }
        float degree = (float) (Math.PI * (d * 1.0f / 180));
        float y = (float) (centerY - ((w_r + arcWidth / 2) * 1.0f * Math.sin(degree)));
        float x = 0.0f;

        if (i < 5) {
            x = (float) (centerX - ((w_r + arcWidth / 2) * 1.0f * Math.cos(degree)));
        } else if (i > 5) {
            x = (float) (centerX + ((w_r + arcWidth / 2) * 1.0f * Math.cos(degree)));
        } else {
            y = centerY - w_r - arcWidth / 2;
            x = centerX;
        }

        p_circle.setStrokeWidth(20);
        canvas.drawCircle(x, y, p_r, p_circle);

//            canvas.drawPoint(x,y,p_circle);
    }
    }


    /**
     * 画背景灰色圆形和点
     *
     * @param canvas
     * @param rf
     */
    private void drawBg(Canvas canvas, RectF rf) {
        p_bc.setStrokeWidth(arcWidth);
        canvas.drawArc(rf, -180, 180, false, p_bc);//从-180度开始画一段180度的弧形，0度是3点钟方向
        drawPoint(rf, canvas);

    }

    float dgree_total = 0.0f;

    /**
     * 画实际刻度
     *
     * @param canvas
     * @param rf
     */
    private void drawRealProgress(Canvas canvas, RectF rf) {
        float startArg = -180f;//记录上次画到的角度，下次从这个角度开始画，以免覆盖了
        /**
         * 根据当前进度值和总进度值计算出当前的总角度，原始设置角度为180
         */
        dgree_total = progress * (180f / maxProgress);//计算出当前刻度值对应应该转动的角度
        int count = (int) (dgree_total / 18);//得到这个角度对应能转过几个点，好根据不同范围分次画不同的颜色
        float last = dgree_total % 18;
        if (last != 0) {
            count += 1;
        }

        p_real.setStrokeWidth(arcWidth);
        p_real.setStyle(Paint.Style.STROKE);
        p_real.setAntiAlias(true);
        for (int i = 0; i < count; i++) {//进行分段画弧
        p_real.setColor(gradientColors[i / 2]);//每两个点范围内的颜色相同，故这样取颜色
        float lastd = 18 * (i + 1) * 1.0f > dgree_total ? (dgree_total - 18 * i * 1.0f) : 18;


        canvas.drawArc(rf, startArg - 0.5f, lastd + 0.5f, false, p_real);//每次往前画一点，有空隙
        startArg += 18;
    }
        drawPoint(rf, canvas);//画完之后刻度点被覆盖了，重新补上
    }
    ValueAnimator animator;

    /**
     * 动画画进度
     * @param v1
     * @param value
     */
    private void StartAnim(int v1, int value) {
        //根据当前所需要画的进度值计算出需要执行动画的时间，以免不管多少进度都执行animDuration 毫秒
        int du = 2000 * (Math.abs(v1 - value)) / maxProgress;


        animator = ValueAnimator.ofInt(v1, value).setDuration(du);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animator.getAnimatedValue();
                dgree_total = 0f;
                invalidate();
            }
        });
        animator.start();
    }
}