package com.example.toolbarex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleApiClient client;
    private EditText generatedURL;
    // generatedURLLength - количество символов в генерируемом для пользователя профиле
    private static final int generatedURLLength = 15;
    private String serverURL, generatedURLString;
    public String hashString;
    private TextView textViewHashString;
    private String userLogin;

    // для хранения настроек во активити - Settings
    // это будет именем файла настроек
    public static final String APP_PREF_hash = "mysettings";
    public static final String APP_PREF_generatedURL = "genURL";
    public static final String APP_PREF_Login = "noname";

    //public static final String APP_HashString = "";

    //Создаём переменную, представляющую экземпляр класса SharedPreferences,
    //который отвечает за работу с настройками:
    //Внутри метода onCreate() вы инициализируете эту переменную::
    private SharedPreferences mSettings, mSettings_URL, mSettings_Login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //основные предустановки:
        //generatedURLLength = 15; //количество сиволов в генерируемом для пользователя профиле
        serverURL = "https://www.homebrewdevelopers.com/userfiles/"; //url сервера нашего приложения

        //инициализируем переменную mSettings для хранения настроек в Settings Activity
        mSettings = getSharedPreferences(APP_PREF_hash, Context.MODE_PRIVATE);
        mSettings_URL = getSharedPreferences(APP_PREF_generatedURL, Context.MODE_PRIVATE);

        // cчитываем из Shared Pref login пользователя и устанавливаем логин в качестве
        // заголовка главного окна приложения
        mSettings_Login = getSharedPreferences(APP_PREF_Login, Context.MODE_PRIVATE);
        //и устанавливаем титул заголовока окна приложения в логин пользователя
        userLogin = mSettings_Login.getString(APP_PREF_Login, "noname");
        setTitle(userLogin);

        generatedURL = (EditText) findViewById(R.id.generatedURL);

        textViewHashString = (TextView) findViewById(R.id.textViewHashString);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.Info, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Если нажали на пункт меню "Настройки", то вызываем ActivitySettings
        if (id == R.id.action_settings) {
            CallSettingsActivity();
            return true;
        }

        //Если нажали на пункт меню "О программе", то вызываем ActivityAbout
        if (id == R.id.about) {
            Intent intent = new Intent(this, ActivityAbout.class);
            startActivity(intent);
            return true;
        }

        //Если нажали на разводной ключ, то ушли в ActivitySettings
        if (id == R.id.action_favorite) {
            CallSettingsActivity();
            return true;
        }
        //Если нажали на Save, то ушли в Save всплывает Toast Save Succesful
        if (id == R.id.action_save) {
            generatedURL.setText(generatedURLString);
            Toast toast = Toast.makeText(getApplicationContext(), R.string.toastSave,
                    Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Активити ActivitySettings вызывается несколько раз (из разных активити) посему сделал
    // приват метод, дабы каждый раз не прописывать сей код, теряя время
    private void CallSettingsActivity() {
        Intent intentSettings = new Intent(this, ActivitySettings.class);
        startActivity(intentSettings);
    }

    //делаю генератор ссылки для меню "Создать ссылку, поделиться с друзьями"
    private String GenerateURL() {
        String dict = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        String finalURL = "";

        //генерируем случайным перебором строку generatedURL, которая будет содержать
        // сгенерированные символы, длиной строки = generatedURLLength
        for (int i = 0; i < generatedURLLength; i++) {
            Random rand = new Random();
            int randIndex = rand.nextInt(dict.length());
            char c = dict.charAt(randIndex);
            finalURL = finalURL + c;
        }

        finalURL = serverURL + finalURL;
        generatedURLString = finalURL;
        return finalURL;
    }

    //создаем функцию вычисления хэша (md5) для строки in
    private String md5(String in) {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Прописываем обработчики нажатия на кнопки меню App Bar
        switch (id) {
            case R.id.nav_camera:
                // Тут прописаны действия связанные с нажатием на пункт Фотографии nav_camera
                Intent intentLoginActivity = new Intent(this, LoginActivity.class);
                startActivity(intentLoginActivity);
                return true;

            case R.id.nav_gallery:
                // Тут прописаны действия связанные с нажатием на пункт Галерея nav_gallery
                Intent intentAbout = new Intent(this, ActivityAbout.class);
                startActivity(intentAbout);
                return true;

            case R.id.nav_slideshow:
                // Тут прописать действия связанные с нажатием на пункт Send SMS
                Intent intentSendSMS = new Intent(this, SMSActivity.class);
                startActivity(intentSendSMS);
                return true;

            case R.id.nav_manage:
                // вызываем Settings Activity
                CallSettingsActivity();
                return true;

            case R.id.nav_share:
                // нажатие на пункт nav_share
                generatedURL.setText(GenerateURL());
                // вычисляем хэш hashString
                hashString = md5(generatedURLString);
                // и выводим хэш-строку в соответствующее поле textViewHashString
                textViewHashString.setText(hashString);
                return true;

            case R.id.nav_send:
                // переход на WordActivity
                Intent intentGenerateWord = new Intent(this, WordActivity.class);
                startActivity(intentGenerateWord);
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // App Indexing API.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.toolbarex/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {

        super.onStop(); //выполняем стандартные onStop

        // Запоминаем данные о настройке APP+HashString с помощью объекта SharedPreferences
        /*Чтобы внести изменения в настройки, нужно использовать класс SharedPreferences.Editor.
        Получить объект Editor можно через вызов метода edit() объекта SharedPreferences.
        После того, как вы внесли все необходимые изменения, вызовите метод apply(),
        чтобы изменения вступили в силу. */
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREF_hash, hashString);
        editor.putString(APP_PREF_generatedURL, generatedURLString);
        editor.apply();

        // App Indexing API.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.toolbarex/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // действия когда приложение перешло в состояние onResume - пауза, свертывание приложения
    @Override
    protected void onPause() {
        super.onPause();
        // Запоминаем данные о настройке Setting 1 с помощью объекта SharedPreferences
        /*Чтобы внести изменения в настройки, нужно использовать класс SharedPreferences.Editor.
        Получить объект Editor можно через вызов метода edit() объекта SharedPreferences.
        После того, как вы внесли все необходимые изменения, вызовите метод apply(),
        чтобы изменения вступили в силу. */
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREF_hash, hashString);
        editor.putString(APP_PREF_generatedURL, generatedURLString);
        editor.apply();
    }

    @Override
    // действия когда приложение перешло в состояние onResume - вышли из паузы, развертывание приложения
    protected void onResume() {
        super.onResume();

        if (mSettings.contains(APP_PREF_hash) || mSettings.contains(APP_PREF_generatedURL)) {
            // Получаем число из настроек
            hashString = mSettings.getString(APP_PREF_hash, "none");
            generatedURLString = mSettings.getString(APP_PREF_generatedURL, "none");

            //читаем из Shared Pref логин пользователя и делаем заголовок окна этим логином
            userLogin = mSettings_Login.getString(APP_PREF_Login, "noname");
            setTitle(userLogin); //устанавливаем заголовок окна main activity логин пользователя

            // Выводим на экран данные из настроек
            textViewHashString.setText(hashString);
            generatedURL.setText(generatedURLString);
        }
    }
}
