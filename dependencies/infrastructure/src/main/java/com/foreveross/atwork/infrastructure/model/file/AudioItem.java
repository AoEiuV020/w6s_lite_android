package com.foreveross.atwork.infrastructure.model.file;

import java.io.Serializable;
/***
 * 一个音频(音乐)的对象
 */
public class AudioItem implements Serializable {
	//歌曲id ：MediaStore.Audio.Media._ID
	public int audioId;
	//歌曲的名称 ：MediaStore.Audio.Media.TITL
	public String title ;
	//歌曲的专辑名：MediaStore.Audio.Media.ALBUM
	public String album ;
	//歌曲的歌手名： MediaStore.Audio.Media.ARTIST
	public String artist;
	//歌曲文件的全路径 ：MediaStore.Audio.Media.DATA
	public String path;
	//歌曲文件的名称：MediaStroe.Audio.Media.DISPLAY_NAME
	public String display_name;
	//歌曲文件的发行日期：MediaStore.Audio.Media.YEAR
	public String year;
	//歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
	public int duration;
	//歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
	public int size;
    //添加日期
    public long addDate;
}
