/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.input;

import java.util.Map;
import java.util.TreeMap;

import com.blackrook.gui.GUIInputConstants;

/**
 * Contains tables that map strings to input keys.
 * @author Matthew Tropiano
 */
public interface GUIKeyTables extends GUIInputConstants
{
	/** Keystroke prefix for gamepad actions. */
	public static final String GENERIC_PREFIX = "generic";
	/** Keystroke prefix for gamepad actions. */
	public static final String GAMEPAD_PREFIX = "pad";
	/** Keystroke prefix for key release. */
	public static final String KEY_RELEASE = "release";
	
	/** 
	 * Keyboard key name to KEY map. 
	 */
	public static final Map<String, Integer> KEYBOARD_KEY_MAP = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER)
	{
		private static final long serialVersionUID = 2221006455786164273L;
		{ 
			put("enter", KEY_ENTER);
			put("back_space", KEY_BACK_SPACE);
			put("backspace", KEY_BACK_SPACE);
			put("bksp", KEY_BACK_SPACE);
			put("tab", KEY_TAB);
			put("cancel", KEY_CANCEL);
			put("clear", KEY_CLEAR);
			put("shift", KEY_SHIFT);
			put("control", KEY_CONTROL);
			put("ctrl", KEY_CONTROL);
			put("alt", KEY_ALT);
			put("pause", KEY_PAUSE);
			put("break", KEY_PAUSE);
			put("caps_lock", KEY_CAPS_LOCK);
			put("caps", KEY_CAPS_LOCK);
			put("escape", KEY_ESCAPE);
			put("esc", KEY_ESCAPE);
			put("space", KEY_SPACE);
			put("sp", KEY_SPACE);
			put("page_up", KEY_PAGE_UP);
			put("pageup", KEY_PAGE_UP);
			put("pgup", KEY_PAGE_UP);
			put("page_down", KEY_PAGE_DOWN);
			put("pagedown", KEY_PAGE_DOWN);
			put("pgdn", KEY_PAGE_DOWN);
			put("end", KEY_END);
			put("home", KEY_HOME);
			put("left", KEY_LEFT);
			put("up", KEY_UP);
			put("right", KEY_RIGHT);
			put("down", KEY_DOWN);
			put("comma", KEY_COMMA);
			put(",", KEY_COMMA);
			put("minus", KEY_MINUS);
			put("-", KEY_MINUS);
			put("period", KEY_PERIOD);
			put(".", KEY_PERIOD);
			put("slash", KEY_SLASH);
			put("/", KEY_SLASH);
			put("0", KEY_0);
			put("1", KEY_1);
			put("2", KEY_2);
			put("3", KEY_3);
			put("4", KEY_4);
			put("5", KEY_5);
			put("6", KEY_6);
			put("7", KEY_7);
			put("8", KEY_8);
			put("9", KEY_9);
			put("semicolon", KEY_SEMICOLON);
			put(";", KEY_SEMICOLON);
			put("equals", KEY_EQUALS);
			put("=", KEY_EQUALS);
			put("a", KEY_A);
			put("b", KEY_B);
			put("c", KEY_C);
			put("d", KEY_D);
			put("e", KEY_E);
			put("f", KEY_F);
			put("g", KEY_G);
			put("h", KEY_H);
			put("i", KEY_I);
			put("j", KEY_J);
			put("k", KEY_K);
			put("l", KEY_L);
			put("m", KEY_M);
			put("n", KEY_N);
			put("o", KEY_O);
			put("p", KEY_P);
			put("q", KEY_Q);
			put("r", KEY_R);
			put("s", KEY_S);
			put("t", KEY_T);
			put("u", KEY_U);
			put("v", KEY_V);
			put("w", KEY_W);
			put("x", KEY_X);
			put("y", KEY_Y);
			put("z", KEY_Z);
			put("open_bracket", KEY_OPEN_BRACKET);
			put("openbracket", KEY_OPEN_BRACKET);
			put("[", KEY_OPEN_BRACKET);
			put("back_slash", KEY_BACK_SLASH);
			put("backslash", KEY_BACK_SLASH);
			put("\\", KEY_BACK_SLASH);
			put("close_bracket", KEY_CLOSE_BRACKET);
			put("closebracket", KEY_CLOSE_BRACKET);
			put("]", KEY_CLOSE_BRACKET);
			put("numpad0", KEY_NUMPAD0);
			put("numpad1", KEY_NUMPAD1);
			put("numpad2", KEY_NUMPAD2);
			put("numpad3", KEY_NUMPAD3);
			put("numpad4", KEY_NUMPAD4);
			put("numpad5", KEY_NUMPAD5);
			put("numpad6", KEY_NUMPAD6);
			put("numpad7", KEY_NUMPAD7);
			put("numpad8", KEY_NUMPAD8);
			put("numpad9", KEY_NUMPAD9);
			put("num0", KEY_NUMPAD0);
			put("num1", KEY_NUMPAD1);
			put("num2", KEY_NUMPAD2);
			put("num3", KEY_NUMPAD3);
			put("num4", KEY_NUMPAD4);
			put("num5", KEY_NUMPAD5);
			put("num6", KEY_NUMPAD6);
			put("num7", KEY_NUMPAD7);
			put("num8", KEY_NUMPAD8);
			put("num9", KEY_NUMPAD9);
			put("multiply", KEY_MULTIPLY);
			put("mult", KEY_MULTIPLY);
			put("add", KEY_ADD);
			put("separater", KEY_SEPARATER);
			put("separator", KEY_SEPARATOR);
			put("subtract", KEY_SUBTRACT);
			put("subt", KEY_SUBTRACT);
			put("decimal", KEY_DECIMAL);
			put("dec", KEY_DECIMAL);
			put("divide", KEY_DIVIDE);
			put("div", KEY_DIVIDE);
			put("delete", KEY_DELETE);
			put("del", KEY_DELETE);
			put("num_lock", KEY_NUM_LOCK);
			put("numlock", KEY_NUM_LOCK);
			put("scroll_lock", KEY_SCROLL_LOCK);
			put("scrolllock", KEY_SCROLL_LOCK);
			put("f1", KEY_F1);
			put("f2", KEY_F2);
			put("f3", KEY_F3);
			put("f4", KEY_F4);
			put("f5", KEY_F5);
			put("f6", KEY_F6);
			put("f7", KEY_F7);
			put("f8", KEY_F8);
			put("f9", KEY_F9);
			put("f10", KEY_F10);
			put("f11", KEY_F11);
			put("f12", KEY_F12);
			put("f13", KEY_F13);
			put("f14", KEY_F14);
			put("f15", KEY_F15);
			put("f16", KEY_F16);
			put("f17", KEY_F17);
			put("f18", KEY_F18);
			put("f19", KEY_F19);
			put("f20", KEY_F20);
			put("f21", KEY_F21);
			put("f22", KEY_F22);
			put("f23", KEY_F23);
			put("f24", KEY_F24);
			put("printscreen", KEY_PRINTSCREEN);
			put("printscr", KEY_PRINTSCREEN);
			put("sysrq", KEY_PRINTSCREEN);
			put("insert", KEY_INSERT);
			put("ins", KEY_INSERT);
			put("help", KEY_HELP);
			put("meta", KEY_META);
			put("back_quote", KEY_BACK_QUOTE);
			put("backquote", KEY_BACK_QUOTE);
			put("grave", KEY_BACK_QUOTE);
			put("`", KEY_BACK_QUOTE);
			put("quote", KEY_QUOTE);
			put("'", KEY_QUOTE);
			put("kp_up", KEY_KP_UP);
			put("kp_down", KEY_KP_DOWN);
			put("kp_left", KEY_KP_LEFT);
			put("kp_right", KEY_KP_RIGHT);
			put("kpup", KEY_KP_UP);
			put("kpdown", KEY_KP_DOWN);
			put("kpleft", KEY_KP_LEFT);
			put("kpright", KEY_KP_RIGHT);
			put("dead_grave", KEY_DEAD_GRAVE);
			put("dead_acute", KEY_DEAD_ACUTE);
			put("dead_circumflex", KEY_DEAD_CIRCUMFLEX);
			put("dead_tilde", KEY_DEAD_TILDE);
			put("dead_macron", KEY_DEAD_MACRON);
			put("dead_breve", KEY_DEAD_BREVE);
			put("dead_abovedot", KEY_DEAD_ABOVEDOT);
			put("dead_diaeresis", KEY_DEAD_DIAERESIS);
			put("dead_abovering", KEY_DEAD_ABOVERING);
			put("dead_doubleacute", KEY_DEAD_DOUBLEACUTE);
			put("dead_caron", KEY_DEAD_CARON);
			put("dead_cedilla", KEY_DEAD_CEDILLA);
			put("dead_ogonek", KEY_DEAD_OGONEK);
			put("dead_iota", KEY_DEAD_IOTA);
			put("dead_voiced_sound", KEY_DEAD_VOICED_SOUND);
			put("dead_semivoiced_sound", KEY_DEAD_SEMIVOICED_SOUND);
			put("ampersand", KEY_AMPERSAND);
			put("&", KEY_AMPERSAND);
			put("asterisk", KEY_ASTERISK);
			put("*", KEY_ASTERISK);
			put("quotedbl", KEY_QUOTEDBL);
			put("\"", KEY_QUOTEDBL);
			put("less", KEY_LESS);
			put("<", KEY_LESS);
			put("greater", KEY_GREATER);
			put(">", KEY_GREATER);
			put("braceleft", KEY_BRACELEFT);
			put("{", KEY_BRACELEFT);
			put("braceright", KEY_BRACERIGHT);
			put("}", KEY_BRACERIGHT);
			put("at", KEY_AT);
			put("colon", KEY_COLON);
			put(":", KEY_COLON);
			put("circumflex", KEY_CIRCUMFLEX);
			put("dollar", KEY_DOLLAR);
			put("$", KEY_DOLLAR);
			put("euro_sign", KEY_EURO_SIGN);
			put("exclamation_mark", KEY_EXCLAMATION_MARK);
			put("exclamationmark", KEY_EXCLAMATION_MARK);
			put("!", KEY_EXCLAMATION_MARK);
			put("inverted_exclamation_mark", KEY_INVERTED_EXCLAMATION_MARK);
			put("left_parenthesis", KEY_LEFT_PARENTHESIS);
			put("leftparenthesis", KEY_LEFT_PARENTHESIS);
			put("(", KEY_LEFT_PARENTHESIS);
			put("number_sign", KEY_NUMBER_SIGN);
			put("numbersign", KEY_NUMBER_SIGN);
			put("#", KEY_NUMBER_SIGN);
			put("plus", KEY_PLUS);
			put("+", KEY_PLUS);
			put("right_parenthesis", KEY_RIGHT_PARENTHESIS);
			put("rightparenthesis", KEY_RIGHT_PARENTHESIS);
			put(")", KEY_RIGHT_PARENTHESIS);
			put("underscore", KEY_UNDERSCORE);
			put("_", KEY_UNDERSCORE);
			put("windows", KEY_WINDOWS);
			put("win", KEY_WINDOWS);
			put("context_menu", KEY_CONTEXT_MENU);
			put("contextmenu", KEY_CONTEXT_MENU);
			put("final", KEY_FINAL);
			put("convert", KEY_CONVERT);
			put("nonconvert", KEY_NONCONVERT);
			put("accept", KEY_ACCEPT);
			put("modechange", KEY_MODECHANGE);
			put("kana", KEY_KANA);
			put("kanji", KEY_KANJI);
			put("alphanumeric", KEY_ALPHANUMERIC);
			put("katakana", KEY_KATAKANA);
			put("hiragana", KEY_HIRAGANA);
			put("full_width", KEY_FULL_WIDTH);
			put("half_width", KEY_HALF_WIDTH);
			put("roman_characters", KEY_ROMAN_CHARACTERS);
			put("all_candidates", KEY_ALL_CANDIDATES);
			put("previous_candidate", KEY_PREVIOUS_CANDIDATE);
			put("code_input", KEY_CODE_INPUT);
			put("japanese_katakana", KEY_JAPANESE_KATAKANA);
			put("japanese_hiragana", KEY_JAPANESE_HIRAGANA);
			put("japanese_roman", KEY_JAPANESE_ROMAN);
			put("kana_lock", KEY_KANA_LOCK);
			put("input_method_on_off", KEY_INPUT_METHOD_ON_OFF);
			put("cut", KEY_CUT);
			put("copy", KEY_COPY);
			put("paste", KEY_PASTE);
			put("undo", KEY_UNDO);
			put("again", KEY_AGAIN);
			put("find", KEY_FIND);
			put("props", KEY_PROPS);
			put("stop", KEY_STOP);
			put("compose", KEY_COMPOSE);
			put("alt_graph", KEY_ALT_GRAPH);
			put("altgraph", KEY_ALT_GRAPH);
			put("altg", KEY_ALT_GRAPH);
			put("begin", KEY_BEGIN);
			put("undefined", KEY_UNDEFINED);
		}
	};

	/** 
	 * Keyboard key mask to KEY map. 
	 */
	public static final Map<String, Integer> KEYBOARD_MASK_MAP = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER)
	{
		private static final long serialVersionUID = -9218520729131703025L;
		{
			put("shift", GUIKeyStroke.MASK_SHIFT);
			put("control", GUIKeyStroke.MASK_CTRL);
			put("ctrl", GUIKeyStroke.MASK_CTRL);
			put("alt", GUIKeyStroke.MASK_ALT);
			put("window", GUIKeyStroke.MASK_WIN);
			put("win", GUIKeyStroke.MASK_WIN);
			put("meta", GUIKeyStroke.MASK_META);
		}
	};

	/** 
	 * Gamepad button name to KEY map. 
	 */
	public static final Map<String, Integer> GAMEPAD_BUTTON_MAP = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER)
	{
		private static final long serialVersionUID = -6552194762250416114L;
		{
			put("1", GAMEPAD_1);
			put("2", GAMEPAD_2);
			put("3", GAMEPAD_3);
			put("4", GAMEPAD_4);
			put("5", GAMEPAD_5);
			put("6", GAMEPAD_6);
			put("7", GAMEPAD_7);
			put("8", GAMEPAD_8);
			put("9", GAMEPAD_9);
			put("10", GAMEPAD_10);
			put("11", GAMEPAD_11);
			put("12", GAMEPAD_12);
			put("A", GAMEPAD_XBOX_A);
			put("B", GAMEPAD_XBOX_B);
			put("X", GAMEPAD_XBOX_X);
			put("Y", GAMEPAD_XBOX_Y);
			put("LB", GAMEPAD_XBOX_LB);
			put("RB", GAMEPAD_XBOX_RB);
			put("LSTICK", GAMEPAD_XBOX_LSTICK);
			put("RSTICK", GAMEPAD_XBOX_RSTICK);
			put("BACK", GAMEPAD_XBOX_BACK);
			put("START", GAMEPAD_XBOX_START);
		}
	};
	
	/**
	 *  Gamepad axis tap to AXIS map. 
	 */
	public static final Map<String, Integer> GAMEPAD_AXIS_TAP_MAP = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER)
	{
		private static final long serialVersionUID = -3194322182616028981L;
		{
			put("X+", AXIS_X);
			put("X-", AXIS_X);
			put("Y+", AXIS_Y);
			put("Y-", AXIS_Y);
			put("Z+", AXIS_Z);
			put("Z-", AXIS_Z);
			put("RX+", AXIS_RX);
			put("RX-", AXIS_RX);
			put("RY+", AXIS_RY);
			put("RY-", AXIS_RY);
			put("RZ+", AXIS_RZ);
			put("RZ-", AXIS_RZ);
			
			put("LSX+", AXIS_XBOX_LEFT_X);
			put("LSX-", AXIS_XBOX_LEFT_X);
			put("RSX+", AXIS_XBOX_RIGHT_X);
			put("RSX-", AXIS_XBOX_RIGHT_X);
			put("LT", AXIS_XBOX_TRIGGER);
			put("RT", AXIS_XBOX_TRIGGER);
		}
	};

	/** 
	 * Gamepad axis tap to polarity flag map. 
	 */
	public static final Map<String, Boolean> GAMEPAD_AXIS_TAP_FLAG_MAP = new TreeMap<String, Boolean>(String.CASE_INSENSITIVE_ORDER)
	{
		private static final long serialVersionUID = -3463013384157536280L;
		{
			put("X+", true);
			put("X-", false);
			put("Y+", true);
			put("Y-", false);
			put("Z+", true);
			put("Z-", false);
			put("RX+", true);
			put("RX-", false);
			put("RY+", true);
			put("RY-", false);
			put("RZ+", true);
			put("RZ-", false);
			
			put("LSX+", true);
			put("LSX-", false);
			put("RSX+", true);
			put("RSX-", false);
			put("LT", true);
			put("RT", false);
		}
	};
	
}
