package com.main.active;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

public class DownLoadHTML1 {

	/**
	 * 获取文件名
	 * 
	 * @param url
	 * @return
	 */
	private String getName(String url) {

		// s3.substring(s3.lastIndexOf("/")+1)

		return url.substring(url.lastIndexOf("/") + 1);

	}

	/**
	 * 写文件
	 * 
	 * @param path
	 * @param content
	 */
	public static boolean write(String path, String content) {
		String s = new String();
		String s1 = new String();
		BufferedWriter output = null;
		try {
			File f = new File(path);
			if (f.exists()) {
			} else {
				System.out.println("文件不存在，正在创建...");
				if (f.createNewFile()) {
					System.out.println("文件创建成功！");
				} else {
					System.out.println("文件创建失败！");
				}
			}
			BufferedReader input = new BufferedReader(new FileReader(f));
			while ((s = input.readLine()) != null) {
				s1 += s + "\n";
			}
			System.out.println("原文件内容：" + s1);
			input.close();
			s1 += content;
			output = new BufferedWriter(new FileWriter(f));
			output.write(s1);
			output.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 开启总方法
	 * 
	 * @param startUrl
	 */
	public void start(String startUrl) {

		downloadHTML(startUrl);

		System.out.println("----------------------------------");
		System.out.println("------------下载成功-------------");
		System.out.println("----------------------------------");

	}

	/**
	 * 下载图片
	 * 
	 * @param url
	 * @param is
	 */
	private int downloadHTML(String url) {

		String path = "D:/media/html/";
		File file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}

		path += getName(url) + ".txt";

		String content = getContent(url);
		if (content.length() == 0)
			return 0;
		else {
			write(path, content);
			return 1;
		}

	}

	/**
	 * 获取整个html以String形式输出
	 * 
	 * @param url
	 * @return
	 */
	public String getContent(String url) {

		HttpEntity httpEntity = getHttpEntity(url);
		String content = "";
		try {

			InputStream is = httpEntity.getContent();

			InputStreamReader isr = new InputStreamReader(is);

			char[] c = new char[1024];
			int l = 0;

			while ((l = isr.read(c)) != -1) {
				content += new String(c, 0, l);
			}

			isr.close();
			is.close();
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}

	/**
	 * 获取HttpEntity
	 * 
	 * @return HttpEntity网页实体
	 */
	private HttpEntity getHttpEntity(String url) {

		HttpResponse response = null;// 创建请求响应

		// 创建httpclient对象
		HttpClient httpClient = HttpClients.createDefault();

		HttpGet get = new HttpGet(url);

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000) // 设置请求超时时间
				.setConnectionRequestTimeout(5000) // 设置传输超时时间
				.build();

		get.setConfig(requestConfig);// 设置请求的参数

		//
		try {
			response = httpClient.execute(get);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 获取返回状态 200为响应成功
		// StatusLine state = response.getStatusLine();

		// 获取网页实体
		HttpEntity httpEntity = response.getEntity();

		return httpEntity;

	}

}