package com.ibusybox.idownloader;

import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {

//	drop table if exists TB_SUB_TASK;
//
//	drop table if exists TB_TASK;
//
//	/*==============================================================*/
//	/* Table: TB_SUB_TASK                                           */
//	/*==============================================================*/
//	create table TB_SUB_TASK
//	(
//	   id                   bigint not null,
//	   pid                  bigint not null,
//	   start_point          bigint not null,
//	   block_size           bigint not null,
//	   pulled               bigint,
//	   status               int not null,
//	   primary key (id)
//	);
//
//	/*==============================================================*/
//	/* Table: TB_TASK                                               */
//	/*==============================================================*/
//	create table TB_TASK
//	(
//	   id                   bigint not null,
//	   url                  varchar(256) not null,
//	   length               bigint not null,
//	   status               int not null,
//	   file_name            varchar(256) not null,
//	   file_path            varchar(1024),
//	   file_extensions      varchar(32),
//	   primary key (id)
//	);
//
//	alter table TB_SUB_TASK add constraint FK_fk_tb_task_tb_sub_task foreign key (pid)
//	      references TB_TASK (id) on delete restrict on update restrict;

	public HttpURLConnection getHttpURLConnection(String url) throws Exception {
		URL dlUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) dlUrl.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Referer", url);
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
		return conn;
	}

	public HttpURLConnection getHttpURLConnectionByRange(String url, long start, long end) throws Exception {
		URL dlUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) dlUrl.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Referer", url);
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
		return conn;
	}
}
