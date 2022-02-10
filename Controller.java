import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	
	public static String fetch_filename = "fetch_usatoday.csv";
	public static String visit_filename = "visit_usatoday.csv";
	public static String crawl_filename = "CrawlReport_usatoday.csv";
	public static File fetch_file = new File(fetch_filename);
	public static File visit_file = new File(visit_filename);
	public static File crawl_file = new File(crawl_filename);
	
	public static FileWriter fetch_fw;
	public static FileWriter visit_fw;
	public static FileWriter crawl_fw;

	public static void main(String[] args) throws Exception {
		fetch_fw = new FileWriter(fetch_file);
		visit_fw = new FileWriter(visit_file);
		crawl_fw = new FileWriter(crawl_file);
		
		writeHeaders();
		
		String crawlStorageFolder = "/Users/suki/Desktop/CSCI572/data/crawl";
		int politenessDelay = 800;
		int maxDepth = 16;
		int maxPagesToFetch = 20_000;
		int numberOfCrawlers = 7;
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setPolitenessDelay(politenessDelay);
		config.setMaxDepthOfCrawling(maxDepth);
		config.setIncludeHttpsPages(true);
		config.setMaxPagesToFetch(maxPagesToFetch);
		config.setIncludeBinaryContentInCrawling(true);
		
		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		/* 
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		controller.addSeed("https://www.usatoday.com");
		
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(MyCrawler.class, numberOfCrawlers);
		
		writeReport();
		
		fetch_fw.flush();
		visit_fw.flush();
		crawl_fw.flush();
		
		fetch_fw.close();
		visit_fw.close();
		crawl_fw.close();
	}
	
	private static void writeHeaders() {
		try {
			// Write headers for fetch
			fetch_fw.write("URL, HTTP/HTTPS\n");
			// Write headers for visit
			visit_fw.write("URL, Size (KB), # of Outlinks, Content Type\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeReport() {
        // Write to CrawlReport_usatoday.txt file
    	try {
			crawl_fw.write("Name: Suki Sahota\n");
			crawl_fw.write("USC ID: 5807825570\n");
			crawl_fw.write("News site crawled: usatoday.com\n");
			crawl_fw.write("Number of threads: 7\n");

			crawl_fw.write("\nFetch Statistics\n");
			crawl_fw.write("================\n");
			crawl_fw.write("# fetches attempted: " + MyCrawler.myCrawlStat.numAttemptFetch + "\n");
			crawl_fw.write("# fetches succeeded: " + MyCrawler.myCrawlStat.numFetchSuccess + "\n");
			crawl_fw.write("# fetches failed or aborted: " + MyCrawler.myCrawlStat.numFetchFailOrAbort + "\n");

			crawl_fw.write("\nOutgoing URLs:\n");
			crawl_fw.write("==============\n");
			crawl_fw.write("Total URLs extracted: " + MyCrawler.myCrawlStat.totalUrlExtract + "\n");
			crawl_fw.write("# unique URLs extracted: " + MyCrawler.myCrawlStat.uniqueUrlExtract + "\n");
			crawl_fw.write("# unique URLs within News Site: " + MyCrawler.myCrawlStat.uniqueUrlInsideWebsite + "\n");
			crawl_fw.write("# unique URLs outside News Site: " + MyCrawler.myCrawlStat.uniqueUrlOutsideWebsite + "\n");

			crawl_fw.write("\nStatus Codes:\n");
			crawl_fw.write("=============\n");
			crawl_fw.write("200 OK: " + MyCrawler.myCrawlStat.status200 + "\n");
			crawl_fw.write("301 Moved Permanently: " + MyCrawler.myCrawlStat.status301 + "\n");
			crawl_fw.write("401 Unauthorized: " + MyCrawler.myCrawlStat.status401 + "\n");
			crawl_fw.write("403 Forbidden: " + MyCrawler.myCrawlStat.status403 + "\n");
			crawl_fw.write("404 Not Found: " + MyCrawler.myCrawlStat.status404 + "\n");

			crawl_fw.write("\nFile Sizes:\n");
			crawl_fw.write("===========\n");
			crawl_fw.write("< 1KB: " + MyCrawler.myCrawlStat.sizeLess1KB + "\n");
			crawl_fw.write("1KB ~ <10KB: " + MyCrawler.myCrawlStat.sizeLess10KB + "\n");
			crawl_fw.write("10KB ~ <100KB: " + MyCrawler.myCrawlStat.sizeLess100KB + "\n");
			crawl_fw.write("100KB ~ <1MB: " + MyCrawler.myCrawlStat.sizeLess1MB + "\n");
			crawl_fw.write(">= 1MB: " + MyCrawler.myCrawlStat.sizeGreaterOrEqual1MB + "\n");

			crawl_fw.write("\nContent Types:\n");
			crawl_fw.write("==============\n");
			crawl_fw.write("text/html: " + MyCrawler.myCrawlStat.textHtml + "\n");
			crawl_fw.write("image/gif: " + MyCrawler.myCrawlStat.imageGif + "\n");
			crawl_fw.write("image/jpeg: " + MyCrawler.myCrawlStat.imageJpeg + "\n");
			crawl_fw.write("image/png: " + MyCrawler.myCrawlStat.imagePng + "\n");
			crawl_fw.write("application/pdf: " + MyCrawler.myCrawlStat.applicationPdf + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
