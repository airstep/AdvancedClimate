package com.tgs.advancedclimate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.util.Calendar;

public class ACWallpaperService extends WallpaperService
{
    public void onCreate()
    {
        super.onCreate();
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    public Engine onCreateEngine()
    {
        return new MyWallpaperEngine();
    }

    class MyWallpaperEngine extends Engine
    {
        private float x,y;

        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private final Rect mRecScreenRectangleFrame;
        private boolean visible = true;
        public Bitmap imgShip, imgBackDay, imgBackNight;

        MyWallpaperEngine()
        {
            imgShip = BitmapFactory.decodeResource(getResources(),R.drawable.airship);
            imgBackDay = BitmapFactory.decodeResource(getResources(),R.drawable.bg_day);
            imgBackNight = BitmapFactory.decodeResource(getResources(),R.drawable.bg_night);

            x = -130;
            y = 250;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            display.getMetrics(displayMetrics);
            mRecScreenRectangleFrame = new Rect(0, 0,  (int) (displayMetrics.widthPixels*2.0), displayMetrics.heightPixels);
        }

        public boolean isNight() {
            Boolean isNight;
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            isNight = hour < 6 || hour > 18;
            return isNight;
        }

        public void onCreate(SurfaceHolder surfaceHolder)
        {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder)
        {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels)
        {
            draw();
        }

        void draw()
        {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try
            {
                c = holder.lockCanvas();
                if (c != null)
                {
                    c.drawColor(Color.BLACK);
                    c.drawBitmap((isNight()) ? imgBackNight : imgBackDay, null, mRecScreenRectangleFrame, new Paint());
                    c.drawBitmap(imgShip, x, y, null);

                    int width = c.getWidth();
                    if(x > width + 100)
                    {
                        x = -230;
                        y = 250;
                    }

                    x += 1;
                    y += Math.sin(1.5*x/(Math.PI*22));

                }
            }
            finally
            {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }

            handler.removeCallbacks(drawRunner);
            if (visible)
            {
                handler.postDelayed(drawRunner, 10);
            }
        }
    }
}