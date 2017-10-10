package com.jc.sb_pos.helper;

public class AppConfig {
	//API URL
	public static String API_URL = "http://uglybatstudios.co.uk/dev/sb-slim-api/v1/";

	//URL to login
	public static String LOGIN = "login";

	//URL to register
	public static String REGISTER = "register";

	//URL to topup user account
	public static String USER_TOPUP = "users/user/top-up";

	//URL to orders
	public static String ORDERS = "orders";

	//URL to categories (all)
	public static String CATEGORIES = "categories";

	//URL to a single category (needs an id after)
	public static String CATEGORY = "category";

	//URL to products (all)
	public static String PRODUCTS = "products";


	//URL to our video file
	public static String VIDEO_URL = "http://uglybatstudios.co.uk/dev/sb-slim-api/movies/coffee.mp4";
	public static String VIDEO_URL_2 = "http://uglybatstudios.co.uk/dev/sb-slim-api/movies/videoviewdemo.mp4";


	//Keys to send on our $_POST['key'] requests in the login.php and register.php
	public static final String KEY_USER_NAME = "user_name";
	public static final String KEY_FIRST_NAME = "first_name";
	public static final String KEY_LAST_NAME = "last_name";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_CONTACT_NUMBER = "contact_number";
	public static final String KEY_PASSWORD = "password";

	//Keys to send on our $_PUT['key'] requests in topup
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_TOPUP_AMOUNT = "amount";

}
