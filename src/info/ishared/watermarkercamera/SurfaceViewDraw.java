package info.ishared.watermarkercamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceViewDraw extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap bmp;
    private String imgPath = "";
    protected SurfaceHolder sh; // 专门用于控制surfaceView的
    private int width;
    private int height;

    // XML文件解析需要调用View的构造函数View(Context , AttributeSet)
    // 因此自定义SurfaceView中也需要该构造函数
    public SurfaceViewDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        sh = getHolder();
        sh.addCallback(this);
        sh.setFormat(PixelFormat.TRANSPARENT); // 设置为透明
        setZOrderOnTop(true);// 设置为顶端
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
        // TODO Auto-generated method stub
        width = w;
        height = h;
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    void clearDraw() {

        Canvas canvas = sh.lockCanvas();
        canvas.drawColor(Color.BLUE);// 清除画布
        sh.unlockCanvasAndPost(canvas);
    }

    /**
     * 绘制
     */
    public void doDraw() {
        if (bmp != null) {
            Canvas canvas = sh.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景
            Paint p = new Paint(); // 笔触
            p.setAntiAlias(true); // 反锯齿
            p.setColor(Color.RED);
            p.setStyle(Style.STROKE);
            canvas.drawBitmap(bmp, 0, 0, p);
            canvas.drawLine(width / 2 - 100, 0, width / 2 - 100, height, p);
            canvas.drawLine(width / 2 + 100, 0, width / 2 + 100, height, p);
            // ------------------------ 画边框---------------------
            Rect rec = canvas.getClipBounds();
            rec.bottom--;
            rec.right--;
            p.setColor(Color.GRAY); // 颜色
            p.setStrokeWidth(5);
            canvas.drawRect(rec, p);
            // 提交绘制
            sh.unlockCanvasAndPost(canvas);
        }

    }

    public void drawLine() {

        Canvas canvas = sh.lockCanvas();

        canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景
        Paint p = new Paint(); // 笔触
        p.setAntiAlias(true); // 反锯齿
        p.setColor(Color.RED);
        p.setStyle(Style.STROKE);
        canvas.drawLine(width / 2 - 100, 0, width / 2 - 100, height, p);
        canvas.drawLine(width / 2 + 100, 0, width / 2 + 100, height, p);

        // 提交绘制
        sh.unlockCanvasAndPost(canvas);
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
        // 根据路径载入目标图像
        bmp = BitmapFactory.decodeFile(imgPath);
    }

}
