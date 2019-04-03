package com.t3h.filternews.parse;

import android.util.Log;

import com.t3h.filternews.model.News;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;

public class XMLHandler extends DefaultHandler {
    private ArrayList<News> arrNews = new ArrayList<>();
    private News news;
    private final String TAG_ITEM = "item";
    private final String TAG_TITLE = "title";
    private final String TAG_DESC = "description";
    private final String TAG_DATE = "pubDate";
    private final String TAG_LINK = "link";
    private final String TAG_IMAGE ="<media:content";
    private StringBuilder value;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName == TAG_ITEM) {
            news = new News();
        }
        value = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        value.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (news == null) {
            return;
        }
        switch (qName) {
            case TAG_TITLE:
                news.setTitle(value.toString());
                break;
            case TAG_DESC:
                String src = "<p>";
                if (value.indexOf(src) == -1) {
                    break;
                } else {
                    int index = value.indexOf(src);
                    String str = value.substring(index + src.length());
                    index = str.indexOf("</p>");
                    String desc = str.substring(0,index);
                    news.setDesc(desc);
                }
                break;
            case TAG_LINK:
//                String url = "url=";
//                if(value.indexOf(url) == -1){
//                    break;
//                }else {
//                    int index = value.indexOf(url);
//                    String link = value.substring(index + url.length());
//                    news.setLink(link);
//                }
                news.setLink(value.toString());
                break;
            case TAG_ITEM:
                arrNews.add(news);
                break;
            case TAG_DATE:
                news.setPubDate(value.toString());
                break;
            case TAG_IMAGE:
//                String url = "url=\"";
//                if(value.indexOf(url) == -1){
//                    break;
//                }else {
//                    int index = value.indexOf(url);
//                    String img = value.substring(index + url.length());
//                    index = img.indexOf("\"");
//                    String linkImg = img.substring(0,index);
//                    news.setImg(linkImg);
//                }
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    String img = parser.getAttributeValue(null, "url\"");
                    news.setImg(img);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }


    public ArrayList<News> getArrNews() {
        return arrNews;
    }
}
