package com.ludicrus.core.model;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

public class News
{
	private String title;
	private String content;
	private String imgPath;
	private String newsPath;
	private HashMap<String, News> relatedNews;
	private String source;
	
	public News()
	{
		
	}

	public void decodeNews(String xml)
	{
		try
		{
			byte[] byteArray = xml.getBytes("UTF-8");
		    ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
		    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		    XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
		    
		    boolean isNewsBlock = false;
		    int noOfNewLines = 0;
		    relatedNews = new HashMap<String, News>();
		    News related = null;
		    
			// Read the XML document
			while (eventReader.hasNext())
			{
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement())
				{
					if (event.asStartElement().getName().getLocalPart().equals("img"))
					{
						Iterator<Attribute> iterator = event.asStartElement().getAttributes();
						while(iterator.hasNext())
						{
							Attribute attr = (Attribute)iterator.next();
//							System.out.println(attr.getName());
//							System.out.println(attr.getName().toString());
//							System.out.println(attr.getName().equals("src"));
//							System.out.println(attr.getName().toString().equals("src"));
							if(attr.getName().toString().equals("src"))
								this.imgPath = attr.getValue();
						}
						event = eventReader.nextEvent();
						continue;
					}
					if (event.asStartElement().getName().getLocalPart().equals("div"))
					{
						Iterator<Attribute> iterator = event.asStartElement().getAttributes();
						while(iterator.hasNext())
						{
							Attribute attr = (Attribute)iterator.next();
							if(attr.getName().toString().equals("class"))
								if(attr.getValue().equals("lh"))
									isNewsBlock = true;
						}
						continue;
					}
					if (event.asStartElement().getName().getLocalPart().equals("a"))
					{
						Iterator<Attribute> iterator = event.asStartElement().getAttributes();
						while(iterator.hasNext())
						{
							Attribute attr = (Attribute)iterator.next();
							if(attr.getName().toString().equals("href"))
							{
								if(isNewsBlock)
								{
									if(this.newsPath == null)
									{
										this.newsPath = attr.getValue();
										event = eventReader.nextEvent();
										this.title = getBlockCharacters("a", eventReader);
									}
									else
									{
										event = eventReader.nextEvent();
										related = new News();
										related.title = getBlockCharacters("a", eventReader);
										related.newsPath = attr.getValue();
									}
								}
									 
							}
						}
						continue;
					}
					if (event.asStartElement().getName().getLocalPart().equals("nobr"))
					{
						event = eventReader.nextEvent();
						if(related != null)
						{
							related.source = event.asCharacters().getData();
							System.out.println(event.asCharacters().getData());
							relatedNews.put(related.newsPath, related);
						}
						related = null;
						continue;
					}
					if (event.asStartElement().getName().getLocalPart().equals("br"))
					{
						if(isNewsBlock)
							noOfNewLines++;
						continue;
					}
					if (event.asStartElement().getName().getLocalPart().equals("font"))
					{
						Iterator<Attribute> iterator = event.asStartElement().getAttributes();
						while(iterator.hasNext())
						{
							Attribute attr = (Attribute)iterator.next();
							if(attr.getName().toString().equals("size"))
							{
								if(attr.getValue().equals("-1"))
								{
									if(isNewsBlock && noOfNewLines == 2)
									{
										this.content = getBlockCharacters("font", eventReader);
									}
								}
							}
							if(attr.getName().toString().equals("color"))
							{
								if(isNewsBlock && noOfNewLines == 1)
								{
									event = eventReader.nextEvent();
									this.source = event.asCharacters().getData();
									System.out.println(event.asCharacters().getData());
								}
							}
						}
						continue;
					}
				} else if (event.isEndElement()) 
				{
					if (event.asEndElement().getName().getLocalPart().equals("img")) {
						
						event = eventReader.nextEvent();
						continue;
					}
				}
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	private String getBlockCharacters(String tag, XMLEventReader eventReader)
	{
		try
		{
			XMLEvent event;
			String text = "";
			int tagCount = 1;
			while(tagCount > 0)
			{
				event = eventReader.nextEvent();
				if(event.isCharacters())
				{
					System.out.println(event.asCharacters().getData());
					text += event.asCharacters().getData();
				}
				else if (event.isEndElement())
				{
					if (event.asEndElement().getName().getLocalPart().equals(tag))
						tagCount--;	
				}
			}
			
			return text;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getNewsPath() {
		return newsPath;
	}

	public void setNewsPath(String newsPath) {
		this.newsPath = newsPath;
	}

	public HashMap<String, News> getRelatedNews() {
		return relatedNews;
	}

	public void setRelatedNews(HashMap<String, News> relatedNews) {
		this.relatedNews = relatedNews;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
