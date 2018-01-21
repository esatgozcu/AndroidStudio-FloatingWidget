package com.example.esatgozcu.floatingwidgetkullanimi;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bir uygulamanın telefonunuzda diğer uygulamaların üzerinde görüntülenmesine izin vermemiz gerekiyor
        //ACTION_MANAGE_OVERLAY_PERMISSION ve CODE_DRAW_OVER_OTHER_APP_PERMISSION izinleri uygulamaya
        //başlamadan önce almamız gerekli o yüzden kontolünü yapıyoruz.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //Eğer izin verilmemiş ise izin verilmesini sağlıyoruz
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            //Eğer izinler verilmiş ise servisi başlatıyoruz
            initializeView();
        }
    }

    private void initializeView() {
        findViewById(R.id.olustur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, FloatingViewService.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //İzinin verilip verilmediği kontrol ediliyor
            if (resultCode == RESULT_OK) {
                //Verildiyse servis başlatılıyor
                initializeView();
            } else {
                //İzin verilmediyse mesaj bastırıyoruz
                Toast.makeText(this,
                        "Uygulamayı kullanabilmek için lütfen izin veriniz",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            //İzinler verilmediyse tekrardan izinleri almasını sağlıyoruz.
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
