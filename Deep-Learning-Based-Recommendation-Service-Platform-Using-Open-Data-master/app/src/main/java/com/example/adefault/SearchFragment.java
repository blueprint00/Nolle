package com.example.adefault;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.esafirm.imagepicker.model.Image;
import com.example.adefault.Decoration.MyListDecoration;
import com.example.adefault.Decoration.OnBackPressedListener;
import com.example.adefault.Decoration.Result;
import com.example.adefault.Decoration.SearchHistory;
import com.example.adefault.Decoration.SearchHistoryDecoration;
import com.example.adefault.Decoration.SearchPopularDecoration;
import com.example.adefault.Decoration.SearchRealtimeDecoration;
import com.example.adefault.adapter.SearchFirstRecommendAdapter;
import com.example.adefault.adapter.SearchHistoryAdapter;
import com.example.adefault.adapter.SearchPopularAdapter;
import com.example.adefault.adapter.SearchRealtimeAdapter;
import com.example.adefault.data.FirstRecommendData;
import com.example.adefault.data.ReviewData;
import com.example.adefault.manager.AppManager;
import com.example.adefault.manager.ImageManager;
import com.example.adefault.util.RetrofitClient;
import com.example.adefault.util.UserToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mvc.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class SearchFragment extends Fragment implements OnBackPressedListener {

    private final int GET_GALLERY_IMAGE = 200;
    private static final int PICK_IMAGE_ID = 234;
    private static final int SECOND_PIC_REQ = 1313;
    private static final int PICK_FROM_FILE = 100;


    ArrayList<ReviewData> movieDataList;
    private View view;
    private RecyclerView listview;
    private RecyclerView realtime_listview;
    private RecyclerView popular_listview;
    private SearchFirstRecommendAdapter adapter;
    private SearchPopularAdapter popular_adpater;
    private SearchRealtimeAdapter realtime_adpater;
    private SearchHistoryAdapter history_adapter;
    private RecyclerView history_listview;
    private SearchView mSearchView;
    private LinearLayout ll;
    private LinearLayout.LayoutParams paramll;
    private LayoutInflater layoutinflater;
    private View history_view;
    private View searchView;
    private SearchView sv;
    private MenuItem myActionMenuItem;
    RetrofitClient retrofitClient = new RetrofitClient();

    private com.esafirm.imagepicker.features.ImagePicker imagePicker;
    public File imgFile;
    public Image image;
    private String sentence;

    private Button tagBtn1;
    private Button tagBtn2;
    private Button tagBtn3;
    private Button tagBtn4;
    private Button tagBtn5;

    private ArrayList<FirstRecommendData> result_itemList;
    private ArrayList<SearchHistory> search_itemList;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).showOptionMenu(true);
        ((MainActivity) getActivity()).setOnBackPressedListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        ImagePicker.setMinQuality(600, 600);

        inflater.inflate(R.menu.menu_search, menu);
        myActionMenuItem = menu.findItem(R.id.menu_search);
        sv = (SearchView) myActionMenuItem.getActionView();
        myActionMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override // ???????????? ??????????????? ???
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d("tlqkf", "??????");

                Toast.makeText(getActivity(), "?????????: ", Toast.LENGTH_SHORT).show();
                if (ll == null) {


                    //???????????? ????????????
                    ll = (LinearLayout) layoutinflater.inflate(R.layout.search_history_list, null);
                    //???????????? ?????? ????????? ??????
                    ll.setBackgroundColor(Color.parseColor("#99000000"));
                    //???????????? ?????? ?????????
                    paramll = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    getActivity().addContentView(ll, paramll);
                }
                //???????????? ??????????????? ????????? ???????????????
                //ll.setOnClickListener(writeListener);
                //????????? ??????
                //((ViewManager)ll.getParent()).removeView(ll);
                return false;
            }

            @Override // ???????????? ???????????????.
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Toast.makeText(getActivity(), "?????????", Toast.LENGTH_SHORT).show();

                if (ll != null) {
                    ((ViewManager) ll.getParent()).removeView(ll);
                }
                return false;
            }
        });


        if (sv != null) {
            Log.d("???", "???");
            sv.setMaxWidth(Integer.MAX_VALUE);
            sv.setBackgroundColor(Color.DKGRAY);
            sv.setQueryHint(getString(R.string.search));

            sv.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Toast.makeText(getActivity(), "x????????? ", Toast.LENGTH_SHORT).show();

                    if (ll != null) {
                        ((ViewManager) ll.getParent()).removeView(ll);
                    }

                    sv.onActionViewCollapsed();
                    return true;
                }
            });

            sv.setOnSearchClickListener(new SearchView.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //sv.onActionViewExpanded();
                    Log.d("whatthe", "click");

                }
            });
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    // ???????????? ????????? ??????
                    Intent intent = new Intent(AppManager.getInstance().getContext(), SearchResultActivity.class);
                    intent.putExtra("searchSentence", s);
                    intent.putExtra("uri", "");
                    startActivity(intent);

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    // ???????????? ???????????? ?????? ??? ??????

                    if (TextUtils.isEmpty(s)) {
                        history_adapter.filter("");
                        //listView.clearTextFilter();
                    } else {
                        Log.d("????????????", s);
                        history_adapter.filter(s);
                    }
                    return true;

                }
            });
        }
    }



    // ?????? ?????? ???, onOptionsItemSelected ???????????? ????????????.
    // ??? ??? MenuItem ????????? ??????????????? ???????????? ??????, ?????? ????????? ?????????????????? id??? ???????????? ????????? ??? ??????.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_image:
                Toast.makeText(getActivity(), "????????? ?????? ??????", Toast.LENGTH_SHORT).show();
                imagePicker.start();
                break;
            case R.id.menu_search:
                Toast.makeText(getActivity(), "?????? ??????", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_voice:
                Toast.makeText(getActivity(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                Intent intent_voice = new Intent(getActivity(), VoiceActivity.class);
                startActivity(intent_voice);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (com.esafirm.imagepicker.features.ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            image = com.esafirm.imagepicker.features.ImagePicker.getFirstImageOrNull(data);
            imagePick(image);
        }
    }

    public void imagePick(Image image) {
        Intent intent = new Intent(AppManager.getInstance().getContext(), SearchResultActivity.class);
        intent.putExtra("searchSentence", "");
        intent.putExtra("uri", image.getPath());
        startActivity(intent);

    }



    private void init() {
        imagePicker = com.esafirm.imagepicker.features.ImagePicker.create(this)
                .single();
        //btn init()
        tagBtn1 = view.findViewById(R.id.tag1);
        tagBtn2 = view.findViewById(R.id.tag2);
        tagBtn3 = view.findViewById(R.id.tag3);
        tagBtn4 = view.findViewById(R.id.tag4);
        tagBtn5 = view.findViewById(R.id.tag5);

        tagBtn1.setOnClickListener(onClickItem2);
        tagBtn2.setOnClickListener(onClickItem2);
        tagBtn3.setOnClickListener(onClickItem2);
        tagBtn4.setOnClickListener(onClickItem2);
        tagBtn5.setOnClickListener(onClickItem2);


        //first recommend listview init

        listview = view.findViewById(R.id.main_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);
        listview.setAdapter(adapter);

        MyListDecoration decoration = new MyListDecoration();
        listview.addItemDecoration(decoration);
        /*==================================================================================================*/
        //realtime_listview init
        realtime_listview = view.findViewById(R.id.realtime_listview);
        LinearLayoutManager realtime_layoutManager = new LinearLayoutManager(getActivity());
        realtime_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        realtime_listview.setLayoutManager(realtime_layoutManager);


        /*==================================================================================================*/
        //popular_listview init
        popular_listview = view.findViewById(R.id.popular_listview);
        LinearLayoutManager popular_layoutManager = new LinearLayoutManager(getActivity());
        popular_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        popular_listview.setLayoutManager(popular_layoutManager);


        /*==================================================================================================*/

        search_itemList = new ArrayList<>();


        //init history_listview
        history_listview = history_view.findViewById(R.id.history_list);
        LinearLayoutManager history_layoutManager = new LinearLayoutManager(getActivity());
        history_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        history_listview.setLayoutManager(history_layoutManager);


    }


    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
            Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener onClickItem_feed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int posting_id = (int)v.getTag();
            Intent activityIntent = new Intent(AppManager.getInstance().getContext(), FeedDetailActivity.class);
            activityIntent.putExtra("posting_idx", posting_id);
            startActivity(activityIntent);
        }
    };

    private View.OnClickListener onClickItem2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btn = (Button)v;
            String str = (String) btn.getText();
            Intent intent = new Intent(AppManager.getInstance().getContext(), SearchResultActivity.class);
            intent.putExtra("searchSentence", str);
            intent.putExtra("uri", "");
            startActivity(intent);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        //??????????????? ?????? ????????? ????????? ??????
        layoutinflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        history_view = layoutinflater.inflate(R.layout.search_history_list, container, false);
        searchView = inflater.inflate(R.layout.menu_editsearch, container, false);

        init();

        getSearchFragmentData();
        return view;
    }//onCreateView()

    @Override
    public void onBackPressed() {
        Log.d("data", "??????");


    }

    public void getSearchFragmentData() {
        progressON("?????? ????????????. ????????? ??????????????????...");
        Call<Result> call = retrofitClient.apiService.getretrofitdata("Token "+ UserToken.getToken());
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    //?????? ?????? ??????
                    tagBtn1.setText("#" + result.getCategory_m().get(1).get("ctgr_name").getAsString());
                    tagBtn2.setText("#" + result.getCategory_s().get(1).get("ctgr_name").getAsString());
                    tagBtn3.setText("#" + result.getCategory_m().get(2).get("ctgr_name").getAsString());
                    tagBtn4.setText("#" + result.getCategory_s().get(2).get("ctgr_name").getAsString());
                    tagBtn5.setText("#" + result.getCategory_m().get(3).get("ctgr_name").getAsString());
                    //????????? ?????? ?????? ??????
                    for (int i = 0; i < result.getSearch_history().size(); i++) {
                        search_itemList.add(new SearchHistory(R.drawable.ic_search_black_24dp, result.getSearch_history().get(i).get("text").getAsString()));
                    }
                    history_adapter = new SearchHistoryAdapter(getActivity(), search_itemList, onClickItem);
                    history_listview.setAdapter(history_adapter);
                    SearchHistoryDecoration searchHistoryDecoration = new SearchHistoryDecoration();
                    history_listview.addItemDecoration(searchHistoryDecoration);
                    //????????? ?????? ?????? ??????
                    //place_detail??? ?????? ??????????????????
                    NetworkTask networkTask = new NetworkTask(getActivity(), result);
                    networkTask.execute();

                    //?????? ?????? ??????
                    ArrayList<ReviewData> popular_itemList = new ArrayList<>();
                    for (int i = 0; i < result.getHot_posting().size(); i++) {
                        if (!result.getHot_posting().get(i).get("img_url_1").isJsonNull()) {
                            popular_itemList.add(new ReviewData(result.getHot_posting().get(i).get("img_url_1").getAsString(), result.getHot_posting().get(i).get("place_name").getAsString()
                                    , (int) (result.getHot_posting().get(i).get("rating").getAsDouble()), result.getHot_posting().get(i).get("context").getAsString(),result.getHot_posting().get(i).get("idx").getAsInt()));
                        } else {
                            popular_itemList.add(new ReviewData("none", result.getHot_posting().get(i).get("place_name").getAsString()
                                    , (int) (result.getHot_posting().get(i).get("rating").getAsDouble()), result.getHot_posting().get(i).get("context").getAsString(),result.getHot_posting().get(i).get("idx").getAsInt()));
                        }

                    }

                    popular_adpater = new SearchPopularAdapter(getActivity(), popular_itemList, onClickItem_feed);
                    popular_listview.setAdapter(popular_adpater);

                    SearchPopularDecoration searchPopularDecoration = new SearchPopularDecoration();
                    popular_listview.addItemDecoration(searchPopularDecoration);

                    //????????? ?????? ??????
                    ArrayList<ReviewData> realtime_itemList = new ArrayList<>();
                    for (int i = 0; i < result.getRealtime_posting().size(); i++) {
                        if (!result.getRealtime_posting().get(i).get("img_url_1").isJsonNull()) {
                            realtime_itemList.add(new ReviewData(result.getRealtime_posting().get(i).get("img_url_1").getAsString(), result.getRealtime_posting().get(i).get("place_name").getAsString()
                                    , (int) (result.getRealtime_posting().get(i).get("rating").getAsDouble()), result.getRealtime_posting().get(i).get("context").getAsString(),result.getRealtime_posting().get(i).get("idx").getAsInt()));
                        } else {
                            realtime_itemList.add(new ReviewData("none", result.getRealtime_posting().get(i).get("place_name").getAsString()
                                    , (int) (result.getRealtime_posting().get(i).get("rating").getAsDouble()), result.getRealtime_posting().get(i).get("context").getAsString(),result.getRealtime_posting().get(i).get("idx").getAsInt()));
                        }

                    }

                    realtime_adpater = new SearchRealtimeAdapter(getActivity(), realtime_itemList, onClickItem_feed);
                    realtime_listview.setAdapter(realtime_adpater);
                    progressOFF();

                }
                else {
                    progressOFF();
                    Log.d("data", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressOFF();
                Log.d("response", "????????????");
                t.printStackTrace();
            }
        });

    }

    //----------------------------
    /*  ????????? ???????????? ?????????     */
