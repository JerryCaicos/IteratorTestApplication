package com.yg.mobile.iteratort.app.utils;

/**
 * 图片加载完成的参数<P>
 * {@link ImageLoadedParams#loadDuration} : 图片加载耗时,正常网络加载的值>0;否则:
 * <LI>如果来自内存,值为:{@link ImageLoadedParams#DURATION_MEMORY}</LI>
 * <LI>如果来自SD卡,值为:{@link ImageLoadedParams#DURATION_SDCARD}</LI>
 * <LI>如果加载失败,值为:{@link ImageLoadedParams#DURATION_ERROR}</LI>
 * <LI>如果加载成功,但是系统本身I/O造成异常,值为实际网络加载时长</LI>
 * <p>
 * {@link ImageLoadedParams#responseCode} : 图片网络响应码
 * <p>
 * {@link ImageLoadedParams#errorMessage} : 图片加载异常信息,无论成功失败,都可能有异常信息
 *
 */
public class ImageLoadedParams
{
	/**
	 * [memory]加载时长
	 **/
	public static final long DURATION_MEMORY = 0l;
	/**
	 * [SDCard]加载时长
	 **/
	public static final long DURATION_SDCARD = -1l;
	/**
	 * [exception]加载时长
	 **/
	public static final long DURATION_ERROR = -2l;

	/**
	 * 加载时长
	 **/
	public long loadDuration;
	/**
	 * 加载响应码
	 **/
	public int responseCode;
	/**
	 * 错误信息,异常状态才会有
	 **/
	public String errorMessage;

	/**
	 * 构造方法,用于[memory]&[SDCard]加载 { responseCode为-1 , errorMessage为“” }
	 *
	 * @param loadDuration 加载时长
	 */
	public ImageLoadedParams(long loadDuration)
	{
		//memory&SDCard图片加载,responseCode为-1,errorMessage为“”
		this(loadDuration,-1,"");
	}

	/**
	 * 构造方法
	 *
	 * @param loadDuration 加载耗时
	 * @param responseCode 响应码
	 * @param errorMessage 错误信息
	 */
	public ImageLoadedParams(long loadDuration,int responseCode,String errorMessage)
	{
		this.loadDuration = loadDuration;
		this.responseCode = responseCode;
		this.errorMessage = errorMessage;
	}
}
