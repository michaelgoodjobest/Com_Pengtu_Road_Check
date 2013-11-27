package com.road.check.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtil {

	private String SDCardRoot;

	public FileUtil() {
		// �õ���ǰ�ⲿ�洢�豸Ŀ¼
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
	}

	/**
	 * ��SDCard�ϴ����ļ�
	 * */
	public File createFileInSDCard(String fileName, String dir)
			throws Exception {
		File file = new File(SDCardRoot + File.separator + dir + File.separator
				+ fileName);
		file.createNewFile();
		return file;
	}

	public File createFileInSDCard(String filePath) throws Exception {
		File file = new File(filePath);
		file.createNewFile();
		return file;
	}

	/**
	 * ��SDCard�ϴ���Ŀ¼
	 * */

	public File createDIR(String dirpath) {
		File dir = new File(SDCardRoot + dirpath);
		dir.mkdir();
		return dir;
	}

	public File createSDDir(String dir) {
		File file = new File(SDCardRoot + File.separator + dir + File.separator);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * �ж�SDCard�ϵ��ļ��Ƿ����
	 * */
	public boolean isFileExist(String fileName, String path) {
		File file = new File(SDCardRoot + File.separator + path
				+ File.separator + fileName);
		return file.exists();
	}

	/**
	 * ��ȡ�ļ�
	 * 
	 * @param fileName
	 * @param path
	 * @return
	 */
	public File getFile(String fileName, String path) {
		File file = new File(SDCardRoot + File.separator + path
				+ File.separator + fileName);
		return file;
	}

	public File getFile(String filepath) {
		File file = new File(SDCardRoot + File.separator + filepath);
		return file;
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param fileName
	 * @param path
	 */
	public void deleteFile(String fileName, String path) {
		System.out.println("delete");
		File file = new File(SDCardRoot + File.separator + path
				+ File.separator + fileName);
		file.delete();
	}

	/**
	 * ��һ��InputStreamд��SDCard
	 * */
	public File writeToSDCardFromInput(String path, String fileName,
			InputStream inputStream) {
		File file = null;
		OutputStream outputStream = null;

		try {
			createSDDir(path);
			file = createFileInSDCard(fileName, path);

			outputStream = new FileOutputStream(file);
			byte data[] = new byte[4 * 1024];
			int tmp;
			while ((tmp = inputStream.read(data)) != -1) {
				outputStream.write(data, 0, tmp);
			}
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * @param file
	 * @return
	 */
	public byte[] getBytesFromFile(File file) {
		if (file == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1) {
				out.write(b, 0, n);
			}

			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {

		}
		return null;
	}

	// ɾ���ļ���
	// param folderPath �ļ�����������·��

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ɾ��ָ���ļ����������ļ�
	// param path �ļ�����������·��
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}
}
