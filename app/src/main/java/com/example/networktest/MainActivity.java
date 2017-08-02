package com.example.networktest;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView responseText;
    ImageView imageView;
    WebView webView;
    private Handler handler;
    String id;
    String password;
    String imageCode;
    String __VIEWSTATE;
    EditText idEText ;
    EditText passwordEdit;
    EditText imageCodeEdit ;
    String session = "";
    /**
     * 是否正在请求验证码
     */
    private boolean isGetCheckCode;

    /**
     * 数据持久化
     */
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestViewState();
        requestImageCode();
        Button sendRequest = (Button) findViewById(R.id.send_request);
        Button loginButton = (Button)findViewById(R.id.login);
        imageView = (ImageView) findViewById(R.id.imageView);
        idEText = (EditText)findViewById(R.id.id);
        passwordEdit = (EditText)findViewById(R.id.password);
        imageCodeEdit = (EditText)findViewById(R.id.image_code);
        Button test = (Button) findViewById(R.id.test);
        sendRequest.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        test.setOnClickListener(this);
        handler = (Handler) new Handler() {
            @Override
            public void  handleMessage(Message msg) {
                if (msg.obj != null && msg.obj instanceof Bitmap) {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitmap);
                } else if (msg.obj != null && msg.obj instanceof String) {
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                }
            }
        };
        sharedPreferences = getSharedPreferences("session",MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        String password = sharedPreferences.getString("password","");
        //
        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            idEText.setText(username);
            passwordEdit.setText(password);
        }
    }

    @Override
    public void onClick(View v){
        id = idEText.getText().toString();
        password = passwordEdit.getText().toString();
        imageCode = imageCodeEdit.getText().toString();
        switch (v.getId()){
            case R.id.login:
                Toast.makeText(this,"Login",Toast.LENGTH_SHORT).show();
                //判断用户名密码不能为空
                if (TextUtils.isEmpty(id ) || TextUtils.isEmpty(password)||TextUtils.isEmpty(imageCode)) {
                    Toast.makeText(this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putString("username", id).commit();
                sharedPreferences.edit().putString("password", password).commit();
                login(id,password,imageCode);
                break;
            case R.id.send_request:
                Toast.makeText(this,"change_image_code",Toast.LENGTH_SHORT).show();
                requestImageCode();
                break;
            case R.id.test:
                test();
                break;
            default:
                break;
        }

    }
    private void requestImageCode(){
        //若正在请求中，直接return
        if (isGetCheckCode) {
            return;
        }
        Request imageCodeRequest = OkHttpUtil.getRequest(OkHttpUtil.getUrlCode(),session);
        OkHttpUtil.getOkHttpClient().newCall(imageCodeRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = Message.obtain();
                        message.obj=BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_black_24dp);
                        handler.sendMessage(message);
                    }
                });
                Log.d("Log", "getCheckCode -->>> onFailure -->>>" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Headers headers = response.headers();
                Log.d("info_headers", "header " + headers);
                List<String> cookies = headers.values("Set-Cookie");
                if (cookies.size()>0) {
                    session = cookies.get(0);
                    Log.d("info_cookies", "onResponse-size: " + cookies);
                    String s="";
                    session.substring(0, session.indexOf(";"));
                    Log.i("info_s", "session is  :" + session);
                }
                Message message = Message.obtain();
                if (response.code() == 200) {
                    message.obj=BitmapFactory.decodeStream(response.body().byteStream());
                } else {
                    message.obj=BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_black_24dp);
                }
                handler.sendMessage(message);
                Log.d("qq", " getCheckCode -->>> onResponse --> response.code -->" + response.code());
                isGetCheckCode = false;
            }
        });
    }
    private void requestViewState(){
        Request loginInforRequest = OkHttpUtil.getRequest(OkHttpUtil.getUrlLogin(),session);
        OkHttpUtil.getOkHttpClient().newCall(loginInforRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Okhttp","获取登录页面出错");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Headers headers = response.headers();
                Log.d("info_headers", "header " + headers);
                List<String> cookies = headers.values("Set-Cookie");
                if (cookies.size()>0) {
                    session = cookies.get(0);
                    Log.d("info_cookies", "onResponse-size: " + cookies);
                    String s="";
                    session.substring(0, session.indexOf(";"));
                    Log.i("info_s", "session is  :" + session);
                }
                String result =response.body().string();
                Pattern pattern = Pattern.compile("__VIEWSTATE\\\" value=\\\"(.+?)\\\"");
                Matcher matcher = pattern.matcher(result);
                while(matcher.find())
                {
                    __VIEWSTATE = matcher.group(1);
                }
                Log.d("Okhttp","获取登录页面成功"+"\n"+__VIEWSTATE+result);
            }
        });
    }
    private boolean login(String id,String password,String imageCode){
        Log.d("login",id+password+imageCode);
        String url = "http://kmustjwcxk4.kmust.edu.cn/JWWEB/_data/index_LOGIN.aspx ";
        String a = Md5Utils.md5(password).substring(0,30).toUpperCase();
        String dsdsdsdsdxcxdfgfg = Md5Utils.md5(id+Md5Utils.md5(password).substring(0,30).toUpperCase()+"10674").substring(0,30).toUpperCase();
        String fgfggfdgtyuuyyuuckjg = Md5Utils.md5(Md5Utils.md5(imageCode.toUpperCase()).substring(0,30).toUpperCase()+"10674").substring(0,30).toUpperCase();
        String header = "Mozilla/5.0+(Windows+NT+6.1;+WOW64;+rv:45.0)+Gecko/20100101+Firefox/45.0Windows+NT+6.1;+WOW645.0+(Windows)+SN:NULL";
        Log.d("DSDSDS",dsdsdsdsdxcxdfgfg);
        Log.d("FGFGFG",fgfggfdgtyuuyyuuckjg);
        String typeName = "学生" ;
        try {
            String utf8 = new String(typeName.getBytes("UTF-8"));
            Log.d("UTF8",utf8);
            String gb2312= new String (utf8.getBytes("GB2312"));
            Log.d("gb2312",gb2312);
            typeName = gb2312;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("gb2312",OkHttpUtil.encodeUrl("学生"));
        RequestBody requestBody = new FormBody.Builder()
                .add("__VIEWSTATE",__VIEWSTATE)
                .add("typeName",typeName)
                .add("dsdsdsdsdxcxdfgfg",dsdsdsdsdxcxdfgfg)
                .add("fgfggfdgtyuuyyuuckjg",fgfggfdgtyuuyyuuckjg)
                .add("Sel_Type","STU")
                .add("txt_asmcdefsddsd",id)
                .add("txt_pewerwedsdfsdff","")
                .add("txt_sdertfgsadscxcadsads","")
                .add("sbtState","")
                .add("pcInfo:","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36undefined5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36 SN:NULL")
                .build();

        Request loginRequest = new Request.Builder()
                .addHeader("cookie",session)
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0")
                .addHeader("Host","kmustjwcxk4.kmust.edu.cn")
                .addHeader("Origin","http://kmustjwcxk4.kmust.edu.cn")
                .addHeader("Referer","http://kmustjwcxk4.kmust.edu.cn/JWWEB/_data/index_LOGIN.aspx")
                .url(OkHttpUtil.getUrlLogin())
                .post(requestBody)
                .build();
        OkHttpUtil.getOkHttpClient().newCall(loginRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Headers headers = response.headers();
                Log.d("info_headers", "header " + headers);
                List<String> cookies = headers.values("Set-Cookie");
                Log.d("Login",response.body().string());
                if (cookies.size()>0) {
                    session = cookies.get(0);
                    Log.d("info_cookies", "onResponse-size: " + cookies);
                    String s="";
                    session.substring(0, session.indexOf(";"));
                    Log.i("info_s", "session is  :" + session);
                }
            }
        });
        return true;
    }
    public void test (){
        Request loginInforRequest = OkHttpUtil.getRequest("http://kmustjwcxk4.kmust.edu.cn/JWWEB/PUB/foot.aspx",session);
        OkHttpUtil.getOkHttpClient().newCall(loginInforRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Okhttp","登录出错");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result =response.body().string();
                Log.d("Okhttp","登录成功"+"\n"+result);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().putString("session",session);
    }
}
