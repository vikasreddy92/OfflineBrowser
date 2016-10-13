package com.ob.Obrowser;

import java.io.Serializable;

public class Webdata extends AddLink implements Serializable{	
	private static final long serialVersionUID = 8369483990489331147L;
	/**
	 * @uml.property  name="title1"
	 */
	public String title1;	
	/**
	 * @uml.property  name="data1"
	 */
	public String data1;	
	/**
	 * @uml.property  name="url1"
	 */
	public String url1;
	/**
	 * @param title1
	 * @uml.property  name="title1"
	 */
	public void setTitle1(String title1)
	{
		this.title1=title1;		
	}
	/**
	 * @param data1
	 * @uml.property  name="data1"
	 */
	public void setData1(String data1)
	{
		this.data1=data1;	 	
	}
	/**
	 * @param url1
	 * @uml.property  name="url1"
	 */
	public void setUrl1(String url1)
	{
		this.url1=url1;	 	
	}
	/**
	 * @return
	 * @uml.property  name="url1"
	 */
	public String getUrl1()
	{
		return this.url1;
	}
	/**
	 * @return
	 * @uml.property  name="title1"
	 */
	public String getTitle1()
	{
		return this.title1;
	}
	/**
	 * @return
	 * @uml.property  name="data1"
	 */
	public String getData1()
	{
		return this.data1;
	}	
}