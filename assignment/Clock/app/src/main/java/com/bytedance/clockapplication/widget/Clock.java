package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;
    private static final float DEFAULT_HOUR_MINUTE_NEDDLE_WIDTH = 0.015f;
    private static final float DEFAULT_SECOND_NEDDLE_WIDTH = 0.005f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private static final float ANGLE_HALF_PI = (float) (3.14159 / 2);

    private static final int FONT_SIZE = 80;


    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {
        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }

    }

    /**
     * 画Analog表盘刻度
     *
     * @param canvas
     */
    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        //秒
        for (int i = 0; i < FULL_ANGLE; i += 6/* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0) {
                paint.setAlpha(CUSTOM_ALPHA);
            } else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * 画digit表盘
     *
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        //获取时间
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);
        paint.setTextSize(FONT_SIZE);

        int rPadded = mCenterX - (int) (mWidth * 0.15f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 30/* Step */) {

            paint.setAlpha(FULL_ALPHA);

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i) + ANGLE_HALF_PI));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i) + ANGLE_HALF_PI));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i) + ANGLE_HALF_PI));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i) + ANGLE_HALF_PI));

            Path path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);

            //旋转数字使得12点在上
            canvas.drawTextOnPath((FULL_ANGLE - i) / 30 + "", path, 0, FONT_SIZE / 2, paint);
        }


    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor
        Path hourNeddle = new Path();
        Path minuteNeddle = new Path();
        Path secondNeddle = new Path();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        paint.setColor(hoursNeedleColor);

        //获取时间
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        //画时针
        int stopX = (int) (mCenterX + Math.cos(Math.toRadians(hour * 30 - 90 + (minute * 6) / 12)) * 150);
        int stopY = (int) (mCenterY + Math.sin(Math.toRadians(hour * 30 - 90 + (minute * 6) / 12)) * 150);
        hourNeddle.moveTo(mCenterX, mCenterY);
        hourNeddle.lineTo(stopX, stopY);
        paint.setStrokeWidth(mWidth * DEFAULT_HOUR_MINUTE_NEDDLE_WIDTH);
        canvas.drawPath(hourNeddle, paint);

        //画分针
        paint.setColor(minutesNeedleColor);
        int minuteStopX = mCenterX + (int) (Math.cos(Math.toRadians(minute * 6 - 90)) * 300);
        int minuteStopY = mCenterX + (int) (Math.sin(Math.toRadians(minute * 6 - 90)) * 300);
        minuteNeddle.moveTo(mCenterX, mCenterY);
        minuteNeddle.lineTo(minuteStopX, minuteStopY);
        canvas.drawPath(minuteNeddle, paint);

        //画秒针
        paint.setColor(secondsNeedleColor);
        int secondStopX = mCenterX + (int) (Math.cos(Math.toRadians(second * 6 - 90)) * 200);
        int secondStopY = mCenterY + (int) (Math.sin(Math.toRadians(second * 6 - 90)) * 200);
        secondNeddle.moveTo(mCenterX, mCenterY);
        secondNeddle.lineTo(secondStopX, secondStopY);
        paint.setStrokeWidth(mWidth * DEFAULT_SECOND_NEDDLE_WIDTH);
        canvas.drawPath(secondNeddle, paint);


    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint CenterDot = new Paint();
        CenterDot.setStyle(Paint.Style.FILL_AND_STROKE);
        CenterDot.setStrokeCap(Paint.Cap.ROUND);
        CenterDot.setColor(hoursNeedleColor);

        canvas.drawCircle(mCenterX, mCenterY, 40, CenterDot);

    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        ////Invalidate the whole view. If the view is visible
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }


}