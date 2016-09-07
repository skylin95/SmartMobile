package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;

/**
 * Created by eunji on 2016/8/23.
 */
public class AnimationToast {
    static final String TAG = "AnimationToast";
    /**
     * Show the view or text notification for a short period of time.  This time
     * could be user-definable.  This is the default.
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = 0;

    /**
     * Show the view or text notification for a long period of time.  This time
     * could be user-definable.
     * @see #setDuration
     */
    public static final int LENGTH_LONG = 1;

    final Context mContext;
    int mDuration;
    PopupWindow mPopToast;
    View mParent;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;//toast初始宽度
    int height = ViewGroup.LayoutParams.WRAP_CONTENT;//toast初始高度
    int gravity = Gravity.TOP;
    public AnimationToast(Context context)
    {
        mContext = context;
    }

    public void show()
    {
        mPopToast.update();
        mPopToast.showAtLocation(mParent,gravity, 0,0);

        //LONG→2000ms    SHORT→1000ms
        long duration = mDuration== LENGTH_LONG ? 2000 : 1000;

        mParent.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                cancel();
            }
        }, duration);
    }

    public void cancel()
    {
        mPopToast.dismiss();
    }

    /**
     * Set the view to show.
     * @see #getView
     */
    public void setView(View view)
    {
        mPopToast = new PopupWindow(view, width, height);
        mPopToast.setAnimationStyle(R.style.AnimationToast);
    }

    /**
     * Return the view.
     * @see #setView
     */
    public View getView()
    {
        return mPopToast.getContentView();
    }

    public void setParent(View parent)
    {
        mParent = parent;
    }

    public View getParent()
    {
        return mParent;
    }

    /**
     * Set how long to show the view for.
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     */
    public void setDuration(int duration)
    {
        mDuration = duration;
    }

    /**
     * Return the duration.
     * @see #setDuration
     */
    public int getDuration()
    {
        return mDuration;
    }

    public void setGravity(int gravity){
        this.gravity = gravity;
    }
    public void setWidth(int w)
    {
        width = w;
    }

    public void setHeight(int h)
    {
        height =h;
    }

    /**
     * Make a standard toast that just contains a text view.
     *
     * @param context  The context to use.  Usually your {@link android.app.Application}
     *                 or {@link android.app.Activity} object.
     * @param text     The text to show.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or
     *                 {@link #LENGTH_LONG}
     * @param parent AnimationToast use a PopupWindow, so need a parent.
     *                  suggestion → using activity.getWindow().getDecorView()
     *
     */
    public static AnimationToast makeText(Context context, CharSequence text,boolean isTrue,int duration, View parent,int gravity)
    {
        AnimationToast result = new AnimationToast(context);
        View tosatView;
        result.setGravity(gravity);
        tosatView  = LayoutInflater.from(context).inflate(R.layout.com_toast,
                    null);
        TextView tv = (TextView) tosatView.findViewById(R.id.tv_tip);
        ImageView iv = (ImageView) tosatView.findViewById(R.id.iv_state);
        tv.setText(text);
        result.setView(tosatView);
        result.setParent(parent);
        result.setDuration(duration);
        return result;
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param context  The context to use.  Usually your {@link android.app.Application}
     *                 or {@link android.app.Activity} object.
     * @param resId    The resource id of the string resource to use.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or
     *                 {@link #LENGTH_LONG}
     * @param parent AnimationToast use a PopupWindow, so need a parent.
     *                 suggestion → using activity.getWindow().getDecorView()
     *
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    public static AnimationToast makeText(Context context, int resId, boolean isTrue, int duration, View parent,int gravity)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId),isTrue, duration, parent,gravity);
    }
}
