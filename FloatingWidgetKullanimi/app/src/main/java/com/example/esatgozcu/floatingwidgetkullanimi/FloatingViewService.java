package com.example.esatgozcu.floatingwidgetkullanimi;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatingViewService extends Service{

    private WindowManager mWindowManager;
    private View mFloatingView;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //LayoutParams layoutların özelliklerini (en,boy,yazının konumu vb.) kod üzerinde değişiklik yapma imkanı sağlar
        //Ekrana, view elemetini eklemek için gerekli ayarları yapıyoruz
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //Widget başlangıçta nerede gözükmesini istiyorsak ayarını yapıyoruz
        //Widget sol üst köşede başlayacak
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        //Ekrana, layout icinde barınan view elementini ekliyoruz.
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);

        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);


        //Kapatma butonunu tanımlıyoruz
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        //İşlevini tanımlıyoruz
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopSelf();
            }
        });

        //Music player daki play  butonunu tanımlıyoruz
        ImageView playButton = (ImageView) mFloatingView.findViewById(R.id.play_btn);
        //İşlevini tanımlıyoruz
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing the song.", Toast.LENGTH_LONG).show();
            }
        });

        //Music player daki next  butonunu tanımlıyoruz
        ImageView nextButton = (ImageView) mFloatingView.findViewById(R.id.next_btn);
        //İşlevini tanımlıyoruz
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing next song.", Toast.LENGTH_LONG).show();
            }
        });

        //Music player daki previous  butonunu tanımlıyoruz
        ImageView prevButton = (ImageView) mFloatingView.findViewById(R.id.prev_btn);
        //İşlevini tanımlıyoruz
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing previous song.", Toast.LENGTH_LONG).show();
            }
        });

        //Music player daki kapatma  butonunu tanımlıyoruz
        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        //İşlevini tanımlıyoruz
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kapatma butonuna tıklandığında view elemntlerinin görünürlüğünü değiştiriyoruz
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        //Uygulamanın anasayfasına geri dönmek için yapılan Music player üstünde open butonunu tanımlıyoruz
        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        //İşlevinin tanımlıyoruz
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                stopSelf();
            }
        });

        //Kullanıcı Floatwidget butonu sürükleyim istediği yerde bırakacağı zaman yerinin değişmesini sağlıyoruz
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            //Bu method yerinin değişmesini sağlıyor
            //Eğer methodu anlamakta sıkıntı çekiyorsanız ezberlemek zoruda değilsiniz
            //Kopyala yapıştır ile uygulamanızda kullanabilirsiniz
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Layout üzerinde değişen x ve y koordinatlarını güncelliyoruz
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
        public void onDestroy() {
            super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
