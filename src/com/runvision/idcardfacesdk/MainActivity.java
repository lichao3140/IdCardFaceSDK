package com.runvision.idcardfacesdk;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.arcsoft.idcardveri.CompareResult;
import com.arcsoft.idcardveri.DetectFaceResult;
import com.arcsoft.idcardveri.IdCardVerifyError;
import com.arcsoft.idcardveri.IdCardVerifyManager;
import java.lang.reflect.Method;
import java.util.List;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    //�ȶ���ֵ������Ϊ0.82
    private static final double THRESHOLD = 0.82d;
    private SurfaceView surfaceView;
    private SurfaceView surfaceRect;
	private Camera camera;
    //��Ƶ��ͼƬ���������Ƿ��⵽
    private boolean isCurrentReady = false;
    //���֤���������Ƿ��⵽
    private boolean isIdCardReady = false;
    //���Դ���
    private static final int MAX_RETRY_TIME = 2;
    private int tryTime = 0;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ��ʼ��SDK
        ShowMessage(RunvisionUtil.initSDK());

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        SurfaceHolder surfaceholder = surfaceView.getHolder();
        surfaceholder.addCallback(this);
        surfaceRect = (SurfaceView) findViewById(R.id.surfaceview_rect);
        surfaceRect.setZOrderMediaOverlay(true);
        surfaceRect.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Button btnIdCard = (Button) findViewById(R.id.btn_idcard);
        btnIdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //�������֤����
                inputIdCard();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // ����ʼ������������
        IdCardVerifyManager.getInstance().unInit();
        if (camera != null) {
            camera.release();
        }
        super.onDestroy();
    }

    private void inputIdCard() {
        //���֤���� ����ʵ����������
        byte[] nv21Data = new byte[1024];
        //���֤���ݿ�
        int width = 0;
        //���֤���ݸ�
        int height = 0;

        // �������֤ͼƬ�����������
        // data: ���֤ͼ������,֧��NV21 ��ʽ
        DetectFaceResult result = IdCardVerifyManager.getInstance().inputIdCardData(nv21Data, width, height);
        Log.d(TAG, "inputIdCardData result: " + result.getErrCode());
        if(result.getErrCode() == IdCardVerifyError.OK) {
            isIdCardReady = true;
            compare();
        }
    }

    private void inputImage() {
        //ͼƬ���� ����ʵ����������
        byte[] nv21Data = new byte[1024];
        //ͼƬ���ݿ�
        int width = 0;
        //ͼƬ���ݸ�
        int height = 0;

        //������Ƶ��ͼƬ���ݽ���������⣨�ֳ��ɼ��ռ�⣩
        //��ƵΪ true �����ΪͼƬ����������Ϊ false
        // data : ͼ��������NV21 ��ʽ
        DetectFaceResult result = IdCardVerifyManager.getInstance().onPreviewData(nv21Data, width, height, false);
        Log.d(TAG, "onPreviewData image result: " + result.getErrCode());
        if(result.getErrCode() == IdCardVerifyError.OK) {
            isCurrentReady = true;
            compare();
        }
    }

    /**
     * �Ա�
     */
    private void compare() {
        if(!isCurrentReady || !isIdCardReady) {
            return;
        }

        // ��⵽�����������֤�����ȶ�
        CompareResult compareResult = IdCardVerifyManager.getInstance().compareFeature(THRESHOLD);
        Log.d(TAG, "compareFeature: result " + compareResult.getResult() + ", isSuccess "
                + compareResult.isSuccess() + ", errCode " + compareResult.getErrCode());
        isIdCardReady = false;
        isCurrentReady = false;
        if(!compareResult.isSuccess() && tryTime < MAX_RETRY_TIME) {
            tryTime++;
            inputIdCard();
        } else {
            tryTime = 0;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // ǰ������ͷ Camera.CameraInfo.CAMERA_FACING_FRONT
        camera = Camera.open(0);
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size previewSize = getBestSupportedSize(parameters.getSupportedPreviewSizes(), metrics);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            camera.setParameters(parameters);
            final int mWidth = previewSize.width;
            final int mHeight = previewSize.height;
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    //��Ƶ����
                    DetectFaceResult result = IdCardVerifyManager.getInstance().onPreviewData(data, mWidth, mHeight, true);
                    if (surfaceRect != null) {
                        Canvas canvas = surfaceRect.getHolder().lockCanvas();
                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        Rect rect = result.getFaceRect();
                        if (rect != null) {
                            Rect adjustedRect = DrawUtils.adjustRect(rect, mWidth, mHeight,
                                    canvas.getWidth(), canvas.getHeight(), 90, 1);
                            //��������
                            DrawUtils.drawFaceRect(canvas, adjustedRect, Color.YELLOW, 5);
                        }
                        surfaceRect.getHolder().unlockCanvasAndPost(canvas);
                    }
                    if(result.getErrCode() == IdCardVerifyError.OK) {
                        Log.d(TAG, "onPreviewData video result: " + result);
                        isCurrentReady = true;
                        compare();
                    }
                }
            });
            camera.startPreview();
        } catch (Exception e) {
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, DisplayMetrics metrics) {
        Camera.Size bestSize = sizes.get(0);
        float screenRatio = (float) metrics.widthPixels / (float) metrics.heightPixels;
        if (screenRatio > 1) {
            screenRatio = 1 / screenRatio;
        }

        for (Camera.Size s : sizes) {
            if (Math.abs((s.height / (float) s.width) - screenRatio) < Math.abs(bestSize.height /
                    (float) bestSize.width - screenRatio)) {
                bestSize = s;
            }
        }
        return bestSize;
    }
    
    private void ShowMessage(String info) {
        Toast.makeText(this,info, Toast.LENGTH_LONG).show();
    }

}
