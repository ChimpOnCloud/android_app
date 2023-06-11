package com.example.frontend;

import static com.example.frontend.Utils.AvatarUtil.getAvatar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_chatdetail extends AppCompatActivity {
    private chat mChat;
    private final Handler handler = new Handler();
    private RecyclerView mRecyclerView;
    private chatAdapter mAdapter;
    private TextView oppoName;
    private EditText text;
    private Button sendButton;
    private Handler updateChatHandler;
    private Runnable task;
    private Bitmap oppoBitmap,selfBitmap;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    public void chatInsert(message m){
        mChat.insert(m);
        mRecyclerView.setAdapter(mAdapter);
    }

    private Bitmap drawableToBitmap(Drawable drawable)
    {
        //声明将要创建的bitmap
        Bitmap bitmap = null;
        //获取图片宽度
        int width = drawable.getIntrinsicWidth();
        //获取图片高度
        int height = drawable.getIntrinsicHeight();
        //图片位深，PixelFormat.OPAQUE代表没有透明度，RGB_565就是没有透明度的位深，否则就用ARGB_8888。详细见下面图片编码知识。
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        //创建一个空的Bitmap
        bitmap = Bitmap.createBitmap(width,height,config);
        //在bitmap上创建一个画布
        Canvas canvas = new Canvas(bitmap);
        //设置画布的范围
        drawable.setBounds(0, 0, width, height);
        //将drawable绘制在canvas上
        drawable.draw(canvas);
        return bitmap;
    }
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,  opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatdetail);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        Intent intent=this.getIntent();
        mChat=(chat)intent.getSerializableExtra("chat");
        byte[] res = getIntent().getByteArrayExtra("bitmap");
        oppoBitmap=getPicFromBytes(res,null);

        mRecyclerView=findViewById(R.id.chatrecyclerview);
        mAdapter=new chatAdapter(this,mChat,oppoBitmap,selfBitmap);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        oppoName=findViewById(R.id.oppoName);
        oppoName.setText(" "+mChat.getOpposite().getUsername());
        text=findViewById(R.id.chatText);
        sendButton=findViewById(R.id.buttonsend);
        sendButton.setOnClickListener(view -> {
            String content=text.getText().toString();
            if(content.equals("")) return;
            message m=new message(content,activity_homepage.User);
            chatInsert(m);
            text.setText("");
            // TODO: backend adds this message
            String jsonStr = "{\"msgContent\":\""+ content + "\",\"fromUser\":\""+activity_homepage.User.getUsername();
            jsonStr = jsonStr + "\",\"toUser\":\"" + mChat.getOpposite().getUsername() + "\"}";
            System.out.println(jsonStr);
            String requestUrl = getString(R.string.ipv4)+"addMessageToChat/";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            @SuppressWarnings("deprecation")
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("failed");
                    // e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, final Response response)
                        throws IOException {
                    Message msg = new Message();
                    msg.obj = Objects.requireNonNull(response.body()).string();
                    String msg_obj_string = msg.obj.toString(); // ok
                    if (msg_obj_string.equals("ok")) {

                    } else if (msg_obj_string.equals("repeated username!")) {

                    }
                }
            });
        });
        updateChatHandler=new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        int delay=1000;  // ms between refreshing chatContent
        task=new Runnable() {
            @Override
            public void run() {
                update();
                updateChatHandler.postDelayed(this,delay);
            }
        };
        updateChatHandler.postDelayed(task,delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateChatHandler.removeCallbacks(task);
    }

    public void update(){
        String jsonStr = "{\"srcUsername\":\""+ activity_homepage.User.getUsername() + "\",\"dstUsername\":\"" + mChat.getOpposite().getUsername()+"\"}";
        String requestUrl = getString(R.string.ipv4)+"getAllMessages/";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        @SuppressWarnings("deprecation")
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failed");
                // e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                int length=msg_json.size()/2;
                if(length==mChat.getChatContent().size()) return;
                else{
                    for(int i=mChat.getChatContent().size();i<length;i++){
                        String msg_i = msg_json.getString("msg" + i);
                        String is_send_i_string = msg_json.getString("is_send" + i);
                        if (is_send_i_string.equals("false")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    chatInsert(new message(msg_i,mChat.getOpposite()));
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    chatInsert(new message(msg_i,activity_homepage.User));
                                }
                            });
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });
                    }
                }
            }
        });
    }
}
