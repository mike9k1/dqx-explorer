package ca.fraggergames.ffxivextract.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ca.fraggergames.ffxivextract.helpers.FFXIV_String;

public class EXHF_File {	

	int numEntries;
	
	private EXDF_Dataset datasetTable[];
	private EXDF_Page pageTable[];
	
	public EXHF_File(byte[] data) throws IOException {
		loadEXHF(data);
	}

	public EXHF_File(String path) throws IOException, FileNotFoundException {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		loadEXHF(data);
	}

	private void loadEXHF(byte[] data) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.BIG_ENDIAN);

		try {
			
			//Header
			int magicNum = buffer.getInt();
			
			if (magicNum != 0x45574846) //EXHF
				throw new IOException("Not a EXHF");
			
			int version = buffer.getShort();
			
			if (version != 0x3)
				throw new IOException("Not a EXHF");
			
			buffer.getShort();
			int numDataSetTable = buffer.getShort();
			int numPageTable = buffer.getShort();
			buffer.getShort();
			buffer.getShort();
			buffer.getShort();
			numEntries = buffer.getInt();
			buffer.getInt();
			buffer.position(0x20);
			
			datasetTable = new EXDF_Dataset[numDataSetTable];
			pageTable = new EXDF_Page[numPageTable];
			
			//Dataset Table
			for (int i = 0; i < numDataSetTable; i++)			
				datasetTable[i] = new EXDF_Dataset(buffer.getShort(), buffer.getShort());			
			
			//Page Table
			for (int i = 0; i < numPageTable; i++)
				pageTable[i] = new EXDF_Page(buffer.getInt(), buffer.getInt());
			
		} 
		catch (BufferUnderflowException underflowException) {} 
		catch (BufferOverflowException overflowException) {}
	}

	public static class EXDF_Dataset{
		public final short type;
		public final short offset;
		
		public EXDF_Dataset(short type, short offset)
		{
			this.type = type;
			this.offset = offset;
		}
	}
	
	public static class EXDF_Page{
		public final int pageNum;
		public final int numEntries;
		
		public EXDF_Page(int pageNum, int numEntries)
		{
			this.pageNum = pageNum;
			this.numEntries = numEntries;
		}
	}
	
}
