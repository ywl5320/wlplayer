# wlplayer v1.0.1（讨论群：806397913）

## [我的视频课程（基础）：《（NDK）FFmpeg打造Android万能音频播放器》](https://edu.csdn.net/course/detail/6842)
## [我的视频课程（进阶）：《（NDK）FFmpeg打造Android视频播放器》](https://edu.csdn.net/course/detail/8036)
## [我的视频课程（编码直播推流）：《Android视频编码和直播推流》](https://edu.csdn.net/course/detail/8942)

## Update
### *.添加 x86 cpu架构
### *.修复seek后导致音视频不同步问题
### *.视频解码慢时，进行丢帧处理


### Android 基于：FFmpeg+OpenSL+OpenGL+Mediacodec 的视频播放SDK，可播放网络、本地和广播等流媒。
### 1、支持当前播放页直接切换播放源。
### 2、支持视频实时截图。
### 3、优先选用GPU解码，解码速度更快。
### 4、在手机支持1080P、2K、4K等的情况下都可播放。
### 5、封装常用播放状态回调，接入简单。
### 6、直接用mediacodec解码avpacket数据。
### 7、重点是可以学到很多东西：java与C++的相互调用、多线程的使用、opengl、opensl的使用等等。
### 8、附带一个播放视频和广播的demo。

# 以下是实例图片、API和接入流程
## APP Demo（注：视频数据来自于熊猫TV，广播数据来自于中国广播网）
### [App Demo 下载地址](https://pan.baidu.com/s/1cfjQAM)
![image](https://github.com/wanliyang1990/wlplayer/blob/master/imgs/1.png)
![image](https://github.com/wanliyang1990/wlplayer/blob/master/imgs/2.png)</br>
![image](https://github.com/wanliyang1990/wlplayer/blob/master/imgs/3.png)
![image](https://github.com/wanliyang1990/wlplayer/blob/master/imgs/4.png)</br>
![image](https://github.com/wanliyang1990/wlplayer/blob/master/imgs/5.png)</br>
![image](https://github.com/wanliyang1990/wlplayer/blob/master/imgs/6.png)</br>
![image](https://github.com/wanliyang1990/wlplayer/blob/master/imgs/7.png)</br>

## 一、API v1.0.1

#### 1、回调接口
    //准备好播放后回调此接口
    WlOnPreparedListener
    
    //视频加载回调此接口
    WlOnLoadListener
    
    //视频时长和当前播放时长回调此接口
    WlOnInfoListener
    
    //视频出错回调此接口
    WlOnErrorListener
    
    //视频播放结束回调此接口
    WlOnCompleteListener
    
    //视频截屏回调此接口
    WlOnCutVideoImgListener
    
    //播放页切换播放源时回调此接口（stop(false)时），在此接口可重新设置新的播放源
    WlOnStopListener

#### 2、方法
    //设置播放源
    void setDataSource(String dataSource);
    
    //设置是否播放音频（广播）
    void setOnlyMusic(boolean onlyMusic)
    
    //设置视频渲染glsurfaceview
    void setWlGlSurfaceView(WlGlSurfaceView wlGlSurfaceView)
    
    //准备播放（对应回调接口）
    void prepared()
    
    //准备好后，开始播放
    void start()
    
    //暂停
    void pause()
    
    //播放（相对于暂停）
    void resume()
    
    //停止 true：不回调停止接口，false:回调停止接口
    void stop(final boolean exit)
    
    //seek到任意时间（不是关键帧，可能会出现几秒钟花屏）
    void seek(final int secds)
    
    //得到总播放时长
    int getDuration()
     
    //得到视频宽度
    int getVideoWidth()
    
    //得到视频高度
    int getVideoHeight()
    
    //得到音轨数
    int getAudioChannels()
    
    //设置音频音轨（根据音轨数量设置索引）
    void setAudioChannels(int index)
    

## 二、接入流程
#### 1、添加布局
    <com.ywl5320.opengles.WlGlSurfaceView
        android:id="@+id/surfaceview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
     
#### 2、创建播放器对象


    private WlGlSurfaceView surfaceview;
    private WlPlayer wlPlayer;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
        wlPlayer = new WlPlayer();
        wlPlayer.setOnlyMusic(false); // true:播放广播，false:播放视频
        wlPlayer.setDataSource(pathurl); //设置播放源
        wlPlayer.setWlGlSurfaceView(surfaceview); //播放广播可不加视频渲染布局
    
    }

#### 3、准备播放
    wlPlayer.prepared();
    
#### 4、添加回调（注：都在子线程中）

    //视频准备好播放时回调
    wlPlayer.setWlOnPreparedListener(new WlOnPreparedListener() {
            @Override
            public void onPrepared() {
        
                wlPlayer.start();//开始播放
                
            }
        });
        
    //加载回调
    wlPlayer.setWlOnLoadListener(new WlOnLoadListener() {
            @Override
            public void onLoad(boolean load) {//true:加载中 false:加载完成
                
                Message message = Message.obtain();
                message.what = 1;
                message.obj = load;
                handler.sendMessage(message);
            }
        });
        
    //播放时间信息回调
    wlPlayer.setWlOnInfoListener(new WlOnInfoListener() {
            @Override
            public void onInfo(WlTimeBean wlTimeBean) {//当前播放时间和总的时间
            
                Message message = Message.obtain();
                message.what = 2;
                message.obj = wlTimeBean;
                MyLog.d("nowTime is " +wlTimeBean.getCurrt_secds());
                handler.sendMessage(message);
            }
        });
        
    //错误回调
    wlPlayer.setWlOnErrorListener(new WlOnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                MyLog.d("code:" + code + ",msg:" + msg);
                Message message = Message.obtain();
                message.obj = msg;
                message.what = 3;
                handler.sendMessage(message);
            }
        });
        
    //播放完成回调
    wlPlayer.setWlOnCompleteListener(new WlOnCompleteListener() {
            @Override
            public void onComplete() {
                MyLog.d("complete .....................");
                wlPlayer.stop(true);
            }
        });
        
    //视频截屏回调
    wlPlayer.setWlOnCutVideoImgListener(new WlOnCutVideoImgListener() {
            @Override
            public void onCutVideoImg(Bitmap bitmap) {
                Message message = Message.obtain();
                message.what = 4;
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        });
        
    //停止播放回调（在wlPlayer.stop(false)等情况下才会回调这个方法，里面可处理切换播放源操作）
    wlPlayer.setWlOnStopListener(new WlOnStopListener() {
            @Override
            public void onStop() {
                Message message = Message.obtain();
                message.what = 3;
                handler.sendMessage(message);
            }
        });
        




## TODO
### 兼容性和性能优化
</br>

## 注
#### 当前环境：FFmpeg-3.4, AS-3.0， NDK-14b，小米手机2A
#### CPU：arm 和 x86
#### 2018-01-01 happy new year！
</br>

## Create by:ywl5320
</br>

