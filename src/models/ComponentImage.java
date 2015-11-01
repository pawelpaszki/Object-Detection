package models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.princeton.cs.introcs.Picture;

/**
 * @author Pawel Paszki
 * 
 *         This class contains union-find structure to analyse arrays of N
 *         sites. It uses stdlib-package to deal with pictures (Picture class
 *         from the library) It also uses Luminance class, to analyse the
 *         brightness of the picture and based on that and on the settins
 *         of the thresholdValue  assigns white or black
 *         color value to the pixel, when the image is binarised
 */
public class ComponentImage {
	private Picture picture;
	private String fileLocation;
	private double thresholdPixelValue; // defines brightness boundary
	private int[] id; // parent[i] = parent of i
	private int dimension; // number of sites
	private int counter; // label counter
	private int width; // width of the image
	private int height; // height of the image
	private int[] size;
	private int count; // number of distinct objects in an image
	private ArrayList<Integer> labels;
	private ArrayList<Color> colors;
	private int[][] objectsProperties;

	/**
	 * Initialise fields
	 * 
	 * @param fileLocation
	 */
	public ComponentImage(String fileLocation) {

		this.fileLocation = fileLocation;
		picture = new Picture(fileLocation);
		width = picture.width();
		height = picture.height();
		dimension = width * height;
		counter = 0;
		id = new int[dimension];
		size = new int[dimension];
		count = dimension;

	}

	/**
	 * 
	 * @return number of components in the image
	 */
	public int countComponents() {
		return count;
	}

