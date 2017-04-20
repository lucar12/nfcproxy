package org.eleetas.nfc.nfcproxy.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.nfc.Tag;
import android.nfc.tech.TagTechnology;
import android.util.Log;


//TODO: HACK since BasicTagTechnology is not currently visible from SDK. primiarly want the transceive() method (otherwise we could just use TagTechnology) 
//TODO: Just access BasicTagTechnolgy directly but modifying platform library android.jar. (use library with hidden classes from framework.jar)
public class BasicTagTechnologyWrapper implements TagTechnology {
    // TAG for log
    private static final String TAG = "BasicTagTech";

    //	Method get;
    Method transceive;
    Method isConnected;
    Method connect;
    Method getMaxTransceiveLength;
    Method close;
    Tag mTag;
    Object mTagTech;
    Log log;

    public BasicTagTechnologyWrapper(Tag tag, String tech) throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class cls = Class.forName(tech);
        Method get = cls.getMethod("get", Tag.class);
        mTagTech = get.invoke(null, tag);
        transceive = cls.getMethod("transceive", byte[].class);
        isConnected = cls.getMethod("isConnected");
        connect = cls.getMethod("connect");
        getMaxTransceiveLength = cls.getMethod("getMaxTransceiveLength");
        close = cls.getMethod("close");
        mTag = tag;
    }

    @Override
    public boolean isConnected() {
        Boolean ret = false;
        try {
            ret = (Boolean) isConnected.invoke(mTagTech);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return ret;
    }

    @Override
    public void connect() throws IOException {
        try {
            connect.invoke(mTagTech);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof IOException) {
                throw (IOException) e.getTargetException();
            }
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        try {
            close.invoke(mTagTech);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof IOException) {
                throw (IOException) e.getTargetException();
            }
            Log.e(TAG, e.getMessage());
        }
    }

    public int getMaxTransceiveLength() {
        try {
            return (Integer) getMaxTransceiveLength.invoke(mTagTech);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            Log.e(TAG, e.getMessage());
        }
        return 0;
    }

    public byte[] transceive(byte[] data) throws IOException {
        try {
            return (byte[]) transceive.invoke(mTagTech, data);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof IOException) {
                throw (IOException) e.getTargetException();
            }
            Log.e(TAG, e.getMessage());
        }
        // this line is called when the return statement fails, which means there is an exception been thrown.
        // Then throw an additional IOException
        throw new IOException("transceive failed");
    }

    @Override
    public Tag getTag() {
        return mTag;
    }
}
