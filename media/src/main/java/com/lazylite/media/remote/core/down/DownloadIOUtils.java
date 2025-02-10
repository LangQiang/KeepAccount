package com.lazylite.media.remote.core.down;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



/**
 * @author 李建衡：jianheng.li@kuwo.cn
 * 
 */
public class DownloadIOUtils {
	
public static final int MAX_BUFFER_BYTES = 2048;
	
	public static long readInt(InputStream in) throws IOException {
		byte[] buffer = null;
		
		try {
			buffer = new byte[4];
		} catch (OutOfMemoryError oom) {
			return -1L;
		}	
			
		if(in.markSupported())
			in.mark(4);
		
		int count = in.read(buffer, 0, 4);
		// 没有读取到数据，或者数据不充分
		if(count <= 0 ) {
			buffer = null;
			return -1L;
		} 
		else if(count < 4){
			in.reset();
			buffer = null;
			return -1L;
		}			
		else {
			
			long result = (long) (((buffer[0] << 24)& 0xff000000) | 
					((buffer[1]<<16) & 0xff0000) | 
					((buffer[2]<<8)& 0xff00) | 
					(buffer[3] & 0xff));
			
			buffer = null;
			return result;
		}

	}


	// 如果内存申请失败，返回null
	public static CharSequence readString(InputStream in, int len) throws IOException {
		int leftBytes = len, recvBytes = 0;
		int bufLen = Math.min(leftBytes, MAX_BUFFER_BYTES);
		
		byte[] buffer = null;
		
		try {
			buffer = new byte[bufLen];			
		} catch (OutOfMemoryError oom) {
			return null;
		}
		
		ByteArrayOutputStream outputStream = null;
		
		try {
			outputStream = new ByteArrayOutputStream(MAX_BUFFER_BYTES);
			
			while(leftBytes > 0 && (recvBytes = in.read(buffer, 0, bufLen)) != -1 ) {
				outputStream.write(buffer, 0, recvBytes);
				leftBytes -= recvBytes;			
			}	
		} catch (Exception e) {
			e.printStackTrace();
			if(outputStream != null){
				outputStream.close();
			}
			return null;
		}


		buffer = null;
		String result = outputStream.toString();
		outputStream.close();
		return result;
		
	}

	
	public static void writeInt(OutputStream out, int s) throws IOException {
		byte[] buffer = new byte[] {
				(byte)((s >> 24)& 0xff), 
				(byte)((s >> 16)& 0xff),
				(byte)((s >> 8)& 0xff), 
				(byte)(s&0xff)};
		
		out.write(buffer);
		out.flush();
		buffer = null;
	}

	public static String toString(InputStream is)throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = null;
		
		try {
			buffer = new byte[1024];	
		} catch (OutOfMemoryError oom) {
			baos.close();
			return null;
		}
				
		int totalRead = 0;
		int read = 0;
		
		try {
			while ((read = is.read(buffer, totalRead, 1024)) > 0) {
				baos.write(buffer, totalRead, read);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			baos.close();
			return null;
		}
		
		String str = baos.toString();
		
		try {
			baos.close();
		} catch (IOException e) {
		}
		
		return str;
	}
}
