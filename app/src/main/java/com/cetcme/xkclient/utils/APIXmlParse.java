package com.cetcme.xkclient.utils;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *xml 解析类
 * http://www.cnblogs.com/xiaoluo501395377/p/3444744.html
 *
 */
public class APIXmlParse {

    public static String getToken4Xml(String content) throws XmlPullParserException, IOException {
        //    创建XmlPullParserFactory解析工厂
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //    通过XmlPullParserFactory工厂类实例化一个XmlPullParser解析类
        XmlPullParser parser = factory.newPullParser();
        //    根据指定的编码来解析xml文档
        parser.setInput(new ByteArrayInputStream(content.getBytes("UTF-8")), "utf-8");


        //    得到当前的事件类型
        int eventType = parser.getEventType();
        //    只要没有解析到xml的文档结束，就一直解析
        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                //    解析到文档开始的时候
                case XmlPullParser.START_DOCUMENT:

                    break;
                //    解析到xml标签的时候
                case XmlPullParser.START_TAG:
                    if("token".equalsIgnoreCase(parser.getName())){
                        return parser.nextText();
                    }
                    break;
                //    解析到xml标签结束的时候
                case XmlPullParser.END_TAG:

                    break;
            }
            //    通过next()方法触发下一个事件
            eventType = parser.next();
        }

        return null;
    }
    public static String getRespDesc4Xml(String content) throws XmlPullParserException, IOException {
        //    创建XmlPullParserFactory解析工厂
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //    通过XmlPullParserFactory工厂类实例化一个XmlPullParser解析类
        XmlPullParser parser = factory.newPullParser();
        //    根据指定的编码来解析xml文档
        parser.setInput(new ByteArrayInputStream(content.getBytes("UTF-8")), "utf-8");


        //    得到当前的事件类型
        int eventType = parser.getEventType();
        //    只要没有解析到xml的文档结束，就一直解析
        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                //    解析到文档开始的时候
                case XmlPullParser.START_DOCUMENT:

                    break;
                //    解析到xml标签的时候
                case XmlPullParser.START_TAG:
                    if("respDesc".equalsIgnoreCase(parser.getName())){
//                        String temp = icon_new String(parser.nextText().getBytes("ISO-8859-1"), "UTF-8");
//                        testCharset(parser.nextText());
                        String temp = parser.nextText();
                        return temp;
                    }
                    break;
                //    解析到xml标签结束的时候
                case XmlPullParser.END_TAG:

                    break;
            }
            //    通过next()方法触发下一个事件
            eventType = parser.next();
        }

        return null;
    }
    public static String getRealName4Xml(String content) throws XmlPullParserException, IOException {
        //    创建XmlPullParserFactory解析工厂
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //    通过XmlPullParserFactory工厂类实例化一个XmlPullParser解析类
        XmlPullParser parser = factory.newPullParser();
        //    根据指定的编码来解析xml文档
        parser.setInput(new ByteArrayInputStream(content.getBytes("UTF-8")), "utf-8");


        //    得到当前的事件类型
        int eventType = parser.getEventType();
        //    只要没有解析到xml的文档结束，就一直解析
        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                //    解析到文档开始的时候
                case XmlPullParser.START_DOCUMENT:

                    break;
                //    解析到xml标签的时候
                case XmlPullParser.START_TAG:
                    if("realName".equalsIgnoreCase(parser.getName())){
                        String temp = new String(parser.nextText().getBytes("ISO-8859-1"), "UTF-8");
                        return temp;
                    }
                    break;
                //    解析到xml标签结束的时候
                case XmlPullParser.END_TAG:

                    break;
            }
            //    通过next()方法触发下一个事件
            eventType = parser.next();
        }

        return null;
    }

    public static String getRespCode4Xml(String content) throws XmlPullParserException, IOException {
        //    创建XmlPullParserFactory解析工厂
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //    通过XmlPullParserFactory工厂类实例化一个XmlPullParser解析类
        XmlPullParser parser = factory.newPullParser();
        //    根据指定的编码来解析xml文档
        parser.setInput(new ByteArrayInputStream(content.getBytes("UTF-8")), "utf-8");


        //    得到当前的事件类型
        int eventType = parser.getEventType();
        //    只要没有解析到xml的文档结束，就一直解析
        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                //    解析到文档开始的时候
                case XmlPullParser.START_DOCUMENT:

                    break;
                //    解析到xml标签的时候
                case XmlPullParser.START_TAG:
                    if("respCode".equalsIgnoreCase(parser.getName())){
                        return parser.nextText();
                    }
                    break;
                //    解析到xml标签结束的时候
                case XmlPullParser.END_TAG:

                    break;
            }
            //    通过next()方法触发下一个事件
            eventType = parser.next();
        }

        return null;
    }

    /**
     * 从xml中解析一个字段
     */
    public static String getStr4Xml(String content, String str) throws XmlPullParserException, IOException {
        //    创建XmlPullParserFactory解析工厂
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //    通过XmlPullParserFactory工厂类实例化一个XmlPullParser解析类
        XmlPullParser parser = factory.newPullParser();
        //    根据指定的编码来解析xml文档
        parser.setInput(new ByteArrayInputStream(content.getBytes("UTF-8")), "utf-8");


        //    得到当前的事件类型
        int eventType = parser.getEventType();
        //    只要没有解析到xml的文档结束，就一直解析
        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                //    解析到文档开始的时候
                case XmlPullParser.START_DOCUMENT:

                    break;
                //    解析到xml标签的时候
                case XmlPullParser.START_TAG:
                    if(str.equalsIgnoreCase(parser.getName())){
                        return parser.nextText();
                    }
                    break;
                //    解析到xml标签结束的时候
                case XmlPullParser.END_TAG:

                    break;
            }
            //    通过next()方法触发下一个事件
            eventType = parser.next();
        }

        return null;
    }
}
