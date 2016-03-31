package com.andcopro.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andcopro.bean.JsonAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StringUtils {

	
	public static  String getPercentString(String searching) {

		searching = searching.trim();
		String search_final = "";
		String search[] = searching.split(" ");
		for (int i=0; i<search.length; i++) {
			search_final = search_final + search[i].concat("%");
		}
		
		if(!search_final.endsWith("%")) search_final = search_final + "%";
        if(!search_final.startsWith("%")) search_final = "%" + search_final;
        search_final = search_final.toUpperCase();
        
        return search_final;
		
	}	
	
	
	
	public static String getTaggedString(String string, String substring, String begin_tag, String end_tag) {
		String[] tab; 
		String[] new_tab;
		String new_string;
		
		if (string == null)
			return "";
		
		if (substring == null)
			return string;
		else {
			
			tab = substring.replaceAll("%", " ").trim().split(" ");
			if (tab == null || tab.length ==0)
				return string;
			
			new_tab = new String[tab.length];
				
			Pattern pattern; 
		    Matcher matcher;

			
			for (int i=0; i<new_tab.length; i++) {	
				pattern = Pattern.compile("(?i)"+tab[i]);
				matcher = pattern.matcher(string);
				new_tab[i] = begin_tag+tab[i]+end_tag;
				string = matcher.replaceAll(tab[i].toUpperCase());
				string = string.replaceAll(tab[i], new_tab[i]);
			}	
		
		return string;
		}
	}	
	
	
	public static void main(String[] args) {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		
		String date = "2015-10-21T08:00:00.000Z";
		date = "2010-01-01T12:00:00+01:00".replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
		try {
			Date result1 = df.parse(date);
			System.out.println(result1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

		
		String str = "{\"action\":\"updateEvent\",\"resourceId\":\"1000000\",\"event\":{\"id\":1000000,\"confirmed\":false,\"startDate\":\"2015-10-21T08:00:00.000Z\",\"endDate\":null,\"allDay\":false}}";
		

		Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
		JsonAction result = json.fromJson(str, JsonAction.class);
		
		System.out.println(result.getEvent().getStartDate());
//		SchedulerEvent event = json.fromJson(str, SchedulerEvent.class);
//		System.out.println(event);
//		System.out.println(event.getTitle()+"    "+event.getStartDate());
		
		
	}
	
	
	
}
