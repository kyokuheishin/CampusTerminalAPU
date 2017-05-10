/**
 * Created by qbx on 2017/4/20.
 */
import okhttp3.*;
import okio.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.*;
import java.util.HashMap.*;
import java.util.List;

public class CampusTerminal {
//    private static HashMap informationMap;
    private static HashMap informationMap = new HashMap();
    private static OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .cookieJar(new CookieJar() {
                List<Cookie> cookies;
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    this.cookies = cookies;
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    if (cookies != null)
                        return cookies;
                    return new ArrayList<Cookie>();
                }
            }).build();

    static Void ctSpTop() throws IOException{


        final Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/sptop.do")
//                .addHeader("Cookie",JsessionID)
//                .header("Host","portal2.apu.ac.jp")
//                .header("Origin","https://portal2.apu.ac.jp")
//                .header("Referer","https://portal2.apu.ac.jp/campusp/sptop.do")
//                .header("Upgrade-Insecure-Requests","1")
//                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
//                .header("Content-Type","application/x-www-form-urlencoded")
//                .post(formBodya)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();

        return null;
    }

    static void ctInformationFromUniversity() throws IOException{
        final  ArrayList titleList = new ArrayList();
        final ArrayList dateListSending =new ArrayList();
        final ArrayList dateListReading = new ArrayList();
        final ArrayList sourceList =new ArrayList();
        final ArrayList linkList = new ArrayList();
        informationMap.clear();
//        HashMap informationMap = new HashMap();
        int n = 1;
        final Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do?buttonName=searchList&msgsyucds=03")
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        String html = response.body().string();
//        System.out.println(response.body().toString());
        Document document = Jsoup.parse(html);
        Elements cells = document.select("li a");
        for (Element cell :cells) {
            String title = cell.getElementsByTag("h4").text();
            String dateSending = cell.select(".date:eq(1)").text();
            String dateReading = cell.select(".date:eq(2)").text();
            String source = cell.select("[style=\"white-space: normal;\"]").text();
            String link =cell.attr("href");
            titleList.add(title);
            dateListSending.add(dateSending);
            dateListReading.add(dateReading);
            sourceList.add(source);
            linkList.add(link);
        }
        informationMap.put("title",titleList);
        informationMap.put("dateSending",dateListSending);
        informationMap.put("dateReading",dateListReading);
        informationMap.put("source",sourceList);
        informationMap.put("link",linkList);
//        Elements titles = document.getElementsByTag("h4");
//        String stringtitles= document.getElementsByTag("h4").text();
//        System.out.println(stringtitles);
//        Elements dates = document.select(".date");
//        Elements sources = document.select("[style=\"white-space: normal;\"]");

//        for (Element title :titles){
//            titlelist.add(title.text());
//        }
//        informationMap.put("title",titlelist);
//        for (Element date : dates){
//            datelist.add(date.text());
//        }
//        informationMap.put("date",datelist);
//        for (Element source:sources){
//            sourcelist.add(source.text());
//        }
//        informationMap.put("source",sourcelist);


    }

    static Void ctInformationFromUniversityDetail(int messageNo) throws IOException{
        ArrayList links = (ArrayList) informationMap.get("link");
        final Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/"+(String) links.get(messageNo))
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        System.out.println(response.body().string());
        return null;
    }


    static Void ctInformation() throws IOException{
        final Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do?clearAccessData=true&contenam=wbspmgjr&kjnmnNo=9")
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        return null;
    }

    static Void ctLogin(String username,String password) throws IOException {


        FormBody formBody = new FormBody.Builder()
                .addEncoded("forceDevice","sp")
//                .addEncoded("buttonName","login")
                .addEncoded("lang","1")
                .addEncoded("userId",username)
                .addEncoded("password",password)
                .addEncoded("login", "login")

                .build();


        final Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/splogin.do")
//                .addHeader("Cookie",JsessionID)
//                .header("Host","portal2.apu.ac.jp")
//                .header("Origin","https://portal2.apu.ac.jp")
//                .header("Referer","https://portal2.apu.ac.jp/campusp/sptop.do")
//                .header("Upgrade-Insecure-Requests","1")
//                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(formBody)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
//        JsessionID = response.header("Set-Cookie").split(";")[0];
//        Log.d("Cookie",response.headers().toString());
        return null;
    }
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String username = br.readLine();
        String password = br.readLine();

        System.out.println(ctSpTop());
        System.out.println(ctLogin(username,password));
        System.out.println(ctInformation());
        ctInformationFromUniversity();
        ctInformationFromUniversityDetail(0);
        System.out.println(informationMap);
    }
    public void init(){


    }


}
