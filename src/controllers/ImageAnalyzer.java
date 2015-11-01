package controllers;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JSlider;
import javax.swing.filechooser.FileNameExtensionFilter;

import models.ComponentImage;

/**
 * @author Pawel Paszki 
 * 
 * 	       This class initialises the window, used to display an
 *         image and process it according to the buttons pressed. The size of the
 *         container displaying the image is set to 1000 (w) x 520 (w). Larger
 *         images can be displayed and will be processed without an error - ie
 *         objects count will work, but it is recommended to load pictures in
 *         the resolution lower than the container's size. JFileChooser is used To
 *         load an image, therefore there is no need to catch any
 *         FileNotFoundExceptions, as the JFileChooser will only load valid
 *         images. Until the image is loaded for the first time all buttons
 *         except load image are disabled. 
 *         
 *         Running the app:
 *         
 *         - Initially only "Load image" button is enabled and once clicked 
 *         window pops up. you can choose any valid image file from the disk
 *         provided that its extension is one of the following: "jpg", "gif", 
 *         "png", "bmp". 
 *         
 *         - when the image is loaded, "Binarise image" button is enabled 
 *         along with slider, which allows to set the threshold value between 
 *         0 and 255. image dimensions are also displayed
 *         
 *         - when "Binarise image" button is pressed - image is being processed:
 *        threshold pixel value is set to the value read from the slider at the 
 *        time, when the BI button is pressed. background is considered black
 *        images are considered white. objects' count is displayed and if the
 *        number of distinct objects in the image is greater than 1 - "ShowSmallest-
 *        AndLargest" button is enabled. "Paint objects", "Highlight objects" and 
 *        "Reset image" buttons are enabled regardless of the number of distinct objects
 *        
 *        - pressing "Paint objects" leaves enabled "Load image", "Reset image" and 
 *        "Load image" buttons. Distinct objects are being painted with random colours
 *        
 *        - when "Highlight objects" button is pressed - all distinct objects are 
 *        surrounded by red boxes. 
 *        
 *        - "Reset image" button resets the image with its original path 
 *
 */
public class ImageAnalyzer {

	private JFrame mainWindow; // top-level container, ie window
	private ComponentImage componentImage; // instance of ComponentImage

	private JLabel image; // image to be displayed and processed
	private JLabel objectsCount; // objects count
	private JLabel smallestLargest;
	private JLayeredPane imagePanel; // container displaying the image
	private JButton loadImage;
	private JButton reset;
	private JButton binarise;
	private JButton addColours;
	private JButton highlightObjects;
	private JButton showSmallestAndLargest;
	private String imagePath; // image path (gathered from FileChooser)
	private JLabel dimensions; // label showing object's dimensions
	private JSlider thresholdAjustment;
	private JLabel sliderLabel;
	
