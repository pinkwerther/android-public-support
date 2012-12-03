package com.pinkwerther.support.tracking;

import java.util.ArrayList;

import com.pinkwerther.support.resources.Helper;

public class TrackingEvent {
	protected int type;
	protected final static int
		PAGEVIEW=0,
		EVENT=1,
		TRANSACTION=2;
	protected String page;
	public TrackingEvent(String pageview) {
		this.page = pageview;
		this.type=PAGEVIEW;
	}
	protected String category, action, label;
	protected int value;
	public TrackingEvent(String category, String action, String label, int value) {
		this.category = category;
		this.action = action;
		this.label = label;
		this.value = value;
		this.type=EVENT;
	}
	
	public String serialize() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(Integer.toString(type));
		switch (type) {
		case PAGEVIEW:
			ret.add(page);
			break;
		case EVENT:
			ret.add(category);
			ret.add(action);
			ret.add(label);
			ret.add(Integer.toString(value));
			break;
		}
		
		return Helper.adjoin(ret);
	}
	public static TrackingEvent deserialize(String string) {
		String[] content = Helper.separate(string);
		int type = Integer.parseInt(content[0]);
		switch (type) {
		case PAGEVIEW:
			return new TrackingEvent(content[1]);
		case EVENT:
			return new TrackingEvent(content[1],content[2],content[3],Integer.parseInt(content[4]));
		}
		return null;
	}
	
}
