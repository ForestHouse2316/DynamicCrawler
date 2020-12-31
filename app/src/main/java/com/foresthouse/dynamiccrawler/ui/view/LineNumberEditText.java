package com.foresthouse.dynamiccrawler.ui.view;

import androidx.appcompat.widget.AppCompatEditText;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;


public class LineNumberEditText extends AppCompatEditText {
    private Context ctx;
    private Rect rect;
    private Paint paint;

    public LineNumberEditText(Context ctx) {
        super(ctx);
        Init(ctx);
    }
    public LineNumberEditText(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        Init(ctx);
    }
    public LineNumberEditText(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        Init(ctx);
    }
    private void Init(Context ctx){
        this.ctx = ctx;
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#616161"));
        paint.setTextSize(dip2px(13));
        setHorizontallyScrolling(true); //이 부분 때문에 입력된 내용이 좌우로 길어져도 다음줄로 안넘어감. 생략하면 넘어감
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();
        int num = 1;
        for (int n = 0; n < count; n++) {
            int baseline = getLineBounds(n, null);
            if (n == 0 || getText().charAt(getLayout().getLineStart(n) - 1) == '\n') {
                canvas.drawText(num + "", rect.left | rect.centerY(), baseline, paint);
                num++;
            }
        }
        int pad = String.valueOf(num - 1).length() * 8;
        int pad2 = dip2px(9);
        setPadding(dip2px(pad + 2), pad2, pad2, pad2);
        super.onDraw(canvas);
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }


}
