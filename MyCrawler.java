import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;

import com.google.common.collect.ImmutableList;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
    private static final Pattern filters = Pattern.compile(
        ".*(\\.(css|js|json))$");
	
    public static MyCrawlStat myCrawlStat = new MyCrawlStat();

    public MyCrawler() {
        //myCrawlStat = new MyCrawlStat();
    }
	
    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "https://www.usatoday.com/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
		
        if (filters.matcher(href).matches()) {
            return false;
        }
        ++(myCrawlStat.totalUrlExtract);
		
        if (href.startsWith("https://www.usatoday.com")
            || href.startsWith("http://www.usatoday.com")
            || href.startsWith("www.usatoday.com")
            || href.startsWith("usatoday.com")
            || href.startsWith("https://usatoday.com")
            || href.startsWith("http://usatoday.com"))
        {
            insideDomain(url.getURL());
            return true;
        }

        outsideDomain(url.getURL());
        return false;
    }

    public void insideDomain(String url) {
        // Check if already in setUniqueUrlInsideWebsite
        if (!myCrawlStat.setUniqueUrlInsideWebsite.contains(url)) {
            ++(myCrawlStat.uniqueUrlExtract);
            ++(myCrawlStat.uniqueUrlInsideWebsite);
            myCrawlStat.setUniqueUrlInsideWebsite.add(url);
        }
    }

    public void outsideDomain(String url) {
        // Check if already in setUniqueUrlOutsideWebsite
        if (!myCrawlStat.setUniqueUrlOutsideWebsite.contains(url)) {
            ++(myCrawlStat.uniqueUrlExtract);
            ++(myCrawlStat.uniqueUrlOutsideWebsite);
            myCrawlStat.setUniqueUrlOutsideWebsite.add(url);
        }
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        ++(myCrawlStat.numAttemptFetch);
        int statusCode = page.getStatusCode();

        // Add to fetch_usatoday.csv
        try {
            Controller.fetch_fw.write(page.getWebURL().getURL() + ", ");
            Controller.fetch_fw.write(statusCode + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (statusCode == HttpStatus.SC_OK) {
            // Succeeded
            ++(myCrawlStat.numFetchSuccess);
            ++(myCrawlStat.status200);

            String curURL = page.getWebURL().getURL();
            int curSize = page.getContentData().length;
            int numOutlinks = page.getParseData().getOutgoingUrls().size();
            String curContentType = page.getContentType();

            int sizeInKB = processSize(curSize);
            String simpleContentType = processContentType(curContentType);

            // Add the above to visit_usatoday.csv
            try {
                Controller.visit_fw.write(curURL + ", ");
                Controller.visit_fw.write(sizeInKB + ", ");
                Controller.visit_fw.write(numOutlinks + ", ");
                Controller.visit_fw.write(simpleContentType + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Aborted or Failed
            ++(myCrawlStat.numFetchFailOrAbort);

            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                ++(myCrawlStat.status301);
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                ++(myCrawlStat.status401);
            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                ++(myCrawlStat.status403);
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                ++(myCrawlStat.status404);
            }
        }
    }

    private int processSize(int curSize) {
        int sizeInKB = curSize / 1024;
        if (sizeInKB < 1) { ++(myCrawlStat.sizeLess1KB); }
        else if (sizeInKB < 10) { ++(myCrawlStat.sizeLess10KB); }
        else if (sizeInKB < 100) { ++(myCrawlStat.sizeLess100KB); }
        else if (sizeInKB < 1024) { ++(myCrawlStat.sizeLess1MB); }
        else if (sizeInKB >= 1024) { ++(myCrawlStat.sizeGreaterOrEqual1MB); }

        return sizeInKB;
    }

    private String processContentType(String curContentType) {
        if ('t' == curContentType.charAt(0)) {
            ++(myCrawlStat.textHtml);
            return "text/html";
        } else if ('a' == curContentType.charAt(0)) {
            ++(myCrawlStat.applicationPdf);
            return "application/pdf";
        } else if ('g' == curContentType.charAt(6)) {
            ++(myCrawlStat.imageGif);
            return "image/gif";
        } else if ('j' == curContentType.charAt(6)) {
            ++(myCrawlStat.imageJpeg);
            return "image/jpeg";
        } else if ('p' == curContentType.charAt(6)) {
            ++(myCrawlStat.imagePng);
            return "image/png";
        }

        return "other";
    }
}
