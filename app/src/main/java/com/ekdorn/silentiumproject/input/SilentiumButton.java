package com.ekdorn.silentiumproject.input;

/**
 * Created by User on 20.03.2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.MorseListener;
import com.ekdorn.silentiumproject.R;

import java.util.Date;

public class SilentiumButton extends Fragment {

    Context context;
    MorseListener ML;

    Bitmap bitmap;

    int FragmentCount;

    GraphicsView view;
    static int pointMeasure;
    static final int REDRAW_OVERALL_TIME = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        try {
            bitmap = getBitmapFromDrawable(context, R.drawable.resource_biggest_logo);
        } catch (NullPointerException npe) {
            npe.fillInStackTrace();
        }

        pointMeasure = 0;

        ML = new MorseListener(getContext()) {
            @Override
            public void Sender(Message message) {
                if (getActivity().getSupportFragmentManager().getFragments().size() == 1) {
                    FragmentManager manager = getFragmentManager();
                    SelectionDialog dialog = SelectionDialog.newInstance(message.toAnotherString(getContext()));
                    dialog.show(manager, "LOL");
                } else if (getActivity().getSupportFragmentManager().getFragments().get(1) == null) {
                    FragmentManager manager = getFragmentManager();
                    SelectionDialog dialog = SelectionDialog.newInstance(message.toAnotherString(getContext()));
                    dialog.show(manager, "LOL");
                } else {
                    Log.e("TAG", "Sender: Fragment closed, enter SHUT DOWN");
                }
            }

            @Override
            public void Clicker1() {
                pointMeasure = REDRAW_OVERALL_TIME;
                Log.e("TAG", "onDraw: Added");
                view.invalidate();
            }

            @Override
            public void Clicker2() {
                pointMeasure = 0;
                Log.e("TAG", "onDraw: Impressed");
            }
        };

        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        Log.e("TAG", "onStart: ");
        FragmentCount = getActivity().getSupportFragmentManager().getFragments().size();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = new GraphicsView(getActivity());
        return view;
    }

    public class GraphicsView extends View {
        float rad;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public GraphicsView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setAntiAlias(true);
            if (pointMeasure == 0) {
                rad = ((canvas.getWidth() < canvas.getHeight()) ? (canvas.getWidth() / 5 * 2) : (canvas.getHeight() / 5 * 2));
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) rad*2, (int) rad*2, false), canvas.getWidth()/2 - rad, canvas.getHeight()/2 - rad, paint);
            } else {
                if ((rad < (canvas.getWidth() / 40 * 17)) && (rad < (canvas.getHeight() / 40 * 17))) {
                    Log.e("TAG", "onDraw: Invalidated " + new Date().getTime());
                    rad += pointMeasure;
                }
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) rad * 2, (int) rad * 2, false), canvas.getWidth() / 2 - rad, canvas.getHeight() / 2 - rad, paint);
                invalidate();
            }
        }

        @Override
        public boolean onTouchEvent (MotionEvent event) {
            if (Math.sqrt((getWidth()/2 - event.getX()) * (getWidth()/2 - event.getX()) + (getHeight()/2 - event.getY()) * (getHeight()/2 - event.getY())) < rad) {
                return ML.deMorser(event);
            } else {
                return false;
            }
        }
    }

    @Override
    public void onDestroy() {
        bitmap.recycle();
        super.onDestroy();
    }

    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            try {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);

                return bitmap;
            } catch (Exception e) {
                return null;
            }
        }
    }
}

            /*<item name="ic_menu_camera" type="drawable">@android:drawable/ic_menu_camera</item>
    <item name="ic_menu_gallery" type="drawable">@android:drawable/ic_menu_gallery</item>
    <item name="ic_menu_slideshow" type="drawable">@android:drawable/ic_menu_slideshow</item>
    <item name="ic_menu_manage" type="drawable">@android:drawable/ic_menu_manage</item>
    <item name="ic_menu_share" type="drawable">@android:drawable/ic_menu_share</item>
    <item name="ic_menu_send" type="drawable">@android:drawable/ic_menu_send</item>*/