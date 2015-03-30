/**
 * Copyright 2015 (C) <美衣点点> 
 * 
 * @version 1.0
 */
package com.baymin.graphic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.media.ThumbnailUtils;

import com.baymin.net.toolbox.ImageLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class BitmapUtil.
 */
public class BitmapUtil {
	private static Vector<String> pids = new Vector<String>();
	private static ImageLoader imageLoader;
	
	private static final int ww=480;
	private static final int hh=800;

	/**
	 * Sets the product id.
	 * 
	 * @param pid
	 *            the new product id
	 */
	public static void setProductId(String pid) {
		pids.add(pid);
	}

	/**
	 * Gets the product id.
	 * 
	 * @return the product id
	 */
	public static String getProductId() {
		String pid = pids.get(pids.size() - 1);
		pids.remove(pids.size() - 1);
		return pid;
	}

	/**
	 * Product idis null.
	 * 
	 * @return true, if successful
	 */
	public static boolean productIdisNull() {
		if (pids.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 生成倒影图片.
	 * 
	 * @param id
	 *            the id
	 * @param activity
	 *            the activity
	 * @return the bitmap
	 */
	public static Bitmap convertResToBm(int id, Activity activity) {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inSampleSize = 2; // 将原图缩小四分之一读取
		option.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), id, option);

		return ThumbnailUtils.extractThumbnail(bm, 480, 800); // 将图片的大小限定在480*800
	}

	/**
	 * Convert res to bm.
	 * 
	 * @param id
	 *            the id
	 * @param activity
	 *            the activity
	 * @param height
	 *            the height
	 * @param width
	 *            the width
	 * @return the bitmap
	 */
	public static Bitmap convertResToBm(int id, Activity activity, int height, int width) {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inSampleSize = 2; // 将原图缩小四分之一读取
		option.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), id, option);

		return ThumbnailUtils.extractThumbnail(bm, height, width); // 将图片的大小限定在480*800
	}

	/**
	 * Creates the reflected images.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @return the bitmap
	 */
	public static Bitmap createReflectedImages(Bitmap bitmap) {
		final int space = 10;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectedBitmap = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
		Bitmap resultBitmap = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(resultBitmap);
		canvas.drawBitmap(bitmap, 0, 0, null);

		canvas.drawBitmap(reflectedBitmap, 0, height + space, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, resultBitmap.getHeight()
				+ space, 0x4000ff00, 0x0000ff00, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, resultBitmap.getHeight() + space, paint);

		return resultBitmap;
	}

	/**
	 * 将目标图片裁剪生成一个圆形图片，此种方式抗锯齿效果好 目标图片建议为正方形.
	 * 
	 * @param src
	 *            the src
	 * @return the round bitmap
	 */
	public static Bitmap getRoundBitmap(Bitmap src) {
		Bitmap bm = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(0xFFFFCC44);
		c.drawOval(new RectF(0, 0, src.getWidth(), src.getHeight()), p);
		PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
		p.setXfermode(mode);
		c.drawBitmap(src, 0, 0, p);
		return bm;
	}

	/**
	 * 高效模糊算法.
	 * 
	 * @param sentBitmap
	 *            the sent bitmap
	 * @param radius
	 *            the radius
	 * @return the bitmap
	 */
	public static Bitmap fastblur(Bitmap sentBitmap, int radius) {

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		// Log.e("pix", w + " " + h + " " + pix.length);
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}

	/**
	 * @param options
	 *            BitmapFactory选项
	 * @param reqWidth
	 *            要求宽度
	 * @param reqHeight
	 *            要求高度
	 * @return 图片大小
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	} 
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	
	public static int dip2px(Context context,int value){
		float scaleing=context.getResources().getDisplayMetrics().density;
		return (int) (value*scaleing+0.5f);
	}
	
	public static int px2dip(Context context,int value){
		float scaling=context.getResources().getDisplayMetrics().density;
		return (int) (value/scaling+0.5f);
	}
	
	/**
	 * 图片变圆角
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) { 

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888); 
		Canvas canvas = new Canvas(output); 

		final int color = 0xff424242; 
		final Paint paint = new Paint(); 
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
		final RectF rectF = new RectF(rect); 
		final float roundPx = pixels; 

		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
		canvas.drawBitmap(bitmap, rect, rect, paint); 
		return output; 
	}
	
	/**
	 * save
	 * @param path
	 * @param buffer
	 * @return
	 */
	public static int saveBitmap(String path,byte[] buffer){
		int result=-1;
		try {
			FileOutputStream out=new FileOutputStream(new File(path));
			out.write(buffer);
			out.flush();
			out.close();
			result=1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getdecodeBitmap(String filePath){
		if(filePath==null){
			return null;
		}
		BitmapFactory.Options options=new Options();
		options.inJustDecodeBounds=true;
		Bitmap bitmap=BitmapFactory.decodeFile(filePath, options);
		
		int width=options.outWidth;
		int height=options.outHeight;
		float scale=1f;
		if(width>ww&&width>height){
			scale=width/ww;
		}else if(height>hh&&height>width){
			scale=height/hh;
		}else{
			scale=1;
		}
		
		options.inSampleSize=(int) scale;
		options.inJustDecodeBounds=false;
		bitmap=BitmapFactory.decodeFile(filePath, options);
		return bitmap;
	}
	
	public static int saveBitmap(String path,Bitmap bitmap){
		int result=-1;
		try {
			FileOutputStream fos=new FileOutputStream(new File(path));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			result=1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
}
