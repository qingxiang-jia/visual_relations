/**
 * In Step 2, we extract five spatial relationships for each building. They are
 * decided by scanning the map. This class uses the scanned area to assign spatial
 * relationships for each pixel. This is a substitute for SRExtractorPixel, which
 * has a downside of hard to perform transitive reduction. With this, the reduction
 * will be performed based on how close the pixel is to the building, making it
 * possible to perform reduction on the fly.
 */
public class InfluenceMapping
{

}
