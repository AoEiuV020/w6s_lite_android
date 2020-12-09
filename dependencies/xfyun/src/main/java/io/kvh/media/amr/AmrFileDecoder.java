package io.kvh.media.amr;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class AmrFileDecoder {

    private long mDecoderState;

    // 8 k * 16bit * 1 = 8k shorts
    static final int SAMPLE_RATE = 8000;
    // 20 ms second
    // 0.02 x 8000 x 2 = 320;160 short
    static final int PCM_FRAME_SIZE = 160;
    static final int AMR_FRAME_SIZE = 32;


    public byte[] amr2Pcm(String filePath) {
        byte[] result = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            result = start(fileInputStream);
            stop();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public byte[] start(InputStream inputStream) {
        byte[] result = null;
        ByteArrayOutputStream baos = null;

        mDecoderState = AmrDecoder.init();

        byte[] readBuffer = new byte[AMR_FRAME_SIZE];

        //amr file has 6 bytes header: "23 21 41 4D 52 0A" => "#!amr.", so skip here
        try {
            inputStream.skip(6);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            baos = new ByteArrayOutputStream();

            while (inputStream.read(readBuffer) != -1) {
                // amr frame 32 bytes
                byte[] amrFrame = readBuffer.clone();
                // pcm frame 160 shorts
                short[] pcmFrame = new short[PCM_FRAME_SIZE];
                AmrDecoder.decode(mDecoderState, amrFrame, pcmFrame);

                byte[] bytes = shortArrayToByteArray(pcmFrame);
                baos.write(bytes, 0, bytes.length);

                Log.e("log", "decoding~~~~~");

            }

            result = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private byte[] shortArrayToByteArray(short[] pcmFrame) {
        ByteBuffer buffer = ByteBuffer.allocate(pcmFrame.length * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.asShortBuffer().put(pcmFrame);
        return buffer.array();
    }

    public void stop() {

        AmrDecoder.exit(mDecoderState);

    }

}
