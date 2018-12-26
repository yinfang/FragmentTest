package com.clubank.device.op;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.clubank.common.R;
import com.clubank.domain.BRT;
import com.clubank.domain.C;
import com.clubank.domain.MarshalDouble;
import com.clubank.domain.Result;
import com.clubank.util.AppException;
import com.clubank.util.JsonUtil;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.SPUtil;
import com.clubank.util.U;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONStringer;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OPBase {

    protected boolean postJson = false;
    private Context context;
    private String namespace;
    private SharedPreferences sp;
    public static String SessionID = "";
    public OkHttpClient okClient;

    protected String baseUrl = "";//接口的基本地址，默认为项目中设置的,可以在子类中改变

    public OPBase(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(C.APP_ID, Context.MODE_PRIVATE);
        namespace = sp.getString("namespace", "http://www.hclub.com/");
        okClient = OkHttpManager.getInstance().getClient();
        baseUrl = C.baseUrl;
    }

    protected Object operate(String methodName, MyRow args) throws Exception {

        Object ret = 0;
        String soapAction = namespace + methodName;
        SoapObject request = new SoapObject(namespace, methodName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        for (String key : args.keySet()) {
            Object o = args.get(key);
            request.addProperty(key, o);
        }
        envelope.implicitTypes = true;// output xml tag without type declared
        envelope.dotNet = true;

        envelope.headerOut = new Element[1];
        envelope.headerOut[0] = buildAuthHeader();

        new MarshalBase64().register(envelope);
        new MarshalDouble().register(envelope);

        envelope.setOutputSoapObject(request);

        if (C.wsUrl == null) {
            C.wsUrl = sp.getString("wsUrl", null);
        }

        HttpTransportSE ht = new HttpTransportSE(C.wsUrl, C.TIMEOUT);

        ht.debug = true;
        try {
            ht.call(soapAction, envelope);

        } catch (Exception e) {
            String br = System.getProperty("line.separator");
            String s = br + e.getMessage();
            s += br + methodName;
            s += br + "wsUrl=" + C.wsUrl;
            s += br + "Token=" + sp.getString("token", "");
            for (String key : args.keySet()) {
                s += br + key + "=" + args.get(key);
            }
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(b));
            s += br + new String(b.toByteArray());
            throw new AppException(s);
        }
        String requestXml = ht.requestDump;
        Log.d("Request", methodName + ":" + requestXml);
        if (envelope.getResponse() != null) {
            String responseXml = ht.responseDump;
            Log.d("Response", methodName + ":" + responseXml);
            ret = envelope.getResponse();
        }
        return ret;
    }


    protected Result operateHttp(int requestModel, String methodName, Object ob) throws
            Exception {

        Result result = new Result();
        Request.Builder requestBuilder = new Request.Builder();
        String jpushId = SPUtil.getString("jpushId", "");
        String UDID = SPUtil.getString("UDID", "");
        if (TextUtils.isEmpty(UDID)) {
            UDID = U.getUdid(context);
            SPUtil.saveSetting("UDID", UDID);
        }
        /*requestBuilder.addHeader("Accept", "application/json");
        requestBuilder.addHeader("Content-type", "application/json;charset=utf-8");
        requestBuilder.addHeader("token", sp.getString("token", ""));
        requestBuilder.addHeader("userId", sp.getString("userId", ""));
        requestBuilder.addHeader("plat", "2");
        requestBuilder.addHeader("flatType", "2");
        requestBuilder.addHeader("version", SPUtil.getString("vname", ""));
        requestBuilder.addHeader("buildVersion", SPUtil.getInt("vcode", -1) + "");
        requestBuilder.addHeader("UDID", UDID);
        requestBuilder.addHeader("jpushId",jpushId);*/
        if (requestModel == C.HTTP_GET) {
            requestBuilder.url(appendUrl(baseUrl + methodName, (MyRow) ob));
            requestBuilder.get();
        } else if (requestModel == C.HTTP_POST) {
            requestBuilder.url(baseUrl + methodName);
            if (postJson) {
                MyRow row = (MyRow) ob;
                String json = new Gson().toJson(row);
                RequestBody body = RequestBody.create(MediaType.parse("application/json; " +
                        "charset=utf-8"), json);
                requestBuilder.post(body);
                Log.d("Request", baseUrl + methodName + json);
            } else {
                FormBody.Builder fBuilder = new FormBody.Builder();
                MyRow row = (MyRow) ob;
                if (row != null && row.size() > 0) {
                    Set<Map.Entry<String, Object>> mapEntry = row.entrySet();
                    for (Map.Entry<String, Object> entry : mapEntry) {
                        fBuilder.add(entry.getKey(), entry.getValue().toString());
                    }
                }
                requestBuilder.post(fBuilder.build());
                Log.d("Request", baseUrl + methodName + row.toString());
            }
        }
        Response response = okClient.newCall(requestBuilder.build()).execute();
        String res = "";
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                res = responseBody.string();
                Log.d("Response", baseUrl + methodName + res);
            }
        } else {
//            ResponseBody responseBody = response.body();
//            if (responseBody != null) {
//                res = responseBody.string();
//            } else {
//                result.code = response.code();
//            }
            result.code = response.code();
        }
        if (dealData()) {
            analyseData(res, result);
        } else {
            if (!TextUtils.isEmpty(res)) {
                if (methodName.equals("ViewVersion")) {//版本更新
                    result.obj = JsonUtil.getMyData(res);
                } else {
                    MyRow rowRes = JsonUtil.getRow(res);
                    if (rowRes != null) {
                        if (methodName.equals("ReportCheckin")) {//营业统计客流量
                            result.obj2 = rowRes.getInt("TotalEnterNum");
                        }
                        result.obj = rowRes.get(C.apiDataKey);
                        result.code = rowRes.getInt(C.apiState);
                        result.msg = rowRes.getString(C.apiMsg);
                        if (result.code == BRT.AUTH_ERROR.getCode()) {
                            result.msg = context.getString(R.string.please_relogin);
                        }
                    } else {
                        result.code = BRT.UNKNOWN_ERROR.getCode();
                        result.msg = BRT.UNKNOWN_ERROR.getMsg();
                        result.obj = rowRes;
                    }
                }
            }
        }
        return result;
    }


    /**
     * 如果是GET请求，则请求参数在URL中拼
     *
     * @param url 带方法名的url
     * @param row url中拼接的参数
     * @return 请求的get url
     * @throws UnsupportedEncodingException
     */
    private String appendUrl(String url, MyRow row)
            throws UnsupportedEncodingException {
        StringBuilder buf = new StringBuilder(url);
        Set<Map.Entry<String, Object>> entrys = null;
        if (row != null && !row.isEmpty()) {
            buf.append("&");
//			buf.append("?");
            entrys = row.entrySet();
            for (Map.Entry<String, Object> entry : entrys) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue() + "", "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }

        Log.d("Request", buf.toString());
        return buf.toString();
    }


    private Element buildAuthHeader() {
        Element h = new Element().createElement(namespace,
                "AuthenticationSoapHeader");
        SharedPreferences sp = context.getSharedPreferences(C.APP_ID,
                Context.MODE_PRIVATE);
        if (null != C.headerData) {//自定义头文件

            Set<String> set = C.headerData.keySet();
            for (String s : set) {
                addElement(h, s, C.headerData.get(s).toString());
            }

        } else {
            String token = sp.getString("token", "");
            addElement(h, "Token", token);
            addElement(h, "Lang", context.getResources().getConfiguration().locale);
            addElement(h, "SessionID", SessionID);

//            if (C.location != null) {
//                addElement(h, "Longitude", C.location.getLongitude());
//                addElement(h, "Latitude", C.location.getLatitude());
//            }
        }

        return h;
    }

    private void addElement(Element h, String name, Object o) {
        if (o != null) {
            Element e = new Element().createElement(namespace, name);
            e.addChild(Node.TEXT, "" + o.toString());
            h.addChild(Node.ELEMENT, e);
        }
    }

    protected MyData getList(SoapObject v) {
        MyData list = new MyData();
        for (int i = 0; i < v.getPropertyCount(); i++) {

            SoapObject so = (SoapObject) v.getProperty(i);
            list.add(getRow(so));

        }
        return list;
    }

    protected String[] getStringArray(SoapObject v) {
        String[] s = new String[v.getPropertyCount()];
        for (int i = 0; i < v.getPropertyCount(); i++) {
            Object so = (Object) v.getProperty(i);
            s[i] = so.toString();
        }
        return s;
    }

    protected MyRow getRow(SoapObject so) {
        MyRow row2 = new MyRow();
        for (int j = 0; j < so.getPropertyCount(); j++) {
            PropertyInfo pi = new PropertyInfo();
            so.getPropertyInfo(j, pi);
            String value = "";
            if (pi.getType() == SoapPrimitive.class) {
                value = pi.getValue().toString();
                row2.put(pi.getName(), value);
            } else if (pi.getType() == SoapObject.class) {
                if ("anyType{}".equals(pi.getValue().toString())) {
                    continue;// ignore empty value
                }
                SoapObject v = (SoapObject) pi.getValue();
                row2.put(pi.getName(), getList(v));
            }

        }
        return row2;
    }


    public Result execute(Object... args) throws Exception {
        return null;
    }

    protected boolean dealData() {
        return false;
    }

    protected void analyseData(String response, Result result) {

    }
}
