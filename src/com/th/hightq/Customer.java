package com.th.hightq;

public class Customer {
	
	private String customerId;
	
	private String customerName;
	
	private String customerAddr;
	
	private String customerTel;
	
	private String customerZipCode;
	
	public String getCustomerTel() {
		return customerTel;
	}

	public void setCustomerTel(String customerTel) {
		this.customerTel = customerTel;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddr() {
		return customerAddr;
	}

	public void setCustomerAddr(String customerAddr) {
		int index = customerAddr.indexOf('-');
		String zipcode = "";
		// zip - address
		if(index != -1 && index < 8) {
			zipcode = customerAddr.substring(0, index);
			customerAddr = customerAddr.substring(index + 1, customerAddr.length());
		}
		// address (zip)
		int leftIndex = customerAddr.lastIndexOf(".(");
		int rightIndex = customerAddr.lastIndexOf(')');
		if(leftIndex != -1 && rightIndex != -1 && rightIndex > leftIndex) {
			zipcode = customerAddr.substring(leftIndex + 2, rightIndex);
			customerAddr = customerAddr.substring(0, leftIndex);
		}
		setCustomerZipCode(zipcode.trim().replaceAll("\n", ""));
		this.customerAddr = customerAddr.trim().replaceAll("\n", "");
	}

	public String getCustomerZipCode() {
		return customerZipCode;
	}

	public void setCustomerZipCode(String customerZipCode) {
		this.customerZipCode = customerZipCode;
	}
}
