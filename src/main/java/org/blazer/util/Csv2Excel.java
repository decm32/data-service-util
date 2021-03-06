package org.blazer.util;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.blazer.common.file.FHandler;
import org.blazer.common.file.FReader;
import org.blazer.common.util.TimeUtil;

public class Csv2Excel {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			args = new String[] { "/Users/hyy/Downloads/zoc_download/2017_10_01_00_00_18_cron_auto_00001.csv" };
		}
		if (args.length == 0) {
			System.err.println("Usage: java -jar xxx.jar $CsvPath [$Delimeter]");
			System.exit(-1);
		}
		String delimeter = "\t";
		String path = args[0];
		if (args.length > 1) {
			delimeter = args[1];
		}
		convert(path, delimeter);
		System.exit(0);
	}

	public static void convert(String path, String delimeter) throws IOException {
		TimeUtil time = TimeUtil.createAndPoint();
		time.printMs("开始执行");
		SXSSFWorkbook wb = new SXSSFWorkbook();
		FileOutputStream out = null;
		String outPath = path + ".xlsx";
		try {
			wb.setCompressTempFiles(true);
			SXSSFSheet sh = wb.createSheet("Sheet");
			sh.setRandomAccessWindowSize(100);
			time.printMs("初始化excel");
			FReader.create(path, new FHandler(sh, delimeter) {
				SXSSFSheet sh = (SXSSFSheet) getParameter(0);
				String delimeter = (String) getParameter(1);

				public void handle(String row) throws IOException {
					String[] strs = row.split(delimeter);
					// 备注一下：我去他大爷的，这个坑可以留着以后再填。写入excel流不能大于1048575
					if (index() == 1048576) {
						System.out.println("超出最大行数,不能大于1048576.");
					}
					Row r = sh.createRow(index());
					for (int i = 0; i < strs.length; i++) {
						Cell cell = r.createCell(i);
						cell.setCellValue(strs[i]);
					}
				}
			});
			time.printMs("读取文件csv文件并且写入excel");
			out = new FileOutputStream(outPath);
			wb.write(out);
			time.printMs("生成excel");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			try {
				wb.close();
			} catch (Exception e) {
			}
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

}
