package com.main.active;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import dao.Configuration;


public class DownLoadImg {

	List<String> imgFormat = new ArrayList<String>();

	public DownLoadImg() {

		imgFormat.add("jpg");
		imgFormat.add("jpeg");
		imgFormat.add("png");
		imgFormat.add("gif");
		imgFormat.add("bmp");

	}

	/**
	 * 开启总方法
	 * 
	 * @param startUrl
	 */
	public void start(Configuration rec) {

        downloadImage(rec);
        System.out.println("----------------------------------");
		System.out.println("------------下载成功-------------");
		System.out.println("----------------------------------");

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
		// try {
		// return httpEntity.getContent();
		// } catch (IllegalStateException | IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return null;
	}

	/**
	 * 获取整个html以String形式输出
	 * 
	 * @param url
	 * @return
	 */
	private String getContent(String url) {

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
	 * 通过开始时的content获取所有图片的地址
	 * 
	 * @param startUrl
	 * @return
	 */
	private List<String> getAllImageUrls(String content) {

		String regex = "http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?(.jpg|.mp4|.rmvb|.png|.mkv|.gif|.bmp|.jpeg|.flv|.avi|.asf|.rm|.wmv)+";
		// String regex = "http://www.sslingyu.com/mz_pbl/images_min\\S*\\.jpg";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);

		List<String> urls = new ArrayList<String>();

		while (m.find()) {
			String url = m.group();

			// 将获取到的url转换成高清
			url = getHDImageUrl(url);

			System.out.println("获取的url：" + url + "\n是否符合标准：" + isTrueUrl(url));

			if (isTrueUrl(url)) {
				urls.add(url);
			}

		}
		System.out.println("----------------------------------");
		System.out.println("--------获取所有url成功----------");
		System.out.println("----------------------------------");
		return urls;
	}

	/**
	 * 下载图片
	 * 
	 * @param url
	 * @param is
	 */
	private int downloadImage(Configuration rec) {

		try {

			String realPath = "D:/configuration/images/";
			String name =rec.getId()+".png";

			System.out.println("文件夹路径：" + realPath);
			System.out.println("文件名字：" + name);
			
			File file = new File("src/IEDriverServer.exe");
			System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
			ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			WebDriver driver = new InternetExplorerDriver(ieCapabilities);
			driver.get(rec.getUrl());
			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE); // 讲截取的图片以文件的形式返回
			try {
				org.apache.commons.io.FileUtils.copyFile(srcFile, new File(realPath + "/" + name)); // 使用copyFile()方法保存获取到的截图文件
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			driver.quit();

			System.out.println("下载：" + rec.getId() + "成功\n");

		} catch (Exception e) {
			System.out.println("下载：" + rec.getId() + "失败");
			e.printStackTrace();
		}

		return 1;
	}

	/**
	 * 创建并把存储的位置返回回去
	 * 
	 * @param url
	 * @return
	 */
	private String getRealPath(String url) {

		Pattern p = Pattern.compile("images/[a-z]+/[a-z_0-9]+");
		Matcher m = p.matcher(url);

		String format = getName(url).split("\\.")[1];

		String path = null;

		// 说明是图片
		if (imgFormat.contains(format))
		{
			path = "media/images/";
		} 
		else {
			path = "media/video/";
		}

		path += url.split("/")[(url.split("/").length - 2)];

		if (m.find()) {
			path = m.group();
		}
		;

		// 添加盘符
		path = "D:/" + path;

		File file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}

		return path;

	}

	/**
	 * 获取文件名
	 * 
	 * @param url
	 * @return
	 */
	private String getName(String url) {

		// s3.substring(s3.lastIndexOf("/")+1)
      if(url.lastIndexOf("://")>-1)
	  return url.substring(url.lastIndexOf("://") + 3);
	  else
	  return url;

	}

	/**
	 * 获取高清图片地址 就是把images_min换成了Images
	 * 
	 * @param url
	 * @return
	 */
	private String getHDImageUrl(String url) {

		if (url.contains("images_min")) {
			return url.replace("images_min", "images");
		}

		return url;
	}

	/**
	 * 判断url的格式是否正确,必须以http开头，以.jpg结尾
	 * 
	 * @param url
	 * @return
	 */
	private boolean isTrueUrl(String url) {

		return url.matches("^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?(.jpg|.mp4|.rmvb|.png|.mkv|.gif|.bmp|.jpeg|.flv|.avi|.asf|.rm|.wmv)+$");
	}

}
