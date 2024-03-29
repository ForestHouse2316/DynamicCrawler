package com.foresthouse.dynamiccrawler.ui.nav_fragment.codelist;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.R;
import com.foresthouse.dynamiccrawler.ui.ItemTouch.ItemTouchHelperCallback;
import com.foresthouse.dynamiccrawler.ui.RecyclerAdapter;
import com.foresthouse.dynamiccrawler.utils.DataManager;
import com.foresthouse.dynamiccrawler.utils.database.CodeCellEntity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CodeListFragment extends Fragment {
    private static final String TAG = "[ CodeListFragment ]";

    public static RecyclerView CodeList;
    public RecyclerView.LayoutManager CodeListLayoutManager;
    public RecyclerAdapter CodeListAdapter;
    public static boolean ignoreChangeOnce = false;
    LinearLayout SwipeGuide;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_codes, container, false);
        MainActivity.CurrentFragment = MainActivity.FRAGMENT_CODE_LIST;
        //스와이프 가이드 설정
        SwipeGuide = root.findViewById(R.id.layout_swipe_guide);
        SwipeGuide.setVisibility(View.VISIBLE);
        //fab 설정
        MainActivity.fab.setVisibility(View.VISIBLE);


        //리사이클러뷰 애니메이션 관련 작업
        RecyclerAdapter.doAnimation = true;
        RecyclerAdapter.numberOfVisibleItem = -1;
        RecyclerAdapter.animCount = 0;

        //리사이클러뷰 초기화
        CodeList = root.findViewById(R.id.recycler_code_list);
        CodeListLayoutManager = new LinearLayoutManager(container.getContext()){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                RecyclerAdapter.firstVisibleItemPos = findFirstVisibleItemPosition();
                RecyclerAdapter.lastVisibleItemPos = findLastVisibleItemPosition();
//                Log.d(TAG, "onLayoutCompleted: 시작=>"+findFirstVisibleItemPosition()+"\t마지막=>"+findLastVisibleItemPosition());
                if (findLastVisibleItemPosition() > 0 && RecyclerAdapter.numberOfVisibleItem == -1){
                    RecyclerAdapter.numberOfVisibleItem = RecyclerAdapter.lastVisibleItemPos - RecyclerAdapter.firstVisibleItemPos+ 1;
                }
            }
        };
        CodeList.setLayoutManager(CodeListLayoutManager);

        //리사이클러뷰 어댑터 설정 및 새로고침
        CodeListAdapter = new RecyclerAdapter(DataManager.getAllCodeData(), root.getContext());
        if (CodeListAdapter.CodeCellEntityBundle != null) {
            ignoreChangeOnce = true; //옵저버 등록 후 생기는 첫 콜백 무시 (무조건 일어남)
        }else{
            RecyclerAdapter.doAnimation = false; //코드가 하나도 없다면 코드 추가시 애니메이션 안 나오도록 끔
        }
        CodeList.setAdapter(CodeListAdapter);
        CodeListAdapter.notifyDataSetChanged();

        DataManager.DB.DAO().getDBObserver().observe(getViewLifecycleOwner(), new Observer<CodeCellEntity>() {
            @Override
            public void onChanged(CodeCellEntity codeCellEntity) {
                try {
                    Log.d(TAG, "onChanged: 코드 < "+ codeCellEntity.getCodeName()+" > 의 변경 감지");
                    if (!ignoreChangeOnce) {
                        DataManager.reflectAllCodeData(CodeListAdapter);
                    }else{
                        Log.d(TAG, "onChanged: 리사이클러뷰 새로고침 무시됨");
                        ignoreChangeOnce = false;
                    }
//                    CodeListAdapter.notifyDataSetChanged();
                }catch (NullPointerException e){ //만약 DB에 코드가 하나도 없을 경우
                    Log.d(TAG, "onChanged: DB에서 불러올 코드 없음");
                }
            }
        });

        //리사이클러뷰 스와이프 헬퍼 설정
        final ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelperCallback(CodeListAdapter));
        helper.attachToRecyclerView(CodeList);

        CodeList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                helper.onDraw(c, parent, state);
//                super.onDraw(c, parent, state);
            }
        });

        //리사이클러뷰 스와이프 리프레시 설정
        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeLayout = root.findViewById(R.id.layout_code_list_refresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CodeListAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });










        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        CodeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.CurrentFragment != MainActivity.FRAGMENT_CODE_LIST){ //TODO Setting Visibilities
            MainActivity.fab.setVisibility(View.INVISIBLE);
            SwipeGuide.setVisibility(View.GONE);
        }
    }
}
