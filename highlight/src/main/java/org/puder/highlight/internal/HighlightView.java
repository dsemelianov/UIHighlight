package org.puder.highlight.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.puder.highlight.R;

public class HighlightView extends RelativeLayout {

    private final float      innerRadiusScaleMultiplier = 1.2f;
    private final int        highlightPadding = 50;

    private Paint            eraserPaint;
    private Paint            basicPaint;

    private HighlightItem    item;


    public HighlightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        basicPaint = new Paint();
        eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFF);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
    }

    public void setHighlightItem(HighlightItem item, int screenHeight) {
        this.item = item;
        if (item.titleId != -1) {
            TextView title = (TextView) findViewById(R.id.highlight_title);
            title.setText(item.titleId);
        }
        if (item.descriptionId != -1) {
            TextView descr = (TextView) findViewById(R.id.highlight_description);
            descr.setText(item.descriptionId);
        }

        int cy = item.screenTop + (item.screenBottom - item.screenTop) / 2;

        Button okButton = (Button) findViewById(R.id.highlight_button);

        //if the UI element's y value is near the middle of the screen, put the highlight on the button
        if ((cy > (screenHeight/2) - 500) && (cy < (screenHeight/2) + 500)) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) okButton.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lp.removeRule(RelativeLayout.BELOW);
                okButton.setLayoutParams(lp);

                TextView descr = (TextView) findViewById(R.id.highlight_description);
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) descr.getLayoutParams();
            lp2.addRule(RelativeLayout.ABOVE, R.id.highlight_button);
                descr.setLayoutParams(lp2);
        }

        invalidate();
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        // Delegate the click listener to the entire layout and the button
        findViewById(R.id.highlight_hitbox).setOnClickListener(listener);
        findViewById(R.id.highlight_button).setOnClickListener(listener);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        int width = item.screenRight - item.screenLeft;
        int height = item.screenBottom - item.screenTop;
        int cx = item.screenLeft + width / 2 - location[0];
        int cy = item.screenTop + height / 2 - location[1];
        float radius = width > height ? width / 2 : height / 2;
        Bitmap overlay = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas overlayCanvas = new Canvas(overlay);
        overlayCanvas.drawColor(0xcc000000);
        eraserPaint.setAlpha(0);
        if (width == height) {
            overlayCanvas.drawCircle(cx, cy, radius * innerRadiusScaleMultiplier, eraserPaint);
        } else {
            int left = item.screenLeft - highlightPadding;
            int right = item.screenRight + highlightPadding;
            int top = item.screenTop - 100 - highlightPadding;
            int bottom = item.screenBottom - 60 + highlightPadding;
            overlayCanvas.drawOval(left, top, right, bottom, eraserPaint);
        }
        canvas.drawBitmap(overlay, 0, 0, basicPaint);
        super.dispatchDraw(canvas);
    }
}
