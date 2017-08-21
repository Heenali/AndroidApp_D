package trukkeruae.trukkertech.com.trukkeruae.CustomClasses;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import trukkeruae.trukkertech.com.trukkeruae.R;

/**
 * Custom slider view aka "Slide to unlock".
 *
 * @author Vitali Vasilioglo
 */
public class SlideToUnlock extends RelativeLayout {

    private Drawable track;
    private View background;

    public interface OnUnlockListener {

        /**
         * Called when unlock event occurred.
         */
        void onUnlock();
    }

    public interface OnLockListener {

        /**
         * Called when lock event occurred.
         */
        void onLock();
    }

    private OnUnlockListener listener;
    private OnLockListener listener2;
    private SeekBar seekbar;
    //private TextView label;
    private int thumbWidth;

    public SlideToUnlock(Context context) {
        super(context);
        init(context, null);
    }

    public SlideToUnlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlideToUnlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setOnUnlockListener(OnUnlockListener listener) {
        this.listener = listener;
    }

    public void setOnLockListener(OnLockListener listener2) {
        this.listener2 = listener2;
    }

    /**
     * Resets slider to initial state.
     */
    public void reset() {
        seekbar.setProgress(0);
    }

    private void init(final Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.slidetounlock_lt, this, true);

        // label = (TextView) findViewById(R.id.slider_label);
        seekbar = (SeekBar) findViewById(R.id.slider_seekbar);
        seekbar.setProgress(50);
        background = findViewById(R.id.slider_bg);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SlideToUnlockView);
        String text = attributes.getString(R.styleable.SlideToUnlockView_text1);
        Drawable thumb = attributes.getDrawable(R.styleable.SlideToUnlockView_thumb1);
        if (thumb == null) {
            thumb = getResources().getDrawable(R.drawable.truck_orange);
        }

        track = attributes.getDrawable(R.styleable.SlideToUnlockView_track1);
        attributes.recycle();

        thumbWidth = thumb.getIntrinsicWidth();//194
        if (track != null) {
            background.setBackgroundDrawable(track);
        }

       /* if (text != null) {
            label.setText(text);
        }
        label.setPadding(thumbWidth, 0, 0, 0);*/

        int defaultOffset = seekbar.getThumbOffset();
        seekbar.setThumb(thumb);
        seekbar.setThumbOffset(defaultOffset);

      /*  int defaultOffset2 = slider_cancel.getThumbOffset();
        slider_cancel.setThumb(thumb2);
        slider_cancel.setThumbOffset(defaultOffset2);
*/
        seekbar.setOnTouchListener(new OnTouchListener() {
            private boolean isInvalidMove;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //  return isInvalidMove = motionEvent.getX() > thumbWidth;
                    case MotionEvent.ACTION_MOVE:
                        return isInvalidMove;
                    case MotionEvent.ACTION_UP:
                        return isInvalidMove;
                }
                return false;
            }
        });
     /*   slider_cancel.setOnTouchListener(new OnTouchListener() {
            private boolean isInvalidMove;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return isInvalidMove = motionEvent.getX() > thumbWidth;
                    case MotionEvent.ACTION_MOVE:
                        return isInvalidMove;
                    case MotionEvent.ACTION_UP:
                        return isInvalidMove;
                }
                return false;
            }
        });*/

        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                // label.setAlpha(1f - progress * 0.02f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar.getProgress() > 50) {
                    if (seekBar.getProgress() < 95) {
                        ObjectAnimator anim = ObjectAnimator.ofInt(seekBar, "progress", 50);
                        anim.setInterpolator(new AccelerateDecelerateInterpolator());
                        anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                        anim.start();
                    } else {
                        if (listener != null) {
                            listener.onUnlock();
                        }
                    }
                }
                if (seekBar.getProgress() < 50) {
                    if (seekBar.getProgress() > 5) {
                        ObjectAnimator anim = ObjectAnimator.ofInt(seekBar, "progress", 50);
                        anim.setInterpolator(new AccelerateDecelerateInterpolator());
                        anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                        anim.start();
                    } else {
                      //  Toast.makeText(context,"reject",Toast.LENGTH_SHORT).show();
                        if (listener2 != null) {
                            listener2.onLock();
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInEditMode()) {
            return;
        }

        //prevents 9-patch background image from full size stretching
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            background.getLayoutParams().height = seekbar.getHeight() + fromDpToPx(3);
        }

    }

    public int fromDpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
