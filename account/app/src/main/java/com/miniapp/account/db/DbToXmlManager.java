package com.miniapp.account.db;

import android.content.Context;
import android.database.Cursor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

import com.miniapp.account.LogUtil;

/**
 * Created by zl on 20-12-3.
 */
public class DbToXmlManager {
    private static final String TAG = "AccountDbToXmlMgr";

    private Context mContext = null;
    private String mExportDir = null;
    private Cursor mCursor = null;

    public DbToXmlManager(Context context){
        mContext = context;
    }

    public long start(String path, Cursor cursor){
        mExportDir = path;
        mCursor = cursor;
        return export();
    }

    private long export() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document d = null;
        Element root = null;

        try{
            db = dbf.newDocumentBuilder();
        }catch(ParserConfigurationException e){
            LogUtil.e(TAG,"export() newDocumentBuilder Exception = " + e);
            e.printStackTrace();
        }

        if(db != null){
            d = db.newDocument();
            if(d != null){
                root = d.createElement("Account");
                d.appendChild(root);
            }
        }

        String[][] data;

        try {
            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                int count = mCursor.getCount();

                data = new String[count][mCursor.getColumnCount()];

                for(int i = 0; i < count; i++) {
                    for(int j = 0; j < mCursor.getColumnCount(); j++) {
                        data[i][j] = mCursor.getString(j);
                    }
                    mCursor.moveToNext();
                }
                Element mof;
                for(int i = 0; i < count && d != null && root != null; i++) {
                    mof = d.createElement("item");
                    root.appendChild(mof);
                    for(int j = 0; j < mCursor.getColumnCount(); j++) {
                        String column = mCursor.getColumnName(j);
                        Element cell = d.createElement(column);
                        cell.setTextContent(data[i][j]);
                        mof.appendChild(cell);
                    }
                }
            }
        }
        catch (Exception e) {
            LogUtil.e(TAG, "cursor " + e);
            e.printStackTrace();
        }
        finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        try {
            t = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            LogUtil.e(TAG,"export() newTransformer Exception = " + e);
            e.printStackTrace();
        }
        if(t != null){
            t.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yes");
        }
        StringWriter sw = new StringWriter( );
        try {
            if(t != null){
                t.transform(new DOMSource(d) , new StreamResult(sw));
            }
        } catch (TransformerException e) {
            LogUtil.e(TAG,"export() transform Exception = " + e);
            e.printStackTrace();
        }

        LogUtil.i(TAG,"export() path = " + mExportDir);
        File file = new File(mExportDir);
        if(!file.exists()){
            file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (Exception e) {
                LogUtil.e(TAG,"file exists Exception = " + e);
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sw.toString().getBytes());
            fos.close();
        }catch(Exception e){
            LogUtil.e(TAG,"backup() write Exception = " + e);
            e.printStackTrace();
        }

        return file.length();
    }
}
