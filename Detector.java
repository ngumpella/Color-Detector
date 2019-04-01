/**
 * Helper methods, the detector, outputs the final result
 * @author Nikhita Gumpella
 * @version 1.0 December 6th, 2018
 * CS310 Fall 2018
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.AbstractCollection;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import javax.swing.JPanel;

public class Detector extends JPanel
{
   /**
	 * Determines the difference between two colors as a value between 0 and 100
	 * @param Color c1, the first color
	 * @param Color c2, the second color
	 * @return the difference 
 	*/	
	public static int getDifference(Color c1, Color c2)
	{
		int red = (int)Math.floor(Math.pow(c1.getRed() - c2.getRed(), 2));
		int green = (int)Math.floor(Math.pow(c1.getGreen() - c2.getGreen(), 2));
		int blue = (int)Math.floor(Math.pow(c1.getBlue() - c2.getBlue(), 2));
		
		int difference = red + green + blue;
		difference *= 100/(3 * Math.pow(255, 2));
		return (int)Math.floor(difference); 
	}
	
   /**
	 * Thresholds the pixels and recolors them
	 * Colors the pixels white if the pixel is not the color we want
	 * and colors the pixels black if it is the color we want
	 * @param Color c, the color we want
	 * @param int okDist, indicates the acceptable inclusive "distance" 
	 * between the pixel and the color c	
 	*/		
	public static void thresh(BufferedImage image, Color c, int okDist)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				if(getDifference(c, getColor(image, getPixel(image,getId(image, x, y)))) <= okDist)
				{
					image.setRGB(x, y, Color.black.getRGB());
				}
				else
				{
					image.setRGB(x, y, Color.white.getRGB());
				}
			}
		} 
	}
	
   /**
	 * Given an image, a disjoint set, and a pixel ID, this method gets the neighboring set
	 * above and to the left
	 * If there is no above/left neighbor, the respective part of the pair will be null
	 * @param BufferedImage image, the image we are walking through
	 * @param DisjointSets<Pixel> the disjoint set of pixels
	 * @param int pixelId, the pixel's Id
	 * @return the above and left neighbor of the given pixel
 	*/	
	public static Pair<Integer,Integer> getNeighborSets(BufferedImage image, DisjointSets<Pixel> ds, int pixelId)
	{
		int width = image.getWidth();
		Integer aboveroot;
		Integer leftroot;

		//checking edge case for the above root
		if(pixelId <= width)
		{
			aboveroot = null;
		}
		else
		{
			aboveroot = ds.find(pixelId - width);
		}

		//checking edge cases for the left root
		if((pixelId % width == 1 && pixelId != 1) || pixelId == 0)
		{
			leftroot = null;
		}
		else
		{
			leftroot = ds.find(pixelId - 1);
		}

		Pair<Integer, Integer> neighbors = new Pair<>(aboveroot, leftroot);
		return neighbors; 
	}

	/**
	 * Thresholds the image and walks through it and performs 
	 * finds and unions where appropriate
	 * The disjoint set ds wil contain the color blobs
 	*/
	public void detect()
	{	
		int width = img.getWidth();
		int height = img.getHeight();
		ArrayList<Pixel> arrpix = new ArrayList<>();

		//thresholding image
		thresh(img, blobColor, okDist);
		
		//making the disjointset ds
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				arrpix.add(getPixel(img, getId(img, x, y)));
			}
		}
		ds = new DisjointSets<Pixel>(arrpix);

		//walks through the image and performs finds and unions where appropriate
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				int pixelid = getId(img, x, y);
				int pixelroot = ds.find(pixelid);
				Pair<Integer, Integer> neighbors = getNeighborSets(img, ds, pixelid);

				if(neighbors.a != null)
				{
					if(getColor(img, arrpix.get(pixelid)).equals(getColor(img, arrpix.get(neighbors.a))))
					{
						if(pixelroot != neighbors.a)
						{
							ds.union(ds.find(pixelroot), neighbors.a);
						}
					}
				}

				if(neighbors.b != null)
				{
					if(getColor(img, arrpix.get(pixelid)).equals(getColor(img, arrpix.get(neighbors.b))))
					{
						if(pixelroot != neighbors.b)
						{
							ds.union(ds.find(pixelroot), neighbors.b);
						}
					}
				}
			}
		}
	}

	/**
	 * My detect works, but I couldn't figure out outputResults()
	 * Holds all the roots in a treeset
	 * Recolors all the k largest blobs and saves output
	 * @param String outputFileName the output filename
	 * @param String outputECFileName the extra credit filename
	 * @param int k the number of blobs colored
 	*/	
	public void outputResults(String outputFileName, String outputECFileName, int k)
	{
		int width = img.getWidth();
		int height = img.getHeight();

		if(k<1)
		{
			throw new IllegalArgumentException(new String("! Error: k should be greater than 0, current k="+k));
		}
		
		//treeset to hold the roots
		TreeSet<Integer> tr = new TreeSet<>();
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				tr.add(ds.find(getId(img, x, y)));
			}
		}
		
		//using the roots, collect all sets of pixels and sort them by size
		ArrayList<Set<Pixel>> trarr = new ArrayList<>();
		for(int i = 0; i < tr.size(); i++)
		{
			Set<Pixel> temp = ds.get(tr.pollFirst());
			trarr.add(temp);
		}
		//Collections.reverse(trarr);

		System.out.println(k + "/" + tr.size());
		for(int i = 1; i <= k; i++)
		{
			Set<Pixel> pix = trarr.get(i);
			System.out.println("Blob " + i + ": " + pix.size() + " pixels");
		}  
		//Note: you may use Collections.sort() here if you want
		
		//recolor the k-largest blobs from black to a color from getSeqColor()
		//Hint: Use image.setRGB(x,y,c.getRGB()) to change the color of a pixel (x,y) to the given color "c"
		
		//and output all blobs to console
		//save output image -- provided
		try {
			File output = new File(outputFileName);
			ImageIO.write(this.img, "png", output);
			System.err.println("- Saved result to "+outputFileName);
		}
		catch (Exception e) {
			System.err.println("! Error: Failed to save image to "+outputFileName);
		}
		
		/*
		//if you're doing the EC and your output image is still this.img,
		//you can uncomment this to save this.img to the specified outputECFileName
		try {
			File ouptut = new File(outputECFileName);
			ImageIO.write(this.img, "png", output);
			System.err.println("- Saved result to "+outputECFileName);
		}
		catch (Exception e) {
			System.err.println("! Error: Failed to save image to "+outputECFileName);
		}
		*/
	}
	
	//main method just for your testing
	//edit as much as you want
	public static void main(String[] args)
	{
		File imageFile = new File("../input/04_Circles.png");
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(imageFile);
		}
		catch(IOException e) {
			System.err.println("! Error: Failed to read "+imageFile+", error msg: "+e);
			return;
		}
		
		Pixel p = getPixel(img, 110); //100x100 pixel image, pixel id 110
		System.out.println(p.a); //x = 10
		System.out.println(p.b); //y = 1
		System.out.println(getId(img, p)); //gets the id back (110)
		System.out.println(getId(img, p.a, p.b)); //gets the id back (110)
		System.out.println("getDifference(): "+getDifference(Color.red, Color.black));
		System.out.println("getDifference(): "+getDifference(Color.green, Color.white));	
	}

	//-----------------------------------------------------------------------
	//
	// Todo: Read and provide comments, but do not change the following code
	//
	//-----------------------------------------------------------------------

	//Data
	public BufferedImage img;        //this is the 2D array of RGB pixels
	private Color blobColor;         //the color of the blob we are detecting
	private String imgFileName;      //input image file name
	private DisjointSets<Pixel> ds;  //the disjoint set
	private int okDist;              //the distance between blobColor and the pixel which "still counts" as the color

	/**
	 * Constructor, reads image from file
	 * @param String the name of the file
	 * @param Color the wanted color to be detected
	 * @param int the acceptable distance away from the given color
 	*/
	public Detector(String imgfile, Color blobColor, int okDist) {
		this.imgFileName = imgfile;
		this.blobColor = blobColor;
		this.okDist = okDist;
		
		reloadImage();
	}

	/**
	 * Helper function for the constructor, reads image from file
 	*/
	public void reloadImage() {
		File imageFile = new File(this.imgFileName);
		
		try {
			this.img = ImageIO.read(imageFile);
		}
		catch(IOException e) {
			System.err.println("! Error: Failed to read "+this.imgFileName+", error msg: "+e);
			return;
		}
	}

	/**
	 * The JPanel function
 	*/
	public void paint(Graphics g) {
		g.drawImage(this.img, 0, 0,this);
	}

	//private classes below

	/**
	 * Helper class representing a pair of things
 	*/
	private static class Pair<A,B> {
		A a;
		B b;
		public Pair(A a, B b) {
			this.a=a;
			this.b=b;
		}
	}

	/**
	 * A pixel is a set of locations
	 * Represents it in a pair
	 * @param int x: distance from the left
	 * @param int y: distance from the top
 	*/
	private static class Pixel extends Pair<Integer, Integer> {
		public Pixel(int x, int y) {
			super(x,y);
		}
	}

	/**
	 * Converts a pixel in an image to its ID
	 * @param BufferedImage the image
	 * @param Pixel the pixel
	 * @return the pixel's ID
 	*/
	private static int getId(BufferedImage image, Pixel p) {
		return getId(image, p.a, p.b);
	}

	/**
	 * Converts an ID for an image back to a pixel
	 * @param BufferedImage the image
	 * @param int id the id 
	 * @return the pixel we get from the ID
 	*/
	private static Pixel getPixel(BufferedImage image, int id) {
		int y = id/image.getWidth();
		int x = id-(image.getWidth()*y);

		if(y<0 || y>=image.getHeight() || x<0 || x>=image.getWidth())
			throw new ArrayIndexOutOfBoundsException();

		return new Pixel(x,y);
	}

	/**
	 * Converts a location of a pixel in an image into an ID
	 * @param BufferedImage the image
	 * @param int x: distance from the left
	 * @param int y: distance from the top
	 * @return the ID of that (x, y) location
 	*/
	private static int getId(BufferedImage image, int x, int y) {
		return (image.getWidth()*y)+x;
	}

	/**
	 * Returns the color of a given pixel in a given image
	 * @param BufferedImage the image
	 * @param Pixel the pixel
	 * @return the color of that pixel in the image
 	*/
	private static Color getColor(BufferedImage image, Pixel p) {
		return new Color(image.getRGB(p.a, p.b));
	}
	
	/**
	 * Pass 0 -> k-1 as i to get the color for the blogs 0 -> k-1
	 * @param int i
	 * @param int max
	 * @return the next color in the gradient
 	*/
	private Color getSeqColor(int i, int max) {
		if(i < 0) i = 0;
		if(i >= max) i = max-1;
		
		int r = (int)(((max-i+1)/(double)(max+1)) * blobColor.getRed());
		int g = (int)(((max-i+1)/(double)(max+1)) * blobColor.getGreen());
		int b = (int)(((max-i+1)/(double)(max+1)) * blobColor.getBlue());
		
		if(r == 0 && g == 0 && b == 0) {
			r = g = b = 10;
		}
		else if(r == 255 && g == 255 && b == 255) {
			r = g = b = 245;
		}
		
		return new Color(r, g, b);
	}
}