package com.foresthouse.dynamiccrawler.utils;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimationSets {
    public static final String TAG = "[ AnimationSets ]";

    public static final short X_AXIS = 0;
    public static final short Y_AXIS = 1;

    public static void jumpTo(final View v, final short direction, final int distance, final int duration){
        v.animate().translationX(distance*(1-direction)).translationY(distance*direction).setInterpolator(new DecelerateInterpolator(1.f)).setDuration(duration).setStartDelay(0).setListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        v.animate().translationX(0).translationY(0).setInterpolator(new DecelerateInterpolator(1.f)).setDuration(duration).setStartDelay(0);
                    }
                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }
                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

//    public static void shakeTo(final View v, final short direction, final int distanceMax, final int duration){
//        final int oDistanceMaxX = distanceMax*(1-direction);
//        final int oDistanceMaxY = distanceMax*direction;
////        v.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.shake_horizontal));
//    }
    public static class ShakeAsyncTask extends AsyncTask<Void, Integer, Void>{

        @SuppressLint("StaticFieldLeak")
        View v; // NULL 처리
        int count;
        int distanceMax;
        int duration;
        int reverse = 1;
        public ShakeAsyncTask(View v, int count, int distanceMax, int duration){
            this.v = v;
            this.count = count;
            this.distanceMax = distanceMax;
            this.duration = duration;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int x = count; x >= 0; x--){
                publishProgress(reverse*distanceMax*x/count);
                reverse *= -1; //방향 바꾸기
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } //Sleep
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... distance) {
            super.onProgressUpdate(distance);
            v.animate().translationX(distance[0].floatValue()).setInterpolator(new DecelerateInterpolator(1.f)).setDuration(duration);
//            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.ApplicationContext, R.anim.shake_horizontal));
        }
        /* 메모리 누수 방지를 위한 명시적 NULL 처리 */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            v = null;
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            v = null;
        }
    }
}
