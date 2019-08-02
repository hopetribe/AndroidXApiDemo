package com.hopetribe.androidxapidemo.edof;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.hopetribe.androidxapidemo.BaseClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import androidx.annotation.WorkerThread;

public class EdofExtractor extends BaseClass {

    /**
     * "edof"
     */
    private final byte[] EDOF_CORE_HEADER = {0x65, 0x64, 0x6F, 0x66};
    /**
     * ".edof."
     */
    private final byte[] EDOF_HEADER = {0x00, 0x65, 0x64, 0x6F, 0x66, 0x00};
    private final byte[] JPEG_HEADER = {(byte) 0xFF, (byte) 0xD8};
    private byte test = 0x65;

    int index = 0;

    public EdofExtractor() {
        Log.i(TAG, "EdofExtractor: " + bytesToHexString(EDOF_HEADER) + ", JPEG_HEADER_STRING " + bytesToHexString(JPEG_HEADER));
    }

    @WorkerThread
    public int extractEdof(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "extractEdof: invalid fileName");
            return -1;
        }

        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            Log.e(TAG, "extractEdof: file not exist or is directory");
            return -1;
        }

        ByteBuffer fileData = null;
        long fileSize = 0;
        try {

            FileInputStream fis = new FileInputStream(fileName);

            FileChannel fileChannel = fis.getChannel();
            fileSize = fileChannel.size();

            Log.i(TAG, "extractEdof: file size = " + fileSize);
            fileData = ByteBuffer.allocate((int) fileSize);
            fileChannel.read(fileData);
            fileData.clear();

            fileChannel.close();
            fis.close();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        if (fileData == null || fileSize <= 0) {
            Log.e(TAG, "extractEdof: fileData is null or file size <= 0");
            return -1;
        }

        // detect edof header first
        boolean found = false;
        byte[] temp = new byte[6];
        int i = 0;
        boolean printFirst = true;
        while (true) {
            fileData.get(temp);
            if (printFirst) {
                Log.i(TAG, "extractEdof: " + bytesToHexString(temp));
                printFirst = false;
            }
            // 0x00, 0x65, 0x64, 0x6F, 0x66, 0x00
            boolean notHeader = temp[0] != 0x00 || temp[1] != 0x65 || temp[2] != 0x64 || temp[3] != 0x6F || temp[4] != 0x66 || temp[5] != 0x00;
            if (notHeader) {
                i++;
                fileData.position(i);
                if (i >= fileSize - 6) {
                    Log.i(TAG, "extractEdof: not found");
                    break;
                }
            } else {
                Log.i(TAG, "extractEdof: found edof header");
                found = true;
                break;
            }

        }
        if (!found) {
            Log.e(TAG, "extractEdof: no EDOF header found");
            return -1;
        }

        fileData.clear();

        int segmentIndex = 0;

        while (true) {
            int r = scanSegment(fileData, segmentIndex);
            if (r == -1) {
                if (segmentIndex > 1) {
                    fileData.clear();
                    extractEdof(fileData);
                }
                return -1;
            }

            segmentIndex++;
            index = r + 1;
            Log.i(TAG, "extractEdof: index = " + index);
            if (index > fileSize) {
                return -1;
            }
        }

    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    private void extractEdof(ByteBuffer data) {
        byte[] header = new byte[6];
        data.get(header);
        //{0x65, 0x64, 0x6F, 0x66};

        boolean notHeader = header[0] != 0x00 || header[1] != 0x65 || header[2] != 0x64 || header[3] != 0x6F || header[4] != 0x66 || header[5] != 0x00;
        if (notHeader) {
            Log.e(TAG, "extractEdof: frame is not EDOF");
            return;
        }

        index = data.position() - 6 - 3;
        index += 8;
        // columns = int.from_bytes(data[idx + 16: idx + 18], byteorder='little')
        //	rows = int.from_bytes(data[idx + 18: idx + 20], byteorder='little')
        int colums = ((data.get(index + 16) & 0xff) << 8) + (data.get(index + 18) & 0xff);
        int rows = ((data.get(index + 18) & 0xff) << 8) + (data.get(index + 20) & 0xff);
        int orientation = data.get(index + 7);

        index += 68;

        data.rewind();
        try {
            Bitmap bitmap = Bitmap.createBitmap(colums, rows, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(data);

            File file = new File("/sdcard/example_edof.png");
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    public int scanSegment(ByteBuffer data, int segmentIndex) {
        Log.i(TAG, "scanSegment: position: " + data.position() + ", capacity: " + data.capacity() + ", limit: " + data.limit());
        byte[] header = new byte[2];
        data.get(header);

        // {(byte) 0xFF, (byte) 0xD8};
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
            Log.e(TAG, "scanSegment: found header");
        }

        int startPos = index;
        Log.i(TAG, "scanSegment: " + startPos);
        int error = startPos - index;
        if (error < 15000){
            index = startPos;
        }else {
            Log.e(TAG, "scanSegment: error large than 150000, skipping ");
        }

        data.position(index);

        byte[] header2 = new byte[2];
        data.get(header2);

        // {(byte) 0xFF, (byte) 0xD8};
        if ((header2[0] != (byte) 0xFF || header2[1] != (byte) 0xD8)) {
            Log.e(TAG, "scanSegment: not found header");
            return -1;
        }

        Log.i(TAG, "found scanSegment: index = " + index);
        int i = index + 2;
        int length = data.limit();
        while (i < length) {
            if (data.get(i) == (byte) 0xFF) {

                byte t = data.get(i + 1);
                if (t == (byte) 0xD9 || t == (byte) 0xD8) {
                    i += 2;
                    continue;
                }

                if (t == (byte) 0xDA) {
                    int j = i + 2;
                    while (!(data.get(j) == (byte) 0xFF) && (data.get(j + 1) == (byte) 0xD9)) {
                        j += 1;
                    }
                    j += 1;

                    Log.i(TAG, String.format("\t scanSegment: * found segment %d, range %d to %d, length %d", segmentIndex, index, j, j - index));

                    // TODO: 2019/7/31 save origin picture data;
                    data.position(index);
                    data.limit(j + 1);
                    Log.w(TAG, "scanSegment ==== : index = " + index + ", limit = " + (j + 1));
                    try {
                        FileOutputStream fos = new FileOutputStream("/sdcard/example-origin.png");
                        FileChannel fileChannel = fos.getChannel();
                        fileChannel.write(data);

                        fileChannel.close();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "scanSegment FileNotFoundException: ", e);
                    } catch (IOException e) {
                        Log.e(TAG, "scanSegment IOException: ", e);
                    }
                    data.limit(length);
                    Log.i(TAG, "scanSegment: j = " + j);
                    return j;
                }

                int size = 256 * (data.get(i + 2) & 0xFF) + (data.get(i + 3) & 0xFF) + 2;
                //Log.i(TAG, "scanSegment:   ---- size = " + size);
                i += size;

            }
            i++;
        }
        return 0;
    }
}
