package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.princeton.cs.introcs.Stopwatch;

/**
 * @author Pawel Paszki
 * 
 *         JUnit test case, which tests all public methods from 
 *         ComponentImage class. There are no public methods in the GUI
 *         class and therefore there is nothing to test + all errors are
 *         eliminated by enabling / disabling buttons depending on the action
 *         taken by the user. Images used in the test are listed in setUp()
 *         method in this class. They reside in "images" folder
 */

public class ComponentImageTest {

	private ComponentImage whiteBackground;
	private ComponentImage boxesOnTheEdges;
	private ComponentImage multipleSmallStars;
	private ComponentImage largeSquare;
	private ComponentImage checkers8by8;
	private ComponentImage sky;
	private ComponentImage smallAndLarge;
	private int dimensions;
	private Set<Color> colors;
	private Color black, white, green, red;

	@Before
	public void setUp() throws Exception {
		whiteBackground = new ComponentImage("images/white.jpg");
		boxesOnTheEdges = new ComponentImage("images/boundaries.jpg");
		multipleSmallStars = new ComponentImage("images/smallStars.jpg");
		largeSquare = new ComponentImage("images/largeSquare.jpg");
		checkers8by8 = new ComponentImage("images/checkers8by8.jpg");
		sky = new ComponentImage("images/sky.jpg");
		smallAndLarge = new ComponentImage("images/smallAndLarge.jpg");
		colors = new HashSet<Color>();
		black = new Color(0, 0, 0);
		white = new Color(255, 255, 255);
		green = new Color(0, 176, 80);
		red = new Color(255, 0, 0);
	}

	@After
	public void tearDown() throws Exception {
		whiteBackground = null;
		boxesOnTheEdges = null;
		multipleSmallStars = null;
		largeSquare = null;
		checkers8by8 = null;
		sky = null;
		smallAndLarge = null;
		colors = null;
		black = null;
		white = null;
		red = null;
		green = null;
	}

	// boundary conditions: EXISTENCE
	@Test
	public void testObjectsExistence() {
		assertNotNull(whiteBackground);
		assertNotNull(boxesOnTheEdges);
		assertNotNull(multipleSmallStars);
		assertNotNull(largeSquare);
		assertNotNull(checkers8by8);
		assertNotNull(sky);
		assertNotNull(smallAndLarge);
		assertNotNull(colors);
		assertNotNull(black);
	}

	// RIGHT
	// INVERSE RELATIONSHIPS
	@Test
	public void testPictureDimensions() {
		dimensions = checkers8by8.getPicture().width() * checkers8by8.getPicture().height();
		assertEquals(dimensions, 64); // size of the 8by8 is 64 pixels
		assertNotEquals(dimensions, 72); // 8 by 9 = 72
		assertNotEquals(dimensions, 56); // 8 by 7 = 56
		assertNotEquals(dimensions, 65);
		assertNotEquals(dimensions, 63);
		dimensions = boxesOnTheEdges.getPicture().width() * boxesOnTheEdges.getPicture().height();
		assertEquals(dimensions, 64220);
		assertNotEquals(dimensions, 64221);
		assertNotEquals(dimensions, 64219);
		assertNotEquals(dimensions, 0);
		assertNotEquals(dimensions, 1);
		// INVERSE RELATIONSHIPS:
		assertEquals(boxesOnTheEdges.getPicture().width(), 64220 / boxesOnTheEdges.getPicture().height());
		assertEquals(boxesOnTheEdges.getPicture().height(), 64220 / boxesOnTheEdges.getPicture().width());
	}

	// RIGHT conditions + boundary conditions (RANGE): number of objects
	// always smaller than the number of pixels
	@Test
	public void testNumberOfObjects() {
		checkers8by8.setThresholdPixelValue(128);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 32);
		assertNotEquals(checkers8by8.countComponents(), 31);
		assertNotEquals(checkers8by8.countComponents(), 1);
		
