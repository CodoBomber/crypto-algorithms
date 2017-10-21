package crypto;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileEncryptor {

    public static ByteBuffer bufferizeFile(String fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName,"r");
        FileChannel fl = raf.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)fl.size()+1);
        fl.read(byteBuffer);
        fl.close();
        return byteBuffer;
    }

}
