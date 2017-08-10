package com.fxb.receiver.myapplication.bean;

import java.io.Serializable;

/* *
* read the data
**/

public class readmode implements Serializable{
	/**
	 * EPC
	 */
	private String EPCNo="";
	/**
	 * TID
	 */
	private String  TIDNo = "";
	/**
	* the label number
	**/
    private String  CountNo ="";
	public String getEPCNo() {
		return EPCNo;
	}
	public void setEPCNo(String epcNo) {
		EPCNo = epcNo;
	}
	//
	public String getTIDNo() {
		return TIDNo;
	}
	public void setTIDNo(String tidNo) {
		TIDNo = tidNo;
	}
	public String  getCountNo() {
		return CountNo;
	}
	public void setCountNo(String  countNo) {
		CountNo = countNo;
	}

}
