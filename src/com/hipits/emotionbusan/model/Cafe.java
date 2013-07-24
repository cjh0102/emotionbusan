package com.hipits.emotionbusan.model;

import android.graphics.Bitmap;

public class Cafe {
	
	private Bitmap cafeImage;
	private String cafeName;
	private String cafeAddress;
	private String content;
	private String cafeURL;
	private String writerUsername;
	private int favoritesCount;
	private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Bitmap getCafeImage() {
		return cafeImage;
	}

	public void setCafeImage(Bitmap cafeImage) {
		this.cafeImage = cafeImage;
	}

	public String getCafeName() {
		return cafeName;
	}

	public void setCafeName(String cafeName) {
		this.cafeName = cafeName;
	}

	public String getCafeAddress() {
		return cafeAddress;
	}

	public void setCafeAddress(String cafeAddress) {
		this.cafeAddress = cafeAddress;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCafeURL() {
		return cafeURL;
	}

	public void setCafeURL(String cafeURL) {
		this.cafeURL = cafeURL;
	}

	public int getFavoritesCount() {
		return favoritesCount;
	}

	public void setFavoritesCount(int favoritesCount) {
		this.favoritesCount = favoritesCount;
	}

	public String getWriterUsername() {
		return writerUsername;
	}

	public void setWriterUsername(String writerUsername) {
		this.writerUsername = writerUsername;
	}

}
