package com.lvdi.ruitianxia_cus.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ab.util.AbLogUtil;

import android.os.AsyncTask;

public class HttpAsyncDownload extends AsyncTask<String, Integer, Boolean> {

	/**
	 * TAG
	 */
	private static String LOG_TAG = "HttpDownload";
	/**
	 * 
	 */
	private String path_;
	/**
	 * 
	 */
	private String downloadURL_;
	/**
	 * 
	 */
	private long totalSize_;
	/**
	 * 
	 */
	private long progress_;
	/**
	 * 
	 */
	private OnProgressListener listener_;

	/**
	 * 初始化 HttpUpload 类的新实例
	 * 
	 * @param uploadURL
	 *            文件URL
	 * @param path
	 *            本地存储路径
	 */
	public HttpAsyncDownload(String url, String path) {
		this.downloadURL_ = url;
		this.path_ = path;
		this.totalSize_ = 0;
		this.progress_ = 0;
	}

	/**
	 * 设置下载进度的监控。
	 * 
	 * @param I
	 */
	public void setOnProgressListener(OnProgressListener I) {
		this.listener_ = I;
	}

	/**
	 * 
	 */
	protected void onPreExecute() {
		if (listener_ != null) {
			listener_.onPreExecute();
		}
		AbLogUtil.d(LOG_TAG, "Downloading File...");
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		boolean result = false;

		// 生成连接
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(downloadURL_);

		try {
			// 打开连接
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// 获得文件长度
				totalSize_ = entity.getContentLength();

				// 准备输入流和输出流
				OutputStream outputStream = new FileOutputStream(path_);
				InputStream inputStream = entity.getContent();

				// 开始下载
				int count = 0;
				int i = 0;
				byte[] buffer = new byte[1024];
				while ((count = inputStream.read(buffer, 0, buffer.length)) > 0) {
					// 计算进度
					i++;
					progress_ += count;
					int percent = (int) ((progress_ / (float) totalSize_) * 100);

					// 写入文件
					outputStream.write(buffer, 0, count);

					if (i % 40 == 0)
						// 发布进度
						publishProgress(percent);
				}

				outputStream.close();

				AbLogUtil.d("VersionUpdate", "progress= " + progress_ + " total= " + totalSize_);
				// 表示下载完全
				if (progress_ == totalSize_) {
					result = true;
				} else {
					result = false;
				}
			} else {
				AbLogUtil.d(LOG_TAG, "entity is null");
			}
		} catch (Exception e) {
			AbLogUtil.d(LOG_TAG, e.getMessage());
			// 捕获异常
			listener_.onException(e);
		}

		return result;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (listener_ != null) {
			listener_.onProgressUpdate(this.progress_, totalSize_, progress[0]);
		}
		AbLogUtil.d(LOG_TAG, String.format("%d%%", progress[0]));
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (listener_ != null) {
			listener_.onPostExecute(result);
		}
		if (result) {
			AbLogUtil.d(LOG_TAG, "Completed");
		} else {
			AbLogUtil.d(LOG_TAG, "Failure");
		}
	}

	/**
	 * OnProgressListener
	 * 
	 * @author 14042054
	 * 
	 */
	public interface OnProgressListener {
		/**
		 * 执行 HTTP 操作前被调用。可以在该方法中做一些准备工作，如在显示一个进度条。
		 */
		public void onPreExecute();

		/**
		 * 执行 HTTP 操作后被调用。.
		 */
		public void onPostExecute(boolean succeed);

		/**
		 * HTTP 操作的进度发生变化时被调用。
		 * 
		 * @param progress
		 *            当前已经完成的字节数
		 * @param max
		 *            总字节数
		 */
		public void onProgressUpdate(long progress, long max, int precent);

		/**
		 * 捕获异常信息
		 */
		public void onException(Exception exception);
	}
}