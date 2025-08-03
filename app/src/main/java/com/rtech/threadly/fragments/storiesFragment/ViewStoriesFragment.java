package com.rtech.threadly.fragments.storiesFragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rtech.threadly.adapters.storiesAdapters.StoriesViewpagerAdapter;
import com.rtech.threadly.databinding.FragmentViewStoriesBinding;
import com.rtech.threadly.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.threadly.interfaces.OnDestroyFragmentCallback;
import com.rtech.threadly.interfaces.StoriesBackAndForthInterface;
import com.rtech.threadly.managers.StoriesManager;
import com.rtech.threadly.models.StoryMediaModel;
import com.rtech.threadly.utils.ExoplayerUtil;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.StoriesViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ViewStoriesFragment extends Fragment {
    FragmentViewStoriesBinding mainXml;
    OnDestroyFragmentCallback callback;
    StoriesViewModel storiesViewModel;
    ArrayList<StoryMediaModel> storiesData;
    String Userid,profilePic;
    StoriesViewpagerAdapter storiesViewpagerAdapter;
    LinearLayoutManager layoutManager;
    StoriesBackAndForthInterface backAndForthInterface;
    StoriesManager storiesManager;



    public ViewStoriesFragment(OnDestroyFragmentCallback callback,StoriesBackAndForthInterface backAndForthInterface) {
        this.callback=callback;
        this.backAndForthInterface=backAndForthInterface;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainXml=FragmentViewStoriesBinding.inflate(inflater,container,false);
        init();
        load();
        mainXml.progressBar.setVisibility(View.VISIBLE);
        mainXml.viewPager.setVisibility(View.GONE);

//        storiesViewModel.getStoryOf(Userid).observe(getViewLifecycleOwner(), new Observer<ArrayList<StoryMediaModel>>() {
//            @Override
//            public void onChanged(ArrayList<StoryMediaModel> storyMediaModels) {
//                if(storyMediaModels.isEmpty()){
//                    ReUsableFunctions.ShowToast("something went Wrong!");
//                    requireActivity().onBackPressed();
//                }
//                else{
//                    mainXml.progressBar.setVisibility(View.GONE);
//                    mainXml.viewPager.setVisibility(View.VISIBLE);
//                    storiesData.clear();
//                    storiesData.addAll(storyMediaModels);
//                    storiesViewpagerAdapter.notifyDataSetChanged();
//
//                }
//            }});

        return mainXml.getRoot();
    }

    private void load() {
        ArrayList<StoryMediaModel> arrayList=new ArrayList<>();
        storiesManager.getStoriesOf(Userid, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray array=response.getJSONArray("data");
                    if(array.length()>0){

                        for(int i=0;i<array.length();i++){
                            JSONObject object =array.getJSONObject(i);
                            arrayList.add(new StoryMediaModel(
                                    object.getString("userid"),
                                    object.getInt("id"),
                                    object.getString("storyUrl"),
                                    object.getString("type"),
                                    object.getString("createdAt"),
                                    object.getInt("isLiked")
                            ));

                        }
                        storiesData.clear();
                        storiesData.addAll(arrayList);
                        storiesViewpagerAdapter.notifyDataSetChanged();
                        mainXml.progressBar.setVisibility(View.GONE);
                        mainXml.viewPager.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    ReUsableFunctions.ShowToast("something went Wrong!");
                    requireActivity().onBackPressed();
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(String err) {
                ReUsableFunctions.ShowToast("something went Wrong!");
                requireActivity().onBackPressed();

            }
        });

    }

    private void init() {
        Bundle bundle=getArguments();
        storiesManager=new StoriesManager();
        if(bundle!=null){
            Userid=bundle.getString("userId");
            profilePic=bundle.getString("profilePic");
        }else{
            Userid="null";
        }
        storiesData=new ArrayList<>();
        storiesViewpagerAdapter=new StoriesViewpagerAdapter(storiesData, requireActivity(), Userid, profilePic, new StoriesBackAndForthInterface() {
            @Override
            public void previous(int position) {
                if(position>0){
                    mainXml.viewPager.setCurrentItem(position-1,true);
                }else{
                    backAndForthInterface.previous(-1);
                }


            }

            @Override
            public void next(int position,int size) {
                if(position<size-1){
                    mainXml.viewPager.setCurrentItem(position+1,true);
                }else{
                    backAndForthInterface.next(-1,-1);

                }

            }
        });
        mainXml.viewPager.setAdapter(storiesViewpagerAdapter);
        mainXml.viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);


        storiesViewModel=new ViewModelProvider(requireActivity()).get(StoriesViewModel.class);

        mainXml.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @UnstableApi
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mainXml.viewPager.postDelayed(()->{
                    StoriesViewpagerAdapter.viewHolder viewHolder =(StoriesViewpagerAdapter.viewHolder)((RecyclerView) mainXml.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(position);

                    if(viewHolder!=null){
                        if(storiesData.get(position).isVideo()){
                            ExoplayerUtil.stop();
                            ExoplayerUtil.playNoLoop(Uri.parse(storiesData.get(position).getStoryUrl()),viewHolder.playerView);

                        }else{
                            ExoplayerUtil.stop();
                        }


                    }
                },mainXml.viewPager.isFakeDragging()?150:50);


            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        ExoplayerUtil.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback.onDestroy();
    }
}