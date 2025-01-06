package dam.pmdm.spyrothedragon.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class SpyroFireView extends View {

    private Paint firePaint;
    private Path firePath;
    private float fireOffset;
    private ValueAnimator fireAnimator;

    public SpyroFireView(Context context) {
        super(context);
        init();
    }

    public SpyroFireView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        firePaint = new Paint();
        firePaint.setStyle(Paint.Style.FILL);
        firePath = new Path();
        fireOffset = 0f;

        fireAnimator = ValueAnimator.ofFloat(0f, 1f);
        fireAnimator.setDuration(1000);
        fireAnimator.setRepeatCount(ValueAnimator.INFINITE);
        fireAnimator.setInterpolator(new LinearInterpolator());
        fireAnimator.addUpdateListener(animation -> {
            fireOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Crear el degradado aquí, después de que la vista tenga dimensiones
        createFireGradient();
    }

    private void createFireGradient() {
        int width = getWidth();
        int height = getHeight();
        LinearGradient gradient = new LinearGradient(
                width / 2f, height,
                width / 2f, height * 0.25f,
                new int[]{Color.YELLOW, Color.rgb(255, 165, 0)},
                null,
                Shader.TileMode.CLAMP
        );
        firePaint.setShader(gradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        firePath.reset();
        firePath.moveTo(width / 2f, height);
        firePath.cubicTo(width / 4f, height * 0.75f, width / 4f, height * 0.5f, width / 2f, height * 0.25f);
        firePath.cubicTo(width * 0.75f, height * 0.5f, width * 0.75f, height * 0.75f, width / 2f, height);

        canvas.drawPath(firePath, firePaint);
    }

    public void startAnimation() {
        fireAnimator.start();
    }

    public void stopAnimation() {
        fireAnimator.cancel();
    }
}