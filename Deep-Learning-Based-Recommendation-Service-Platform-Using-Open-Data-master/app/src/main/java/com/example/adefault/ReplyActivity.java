package com.example.adefault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adefault.adapter.ReplyAdapter;
import com.example.adefault.manager.AppManager;
import com.example.adefault.manager.ImageManager;
import com.example.adefault.model.ReplyDTO;
import com.example.adefault.model.ReplyResponseDTO;
import com.example.adefault.model.ReplyReview_data;
import com.example.adefault.model.ReplyWriteDTO;
import com.example.adefault.model.ReplyWriteResponseDTO;
import com.example.adefault.util.RestApiUtil;
import com.example.adefault.util.UserToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyActivity extends AppCompatActivity {

    private String posting_id;
    private RestApiUtil mRestApiUtil;
    private ArrayList<ReplyReview_data> data;
    private RecyclerView recyclerView;
    private ArrayList<ReplyReview_data> dataList;
    private ReplyAdapter replyAdapter;
    private Boolean isLoading;
    private EditText replyText;
    private Button writeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reply);
        init();
    }




    private void init() {
        Intent intent =getIntent();
        posting_id= intent.getStringExtra("posting_id");
        mRestApiUtil = new RestApiUtil();
        data = new ArrayList<>();
        dataList = new ArrayList<>();
        isLoading=false;
        writeBtn = findViewById(R.id.writeBtn);
        replyText = findViewById(R.id.replyText);
        recyclerView = findViewById(R.id.replyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firstData();
        initAdapter();
        initScrollListener();
        addListener();
    }

    private void addListener() {
        writeBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!replyText.getText().toString().equals("")){
                    ReplyWriteDTO replyWriteDTO = new ReplyWriteDTO();
                    replyWriteDTO.setContext(replyText.getText().toString());
                    replyText.setText("");
                    mRestApiUtil.getApi().reply_write("Token "+UserToken.getToken(),replyWriteDTO,posting_id).enqueue(new Callback<ReplyWriteResponseDTO>() {
                        @Override
                        public void onResponse(Call<ReplyWriteResponseDTO> call, Response<ReplyWriteResponseDTO> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"????????? ?????????????????????",Toast.LENGTH_SHORT).show();
                                finish();

                            }else{
                                Log.d("????????????","response??????");
                            }
                        }

                        @Override
                        public void onFailure(Call<ReplyWriteResponseDTO> call, Throwable t) {
                            Log.d("????????????","?????? ??????");
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"?????? ????????? ??????????????????",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    private void firstData() {
        progressON("????????? ????????????????????????");
        ReplyDTO replyDTO = new ReplyDTO();
        replyDTO.setPosting_id(posting_id);
        mRestApiUtil.getApi().reply("Token "+ UserToken.getToken(), replyDTO).enqueue(new Callback<ReplyResponseDTO>() {
            @Override
            public void onResponse(Call<ReplyResponseDTO> call, Response<ReplyResponseDTO> response) {
                if(response.isSuccessful()){
                    replyAdapter.notifyDataSetChanged();
                    ReplyResponseDTO replyResponseDTO = response.body();
                    int replySize = replyResponseDTO.getReview_data().size();

                    dataList = replyResponseDTO.getReview_data();
                    for(int i=0;i<replySize;i++){
                        data.add(dataList.get(i));
                    }

                    progressOFF();
                    Log.d("????????? ??????????????????","");

                }else{
                    Log.d("reply","response??????");
                }
            }

            @Override
            public void onFailure(Call<ReplyResponseDTO> call, Throwable t) {
                Log.d("reply","????????????");

            }
        });
    }


    private void dataMore() {
        data.add(null);
        replyAdapter.notifyItemInserted(data.size() -1 );

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                data.remove(data.size() -1 );
                int scrollPosition = data.size();
                replyAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                for (int i=currentSize; i<nextLimit; i++) {
                    if (i == dataList.size()) {
                        return;
                    }
                    data.add(dataList.get(i));
                }

                replyAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);

    }


    private void initAdapter() {
        replyAdapter = new ReplyAdapter(data,getApplicationContext());
        recyclerView.setAdapter(replyAdapter);
    }

    // ?????????????????? ????????????
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == data.size() - 1) {
                        dataMore();
                        isLoading = true;
                    }
                }
            }
        });
    }


    //?????? ?????? ??????
    public void mOnClose(View v){

        //????????????(??????) ??????
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //??????????????? ????????? ????????????
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //??????????????? ????????? ??????
        return;
    }

    public void progressON(String message) {
        ImageManager.getInstance().progressON((Activity) AppManager.getInstance().getContext(), message);
    }
    public void progressOFF() {
        ImageManager.getInstance().progressOFF();
    }
}
