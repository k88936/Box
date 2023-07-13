package org.kvto;

import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HiMaLaYaSpider implements PageProcessor {


    HiMaLaYaSpider(int albumID) {
        super();
        this.URL_MAIN = "https://www.ximalaya.com/revision/album/v1/getTracksList?albumId=" +

                albumID +"&pageNum=";

    }

    public  final String URL_MAIN ;
    public static final String URL_HOME = "https://www.ximalaya.com";
    static int pageNum = 0;
    private final Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);

    public static void main(String[] args) {

        new File(".\\data\\").mkdirs();

        MyConsolePipeline CP = new MyConsolePipeline(".\\data\\");
        var hiMaLaYaSpider = new HiMaLaYaSpider(30917322);
        Spider.create(hiMaLaYaSpider)

                .setScheduler(new MyFileCacheQueueScheduler(".\\.cfg"))

                .addUrl(hiMaLaYaSpider.URL_MAIN+(++pageNum))
                .addPipeline(CP)
                .thread(10)

                .run();

    }


    @Override
    public void process(Page page) {

       // if (pageNum>2)System.exit(2);


        if (page.isDownloadSuccess()) {
            if (page.getUrl().regex("album").match()) {


                List<Selectable> list = page.getJson().jsonPath("data.tracks").nodes();

                if(list.isEmpty())
                {
                    return;
                }

                for (Selectable selectable : list) {

                    Json dataJson = new Json(selectable.toString());


                    page.addTargetRequest(URL_HOME + dataJson.jsonPath("url").toString());

                }

                page.addTargetRequest((URL_MAIN.replaceFirst("INDEX", String.valueOf(++pageNum))));


            } else {



                page.putField("title", page.getHtml().css("h1").toString().replaceAll("<h1 class=\"title-wrapper kn_\">|</h1>", ""));

                page.putField("time", page.getHtml().regex("(?<=<span class=\"time kn_\">).*?(?=</span>)").toString());

                String article=    page.getHtml().css("article").all().toString()


                        .replaceAll("<strong style=\"color:#FC5832;word-break:break-all;font-family:Helvetica,Arial,sans-serif;font-weight: normal;\">|</span>|<span>|<br>|&nbsp;|</p>|<article class=\"intro {2}_Fc\">|</article>|<article class=\"lyric hidden _Fc\">", "")
                        .replaceAll("\n", " ")
                        .replaceAll("<p style=\"color:#333333;font-weight:normal;font-size:16px;line-height:30px;font-family:Helvetica,Arial,sans-serif;hyphens:auto;text-align:justify;\" data-flag=\"normal\">|<p style=\"color:#333333;font-weight:normal;font-size:16px;line-height:30px;font-family:Helvetica,Arial,sans-serif;hyphens:auto;text-align:justify;\">", "\n")
                        .replaceAll(",", " ");

                page.putField("article",article );





               }
        } else {


        }
    }

    @Override
    public Site getSite() {

        return site;
    }
}

class MyConsolePipeline implements Pipeline {


    int index = 1;
    String address;

    public MyConsolePipeline(String address) {

        this.address = address;

    }


    @Override
    public void process(ResultItems resultItems, Task task) {

//        Logger logger = LoggerFactory.getLogger(getClass());
        Map<String, Object> map = resultItems.getAll();


        if (map.get("time") != null) {

            String time = map.get("time").toString();
            String title = map.get("title").toString();
            String article = map.get("article").toString();

            if (article.length()<500)return;

            File file = new File(address + time.substring(0, 10) + title.replaceAll("\\p{Punct}", "") + ".txt");




            try {
                FileWriter writer = new FileWriter(file, false);
                writer.write("\r\n" + title);
                writer.write("\r\n" + time);
                writer.write("\r\n" + article);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }






//            logger.info("finish: " +index++ + "\s\scontent: " +article.length()+ "\s\stitle: " +map.get("title").toString());
            }






        }

    }

















