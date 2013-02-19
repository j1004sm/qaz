package com.qaz.dor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class QazHttpServer {
	
	public static String QAZ_URL_SERVER = "http://www.manjong.org:8255/qaz";

	public static String QAZ_URL_TERMS = QAZ_URL_SERVER + "/terms.html";
	public static String QAZ_URL_PRIVACY = QAZ_URL_SERVER + "/privacy.html";
	public static String QAZ_URL_LOGIN = QAZ_URL_SERVER + "/login.jsp";
	public static String QAZ_URL_JOIN = QAZ_URL_SERVER + "/join.jsp";
	public static String QAZ_URL_FINDID = QAZ_URL_SERVER + "/find_id.jsp";
	public static String QAZ_URL_PWCHANGE = QAZ_URL_SERVER + "/pwchange.jsp";
	public static String QAZ_URL_UPLOAD = QAZ_URL_SERVER + "/upload.jsp";
	public static String QAZ_URL_SECESSION = QAZ_URL_SERVER + "/secession.jsp";
	public static String QAZ_URL_IMAGEDIR = QAZ_URL_SERVER + "/upload/";
	
	public static int QAZ_SERVER_FAIL = 0;
	public static int QAZ_SERVER_SUCCESS = 1;
	
	public static String UploadImageText(String urlString, File fileName, String realName, double lat, double lon, double alt, String user) {

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String ret = "Qaz_Server_Fail";
		
		try {
		
			FileInputStream mFileInputStream = new FileInputStream(fileName);
			URL connectUrl = new URL(urlString);
			Log.d("Qaz-ImageUpload", "mFileInputStream  is " + mFileInputStream);

			// open connection
			HttpURLConnection conn = (HttpURLConnection) connectUrl
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			// write data
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			// utf-8로 변환해서 보냄
			StringBuffer pd = new StringBuffer();

			pd.append(twoHyphens + boundary + lineEnd);
			pd.append(lineEnd);

			pd.append(twoHyphens + boundary + lineEnd);
			pd.append("Content-Disposition: form-data; name=\"name\"" + lineEnd
					+ lineEnd + realName);
			pd.append(lineEnd);

			pd.append(twoHyphens + boundary + lineEnd);
			pd.append("Content-Disposition: form-data; name=\"latitude\""
					+ lineEnd + lineEnd + Double.toString(lat));
			pd.append(lineEnd);

			pd.append(twoHyphens + boundary + lineEnd);
			pd.append("Content-Disposition: form-data; name=\"longitude\""
					+ lineEnd + lineEnd + Double.toString(lon));
			pd.append(lineEnd);

			pd.append(twoHyphens + boundary + lineEnd);
			pd.append("Content-Disposition: form-data; name=\"altitude\""
					+ lineEnd + lineEnd + Double.toString(alt));
			pd.append(lineEnd);
			
			pd.append(twoHyphens + boundary + lineEnd);
			pd.append("Content-Disposition: form-data; name=\"user\""
					+ lineEnd + lineEnd + user);
			pd.append(lineEnd);

			pd.append(twoHyphens + boundary + lineEnd);
			pd.append("Content-Disposition: form-data; name=\"image\"; filename=\""
					+ fileName + "\"" + lineEnd);
			pd.append(lineEnd);

			dos.writeUTF(pd.toString());

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			Log.d("Qaz-ImageUpload", "image byte is " + bytesRead);

			// read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			Log.d("Qaz-ImageUpload", "File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...
			dos.close();

			// get response
			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = rd.readLine()) != null) {
				Log.d("Qaz-ImageUpload", line);
				ret = line.trim();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return ret;
	}

	public static String RequestLogin(String reqUrl, String usrId, String encPw) {
		try {
			// URL설정, 접속
			URL url = new URL(reqUrl);

			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestMethod("POST");

			http.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");

			StringBuffer buffer = new StringBuffer();

			buffer.append("id").append("=").append(usrId).append("&");
			buffer.append("encpw").append("=").append(encPw);

			OutputStreamWriter outStream = new OutputStreamWriter(
					http.getOutputStream(), "EUC-KR");

			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());

			writer.flush();

			InputStreamReader inputStream = new InputStreamReader(
					http.getInputStream(), "EUC-KR");
			BufferedReader bufferReader = new BufferedReader(inputStream);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = bufferReader.readLine()) != null) {
				builder.append(str + "\n");
			}

			String result = builder.toString();
//			Log.d("Qaz-HttpPost", "result :" + result);
			
			return result.trim();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "Qaz_Server_Fail";
	}

	public static int RequestJoin(String reqUrl, String join_id,
			String join_pw_code, String join_name, String join_email) {

		try {
			URL url = new URL(reqUrl);

			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestMethod("POST");

			http.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");

			StringBuffer buffer = new StringBuffer();

			buffer.append("id").append("=").append(join_id).append("&");
			buffer.append("encpw").append("=").append(join_pw_code).append("&");
			buffer.append("name").append("=").append(join_name).append("&");
			buffer.append("email").append("=").append(join_email);

			OutputStreamWriter outStream = new OutputStreamWriter(
					http.getOutputStream(), "UTF-8");

			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());

			writer.flush();

			InputStreamReader inputStream = new InputStreamReader(
					http.getInputStream(), "UTF-8");
			BufferedReader bufferReader = new BufferedReader(inputStream);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = bufferReader.readLine()) != null) {
				builder.append(str + "\n");
			}

			String result = builder.toString();
			// Log.d("Qaz-HttpPost", "result : " + result);

			return Integer.parseInt(result.trim());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return QAZ_SERVER_FAIL;
	}
	
	public static int RequestPasswordChanging(String reqUrl, String encPw) {
		try {
			// URL설정, 접속
			URL url = new URL(reqUrl);

			HttpURLConnection http = (HttpURLConnection) url
					.openConnection();

			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestMethod("POST");

			http.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");

			StringBuffer buffer = new StringBuffer();

			buffer.append("id").append("=").append(LoginActivity.usrId)
					.append("&");
			buffer.append("encpw").append("=").append(encPw);

			OutputStreamWriter outStream = new OutputStreamWriter(
					http.getOutputStream(), "UTF-8");

			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());

			writer.flush();

			InputStreamReader inputStream = new InputStreamReader(
					http.getInputStream(), "UTF-8");
			BufferedReader bufferReader = new BufferedReader(inputStream);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = bufferReader.readLine()) != null) {
				builder.append(str + "\n");
			}

			String result = builder.toString();
//			Log.d("Qaz-HttpPost", "result : " + result);
			
			return Integer.parseInt(result.trim());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return QAZ_SERVER_FAIL;
		
	}

	public static int RequestSecession(String reqUrl) {
		try {
			// URL설정, 접속
			URL url = new URL(reqUrl);

			HttpURLConnection http = (HttpURLConnection) url
					.openConnection();

			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestMethod("POST");

			http.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");

			StringBuffer buffer = new StringBuffer();

			buffer.append("id").append("=").append(LoginActivity.usrId);

			OutputStreamWriter outStream = new OutputStreamWriter(
					http.getOutputStream(), "UTF-8");

			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());

			writer.flush();

			InputStreamReader inputStream = new InputStreamReader(
					http.getInputStream(), "UTF-8");
			BufferedReader bufferReader = new BufferedReader(inputStream);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = bufferReader.readLine()) != null) {
				builder.append(str + "\n");
			}

			String result = builder.toString();
//			Log.d("Qaz-HttpPost", "result : " + result);
			
			return Integer.parseInt(result.trim());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return QAZ_SERVER_FAIL;
	}

}
