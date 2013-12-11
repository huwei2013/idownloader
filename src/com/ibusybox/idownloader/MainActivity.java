package com.ibusybox.idownloader;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private Downloader downloader;

	private Handler handler;
	private ExpandableListView elvDownloadList;
	private BaseExpandableListAdapter mDownloadAdapter;

	private EditText etDlUrl;
	private Button btnDownload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		downloader = new Downloader();
		handler = new Handler();
		etDlUrl = (EditText) findViewById(R.id.etDlUrl);
		btnDownload = (Button) findViewById(R.id.btnDownload);
	}

	public void download(View v) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					dl(etDlUrl.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void dl(String dlUrl) throws Exception {
		DownloadManager d;
		HttpURLConnection conn = downloader.getHttpURLConnection(dlUrl);
		conn.connect();
		printResponseHeader(conn);
		if (conn.getResponseCode() == 200) {
			int fileSize = conn.getContentLength();// 根据响应获取文件大小
			if (fileSize < 0)
				throw new RuntimeException("Unkown file size ");
			String filename = getFileName(conn, dlUrl);
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				return;
			File sdCard = Environment.getExternalStorageDirectory();
			File saveFile = new File(sdCard, filename);/* 保存文件 */
			final RandomAccessFile raf = new RandomAccessFile(saveFile, "rw");
			raf.setLength(fileSize);
			// raf.close();
			final InputStream is = conn.getInputStream();
			// final InputStream is = new BaseDownloader(MainActivity.this).getStream(dlUrl, null);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int len = -1;
						byte[] buffer = new byte[8 * 1024];
						while ((len = is.read(buffer)) != -1) {
							raf.write(buffer, 0, len);
						}
						is.close();
						raf.close();
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(MainActivity.this, "download success....", Toast.LENGTH_SHORT).show();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

		}
	}

	// /**
	// * 开始下载文件
	// * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	// * @return 已下载文件大小
	// * @throws Exception
	// */
	// public int download(SmartDownloadProgressListener listener, String dlUrl)
	// throws Exception{
	// int fileSize = 1;
	// RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
	// if(fileSize>0) randOut.setLength(fileSize);
	// randOut.close();
	// URL url = new URL(dlUrl);
	// if(this.data.size() != this.threads.length){
	// this.data.clear();//清除数据
	// for (int i = 0; i < this.threads.length; i++) {
	// this.data.put(i+1, 0);
	// }
	// }
	// for (int i = 0; i < this.threads.length; i++) {
	// int downLength = this.data.get(i+1);
	// if(downLength < this.block && this.downloadSize<this.fileSize){
	// //该线程未完成下载时,继续下载
	// this.threads = new SmartDownloadThread(this, url, this.saveFile,
	// this.block, this.data.get(i+1), i+1);
	// this.threads.setPriority(7);
	// this.threads.start();
	// }else{
	// this.threads = null;
	// }
	// }
	// this.fileService.save(this.downloadUrl, this.data);
	// boolean notFinish = true;//下载未完成
	// while (notFinish) {// 循环判断是否下载完毕
	// Thread.sleep(900);
	// notFinish = false;//假定下载完成
	// for (int i = 0; i < this.threads.length; i++){
	// if (this.threads != null && !this.threads.isFinish()) {
	// notFinish = true;//下载没有完成
	// if(this.threads.getDownLength() == -1){//如果下载失败,再重新下载
	// this.threads = new SmartDownloadThread(this, url, this.saveFile,
	// this.block, this.data.get(i+1), i+1);
	// this.threads.setPriority(7);
	// this.threads.start();
	// }
	// }
	// }
	// if(listener!=null) listener.onDownloadSize(this.downloadSize);
	// }
	// fileService.delete(this.downloadUrl);
	//
	// return this.downloadSize;
	// }
	//
	/**
	 * 获取文件名
	 */
	private String getFileName(HttpURLConnection conn, String dlUrl) {
		String filename = dlUrl.substring(dlUrl.lastIndexOf('/') + 1);
		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null)
					break;
				if ("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if (m.find())
						return m.group(1);
				}
			}
			filename = UUID.randomUUID() + ".tmp";// 默认取一个文件名
		}
		return filename;
	}

	/**
	 * 打印Http头字段
	 * 
	 * @param http
	 */
	public static void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			print(key + entry.getValue());
		}
	}

	/**
	 * 获取Http响应头字段
	 * 
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}

	// 打印日志
	private static void print(String msg) {
		Log.i(TAG, msg);
	}
}