	public static void main(String[] args) {
		
		// standard thread invocation in swing apps
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageAnalyzer analyzer = new ImageAnalyzer();
					analyzer.initialise();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * initialises the window.
	 */
	private void initialise() {
		mainWindow = new JFrame("Image Analyzer. Developed by Pawel Paszki (pawelpaszki@gmail.com");

		mainWindow.setSize(1290, 720);
		mainWindow.setLocationRelativeTo(null);// centers the window
		mainWindow.setVisible(true); // makes the window visible
		// closes the window, when stop button is pressed
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		// main frame background
		mainWindow.setContentPane(new JLabel(new ImageIcon("images/background.jpg")));

		loadImage = new JButton("Load image");
		loadImage.setBounds(10, 20, 200, 30);

		binarise = new JButton("Binarise image");
		binarise.setBounds(220, 20, 200, 30);
		binarise.setEnabled(false);

		addColours = new JButton("Paint objects");
		addColours.setBounds(430, 20, 200, 30);
		addColours.setEnabled(false);

		highlightObjects = new JButton("Highlight objects");
		highlightObjects.setBounds(640, 20, 200, 30);
		highlightObjects.setEnabled(false);

		reset = new JButton("Reset image");
		reset.setBounds(1060, 20, 200, 30);
		reset.setEnabled(false);

		showSmallestAndLargest = new JButton("Show smallest and largest");
		showSmallestAndLargest.setBounds(850, 20, 200, 30);
		showSmallestAndLargest.setEnabled(false);

		dimensions = new JLabel();
		dimensions.setBounds(640, 70, 200, 40);
		dimensions.setVisible(false);
		dimensions.setFont(new Font("Arial", Font.ITALIC, 16));
		dimensions.setForeground(new Color(0, 255, 35)); // font color

		objectsCount = new JLabel();
		objectsCount.setBounds(430, 70, 200, 40);
		objectsCount.setVisible(false);
		objectsCount.setFont(new Font("Arial", Font.ITALIC, 16));
		objectsCount.setForeground(new Color(0, 255, 35)); // font color

		smallestLargest = new JLabel(new ImageIcon("images/smallestLargest.jpg"));
		smallestLargest.setBounds(850, 65, 410, 50);
		smallestLargest.setVisible(false);

		final int FPS_MIN = 0;
		final int FPS_MAX = 255;
		final int FPS_INIT = 128;

		thresholdAjustment = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, FPS_INIT);
		thresholdAjustment.setBounds(10, 85, 410, 40);
		thresholdAjustment.setMajorTickSpacing(50);
		thresholdAjustment.setMinorTickSpacing(5);
		thresholdAjustment.setPaintTicks(true);
		thresholdAjustment.setPaintLabels(true);
		sliderLabel = new JLabel("Set Threshold: ", JLabel.CENTER);
		sliderLabel.setBounds(10, 60, 410, 20);
		sliderLabel.setFont(new Font("Arial", Font.ITALIC, 16));
		sliderLabel.setForeground(new Color(0, 255, 35));
		sliderLabel.setVisible(false);
		thresholdAjustment.setAlignmentX(Component.CENTER_ALIGNMENT);
		thresholdAjustment.setVisible(false);

		imagePanel = new JLayeredPane();
		imagePanel = new JLayeredPane();
		imagePanel.setBounds(145, 150, 1000, 520);
		imagePanel.setOpaque(true);// sets the background to be visible
		
		// no layout manager - all components have set their position related
		// to top left corner
		mainWindow.getContentPane().setLayout(null);
		
		// adding components to the main frame
		mainWindow.add(loadImage);
		mainWindow.add(binarise);
		mainWindow.add(addColours);
		mainWindow.add(highlightObjects);
		mainWindow.add(reset);
		mainWindow.add(showSmallestAndLargest);
		mainWindow.add(dimensions, 1);
		mainWindow.add(objectsCount, 1);
		mainWindow.add(imagePanel);
		mainWindow.add(sliderLabel);
		mainWindow.add(thresholdAjustment);
		mainWindow.add(smallestLargest);

		// action to be taken, when loadImage button is pressed
		loadImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				imagePanel.removeAll();
				// slightly modified code taken from:
				// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("images", "jpg", "gif", "png", "bmp");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(mainWindow);
				// ^^^^^^^^^^^^^
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setImagePath(chooser.getSelectedFile().getAbsolutePath());
					if (returnVal == 0) {
						componentImage = new ComponentImage(getImagePath());
						image = componentImage.getPicture().getJLabel();
						image.setSize(1000, 540);
						imagePanel.add(image);
						imagePanel.moveToFront(image);
						binarise.setEnabled(true);
						dimensions.setVisible(true);
						addColours.setEnabled(false);
						highlightObjects.setEnabled(false);
						reset.setEnabled(false);
						showSmallestAndLargest.setEnabled(false);
						smallestLargest.setVisible(false);
						objectsCount.setVisible(false);
						thresholdAjustment.setVisible(true);
						sliderLabel.setVisible(true);
						dimensions.setText("<html>image dimensions: <br>" + componentImage.getPicture().width()
								+ " (w) x " + componentImage.getPicture().height() + " (h)</html>");
					}
				}

			}

		});

		// action to be taken, when binarise button is pressed
		binarise.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				componentImage = new ComponentImage(getImagePath());
				// there is no need to update threshold pixel value, unless the
				// picture is going to be binarised:
				componentImage.setThresholdPixelValue(thresholdAjustment.getValue());
				image = componentImage.binaryComponentImage().getJLabel();
				image.setSize(1000, 540);
				imagePanel.add(image);
				imagePanel.moveToFront(image);
				addColours.setEnabled(true);
				binarise.setEnabled(false);
				reset.setEnabled(true);
				highlightObjects.setEnabled(true);
				// showing smallest and largest objects only possible, if 
				// number of objects on an image is greater than 1
				if (componentImage.countComponents() > 1) {
					showSmallestAndLargest.setEnabled(true);
					smallestLargest.setVisible(true);
				}
				objectsCount.setVisible(true);
				objectsCount.setText("<html>number of objects: <br>" + componentImage.countComponents() + " </html>");
			}

		});

		// action to be taken, when addColours button is pressed
		addColours.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				image = componentImage.colourComponentImage().getJLabel();
				image.setSize(1000, 540);
				imagePanel.add(image);
				imagePanel.moveToFront(image);
				addColours.setEnabled(false);
				showSmallestAndLargest.setEnabled(false);
				reset.setEnabled(true);
			}

		});

		// action to be taken, when highlightObjects button is pressed
		highlightObjects.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				image = componentImage.highlightComponentImage().getJLabel();
				image.setSize(1000, 540);
				imagePanel.add(image);
				imagePanel.moveToFront(image);
				binarise.setEnabled(false);
				addColours.setEnabled(false);
				highlightObjects.setEnabled(false);
				showSmallestAndLargest.setEnabled(false);
				reset.setEnabled(true);
				showSmallestAndLargest.setEnabled(false);

			}
		});

		// action to be taken, when highlightObjects button is pressed
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				componentImage = new ComponentImage(getImagePath());
				image = componentImage.getPicture().getJLabel();
				image.setSize(1000, 540);
				imagePanel.add(image);
				imagePanel.moveToFront(image);
				binarise.setEnabled(true);
				addColours.setEnabled(false);
				highlightObjects.setEnabled(false);
				reset.setEnabled(false);
				showSmallestAndLargest.setEnabled(false);
				smallestLargest.setVisible(false);
			}
		});

		// action to be taken, when showSmallestAndLargest button is pressed
		showSmallestAndLargest.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				image = componentImage.getSmallestAndLargest().getJLabel();
				image.setSize(1000, 540);
				imagePanel.add(image);
				imagePanel.moveToFront(image);
				binarise.setEnabled(true);
				addColours.setEnabled(false);
				highlightObjects.setEnabled(false);
				reset.setEnabled(true);
				showSmallestAndLargest.setEnabled(false);
			}
		});

	}

	/**
	 * 
	 * @return the absolute path of the image
	 */
	private String getImagePath() {
		return imagePath;
	}

	/**
	 * 
	 * @param imagePath
	 *            taken from JFileChooser, once the image is loaded is set as
	 *            imagePath
	 */
	private void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
