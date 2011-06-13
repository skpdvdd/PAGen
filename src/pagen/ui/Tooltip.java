package pagen.ui;

import processing.core.PConstants;
import processing.core.PFont;

/**
 * Displays a tooltip.
 */
public class Tooltip
{
	/**
	 * The default font.
	 */
	public static String defaultFont = "Arial";
	
	/**
	 * The default font size.
	 */
	public static int defaultFontSize = 15;
	
	/**
	 * The default background color.
	 */
	public static int defaultBgColor = 0xFF222222;
	
	/**
	 * The default border color.
	 */
	public static int defaultBorderColor = 0xFFAAAAAA;
	
	/**
	 * The default font color.
	 */
	public static int defaultFontColor = 0xFFCCCCCC;
	
	private final PAGen _p;
	private final String _title;
	private final PFont _font;
	private final int _bgColor;
	private final int _borderColor;
	private final int _fontColor;
	
	private String[] _text;
	private int _width;
	private int _height;
	
	/**
	 * Ctor.
	 * 
	 * @param p Where to draw to
	 * @param title The title of the tooltip
	 */
	public Tooltip(PAGen p, String title)
	{
		this(p, title, null);
	}
	
	/**
	 * Ctor.
	 * 
	 * @param p Where to draw to
	 * @param title The title of the tooltip
	 * @param text The text to draw
	 */
	public Tooltip(PAGen p, String title, String[] text)
	{
		this(p, title, text, defaultFont, defaultFontSize, defaultBgColor, defaultBorderColor, defaultFontColor);
	}
	
	/**
	 * Ctor.
	 * 
	 * @param p Where to draw to
	 * @param title The title of the tooltip
	 * @param text The text to draw
	 * @param font The font to use
	 * @param fontSize The font size
	 * @param bgColor The bg color
	 * @param borderColor The border color
	 * @param fontColor The font color
	 */
	public Tooltip(PAGen p, String title, String[] text, String font, int fontSize, int bgColor, int borderColor, int fontColor)
	{
		_p = p;
		_title = title;
		_font = _p.getFont(font, fontSize);
		_bgColor = bgColor;
		_borderColor = borderColor;
		_fontColor = fontColor;
		_text = text;
	}
	
	/**
	 * Displays the tooltip
	 */
	public void display()
	{
		display(_text);
	}
	
	/**
	 * Displays the tooltip with the new text.
	 * 
	 * @param text The new text
	 */
	public void display(String[] text)
	{
		if(text != null) {
			_text = text;
		}
		else {
			text = new String[0];
		}
		
		int numc = _title.length();
		for(String s : text) {
			if(s.length() > numc) {
				numc = s.length();
			}
		}
		
		int width = numc * 10 / 2;
		int height = (text.length * 20 + 35) / 2;
		
		_width = PAGen.max(_width, width);
		_height = PAGen.max(_height, height);
		
		int x = _p.width / 2;
		int y = _p.height / 2;
		
		_p.rectMode(PConstants.CORNERS);
		_p.fill(_bgColor);
		_p.stroke(_borderColor);
		_p.strokeWeight(1);
		_p.rect(x - _width, y - _height, x + _width, y + _height);
		
		_p.fill(255);
		_p.textFont(_font);
		_p.textAlign(PConstants.CENTER, PConstants.CENTER);
		_p.text(_title, x, y - _height + 10);
		_p.fill(_fontColor);
		
		int ty = y - _height + 40;
		
		for(String s : text) {
			_p.text(s, x, ty);
			ty += 20;
		}
	}
}
