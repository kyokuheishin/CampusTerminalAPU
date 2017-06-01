/**
 * Created by qbx on 2017/4/20.
 */
import okhttp3.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CampusTerminal {
//    private static HashMap informationMap;
    private static HashMap<String, ArrayList<String>> informationMap = new HashMap<>();
    private static HashMap<String,ArrayList<String>> messageMap = new HashMap<>();
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
                    return new ArrayList<>();
                }
            }).build();

    private static Void ctSpTop() throws IOException{


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
    private static class ctMessage{

        private static void getInformationFromUniversity() throws IOException {
            ctGetMessage(0);
        }

        private static void getImportantMessageToYou() throws IOException{
            ctGetMessage(1);
        }
        private static void ctGetMessage(int type) throws IOException{
            HashMap<String, ArrayList<String>> map = new HashMap<>();
            String msgsyucds = new String();
            final ArrayList<String> titleList = new ArrayList<>();
            final ArrayList<String> dateListSending =new ArrayList<String>();
            final ArrayList<String> dateListReading = new ArrayList<String>();
            final ArrayList<String> sourceList =new ArrayList<>();
            final ArrayList<String> linkList = new ArrayList<>();
            if (type == 0){
                informationMap.clear();
                map =informationMap;
                msgsyucds = "03";
            }
            else if (type == 1){
                messageMap.clear();
                map = messageMap;
                msgsyucds = "05";
            }

//        HashMap informationMap = new HashMap();
            int n = 1;
            final Request request = new Request.Builder()
                    .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do?buttonName=searchList&msgsyucds="+msgsyucds)
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

            map.put("title",titleList);
            map.put("dateSending",dateListSending);
            map.put("dateReading",dateListReading);
            map.put("source",sourceList);
            map.put("link",linkList);
            System.out.println(map);
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
    }




    private static void ctInformationFromUniversityDetail(int messageNo) throws IOException{
        HashMap<String,ArrayList<String>> detailMap = new HashMap();
        ArrayList<String> bodyList = new ArrayList<>();
        ArrayList<String> otherInformationList = new ArrayList<>();
        ArrayList<String> otherInformationContentList = new ArrayList<>();
        ArrayList<String> otherInformationLinkList = new ArrayList<>();
        ArrayList<String> otherInformationFileLinkList = new ArrayList<>();
        ArrayList<String> links = informationMap.get("link");

        final Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/"+ links.get(messageNo))

                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        String html = response.body().string();
//        System.out.println(html);
//        System.out.println(response.body().toString());
        Document document = Jsoup.parse(html);
//        System.out.println(document.body().text());
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
//        System.out.println(document.text());
        Element detail = document.getElementById("detail");
//        System.out.println(detail.text());
//        String content = detail.select("h3:eq(1)").text();
//        System.out.println(content);
        String body = detail.select("p:not(.content)").toString();
        bodyList.add(br2nl(body));

//        System.out.println(br2nl(body));
        Elements otherInformations = detail.select("font.label");
        Elements otherInformationContents = detail.select(".content");
        Elements otherInformationLinks = detail.select("a:not([data-role])").select("a:not([onclick])");
        Elements otherInformationFileLinks = detail.select("a:not([target])").select("a:not([data-role])");

//        System.out.println(otherInformationContent.text());
        for (Element otherInformation : otherInformations){
            otherInformationList.add(otherInformation.text());
            System.out.println(otherInformation.text());
        }

        for (Element otherInformationContent : otherInformationContents){
            otherInformationContentList.add(otherInformationContent.text());
            System.out.println(otherInformationContent.text());
        }
        for (Element otherInformationLink:otherInformationLinks){
            System.out.println(otherInformationLink.text());
            otherInformationLinkList.add(otherInformationLink.attr("href"));
        }

        for (Element otherInformationFileLink:otherInformationFileLinks){
            System.out.println(otherInformationFileLink.attr("href"));
            otherInformationFileLinkList.add(otherInformationFileLink.attr("href"));
        }
        detailMap.put("body",bodyList);
        detailMap.put("otherInformationTitle",otherInformationList);
        detailMap.put("otherInformationContent",otherInformationContentList);
        detailMap.put("otherInformationLink",otherInformationLinkList);
        detailMap.put("otherInformationFileLink",otherInformationFileLinkList);
        System.out.println(detailMap);

//        if ()
        if (otherInformationList.get(0) == "ＵＲＬ"){

            if (otherInformationList.get(1) == "ＵＲＬ"|otherInformationList.get(1) == "添付ファイル"){

            }
        }
        else if (otherInformationList.get(0) == "添付ファイル"){

        }
//        String otherInformation = detail.select("font.label").text();
//        System.out.println(otherInformation);

    }
    private static String br2nl(String html) {
        if(html==null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    static String cleanPreserveLineBreaks(String bodyHtml) {

        // get pretty printed html with preserved br and p tags
        String prettyPrintedBodyFragment = Jsoup.clean(bodyHtml, "", Whitelist.none().addTags("br", "p"), new Document.OutputSettings().prettyPrint(true));
        // get plain text with preserved line breaks by disabled prettyPrint
        return Jsoup.clean(prettyPrintedBodyFragment, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    private static Void ctInformation() throws IOException{
        final Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do?clearAccessData=true&contenam=wbspmgjr&kjnmnNo=9")
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        return null;
    }

    private static Void ctLogin(String username, String password) throws IOException {


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
        ctMessage.getImportantMessageToYou();
//        ctMessage.getInformationFromUniversity();
//        ctInformationFromUniversityDetail(1);
//        System.out.println(informationMap);
    }
    public void init(){


    }


}
