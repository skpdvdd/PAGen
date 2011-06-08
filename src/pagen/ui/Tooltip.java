package pagen.ui;

import processing.core.PConstants;

/**
 * Displays a tooltip. This is a static class.
 */
public class Tooltip
{
	/**
	 * The font to use.
	 */
	public static String font = "sans";
	
	/**
	 * The font size to use.
	 */
	public static int fontSize = 15;
	
	/**
	 * The default background color.
	 */
	public static int defaultBgColor = 0xFF4b6d78;
	
	/**
	 * The default font color.
	 */
	public static int defaultFontColor = 0xFFCCCCCC;
	
	/**
	 * Displays a tooltip.
	 * 
	 * @param p Where to draw to. Must not be null
	 * @param title The title
	 * @param text The lines of text
	 */
	public static void display(PAGen p, String title, String[] text)
	{
		display(p, defaultBgColor, defaultFontColor, title, text);
	}
	
	/**
	 * Displays a tooltip.
	 * 
	 * @param p Where to draw to. Must not be null
	 * @param bgColor The bg color to use
	 * @param fontColor The font color to use
	 * @param title The title
	 * @param text The lines of text
	 */
	public static void display(PAGen p, int bgColor, int fontColor, String title, String[] text)
	{
		int numc = title.length();
		for(String s : text) {
			if(s.length() > numc) {
				numc = s.length();
			}
		}
		
		int x = p.width / 2;
		int y = p.height / 2;
		int dx = numc * 10 / 2;
		int dy = (text.length * 20 + 35) / 2;
		
		p.rectMode(PConstants.CORNERS);
		p.fill(bgColor);
		p.rect(x - dx, y - dy, x + dx, y + dy);
		
		p.fill(255);
		p.textFont(p.getFont(font, fontSize));
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.text(title, x, y - dy + 10);
		p.fill(fontColor);
		
		int ty = y - dy + 40;
		
		for(String s : text) {
			p.text(s, x, ty);
			ty += 20;
		}
	}
	
	private Tooltip() { }
}
