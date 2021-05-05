package it.erm.graphics;


import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class ResourceUtils {
	
	public enum Images {
		NOT_FOUND, QUERY, PLAY, PLAYALL, PAUSE, DETAILS
	}
	
	public enum ImgColour {
		BLUE, RED, GREEN, YELLOW, WHITE
	}
	
	public enum ImgSize {
		h128w128, h96w96, h64w64, h32w32, h16w16
	}
	
	private static ConcurrentHashMap<String, ImageIcon> mapIcon = new ConcurrentHashMap<String, ImageIcon>();
	
	private static Class<?> classLoader = null;
	
	public static void setClassLoader(Class<?> classLoader) {
		ResourceUtils.classLoader = classLoader;
	}
	
	public static Class<?> getLoader() {
		if( classLoader == null ) {
			classLoader = ResourceUtils.class;
		}
		return classLoader;
	}
	
	private static String buildKey(Images img, ImgColour imgC, ImgSize imgS) {
		StringBuilder key = new StringBuilder();
		key.append(getEnumName(img));
		key.append('.');
		key.append(getEnumName(imgC));
		key.append('.');
		key.append(getEnumName(imgS));
		return key.toString();
	}
	
	public static ImageIcon getImageIcon(Images img, ImgColour imgC, ImgSize imgS) {
		return getImageIcon(img, imgC, imgS, "");
	}
	
	public static Image getImage(Images img, ImgColour imgC, ImgSize imgS, String description) {
		ImageIcon imageIcon = getImageIcon(img, imgC, imgS, "");
		if( imageIcon != null ) {
			return imageIcon.getImage();
		} else {
			return null;
		}
	}
	
	
	public static ImageIcon getImageIcon(Images img, ImgColour imgC, ImgSize imgS, String description) {
		String key = buildKey(img, imgC, imgS);
		ImageIcon imageIcon = mapIcon.get(key);
		if( imageIcon == null ) {
			imageIcon = loadIcon(img, imgC, imgS, description);
			if( imageIcon == null ) {
				imageIcon = loadIcon(Images.NOT_FOUND, ImgColour.YELLOW, ImgSize.h16w16, "File not found");
			}
			if( imageIcon != null ) {
				mapIcon.put(key, imageIcon);
			}
		}
		return imageIcon;
	}
	
	private static ImageIcon loadIcon(Images img, ImgColour imgC, ImgSize imgS, String description) {
		ImageIcon imgIcon = null;
		
		String buidImageFileName = null;
		if( imgIcon == null ) try {
			buidImageFileName = buidImageFileName(img, imgC, imgS);
			imgIcon = getImageIcon(buidImageFileName, description);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return imgIcon;
	}
	
	private static boolean imgKeyPropertyIsFile(Images img, ImgColour imgC, ImgSize imgS, String imgFileName) {
		boolean result = false;
		String imgName = getEnumName(img).toLowerCase();
		String imgCName = getEnumName(imgC).toLowerCase();
		String imgSName = getEnumName(imgS).toLowerCase();
		
		int indexOfImg = imgFileName.indexOf(imgName);
		int indexOfImgC = imgFileName.indexOf(imgCName, indexOfImg);
		int indexOfImgS = imgFileName.indexOf(imgSName, indexOfImgC);
		
		boolean imgFound = img != null ? indexOfImg >= 0 : true;
		boolean imgCFound = imgC != null ? indexOfImgC > 0 : true;
		boolean imgSFound = imgS != null ? indexOfImgS > 0 : true;
		
		result = imgFound && imgCFound && imgSFound;
		return result;
	}
	
	private static String getEnumName(Enum<?> e) {
		return e==null?"null":e.name();
	}
	
	
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon getImageIcon(String fileName, String description) {
	    ImageIcon imageIcon = mapIcon.get(fileName);
	    if( imageIcon != null ) {
	        return imageIcon;
	    }
	    
		Package pck = getLoader().getPackage();
		String pckNname = pck.getName().replace('.', '/');
		String resource = pckNname+"/images/"+fileName;
		
		try {
		    imageIcon = new ImageIcon(getLoader().getClassLoader().getResource(resource));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if( imageIcon == null ) {
			try {
				BufferedImage image = ImageIO.read(getLoader().getClassLoader().getResourceAsStream(resource));
				imageIcon = new ImageIcon(image);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if( imageIcon != null ) {
		    imageIcon.setDescription(description);
		    mapIcon.put(fileName, imageIcon);
		}
	    return imageIcon;
	}

	   /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String fileName) {
        ImageIcon createImageIcon = getImageIcon(fileName, "");
        return createImageIcon;
    }
	
	public static boolean resourcesLoaded() {
		ImageIcon loadIcon = loadIcon(Images.NOT_FOUND, ImgColour.YELLOW, ImgSize.h16w16, "File not found");
		return loadIcon != null;
	}
	
	private static String buidImageFileName(Images img, ImgColour imgC, ImgSize imgS) {
		StringBuilder sb = new StringBuilder();
		if( img != null ) {
			sb.append(img.name().toLowerCase()).append(' ');
		}
		if( imgC != null ) {
			sb.append(imgC.name().toLowerCase()).append(' ');
		}
		if( imgS != null ) {
			sb.append(imgS.name().toLowerCase()).append(".png");
		}
		return sb.toString();
	}

   public static void FontStyle(JComponent c, int style) {
        Font font = c.getFont();
        Font newFont = new Font(font.getName(), style, font.getSize());
        c.setFont(newFont);
    }
	
	public static void FontAdd(JComponent c, int sizeAdd) {
        Font font = c.getFont();
        Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+sizeAdd);
        c.setFont(newFont);
	}
	
    public static void FontChange(JComponent c, int style, int sizeAdd) {
        Font font = c.getFont();
        Font newFont = new Font(font.getName(), style, font.getSize()+sizeAdd);
        c.setFont(newFont);
    }

	
}
