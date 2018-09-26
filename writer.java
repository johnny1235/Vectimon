import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class writer {
	
	int num = 0;
	long Max = 50000000; // 50 mb
	String name;
	String prevName;
// final long SIZE_1GB = 1073741824L;
	PrintWriter write;
	writer(String name1) throws FileNotFoundException, UnsupportedEncodingException
	{
		StringBuilder fileName = new StringBuilder(0);
		fileName.append(name1);
		fileName.append(num);
		fileName.append(".log");
		PrintWriter writer = new PrintWriter(fileName.toString(), "UTF-8");		
		write = writer;
		name= name1;
		prevName = fileName.toString();
	}
	
	public void checkFile() throws FileNotFoundException, UnsupportedEncodingException
	{
		File file = new File(prevName);
		if (file.length() > Max)
		{
			write.close();
			num++;
			if(num == 21)
			{
				num = 0;
			}
			StringBuilder fileName = new StringBuilder(0);
			fileName.append(name);
			fileName.append(num);
			fileName.append(".log");
			prevName = fileName.toString();
			writer2(fileName.toString());
		}
		
	}

	private void writer2(String name2) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(name2, "UTF-8");		
		write = writer;
		
	}

	public void writeData(String notice) throws FileNotFoundException, UnsupportedEncodingException
	{
			checkFile();
			write.println(notice);
			write.flush();
	}

	public void close() {
		write.close();
		// TODO Auto-generated method stub
		
	}
}
