package com.pinkwerther.support.resources;

import java.util.ArrayList;
import java.util.List;

import com.pinkwerther.support.R;
import com.pinkwerther.support.R.string;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class Helper {

	public final static void sendMail(Context context, int mailId, int subjectId, String text) {
		sendMail(context,context.getString(mailId),context.getString(subjectId),text);
	}
	public final static void sendMail(Context context, int mailId, String subject, String text) {
		sendMail(context,context.getString(mailId),subject,text);
	}
	public final static void sendMail(Context context, String mailadress, String subject, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailadress});
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		try {
			context.startActivity(Intent.createChooser(intent, context.getString(R.string.sendmail)));
		} catch (android.content.ActivityNotFoundException e) {
			Toast.makeText(context, R.string.nomailclients, Toast.LENGTH_SHORT).show();
		}
	}
	
	private final static String getSep(int index) {
		return "<&#"+index+";>";
	}
	public static String[] separate(String s) {
		for (int i = 0; i<Integer.MAX_VALUE; i++) {
			if (s.indexOf(getSep(i))<0) {
				if (i == 0) {
					String[] ret = new String[1];
					ret[0] = s;
					return ret;
				}
				//if (TarotActivity.DEVEL) Log.d(TAG,"Separation level: "+(i-1)); 
				return s.split(getSep(i-1));
			}
		}
		return null;
	}
	public static List<String> getEmptyList() {
		return new ArrayList<String>();
	}
	public static String adjoin(List<String> strings) {
		sepreaching: 
			for (int i=0; i<Integer.MAX_VALUE; i++) {//String sep : seps) {
				for (String s : strings) {
					if (s.contains(getSep(i)))
						continue sepreaching;
				}
				//if (TarotActivity.DEVEL) Log.d(TAG,"Separation level: "+(i)); 
				StringBuffer ret = new StringBuffer();
				for (String s : strings) {
					if (strings.indexOf(s)!=0)
						ret.append(getSep(i)+s);
					else
						ret.append(s);
				}
				return ret.toString();
			}
		return null;
	}
	
	public static float px2dip(int px,Context c) {
		float scale = c.getResources().getDisplayMetrics().density;
		float size = (float)((float)px/scale);
		return size;
	}
    public static int dip2px(float dip,Context c) {
    	float scale = c.getResources().getDisplayMetrics().density;
    	int size = (int)(dip * scale);
    	return size;
    }
    public static void setViewPadding(float l,float t,float r,float b,View v) {
    	Context c = v.getContext();
    	v.setPadding(dip2px(l,c), dip2px(t,c), dip2px(r,c), dip2px(b,c));
    }
    public static void setViewPadding(float dip,View v) {
    	setViewPadding(dip,dip,dip,dip,v);
    }

}
