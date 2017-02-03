
import java.io.*; 
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputeSHA{
	public static void main(String args[]) throws IOException{

		if(args.length!=1)
		{
			System.out.println("the number of input parameter incorrect");
		}

		MessageDigest m;

		try{
			InputStream is = new FileInputStream(args[0]);
			m = MessageDigest.getInstance("SHA1");
			int size =0;
			//byte buf[] = is.read(size);
			while(size!=-1){
				byte buf[] = new byte[1024];
				size = is.read(buf);
				if(size!=-1)
				{
					System.out.println(Integer.toString(size));	
					m.update(buf,0,size); 
				}
			}
			byte resultData[] = m.digest();
			System.out.println(convertToHexString(resultData));

		 }catch(IOException e){
		 		e.printStackTrace();
		 		throw e;
		 }
		 catch(NoSuchAlgorithmException e){
		 		e.printStackTrace();
		 }
	}
	static String convertToHexString(byte data[]) {
  		StringBuffer strBuffer = new StringBuffer();
  		for (int i = 0; i < data.length; i++) {
   			strBuffer.append(Integer.toHexString(0xff & data[i]));
  		}
  		return strBuffer.toString();
 	}
}