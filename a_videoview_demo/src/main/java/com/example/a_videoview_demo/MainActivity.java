package com.example.a_videoview_demo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

//作用:加载视频,它可以设置多种自定义属性(如loading样式,错误视频等),并且很容易在全屏与非全屏之间切换
//缺点:他实际就是UI效果的改变,加载视频依旧用的是系统原生的MediaPlayer,因此系统可以加载什么格式的视频,他就可以加载什么格式的视频,系统加载不了,他也不能加载
//研究技术的方法:
// 第一步根据自己的需求,在GitHub找合适的资源
//阅读GitHub上的使用指南,下载提供的资源
//找到资源里的demo,通过这个demo运行出效果,看是否大致符合需要
//理清Demo里的代码思路,然后粘贴复制做出新的demo和自己的需求一模一样
//根据核心的逻辑代码写清注释,最后集成到自己的项目中
public class MainActivity extends AppCompatActivity implements UniversalVideoView.VideoViewCallback {
    private View videoLayout;
    private View bottomLayout;
    private UniversalVideoView videoView;
    private UniversalMediaController mediaController;
    private int seekPosition;//当前进度
    private int cachedHeight;//视频区域大小
    private boolean isFullscreen;//是否全屏
    private static final String VIDEO_URL = "http://vf1.mtime.cn/Video/2017/03/15/mp4/170315222409670447.mp4";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";//用于保存seekPosition的key值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        intiEvent();
    }
    private void initView() {
        videoLayout = findViewById(R.id.videoLayout);
        bottomLayout = findViewById(R.id.bottomLayout);
        videoView = (UniversalVideoView) findViewById(R.id.videoView);
        mediaController = (UniversalMediaController) findViewById(R.id.mediaController);
    }
    private void intiEvent() {
//把视频控制的按钮设置到播放器里
        videoView.setMediaController(mediaController);
//设置置视频区域大小
        setVideoAreaSize();
//设置屏幕状态和播放状态的监听
        videoView.setVideoViewCallback(this);
    }
    private void setVideoAreaSize() {
        videoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = videoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
// cachedHeight = (int) (width * 3f / 4f);
// cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = videoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                videoLayout.setLayoutParams(videoLayoutParams);
                videoView.setVideoPath(VIDEO_URL);
                videoView.requestFocus();
            }
        });
    }
    /**
     * 开始按钮
     *
     * @param v
     */
    public void startClick(View v) {
//设置视频开始
        videoView.start();
//设置视频标题
        mediaController.setTitle("寻梦环游记");
    }
//--------------重写下面三个方面是为了保存seekPosition--------------
    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            seekPosition = videoView.getCurrentPosition();
            Log.i("info", "onPause mSeekPosition=" + seekPosition);
            videoView.pause();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("info", "onSaveInstanceState Position=" + videoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, seekPosition);
    }
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        seekPosition = outState.getInt(SEEK_POSITION_KEY);
        Log.i("info", "onRestoreInstanceState Position=" + seekPosition);
    }
//--------------以下方法都是VideoViewCallback接口的实现方法--------------
    /**
     * 全屏和默认的切换
     *
     * @param isFullscreen
     */
    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = videoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            videoLayout.setLayoutParams(layoutParams);
//设置全屏时,无关的View消失,以便为视频控件和控制器控件留出最大化的位置
            bottomLayout.setVisibility(View.GONE);
        } else {
            ViewGroup.LayoutParams layoutParams = videoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            videoLayout.setLayoutParams(layoutParams);
            bottomLayout.setVisibility(View.VISIBLE);
        }
        switchTitleBar(!isFullscreen);
    }
    /**
     * 横竖屏切换的时候控制ActionBar的状态
     *
     * @param show
     */
    private void switchTitleBar(boolean show) {
        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (show) {
                supportActionBar.show();
            } else {
                supportActionBar.hide();
            }
        }
    }
    @Override
    public void onPause(MediaPlayer mediaPlayer) {// 视频暂停
        Log.i("info", "onPause");
    }
    @Override
    public void onStart(MediaPlayer mediaPlayer) {// 视频开始播放或恢复播放
        Log.i("info", "onStart");
    }
    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {// 视频开始缓冲
        Log.i("info", "onBufferingStart");
    }
    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {// 视频结束缓冲
        Log.i("info", "onBufferingEnd");
    }

    //退出按钮执行的回调方法
    @Override
    public void onBackPressed() {

        //判断是否出去播放全屏,进行不同逻辑处理,如果是全屏,我就先退出全屏
        if (this.isFullscreen) {
            videoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }
}


