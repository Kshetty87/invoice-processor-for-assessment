package com.eg.invoiceassessment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component
public class ResponseDTO<T> implements Serializable {

	@JsonProperty(value = "message")
	private String message;

	@JsonProperty(value = "data")
	private T data;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ResponseDTO(String message, T data) {
		super();
		this.message = message;
		this.data = data;
	}

	public ResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}


	

}