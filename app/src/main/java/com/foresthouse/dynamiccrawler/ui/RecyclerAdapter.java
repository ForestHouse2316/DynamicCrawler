package com.foresthouse.dynamiccrawler.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.R;
import com.foresthouse.dynamiccrawler.ui.ItemTouch.ItemTouchHelperListener;
import com.foresthouse.dynamiccrawler.ui.nav_fragment.codelist.CodeListFragment;
import com.foresthouse.dynamiccrawler.utils.DataManager;
import com.foresthouse.dynamiccrawler.utils.Generator;
import com.foresthouse.dynamiccrawler.utils.database.CodeCellEntity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements ItemTouchHelperListener {
    private static final String TAG = "[ RecyclerAdapter ]";

    public ArrayList<CodeCellEntity> CodeCellEntityBundle;
    public static int animCount = 0;
    public static int firstVisibleItemPos;
    public static int lastVisibleItemPos;
    public static int numberOfVisibleItem = -1;
    public static boolean doAnimation = true;

    private static CodeCellEntity SelectedCell;
    Context ctx;

    public RecyclerAdapter(ArrayList<CodeCellEntity> array, Context ctx){
        this.CodeCellEntityBundle = array;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context Ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)Ctx.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        View view = inflater.inflate(R.layout.item_code_list_cell, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CodeCellEntity curEntity = CodeCellEntityBundle.get(position);
        holder.CodeName.setText(curEntity.getCodeName());
        holder.ThirdPartyIndicator.setVisibility(curEntity.isThirdParty() ? View.VISIBLE : View.GONE);
        holder.TriggerIndicator.setVisibility((curEntity.isTriggered() ? View.VISIBLE : View.GONE));
    }

    @Override
    public int getItemCount() {
        try {
            return CodeCellEntityBundle.size();
        } catch (NullPointerException e) { //DB 에 아무것도 없으면 ArrayList 가 NULL 임
            return 0;
        }
    }

    @Override
    public boolean onItemMove(int from, int to) {
        return false;
    }

    @Override
    public void onItemSwipe(final int pos, final int direction) {
        final int LEFT = 16;
        final int RIGHT = 32;

        Log.d(TAG, "onItemSwipe: "+direction);
        final CodeCellEntity curCell = CodeCellEntityBundle.get(pos);
        if (direction == LEFT) { //LEFT Swipe
            Generator.makeYNDialog(ctx, curCell.getCodeName() + " " + DataManager.getStringResources(R.string.str_delete),
                                   DataManager.getStringResources(R.string.str_delete_warn_msg), DataManager.getStringResources(R.string.str_delete),
                                   DataManager.getStringResources(R.string.str_cancel), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DataManager.removeCode(curCell);
                            CodeCellEntityBundle.remove(pos);
                            if (!CodeCellEntityBundle.isEmpty()) {
                                CodeListFragment.ignoreChangeOnce = true;
                            }
                            notifyItemRemoved(pos);
                            Generator.makeToastMessage(MainActivity.ApplicationContext, DataManager.getStringResources(R.string.str_deleted));
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    notifyItemChanged(pos);
                }
            }, null, null);
        } else if (direction == RIGHT){ //RIGHT Swipe
            setSelectedCell(curCell);
            try {
//                MainActivity.MainContext.startActivity(new Intent(MainActivity.MainContext, EditorActivity.class));
                MainActivity.ApplicationContext.startActivity(new Intent(MainActivity.ApplicationContext, EditorActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (NullPointerException e) {
                Log.d(TAG, "onItemSwipe: 너무 빨리 수정모드에 진입하여 NullPointException 발생");
//                Generator.makeToastMessage(MainActivity.ApplicationContext, MainActivity.ApplicationContext.getString(R.string.str_too_fast));
            }
        }

    }

    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "[ ViewHolder ]";

        TextView CodeName;
        ImageView ThirdPartyIndicator;
        ImageView TriggerIndicator;


        public ViewHolder(@NonNull final View v) {


            super(v);
            if (numberOfVisibleItem == animCount && numberOfVisibleItem != -1) { // 애니메이션 여부 결정. 내폰 기준으로 nOVI = 7, animCount = 현재 셀 인덱스
                doAnimation = false;
                animCount = 0;
                Log.d(TAG, "ViewHolder: 셀 애니메이션 중지");
            }else if (doAnimation){
                v.setTranslationX(200);
                v.setAlpha(0.f);
                v.animate().translationX(0).alpha(1.f).setStartDelay(doAnimation ? 100*animCount+1 : 0).setInterpolator(new DecelerateInterpolator(1.f)).setDuration(400);
                animCount += 1;
            }
            CodeName = v.findViewById(R.id.tv_code_name);
            ThirdPartyIndicator = v.findViewById(R.id.img_third_party_icon);
            TriggerIndicator = v.findViewById(R.id.img_trigger_icon);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }
    }

    // 선택된 셀 변수에 대해 직접 접근 방지
    public static CodeCellEntity getSelectedCell() {
        if (SelectedCell == null) {
            Log.w(TAG, "getSelectedCell: 선택된 코드를 불러올 수 없음.");
            Generator.makeYNDialog(MainActivity.ApplicationContext, DataManager.getStringResources(R.string.str_error_occured),
                                   "Error Code : Cannot check selected cell", DataManager.getStringResources(R.string.str_confirm), null, null, null,
                                   null, null, null);
            return new CodeCellEntity("[ Error ]", false, false, "An error occurred :(\n" + "Error Code : Cannot check selected cell\n" +
                    "If this error continues in the future, please send feedback with 'ErrorCode' and the thing you tried to do to DEV e-mail." +
                    "(You can check e-mail on PlayStore)" +
                    "Saving this code isn't recommended, because name '[ Error ]' code will be made if you do so.");
        }
        CodeCellEntity entity = SelectedCell;
        SelectedCell = null;
        return entity;
    }

    public static void setSelectedCell(CodeCellEntity entity) {
        if (SelectedCell != null) {
            Log.w(TAG, "setSelectedCell: 선택된 셀 값이 이미 존재합니다. 새 객체로 덮어쓰기 되며 안정성을 위해 코드를 확인하세요.");
        }
        SelectedCell = entity;
    }
}