		checkers8by8.setThresholdPixelValue(255);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 0);
		assertNotEquals(checkers8by8.countComponents(), 32);
		
		checkers8by8 = new ComponentImage("images/checkers8by8.jpg");
		checkers8by8.setThresholdPixelValue(0);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 1);
		assertNotEquals(checkers8by8.countComponents(), 0);
		assertNotEquals(checkers8by8.countComponents(), 32);
		
		sky.setThresholdPixelValue(128);
		sky.binaryComponentImage();
		// RANGE
		assertTrue(sky.countComponents() < sky.getPicture().width() * sky.getPicture().height());
	}

	// RIGHT - ie expected green color of large object's pixels and red
	// of small object's pixels. In this particular tested image - the smallest
	// object is in top left corner, the largest in bottom right corner
	@Test
	public void testGetSmallestAndLargest() {
		smallAndLarge.setThresholdPixelValue(128);
		smallAndLarge.binaryComponentImage();
		smallAndLarge.getSmallestAndLargest();
		assertEquals(smallAndLarge.getPicture().get(1, 1), red);
		assertEquals(smallAndLarge.getPicture().get(240, 215), green);
	}

	// PERFORMANCE characteristics. Picture with number of distinct objects is
	// binarised
	// time limit is 2 seconds for this picture
	// NOTE: I have noticed that depending on battery power plan execution may vary.
	// in my case on power saver the time was doubled comparing to the normal mode
	@Test
	public void testPerformance() {
		multipleSmallStars.setThresholdPixelValue(100);
		Stopwatch stopwatch = new Stopwatch();
		multipleSmallStars.binaryComponentImage();
		double time = stopwatch.elapsedTime();
		assertTrue(time < 2.0);
	}

	// RIGHT
	// Note: testing binarising image with different threshold values is
	// being tested in testNumberOfObjects
	@Test
	public void testThresholdValueSettersAndGetters() {
		// initially thresholdValue: 0
		assertEquals(largeSquare.getThresholdPixelValue(), 0, 0.1);

		largeSquare.setThresholdPixelValue(200);
		assertEquals(largeSquare.getThresholdPixelValue(), 200, 0.1);
		assertNotEquals(largeSquare.getThresholdPixelValue(), 0, 0.1);

		largeSquare.setThresholdPixelValue(300); // value too high
		assertEquals(largeSquare.getThresholdPixelValue(), 200, 0.1);
		assertNotEquals(largeSquare.getThresholdPixelValue(), 300, 0.1);

		largeSquare.setThresholdPixelValue(-5); // value too low
		assertEquals(largeSquare.getThresholdPixelValue(), 200, 0.1);
		assertNotEquals(largeSquare.getThresholdPixelValue(), -5, 0.1);
		
		// all pixels should be white:
		checkers8by8.setThresholdPixelValue(0);
		checkers8by8.binaryComponentImage();
		for (int x = 0; x < checkers8by8.getPicture().width(); x++) {
			for (int y = 0; y < checkers8by8.getPicture().height(); y++) {
				assertEquals(checkers8by8.getPicture().get(x, y), white);

			}
		}
	}

	// RIGHT - for simplicity white background image is used:
	// boundary pixels should be set to red
	@Test
	public void testHighlightComponentImage() {
		whiteBackground.binaryComponentImage();
		whiteBackground.highlightComponentImage();
		assertEquals(whiteBackground.getPicture().get(0, 0), Color.RED);
		assertEquals(whiteBackground.getPicture().get(0, 50), Color.RED);
		assertEquals(whiteBackground.getPicture().get(50, 0), Color.RED);
		assertEquals(whiteBackground.getPicture().get(whiteBackground.getPicture().width() - 1,
				whiteBackground.getPicture().height() - 1), Color.RED);
	}

	/*
	 * CROSS CHECKING: the image contains five distinct objects, when binarised.
	 * countComponents() output will be compared with the outcome of the
	 * iteration through each pixel and search for different colours RIGHT - if
	 * values are as expected
	 */
	@Test
	public void testColourComponentImage() {
		boxesOnTheEdges.setThresholdPixelValue(128);
		boxesOnTheEdges.binaryComponentImage();
		boxesOnTheEdges.colourComponentImage();

		for (int x = 0; x < boxesOnTheEdges.getPicture().width(); x++) {
			for (int y = 0; y < boxesOnTheEdges.getPicture().height(); y++) {
				colors.add(boxesOnTheEdges.getPicture().get(x, y));

			}
		}
		colors.remove(black); // remove background
		assertEquals(boxesOnTheEdges.countComponents(), colors.size());

		/*
		 * checking if leftmost and rightmost pixels in top and bottom rows are
		 * connected (they shouldn't be, but there is a risk of such an error to
		 * occur while using 1-d array)
		 */
		// left most with rightmost at the top
		assertNotEquals(boxesOnTheEdges.getPicture().get(0, 0),
				boxesOnTheEdges.getPicture().get(boxesOnTheEdges.getPicture().width() - 1, 0));
		// left most with rightmost at the bottom
		assertNotEquals(boxesOnTheEdges.getPicture().get(0, boxesOnTheEdges.getPicture().height() - 1), boxesOnTheEdges
				.getPicture().get(boxesOnTheEdges.getPicture().width() - 1, boxesOnTheEdges.getPicture().height() - 1));
		// top left corner with bottom left corner
		assertNotEquals(boxesOnTheEdges.getPicture().get(0, 0),
				boxesOnTheEdges.getPicture().get(0, boxesOnTheEdges.getPicture().height() - 1));
		// top right corner with bottom right corner
		assertNotEquals(boxesOnTheEdges.getPicture().get(boxesOnTheEdges.getPicture().width() - 1, 0), boxesOnTheEdges
				.getPicture().get(boxesOnTheEdges.getPicture().width() - 1, boxesOnTheEdges.getPicture().height() - 1));
	}
	
	// RIGHT - testing if the same results are obtained repeateadly
	@Test
	public void testObtainingSameCount() {
		checkers8by8.setThresholdPixelValue(128);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 32);
		
		checkers8by8 = new ComponentImage("images/checkers8by8.jpg");
		checkers8by8.setThresholdPixelValue(128);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 32);
		
		checkers8by8 = new ComponentImage("images/checkers8by8.jpg");
		checkers8by8.setThresholdPixelValue(128);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 32);
		
		checkers8by8 = new ComponentImage("images/checkers8by8.jpg");
		checkers8by8.setThresholdPixelValue(0);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 1);
		
		checkers8by8 = new ComponentImage("images/checkers8by8.jpg");
		checkers8by8.setThresholdPixelValue(0);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 1);
		
		checkers8by8 = new ComponentImage("images/checkers8by8.jpg");
		checkers8by8.setThresholdPixelValue(0);
		checkers8by8.binaryComponentImage();
		assertEquals(checkers8by8.countComponents(), 1);
	}
}