//----------------------------
    public class NetworkTask extends AsyncTask<Void, Void, Result> {

        Result value;
        ContentValues values;
        Context mcontext;

        NetworkTask(Context mcontext, ContentValues values) {
            this.mcontext = mcontext;
            this.values = values;
        } // ?????????

        NetworkTask(Context mcontext, Result value) {
            this.mcontext = mcontext;
            this.value = value;
        }//?????????

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar??? ???????????? ????????? ??????
        }  //?????? ????????? ???????????? ????????? ???????????? ??????

        @Override
        protected Result doInBackground(Void... params) {

            //?????? ??????????????? ui????????? ?????????
            result_itemList = new ArrayList<>();
            Log.d("Size: ", Integer.toString(value.getHome_recommendation().size()));

            for (int i = 0; i < value.getHome_recommendation().size(); i++) {
                String photo_reference = "";
                JsonArray place_id_list = value.getHome_recommendation().get(i).get("recommend_place").getAsJsonArray();
                for (int j = 0; j < place_id_list.size(); j++) {
                    JsonObject JsonObj = place_id_list.get(j).getAsJsonObject();
                    Log.d("???????????? ????????? ",JsonObj.get("place_id").getAsString());
                    Place place = (PlacesService.details(JsonObj.get("place_id").getAsString()));
                    if (place.getPhotos() != null) {
                        try {
                            JSONObject tmp;
                            if(place.getPhotos()!=null)
                            {
                                tmp = (JSONObject) place.getPhotos().get(0);//????????? ????????? ???????????? ????????????.
                                photo_reference = (String) tmp.get("photo_reference");
                                result_itemList.add(new FirstRecommendData(PlacesService.Photo(photo_reference),JsonObj.get("place_id").getAsString()));
                            }
                            else{
                                result_itemList.add(new FirstRecommendData(PlacesService.Photo(null),JsonObj.get("place_id").getAsString()));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    else {
//                        Drawable drawable = getResources().getDrawable(R.drawable.movieposter1);
//                        // drawable ????????? bitmap?????? ??????
//                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                        result_itemList.add(new FirstRecommendData(bitmap));
                    }
                }
            }

            return value; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        } //??????????????? ?????? ??????

        //------------------------------------
        /* ????????? ?????? ????????? ???????????? UI ??????  */
        //------------------------------------
        @Override
        protected void onPostExecute(Result result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????.

            if (result != null) {
                adapter = new SearchFirstRecommendAdapter(getActivity(), result_itemList, onClickItem);

                listview.setAdapter(adapter);

                MyListDecoration myListDecoration = new MyListDecoration();
                listview.addItemDecoration(myListDecoration);
            } else {
                Toast.makeText(getActivity(), "??????????????? ??????.", Toast.LENGTH_SHORT).show();
            }
        }
    } //NetWorkTask Class

    public void progressON(String message) {
        ImageManager.getInstance().progressON((Activity)AppManager.getInstance().getContext(), message);
    }

    public void progressOFF() {
        ImageManager.getInstance().progressOFF();
    }

}
