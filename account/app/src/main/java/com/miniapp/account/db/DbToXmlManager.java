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

import com.miniapp.account.LogUtil;
import com.miniapp.account.activity.AccountConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

/**
 * Created by zl on 20-12-3.
 */
public class DbToXmlManager {
    private static final String DBG_TAG = "AccountDbToXmlMgr";

    private Context mCtx = null;
    private String mBackUpDir = null;
    private AccountItemDb databaseHelper = null;

    public DbToXmlManager(Context context){
        mCtx = context;
        databaseHelper = new AccountItemDb(context);
    }

    public void start(String path){
        mBackUpDir = path;
        backup();
    }

    private long backup() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document d = null;
        Element root = null;

        try{
            db = dbf.newDocumentBuilder();
        }catch(ParserConfigurationException e){
            LogUtil.e(DBG_TAG,"backup() newDocumentBuilder Exception = " + e);
            e.printStackTrace();
        }
        if(db!=null){
            d = db.newDocument();
            if(d!=null){
                root = d.createElement("Account");
                d.appendChild(root);
            }
        }


        Cursor cursor = null;
        String data[][];

        try {
            cursor = databaseHelper.getCursor();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int count = cursor.getCount();

                data = new String[count][cursor.getColumnCount()];

                for(int i=0; i<count; i++) {
                    for(int j=0; j<cursor.getColumnCount(); j++) {
                        data[i][j] = cursor.getString(j);
                    }
                    cursor.moveToNext();
                }
                Element mof;
                for(int i=0; i<count && d!=null && root!=null; i++) {
                    mof = d.createElement("item");
                    root.appendChild(mof);
                    for(int j=0; j<cursor.getColumnCount(); j++) {
                        String column = cursor.getColumnName(j);
                        Element cell = d.createElement(column);
                        cell.setTextContent(data[i][j]);
                        mof.appendChild(cell);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        TransformerFactory tf = TransformerFactory.newInstance( );
        Transformer t = null;
        try {
            t = tf.newTransformer( );
        } catch ( TransformerConfigurationException e ) {
            LogUtil.e(DBG_TAG,"backup() newTransformer Exception = " + e);
            e.printStackTrace( );
        }
        if(t!=null){
            t.setOutputProperty( OutputKeys.ENCODING , "utf-8" );
            t.setOutputProperty( OutputKeys.METHOD , "xml" );
            t.setOutputProperty( OutputKeys.INDENT , "yes" );
            t.setOutputProperty( OutputKeys.CDATA_SECTION_ELEMENTS , "yes" );
        }
        StringWriter sw = new StringWriter( );
        try {
            if(t!=null){
                t.transform( new DOMSource( d ) , new StreamResult( sw ) );
            }
        } catch ( TransformerException e ) {
            LogUtil.e(DBG_TAG,"backup() transform Exception = " + e);
            e.printStackTrace( );
        }

        LogUtil.i(DBG_TAG,"backup() path = " + mBackUpDir);
        File file = new File(AccountConstants.EXTERNAL_FILE_PATH);
        if(!file.exists()){
            //先得到文件的上级目录，并创建上级目录，在创建文件
            file.getParentFile().mkdir();
            try {
                //创建文件
                file.createNewFile();
            } catch (Exception e) {
                LogUtil.e(DBG_TAG,"file exists Exception = " + e);
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sw.toString().getBytes());
            fos.close();
        }catch(Exception e){
            LogUtil.e(DBG_TAG,"backup() write Exception = " + e);
            e.printStackTrace( );
        }

        return file.length();
    }
}
