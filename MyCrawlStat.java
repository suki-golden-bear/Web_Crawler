import java.util.HashSet;
import java.util.Set;

public class MyCrawlStat {
    // Fetch Statistics
    public int numAttemptFetch; // representation invariant: == success + fail/abort
    public int numFetchSuccess; // visited && status code 2xx
    public int numFetchFailOrAbort; // status code 3xx, 4xx, or 5xx, or something else
    
    // Outgoing URLs
    public int totalUrlExtract; // total extracted (including repeats)
    public int uniqueUrlExtract;  // representation invariant: == in + out
    public int uniqueUrlInsideWebsite;
    public int uniqueUrlOutsideWebsite;
    public Set<String> setUniqueUrlInsideWebsite;
    public Set<String> setUniqueUrlOutsideWebsite;
    
    // Status Codes
    public int status200; // ok
    public int status301; // moved permanently
    public int status401; // unauthorized
    public int status403; // forbidden
    public int status404; // not found
    
    public int sizeLess1KB;
    public int sizeLess10KB;
    public int sizeLess100KB;
    public int sizeLess1MB;
    public int sizeGreaterOrEqual1MB;
    
    // Content Types
    public int textHtml;
    public int imageGif;
    public int imageJpeg;
    public int imagePng;
    public int applicationPdf;
    
    public MyCrawlStat() {
    	numAttemptFetch = 0;
        numFetchSuccess = 0;
        numFetchFailOrAbort = 0;
        
        totalUrlExtract = 0;
        uniqueUrlExtract = 0;  
        uniqueUrlInsideWebsite = 0;
        uniqueUrlOutsideWebsite = 0;
        
        setUniqueUrlInsideWebsite = new HashSet<String>();
        setUniqueUrlOutsideWebsite = new HashSet<String>();
        
        status200 = 0; 
        status301 = 0; 
        status401 = 0; 
        status403 = 0; 
        status404 = 0; 
        
        sizeLess1KB = 0;
        sizeLess10KB = 0;
        sizeLess100KB = 0;
        sizeLess1MB = 0;
        sizeGreaterOrEqual1MB = 0;
        
        textHtml = 0;
        imageGif = 0;
        imageJpeg = 0;
        imagePng = 0;
        applicationPdf = 0;
    }
}
