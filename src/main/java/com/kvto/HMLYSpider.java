package com.kvto;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HMLYSpider {


    public static final String URL_HOME = "https://www.ximalaya.com";
    public static final Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    static int pageNum = 0;
    public final String URL_MAIN;
    Spider runner;
    Logger logger = LoggerFactory.getLogger();


    HMLYSpider(int albumID) {

        this.URL_MAIN = "https://www.ximalaya.com/revision/album/v1/getTracksList?albumId=" +

                albumID + "&pageNum=";


        new File(".\\SpiderData\\data\\").mkdirs();


        Spider.create(new PageProcessor() {
                    @Override
                    public void process(Page page) {
                        if (page.isDownloadSuccess()) {
                            if (page.getUrl().regex("album").match()) {//目录


                                List<Selectable> list = page.getJson().jsonPath("data.tracks").nodes();

                                if (list.isEmpty()) {
                                    return;
                                }

                                for (Selectable selectable : list) {

                                    Json dataJson = new Json(selectable.toString());


                                    page.addTargetRequest(URL_HOME + dataJson.jsonPath("url").toString());

                                }

                                page.addTargetRequest(URL_MAIN + (++pageNum));


                            } else {//文章


                                page.putField("title", page.getHtml().css("h1").toString().replaceAll("<h1 class=\"title-wrapper kn_\">|</h1>", ""));

                                page.putField("time", page.getHtml().regex("(?<=<span class=\"time kn_\">).*?(?=</span>)").toString());

                                String article = page.getHtml().css("article").all().toString()


                                        .replaceAll("<strong style=\"color:#FC5832;word-break:break-all;font-family:Helvetica,Arial,sans-serif;font-weight: normal;\">|</span>|<span>|<br>|&nbsp;|</p>|<article class=\"intro {2}_Fc\">|</article>|<article class=\"lyric hidden _Fc\">", "").replaceAll("\n", " ").replaceAll("<p style=\"color:#333333;font-weight:normal;font-size:16px;line-height:30px;font-family:Helvetica,Arial,sans-serif;hyphens:auto;text-align:justify;\" data-flag=\"normal\">|<p style=\"color:#333333;font-weight:normal;font-size:16px;line-height:30px;font-family:Helvetica,Arial,sans-serif;hyphens:auto;text-align:justify;\">", "\n").replaceAll(",", " ");

                                page.putField("article", article);


                            }
                        }
                    }

                    @Override
                    public Site getSite() {
                        return HMLYSpider.site;
                    }
                })

                .setScheduler(new MyFileCacheQueueScheduler(".\\SpiderData\\.cfg\\"))

                .addUrl(this.URL_MAIN + (++pageNum))
                .addPipeline(new Pipeline() {
                    final String address = ".\\SpiderData\\data\\";
                    int index = 0;

                    @Override
                    public void process(ResultItems resultItems, Task task) {
                        index++;
//        Logger logger = LoggerFactory.getLogger(getClass());
                        Map<String, Object> map = resultItems.getAll();
                        logger.info("Processing..." + index);

                        if (map.get("time") != null) {

                            String time = map.get("time").toString();
                            String title = map.get("title").toString();
                            String article = map.get("article").toString();

                            if (article.length() < 500) return;

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


                            logger.info("finish: " + index + "\s\scontent: " + article.length() + "\s\stitle: " + map.get("title").toString());
                        }


                    }

                }).thread(5).run();

    }

    public static class MyFileCacheQueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, Closeable {

        private final String fileUrlAllName = ".urls.txt";
        private final String fileCursor = ".cursor.txt";
        private final AtomicBoolean inited = new AtomicBoolean(false);
        Logger logger = LoggerFactory.getLogger();
        private String filePath = System.getProperty("java.io.tmpdir");
        private Task task;
        private PrintWriter fileUrlWriter;
        private PrintWriter fileCursorWriter;
        private AtomicInteger cursor = new AtomicInteger();
        private BlockingQueue<Request> queue;

        private Set<String> urls;

        private ScheduledExecutorService flushThreadPool;

        public MyFileCacheQueueScheduler(String filePath) {
            if (!filePath.endsWith("/") && !filePath.endsWith("\\")) {
                filePath += "/";
            }
            this.filePath = filePath;
            initDuplicateRemover();
        }

        private void initDuplicateRemover() {
            setDuplicateRemover(
                    new DuplicateRemover() {
                        @Override
                        public boolean isDuplicate(Request request, Task task) {
                            if (!inited.get()) {
                                init(task);
                            }

                            if (request.getUrl().contains("album")) {
                                return false;
                            }
                            return !urls.add(request.getUrl());
                        }

                        @Override
                        public void resetDuplicateCheck(Task task) {
                            urls.clear();
                        }

                        @Override
                        public int getTotalRequestsCount(Task task) {
                            return urls.size();
                        }
                    });
        }

        private void init(Task task) {
            this.task = task;
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            readFile();
            initWriter();
            initFlushThread();
            inited.set(true);
            logger.info("init cache scheduler success");
        }


        /*
         * 重新实现
         *
         * 加入筛选
         *
         *
         */

        private void readFile() {
            try {
                queue = new LinkedBlockingQueue<Request>();
                urls = new LinkedHashSet<String>();
                readCursorFile();
                readUrlFile();
                // initDuplicateRemover();
            } catch (FileNotFoundException e) {
                //init
                logger.info("init cache file " + getFileName(fileUrlAllName));
            } catch (IOException e) {
                logger.error("init file error", e);
            }
        }

        private void initWriter() {
            try {
                fileUrlWriter = new PrintWriter(new FileWriter(getFileName(fileUrlAllName), true));
                fileCursorWriter = new PrintWriter(new FileWriter(getFileName(fileCursor), false));
            } catch (IOException e) {
                throw new RuntimeException("init cache scheduler error", e);
            }
        }

        private void initFlushThread() {
            flushThreadPool = Executors.newScheduledThreadPool(1);
            flushThreadPool.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    flush();
                }
            }, 10, 10, TimeUnit.SECONDS);
        }

        private void readCursorFile() throws IOException {
            BufferedReader fileCursorReader = null;
            try {
                fileCursorReader = new BufferedReader(new FileReader(getFileName(fileCursor)));
                String line;
                //read the last number
                while ((line = fileCursorReader.readLine()) != null) {
                    cursor = new AtomicInteger(NumberUtils.toInt(line));
                }
            } finally {
                if (fileCursorReader != null) {
                    IOUtils.closeQuietly(fileCursorReader);
                }
            }
        }

        private void readUrlFile() throws IOException {
            String line;
            BufferedReader fileUrlReader = null;
            try {
                fileUrlReader = new BufferedReader(new FileReader(getFileName(fileUrlAllName)));
                int lineReaded = 0;
                while ((line = fileUrlReader.readLine()) != null) {
                    urls.add(line.trim());
                    lineReaded++;
                    if (lineReaded > cursor.get()) {
                        queue.add(deserializeRequest(line));
                    }
                }
            } finally {
                if (fileUrlReader != null) {
                    IOUtils.closeQuietly(fileUrlReader);
                }
            }
        }

        private String getFileName(String filename) {
            return filePath + task.getUUID() + filename;
        }

        private void flush() {
            fileUrlWriter.flush();
            fileCursorWriter.flush();
        }

        protected Request deserializeRequest(String line) {
            return new Request(line);
        }

        public void close() throws IOException {
            flushThreadPool.shutdown();
            fileUrlWriter.close();
            fileCursorWriter.close();
        }

        @Override
        public int getLeftRequestsCount(Task task) {
            return queue.size();
        }

        @Override
        public int getTotalRequestsCount(Task task) {
            return getDuplicateRemover().getTotalRequestsCount(task);
        }

        @Override
        public synchronized Request poll(Task task) {
            if (!inited.get()) {
                init(task);
            }
            fileCursorWriter.println(cursor.incrementAndGet());
            return queue.poll();
        }

        @Override
        protected void pushWhenNoDuplicate(Request request, Task task) {
            if (!inited.get()) {
                init(task);
            }
            queue.add(request);
            fileUrlWriter.println(serializeRequest(request));
        }

        protected String serializeRequest(Request request) {
            return request.getUrl();
        }

    }
}


















