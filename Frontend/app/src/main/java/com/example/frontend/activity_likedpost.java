package com.example.frontend;

import static com.example.frontend.Utils.BuildDialogUtil.buildDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.example.frontend.Utils.LoadingDialogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_likedpost extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private PostAdapter mAdapter;
    private ArrayList<Post> mPostList;
    private final Handler handler = new Handler();
    public void insertPost(Post p) {
        mPostList.add(p);
        mRecyclerview.setAdapter(mAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        mPostList.clear();
        mRecyclerview.setAdapter(mAdapter);
        String jsonStr = "{\"username\":\"" + activity_homepage.User.getUsername() + "\"}";
        String requestUrl = getString(R.string.ipv4) + "getLikedPosts/";
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
                LoadingDialogUtil.getInstance(activity_likedpost.this).closeLoadingDialog();
                buildDialog("Error", "无法连接至服务器。。或许网络出错了？", activity_likedpost.this);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                if (msg_obj_string.equals("error")) {

                } else {
                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                    JSONObject post_name_dict = new JSONObject();
                    post_name_dict.put("0", "#默认话题");
                    post_name_dict.put("1", "#校园资讯");
                    post_name_dict.put("2", "#二手交易");
                    post_name_dict.put("3", "#思绪随笔");
                    post_name_dict.put("4", "#吐槽盘点");
//                    System.out.println();
                    ArrayList<String> bitmaps = new ArrayList<>();
                    // TODO: initializt bitmaps
//                    System.out.println();
                    for (int i = 0; i < Integer.parseInt(msg_json.getString("num")); i++) {
                        Post post = new Post("",
                                msg_json.getString("username" + i),
                                msg_json.getString("posttime" + i).substring(0, 19),
                                msg_json.getString("title" + i),
                                msg_json.getString("content" + i),
                                msg_json.getString("tag" + i),
                                msg_json.getString("id" + i),
                                Integer.parseInt(msg_json.getString("like_number" + i)),
                                Integer.parseInt(msg_json.getString("shoucang_number" + i)),
                                Integer.parseInt(msg_json.getString("comment_number" + i)),
                                bitmaps);
//                        String t = "like_number" + i;
//                        String number = msg_json.getString(t);
//                        System.out.println(number);
//                        Log.d("shit", String.valueOf(Integer.parseInt(msg_json.getString(t))));
//                        int tmp = Integer.parseInt(msg_json.getString(t));
//                        post.setLikeNumber(tmp);
//                                Integer.parseInt(msg_json.getString("shoucang_number" + i)),
//                                Integer.parseInt(msg_json.getString("comment_number" + i)));
                        ArrayList<message> cur_comments = new ArrayList<message>();
//                        Log.d("hello", msg_json.getString("comment_number" + i));
                        for (int j = 0; j < Integer.parseInt(msg_json.getString("comment_number" + i)); j++) {
                            cur_comments.add(new message(msg_json.getString("commentcontent" + i + "number" + j),
                                    new user(msg_json.getString("commentusername" + i + "number" + j))));
//                            Log.d("msg", cur_comments.get(j).getMessageString());
//                            Log.d("usr", cur_comments.get(j).getFrom().getUsername());
                        }
                        post.setComments(cur_comments);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                insertPost(post);
                            }
                        });
//                        posts.add(post);
                    }
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mPostAdapter = new PostAdapter(posts);
//                            mPostRecyclerView.setAdapter(mPostAdapter);
//                        }
//                    });
                }
                LoadingDialogUtil.getInstance(activity_likedpost.this).closeLoadingDialog();
//                for (int i = 0; i < posts.size(); i++) {
//                    System.out.println(posts.get(i).getAuthor());
//                }
            }
        });

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likedpost);
        mPostList=new ArrayList<>();
        // todo: create mPostList
        mRecyclerview=findViewById(R.id.recyclerview);
        mAdapter=new PostAdapter(mPostList);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
//        insertPost(new Post());
//        String jsonStr = "{\"username\":\""+ activity_homepage.User.getUsername() + "\"}";
//        String requestUrl = getString(R.string.ipv4) + "getLikedPosts/";
//        OkHttpClient client = new OkHttpClient();
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        @SuppressWarnings("deprecation")
//        RequestBody body = RequestBody.create(JSON, jsonStr);
//        Request request = new Request.Builder()
//                .url(requestUrl)
//                .post(body)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                System.out.println("failed");
//                LoadingDialogUtil.getInstance(activity_likedpost.this).closeLoadingDialog();
//                buildDialog("Error", "无法连接至服务器。。或许网络出错了？", activity_likedpost.this);
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response)
//                    throws IOException {
//                Message msg = new Message();
//                msg.obj = Objects.requireNonNull(response.body()).string();
//                String msg_obj_string = msg.obj.toString();
//                if (msg_obj_string.equals("error")) {
//
//                } else {
//                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
//                    JSONObject post_name_dict = new JSONObject();
//                    post_name_dict.put("0", "#默认话题");
//                    post_name_dict.put("1", "#校园资讯");
//                    post_name_dict.put("2", "#二手交易");
//                    post_name_dict.put("3", "#思绪随笔");
//                    post_name_dict.put("4", "#吐槽盘点");
////                    System.out.println();
//                    for (int i = 0; i < Integer.parseInt(msg_json.getString("num")); i++) {
//                        Post post = new Post("",
//                                msg_json.getString("username" + i),
//                                msg_json.getString("posttime" + i).substring(0, 19),
//                                msg_json.getString("title" + i),
//                                msg_json.getString("content" + i),
//                                msg_json.getString("tag" + i),
//                                msg_json.getString("id" + i),
//                                Integer.parseInt(msg_json.getString("like_number" + i)),
//                                Integer.parseInt(msg_json.getString("shoucang_number" + i)),
//                                Integer.parseInt(msg_json.getString("comment_number" + i)));
////                        String t = "like_number" + i;
////                        String number = msg_json.getString(t);
////                        System.out.println(number);
////                        Log.d("shit", String.valueOf(Integer.parseInt(msg_json.getString(t))));
////                        int tmp = Integer.parseInt(msg_json.getString(t));
////                        post.setLikeNumber(tmp);
////                                Integer.parseInt(msg_json.getString("shoucang_number" + i)),
////                                Integer.parseInt(msg_json.getString("comment_number" + i)));
//                        ArrayList<message> cur_comments = new ArrayList<message>();
////                        Log.d("hello", msg_json.getString("comment_number" + i));
//                        for (int j = 0; j < Integer.parseInt(msg_json.getString("comment_number" + i)); j++) {
//                            cur_comments.add(new message(msg_json.getString("commentcontent" + i + "number" + j),
//                                    new user(msg_json.getString("commentusername" + i + "number" + j))));
////                            Log.d("msg", cur_comments.get(j).getMessageString());
////                            Log.d("usr", cur_comments.get(j).getFrom().getUsername());
//                        }
//                        post.setComments(cur_comments);
//                        handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            insertPost(post);
//                        }
//                    });
////                        posts.add(post);
//                    }
////                    handler.post(new Runnable() {
////                        @Override
////                        public void run() {
////                            mPostAdapter = new PostAdapter(posts);
////                            mPostRecyclerView.setAdapter(mPostAdapter);
////                        }
////                    });
//                }
//                LoadingDialogUtil.getInstance(activity_likedpost.this).closeLoadingDialog();
////                for (int i = 0; i < posts.size(); i++) {
////                    System.out.println(posts.get(i).getAuthor());
////                }
//            }
//        });
    }
}