	/**
	 * this method iterates over every single pixel in the image and change the
	 * pixel's value to white, if brighter than thresholdPixelValue or black,
	 * when darker. Count is decremented every time, the pixel turns black, as
	 * black pixels are considered background, not objects. Each white pixel's
	 * site in id array gets the value of its index
	 * 
	 * @return binarised picture
	 * 
	 */
	public Picture binaryComponentImage() {
		counter = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color c = picture.get(x, y);
				if (Luminance.lum(c) < getThresholdPixelValue()) {
					picture.set(x, y, Color.BLACK);
					id[counter] = -1;
					count--;
				} else {
					picture.set(x, y, Color.WHITE);
					id[counter] = counter;
					size[counter] = 1;
				}
				counter++;
			}
		}
		checkForObjects();
		analyseObjectsLabels();
		calculateCoordinates();
		return picture;
	}

	/**
	 * this method takes binarised picture and fills each of the objects found
	 * in the picture with random colour
	 * 
	 * @return picture with random-coloured objects
	 * 
	 */
	public Picture colourComponentImage() {
		colors = new ArrayList<Color>();
		for (int i = 0; i < labels.size(); i++) {
			float red = (float) Math.random();
			float green = (float) Math.random();
			float blue = (float) Math.random();

			colors.add(new Color(red, green, blue));
		}
		counter = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				for (int i = 0; i < labels.size(); i++) {
					if (id[counter] == labels.get(i)) {
						picture.set(x, y, colors.get(i));
					}
				}
				counter++;
			}
		}
		return picture;
	}

	/**
	 * this method takes picture and draws lines around the objects found. The
	 * details about the objects to be processed when drawing the lines are
	 * stores in two-dimensional array coordinates
	 * 
	 * @return picture with boxes around objects
	 * 
	 */
	public Picture highlightComponentImage() {
		for (int i = 0; i < objectsProperties.length; i++) {
			for (int x = objectsProperties[i][0]; x < objectsProperties[i][1]; x++) {
				picture.set(objectsProperties[i][2], x, Color.RED);
				picture.set(objectsProperties[i][3], x, Color.RED);
			}
			for (int y = objectsProperties[i][2]; y < objectsProperties[i][3]; y++) {
				picture.set(y, objectsProperties[i][0], Color.RED);
				picture.set(y, objectsProperties[i][1], Color.RED);
			}
			// bottom right corner
			picture.set(objectsProperties[i][3], objectsProperties[i][1], Color.RED); 
		}
		return picture;
	}

	/**
	 * Returns the component identifier for the component containing site
	 *
	 * @param p
	 *            the integer representing one object
	 * @return the component identifier for the component containing site
	 * 
	 * @throws IndexOutOfBoundsException
	 *             unless 0 < p < dimension
	 */
	private int find(int p) {
		while (p != id[p])
			p = id[p];
		return p;
	}

	/**
	 * Returns true if the the two sites are in the same component.
	 *
	 * @param p
	 *            the integer representing one site
	 * @param q
	 *            the integer representing the other site
	 * @return <tt>true</tt> if the two sites <tt>p</tt> and <tt>q</tt> are in
	 *         the same component; <tt>false</tt> otherwise
	 * @throws IndexOutOfBoundsException
	 *             unless both <tt>0 &le; p &lt; N</tt> and
	 *             <tt>0 &le; q &lt; N</tt>
	 */
	private boolean connected(int p, int q) {
		return find(p) == find(q);
	}

	/**
	 * Merges the component containing site <tt>p</tt> with the the component
	 * containing site <tt>q</tt>.
	 *
	 * @param p
	 *            the integer representing one site
	 * @param q
	 *            the integer representing the other site
	 * @throws IndexOutOfBoundsException
	 *             unless both <tt>0 &le; p &lt; N</tt> and
	 *             <tt>0 &le; q &lt; N</tt>
	 */
	private void union(int p, int q) {
		int rootP = find(p);
		int rootQ = find(q);
		if (rootP == rootQ)
			return;

		if (size[rootP] < size[rootQ]) {
			id[rootP] = rootQ;
			size[rootQ] += size[rootP];
		} else {
			id[rootQ] = rootP;
			size[rootP] += size[rootQ];
		}
		count--;
	}

	/**
	 * two-pass image scan. Scans row by row from top to bottom and connects
	 * adjacent sites. in second pass roots those sites, ie all of the sites,
	 * which are connected to other site get the value of the parent site
	 */
	private void checkForObjects() {
		// first pass
		counter = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > 0) {
					if (id[counter] != -1) {
						if (id[counter - 1] != -1) {
							if (!connected(counter - 1, counter))
								union(counter - 1, counter);
						}
					}
				}
				// pixel at the top of the current pixel is only checked from row two on
				if (y > 0 && x > 0) {
					if (id[counter - width] != -1 && id[counter - 1] != -1 && id[counter] != -1) {
						if (!connected(counter - width, counter))
							union(counter - width, counter);
						if (!connected(counter - width, counter - 1))
							union(counter - width, counter - 1);
					}
					if (id[counter - 1] == -1 && id[counter - width] != -1 && id[counter] != -1) {
						if (!connected(counter - width, counter))
							union(counter - width, counter);
					}
				}
				counter++;
			}
		}
		// second pass
		counter = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (id[counter] != -1) {
					id[counter] = find(id[counter]);
				}
				counter++;
			}
		}
	}

	/**
	 * This method traverses through the array of pixels and searches for pixels
	 * with value != -1 and adds their labels to the hashSet, which will store
	 * only unique values. then those labels are added to the ArrayList of
	 * Integers to be used, when colouring the distinct objects Returns the
	 * number of components identified in the image.
	 * 
	 * @return the number of components (between 1 and N)
	 */
	private void analyseObjectsLabels() {
		Set<Integer> labelsValues = new HashSet<Integer>();
		for (int i = 0; i < id.length; i++) {
			if (id[i] != -1) {
				labelsValues.add(id[i]);
			}
		}
		labels = new ArrayList<Integer>();
		Iterator<Integer> iter = labelsValues.iterator();
		while (iter.hasNext()) {
			labels.add((Integer) iter.next());
		}
	}

	/**
	 * 
	 * @return String representation of the path of the image
	 */
	public String getFileLocation() {
		return fileLocation;
	}

	/**
	 * iterates through the image and estimates the lowest and highest x and y
	 * values for each of the objects in the image along with pixel count for
	 * each image and the value of the label of the image of and stores those
	 * values in 2-d array.
	 */
	private void calculateCoordinates() {
		// only to be run if number of components is greater than 0
		if (countComponents() > 0) {
			// first value - number of labels, ie number of distinct objects
			// second value - number of different values to be stored:
			// min y, max y, min x, max x, pixels count and label
			objectsProperties = new int[countComponents()][6];
			for (int i = 0; i < countComponents(); i++) {
				counter = 0;
				int pixelsCounter = 0;
				int tempX = 0;
				int tempY = 0;
				int maxX = 0;
				int maxY = 0;
				int minX = width;
				int minY = height;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (id[counter] == labels.get(i)) {
							pixelsCounter++;
							tempX = x;
							tempY = y;
							if (tempX > maxX) {
								maxX = tempX;
							}
							if (tempX < minX) {
								minX = tempX;
							}
							if (tempY > maxY) {
								maxY = tempY;
							}
							if (tempY < minY) {
								minY = tempY;
							}
						}
						counter++;
					}
				}
				// min y, max y, min x, max x, pixels count and label of the
				// pixels belonging to the object
				int[] coords = new int[] { minY, maxY, minX, maxX, pixelsCounter, labels.get(i) };
				objectsProperties[i] = coords;
			}
		}

	}

	/**
	 * this method iterates through objectsProperties array and looks for
	 * smallest and largest object, based on pixel count and changes all of the
	 * pixels of the largest one to green and smallest one to red
	 * 
	 * @return changed picture
	 */
	public Picture getSmallestAndLargest() {
		int smallestIndex = 0;
		int largestIndex = 0;
		int largestPixelCount = 0;
		int smallestPixelCount = dimension;
		for (int i = 0; i < countComponents(); i++) {
			if (getObjectsProperties()[i][4] > largestPixelCount) {
				largestIndex = i;
				largestPixelCount = getObjectsProperties()[i][4];
			}
			if (getObjectsProperties()[i][4] < smallestPixelCount) {
				smallestIndex = i;
				smallestPixelCount = getObjectsProperties()[i][4];
			}
		}
		counter = 0;
		Color green = new Color(0, 176, 80);
		Color red = new Color(255, 0, 0);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (id[counter] == getObjectsProperties()[smallestIndex][5]) {
					picture.set(x, y, red);
				}
				if (id[counter] == getObjectsProperties()[largestIndex][5]) {
					picture.set(x, y, green);
				}
				counter++;
			}
		}
		return picture;
	}

	/**
	 * setter for picture
	 * 
	 * @param picture
	 *            is passed to be assigned as picture field
	 */
	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	/**
	 * 
	 * @return picture
	 */
	public Picture getPicture() {
		return picture;
	}

	/**
	 * getter for thresholdPixelValue
	 * 
	 * @return current state of thresholdPixelValue
	 */
	public double getThresholdPixelValue() {
		return thresholdPixelValue;
	}

	/**
	 * setter for thresholdPixelValue
	 * 
	 * @param thresholdPixelValue
	 *            is passed and field thresholdPixelValue is assigned with the
	 *            parameter value
	 */
	public void setThresholdPixelValue(double thresholdPixelValue) {
		if (thresholdPixelValue >= 0 && thresholdPixelValue <= 255) {
			this.thresholdPixelValue = thresholdPixelValue;
		}
	}

	/**
	 * 
	 * @return objectProperties array to be used in highlighting objects and
	 *         painting the smallest and the largest object
	 */
	private int[][] getObjectsProperties() {
		return objectsProperties;
	}

}
