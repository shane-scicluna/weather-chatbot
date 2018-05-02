package weatherchatbot;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.unit.DegreeUnit;

public class WeatherChatbotMain {

	public static Scanner sc = new Scanner(System.in);
	public static String name = "null";
	public static String chatbotName = "a weather chatbot";
	public static int[] tempHigh = {1,2,3,4,5};	//holds high temps
	public static String[] forecast = {"Cloudy", "Showers", "Showers", "Sunny", "Sunny"};	//holds forecasts
	public static String[] currentCondition = {"5","Cloudy","33"};	//holds current weather.. temp/weather/wind speed
	public static String[][] locations = {{"london", "44418"}, {"manchester", "28218"}, {"liverpool", "26734"}, {"brighton", "13911"}};
	public static String WOEID = "44418"; //london code
	public static int count = 0; //keeping track of conversation
	public static boolean stop = false; //while loop controller
	public static String[] strDays = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
	public static String day0 = "monday";
	public static String day1 = "tuesday";
	public static String day2 = "wednesday";
	public static String day3 = "thursday";
	public static String day4 = "friday";
	public static String fav = "null"; //favourite weather to be filled in by user
	public static int error = 0;	//keeping track of mistakes
	
	public static void main(String[] args) throws JAXBException, IOException {
		chatInit();
	}
	
	//initialising data, will save to file later
	public static void chatInit() throws JAXBException, IOException{	
		System.out.println("Hello! I'm " + chatbotName);
		System.out.println("What's your name?");
		name = sc.nextLine();
		System.out.println("Hi " + name + ", what's your location?");

		locationSearch();
	}
	
	//searches for location
	public static void locationSearch() throws JAXBException, IOException{
		String input = sc.nextLine();
		boolean exist = false;
		
		for(int i = 0; i < 4; i++)
		{
			if(input.toLowerCase().contains(locations[i][0]))	//location found
			{
				WOEID = locations[i][1];	//setting location
				exist = true;
				week();	//setting days of the week
				initData();		//downloading data				
			}
		}
		
		if(exist == false)	//if location couldnt be found
		{
			locationError();
		}
	}
	
	//when location isnt known/saved
	public static void locationError() throws JAXBException, IOException{
		System.out.println("I can't seem to find that, I currently know the following:");	//save unknown location for later
		for(int i = 0; i < 4; i++)
		{
			System.out.println(locations[i][0]);
		}
		System.out.println("Please choose one of the above.");
		locationSearch();
	}
	
	//handles initial dialogue
	public static void chatStart() throws JAXBException, IOException{	
		System.out.println("Today is " + today());
		System.out.println("Current weather: ");
		System.out.println(currentCondition[0] + "°C");
		System.out.println(currentCondition[1]);
		System.out.println(currentCondition[2] + "km/h");
		
		System.out.println("Would you like to talk about the weather?");
		String input = sc.nextLine();
		if(responsePositive(input))
		{
			System.out.println("So, " + name + ", " + "would you like today's forecast, or maybe tomorrow's? (remember to type 'stop' to terminate)");
			input = sc.nextLine();
			chatForecast(input);
		}
		else if(responseNegative(input))
		{
			chat();
		}
		else
		{
			System.out.println("I didn't catch that, try saying yes or no.");
			chatStart();
		}
	}
	
	//handles forecasts
	public static void chatForecast(String input) throws JAXBException, IOException{	
			if(input == "null")	//if no specific date was given
			{
				input = sc.nextLine();
				chatForecast(input);
			}
			
			else if(input.toLowerCase().contains(day0) || input.toLowerCase().contains("today"))
			{ 
				System.out.println("Today: " + tempHigh[0] + "°C" + " " + forecast[0]);
				chat();
			}
			else if(input.toLowerCase().contains(day1) || input.toLowerCase().contains("tomorrow"))
			{
				System.out.println("Tomorrow: " + tempHigh[1] + "°C" + " " + forecast[1]);
				chat();
			}
			else if(input.toLowerCase().contains(day2))
			{
				System.out.println(day2 + ": " + tempHigh[2] + "°C" + " " + forecast[2]);
				chat();
			}
			else if(input.toLowerCase().contains(day3))
			{
				System.out.println(day3 + ": " + tempHigh[3] + "°C" + " " + forecast[3]);
				chat();
			}
			else if(input.toLowerCase().contains(day4))
			{
				System.out.println(day4 + ": " + tempHigh[4] + "°C" + " " + forecast[4]);
				chat();
			}
			else if(input.equalsIgnoreCase("stop"))
			{
				chatTerminate();
			}
			else
			{	
				System.out.println("'" + input + "'?" + " please tell me a date such as 'today', 'tomorrow' or more specific such as 'Tuesday' or 'Wednesday'");
				chatForecast("null");
			}
			
	}
	
	//'main' chat method
	public static void chat() throws JAXBException, IOException{
		count++;	//keeping track of conversation
		if(count == 1)
		{
			System.out.println("Go on, say something. Type 'help' for more information or 'stop' to terminate.");
			System.out.println("If you're feeling lost, just say 'lets talk about the weather'!.");
		}
		else if(error == 3)
		{
			System.out.println();
			System.out.println("You seem to be having some trouble, remember I'm only a simple chatbot.");
			System.out.println();
			error = 0;
			help();
		}
		else if(name.contains(" "))	//checking if name is long(has whitespace)
		{
			if(count == 3)
			{
				nameLong();	//checks if name is too long
			}
		}
		String input = sc.nextLine();
		
		//trivia
		if(input.toLowerCase().contains("tell me") && input.toLowerCase().contains("snow"))
		{
			snowTrivia();
		}
		else if(input.toLowerCase().contains("tell me") && (input.toLowerCase().contains("rain") || input.toLowerCase().contains("showers")))
		{
			rainTrivia();
		}
		else if(input.toLowerCase().contains("tell me") && (input.toLowerCase().contains("weather")))
		{
			weatherTrivia();
		}
		else if(input.toLowerCase().contains("tell me") && input.toLowerCase().contains("thunder"))
		{
			thunderTrivia();
		}
		
		//asking about specific forecasts
		else if(input.toLowerCase().contains("rain") || input.toLowerCase().contains("showers"))
		{
			weatherSearch("showers");
		}
		else if(input.toLowerCase().contains("thunder") || input.toLowerCase().contains("storm"))
		{
			weatherSearch("thunderstorms");
		}
		else if(input.toLowerCase().contains("snow"))
		{
			weatherSearch("snow");
		}
		else if(input.toLowerCase().contains("cloud") || input.toLowerCase().contains("cloudy"))
		{
			weatherSearch("cloudy");
		}
		else if(input.toLowerCase().contains("hail"))
		{
			weatherSearch("hail");
		}
		else if(input.toLowerCase().contains("foggy") || input.toLowerCase().contains("fog"))
		{
			weatherSearch("foggy");
		}
		
		else if(input.toLowerCase().contains("sunny"))
		{
			weatherSearch("sunny");
		}
		
		else if(input.toLowerCase().contains("favourite"))
		{
			System.out.println("My favourite weather is sunny!");
			if(fav == "null")
			{
			System.out.println("What's yours?");
			setFav();
			}
			else
			{
				System.out.println("Your favourite weather is " + fav);
				System.out.println("Let's see if I can find that during the week...");
				weatherSearch(fav);
			}
		}
		//asking about weather
		else if(input.toLowerCase().contains("today") || input.toLowerCase().contains(day0))
		{
			chatStart();
		}
		else if(input.toLowerCase().contains("tomorrow") || input.toLowerCase().contains(day1))
		{
			chatForecast(day1);
		}
		else if(input.toLowerCase().contains(day2))
		{
			chatForecast(day2);
		}
		else if(input.toLowerCase().contains(day3))
		{
			chatForecast(day3);
		}
		else if(input.toLowerCase().contains(day4))
		{
			chatForecast(day4);
		}
		
		else if(input.toLowerCase().contains("weather") || input.toLowerCase().contains("forecast"))
		{
			System.out.println("So, " + name + ", " + "would you like today's forecast, or maybe tomorrow's? (remember to type 'stop' to terminate)");
			chatForecast("null");
		}
		
		//asking for user name
		else if(input.toLowerCase().contains("my") && input.toLowerCase().contains("name"))
		{
			if(name == "null")	//if name was never set for some reason
			{
				System.out.println("Weird. You don't seem to have a name. Let's try and fix that...");
				chatInit();
			}
			System.out.println("Your name is " + name + ". But you already knew that, didn't you?");
			chat();
		}
		//talking about chatbots name
		else if(input.toLowerCase().contains("you") && input.toLowerCase().contains("name"))
		{
			if(chatbotName.equals("a weather chatbot"))
			{
				System.out.println("I dont seem to have one. You can name me.");
				chatName();
			}
			else
			{
				System.out.println(chatbotName + ". Do you like it?");
				input = sc.nextLine();
				
				if(responsePositive(input))
				{
					System.out.println("Thanks, so do I.");
					chat();
					
				}
				else if(responseNegative(input))
				{
					System.out.println("Give me a new one then!");
					chatName();
				}
				else
				{
					System.out.println("I don't understand.");
					chat();
				}
			}
		}
		else if(input.equalsIgnoreCase("stop"))
		{
			chatTerminate();
		}
		else if(input.equalsIgnoreCase("help"))
		{
			help();
		}
		
		else if(responseRude(input))
		{
			System.out.println("Somebody's in a bad mood. Try being nicer for a change.");
			error++;
			chat();
		}
		else if(responseGreeting(input))
		{
			System.out.print("Hey there!");
			chat();
		}
		
		else
		{
			error++;
			System.out.println("What? I don't seem to understand. Type 'help' for more information.");
			chat();
		}
	}
	
	//handling shortening of name
	public static void nameLong() throws JAXBException, IOException{
		String newName = name.substring(0, name.indexOf(" ")); 
		System.out.println(name + " is a bit long; considering we're so close now, "
						   + "how about I shorten it to " + newName + "?");
		String input = sc.nextLine();

		if(responsePositive(input))
		{
			name = newName;
			System.out.println("Great, I'll call you " + name + " from now on.");
			chat();
		}
		else if(responseNegative(input))
		{
			System.out.println("That's alright. I like " + name + " anyway.");
			chat();
		}
		else
		{
			System.out.println("what?");
			nameLong();
		}
	}
	
	//handles changing name
	public static void chatName() throws JAXBException, IOException{
		String input = sc.nextLine();
		
		Random rand = new Random();	//random chance the chatbot will like the given name
		int num = rand.nextInt(10);
		
		if(num > 2)	//70% to like the name
		{
			System.out.println(input + "? I like it! That'll be my name from now on.");
			chatbotName = input;
			chat();
		}
		else
		{
			System.out.println(input + "? I don't like that... Try again.");
			chatName();
		}
	}
	
	//returns current date as string
	public static String today(){	
		Calendar calendar = Calendar.getInstance();
		String today = strDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
		
		return today;
	}
	
	//terminating chat
	public static void chatTerminate() throws JAXBException, IOException{
		System.out.println("Would you like to terminate operations? (yes/no)");
		String input = sc.nextLine();
		if(responseNegative(input))
		{	
			chat();
		}
		else if(responsePositive(input))
		{	
			sc.close();
			System.out.println("Terminated");
		}
		else
		{	
			System.out.println("Please say 'yes' or 'no'");
			chatTerminate();
		}
	}
	
	//method calling other data download methods
	public static void initData() throws JAXBException, IOException{	
		currentCondition = currentWeather();
		tempHigh = tempDataHigh();
		forecast = forecastData();
		System.out.println("Finished downloading data");
		chatStart();
	}
	
	//gets current weather
	public static String[] currentWeather() throws JAXBException, IOException{	
		YahooWeatherService service = new YahooWeatherService();
		Channel result = service.getForecast(WOEID, DegreeUnit.CELSIUS);
		String[] weather = new String[3];
		
		System.out.println("Downloading data...");
		weather[0] = Integer.toString(result.getItem().getCondition().getTemp());
		weather[1] = result.getItem().getCondition().getText();
		weather[2] = Double.toString(result.getWind().getSpeed().intValue());
		
		return weather;
	}
	
	//get temps for a week(only highs for now)
	public static int[] tempDataHigh() throws JAXBException, IOException{	
		YahooWeatherService service = new YahooWeatherService();
		Channel result = service.getForecast(WOEID, DegreeUnit.CELSIUS);
		int[] temp = new int[5];
		
		System.out.println("Downloading high temps...");
		for(int i = 0;i < 5;i++)
		{
			temp[i] = result.getItem().getForecasts().get(i).getHigh();
		}
		
		return temp;
	}
	
	//get forecasts
	public static String[] forecastData() throws JAXBException, IOException{	
		YahooWeatherService service = new YahooWeatherService();
		Channel result = service.getForecast(WOEID, DegreeUnit.CELSIUS);
		String[] forecast = new String[5];
		
		System.out.println("Downloading forecasts...");
		for(int i = 0;i < 5;i++)
		{
			forecast[i] = result.getItem().getForecasts().get(i).getText();
		}
		
		return forecast;
	}
	
	//holds possible positive respones
	public static boolean responsePositive(String input){
		boolean isTrue = false;
		String[] positives = {"yes", "sure", "ok", "okay", "alright", "of course","right", "aye", "yep",
							  "roger", "agreed", "certainly", "absolutely","all right","indeed","affirmative"};
		
		for(int i = 0; i < positives.length ;i++ )
		{
			if(input.toLowerCase().contains(positives[i]))
				{
					isTrue = true;
				}
		}
		return isTrue;
	}
	
	//holds possible negative responses
	public static boolean responseNegative(String input){
		boolean isTrue = false;
		String[] negatives = {"no", "nope", "negative", "never", "no way", "not"};
		
		for(int i = 0; i < negatives.length ;i++ )
		{
			if(input.toLowerCase().contains(negatives[i]))
				{
					isTrue = true;
				}
		}
		return isTrue;
	}
	
	//holds possible rude responses
	public static boolean responseRude(String input){
		boolean isTrue = false;
		String[] rude = {"fuck", "shit", "asshole", "cunt", "arse", "arsehole", "bellend", "bastard", "bollocks", "bitch",
						 "motherfucker", "bloody", "damn"};
		
		for(int i = 0; i < rude.length ;i++ )
		{
			if(input.toLowerCase().contains(rude[i]))
				{
					isTrue = true;
				}
		}
		
		return isTrue;
	}
	
	//greetings
	public static boolean responseGreeting(String input){
		boolean isTrue = false;
		String[] greeting = {"hello", "greetings", "hey", "hi", "goodmorning", "goodafternoon", 
							 "good afternoon", "good morning", "hiya", "howdy"};																						
		
		for(int i = 0; i < greeting.length ;i++ )
		{
			if(input.toLowerCase().contains(greeting[i]))
				{
					isTrue = true;
				}
		}
		
		return isTrue;
	}
	
	//dynamically sets the 5 day period from today
	public static void week(){
		day0 = today();
		switch (today()){
		case "monday":
			break;
		case "tuesday":
			day1 = "wednesday";
			day2 = "thursday";
			day3 = "friday";
			day4 = "saturday";
			break;
		case "wednesday":
			day1 = "thurdsay";
			day2 = "friday";
			day3 = "saturday";
			day4 = "sunday";
			break;
		case "thursday":
			day1 = "friday";
			day2 = "saturday";
			day3 = "sunday";
			day4 = "monday";
			break;
		case "friday":
			day1 = "saturday";
			day2 = "sunday";
			day3 = "monday";
			day4 = "tuesday";
			break;
		case "saturday":
			day1 = "sunday";
			day2 = "monday";
			day3 = "tuesday";
			day4 = "wednesday";
			break;
		case "sunday":
			day1 = "monday";
			day2 = "tuesday";
			day3 = "wednesday";
			day4 = "thursday";
			break;
		}
	}
	
	//helps user by showing some commands/keywords
	public static void help() throws JAXBException, IOException{
		System.out.println("Need some help? Here's a list of commands: ");
		System.out.println("Want to talk about the weather? Type 'forecast' or a certain day within a 5 day period from today.");
		System.out.println("You can say 'tell me' followed by a weather forecast and I'll share everything I know on the subject!");
		System.out.println("Try asking about my name or my favourite type of weather.");
		System.out.println("You can also type 'stop' to terminate me at any time. I don't mind, really.");
		chat();
	}
	
	//searches for a specific forecast within the week and prints out the days that match
	public static void weatherSearch(String input) throws JAXBException, IOException{
		boolean found = false;
		for(int i = 0; i < 5; i++){
			if(forecast[i].toLowerCase().contains(input)){
				switch(i){
				case 0:
					System.out.println(input + " on " + day0);
					found = true;
					break;
				case 1:
					System.out.println(input + " on " + day1);
					found = true;
					break;
				case 2:
					System.out.println(input + " on " + day2);
					found = true;
					break;
				case 3:
					System.out.println(input + " on " + day3);
					found = true;
					break;
				case 4:
					System.out.println(input + " on " + day4);
					found = true;
					break;
				}
			}
		}
		if(found == false)
		{
			System.out.println("Couldn't find anything within the week in regards to: " + input);
		}
		chat();
	}
	
	//setting favourite type of weather and searches for it through weatherSearch()
	public static void setFav() throws JAXBException, IOException{
		String input = sc.nextLine();
		fav = input;
		
		System.out.println("Let me see if I can find that during the week...");
		weatherSearch(fav);
	}

	//trivia methods giving information regarding certain weather topics, user can ask for more or go back to chatting
	public static void snowTrivia() throws JAXBException, IOException{
		System.out.println("Here's some trivia relating to snow: ");
		System.out.println();
		System.out.println("Snow refers to forms of ice crystals that precipitate from the atmosphere (usually from clouds) and undergo changes on the Earth's surface.");
		System.out.println("It pertains to frozen crystalline water throughout its life cycle, starting when, under suitable conditions, the ice crystals form in the atmosphere,");
		System.out.println("increase to millimeter size, precipitate and accumulate on surfaces, then metamorphose in place, and ultimately melt, slide or sublimate away.");
		System.out.println();
		System.out.println("Would you like me to continue?");
		String input = sc.nextLine();
		if(responsePositive(input))
		{
			System.out.println("Snowstorms organize and develop by feeding on sources of atmospheric moisture and cold air.");
			System.out.println("Snowflakes nucleate around particles in the atmosphere by attracting supercooled water droplets, which freeze in hexagonal-shaped crystals.");
			System.out.println("Snowflakes take on a variety of shapes, basic among these are platelets, needles, columns and rime.");
			System.out.println("As snow accumulates into a snowpack, it may blow into drifts.");
			System.out.println();
			System.out.println("Would you like me to continue?");
			input = sc.nextLine();
			if(responsePositive(input))
			{
				System.out.println("Over time, accumulated snow metamorphoses, by sintering, sublimation and freeze-thaw.");
				System.out.println("Where the climate is cold enough for year-to-year accumulation, a glacier may form.");
				System.out.println("Otherwise, snow typically melts seasonally, causing runoff into streams and rivers and recharging groundwater.");
				System.out.println();
				System.out.println("Would you like me to continue?");
				input = sc.nextLine();
				if(responsePositive(input))
				{
					System.out.println("Major snow-prone areas include the polar regions, the upper half of the Northern Hemisphere and");
					System.out.println("mountainous regions worldwide with sufficient moisture and cold temperatures.");
					System.out.println("In the Southern Hemisphere, snow is confined primarily to mountainous areas, apart from Antarctica.");
					System.out.println();
					System.out.println("Alright, that's all for now");
					chat();
				}
				else
				{
					System.out.println("Alright, I'll give it a rest.");
					chat();
				}
			}
			else
			{
				System.out.println("Alright, I'll give it a rest.");
				chat();
			}
		}
		else
		{
			System.out.println("Alright, I'll give it a rest.");
			chat();
		}
	}
	
	public static void rainTrivia() throws JAXBException, IOException{
		System.out.println("Here's some trivia relating to rain: ");
		System.out.println();
		System.out.println("Rain is liquid water in the form of droplets that have condensed from atmospheric water vapor");
		System.out.println("and then becomes heavy enough to fall under gravity.");
		System.out.println("Rain is a major component of the water cycle and is responsible for depositing most of the fresh water on the Earth.");
		System.out.println("It provides suitable conditions for many types of ecosystems, as well as water for hydroelectric power plants and crop irrigation.");
		System.out.println();
		System.out.println("Would you like me to continue?");
		String input = sc.nextLine();
		if(responsePositive(input))
		{
			System.out.println("The major cause of rain production is moisture moving along three-dimensional zones");
			System.out.println("of temperature and moisture contrasts known as weather fronts.");
			System.out.println("If enough moisture and upward motion is present,");
			System.out.println("precipitation falls from convective clouds (those with strong upward vertical motion)");
			System.out.println("such as cumulonimbus (thunder clouds) which can organize into narrow rainbands.");
			System.out.println();
			System.out.println("Would you like me to continue?");
			input = sc.nextLine();
			if(responsePositive(input))
			{
				System.out.println("In mountainous areas, heavy precipitation is possible where upslope flow is maximized within");
				System.out.println("windward sides of the terrain at elevation which forces moist air to condense and fall out as rainfall along the sides of mountains.");
				System.out.println("On the leeward side of mountains, desert climates can exist due");
				System.out.println("to the dry air caused by downslope flow which causes heating and drying of the air mass.");
				System.out.println("The movement of the monsoon trough, or intertropical convergence zone, brings rainy seasons to savannah climes.");
				System.out.println();
				System.out.println("Would you like me to continue?");
				input = sc.nextLine();
				if(responsePositive(input))
				{
					System.out.println("The urban heat island effect leads to increased rainfall, both in amounts and intensity, downwind of cities.");
					System.out.println("Global warming is also causing changes in the precipitation pattern globally,");
					System.out.println("including wetter conditions across eastern North America and drier conditions in the tropics.");
					System.out.println("Antarctica is the driest continent. The globally averaged annual precipitation over land is 715 mm (28.1 in),");
					System.out.println("but over the whole Earth it is much higher at 990 mm (39 in).");
					System.out.println();
					System.out.println("Alright, that's all for now");
					chat();
				}
				else
				{
					System.out.println("Alright, I'll give it a rest.");
					chat();
				}
			}
			else
			{
				System.out.println("Alright, I'll give it a rest.");
				chat();
			}
		}
		else
		{
			System.out.println("Alright, I'll give it a rest.");
			chat();
		}
	}
	public static void weatherTrivia() throws JAXBException, IOException{
		System.out.println("Here's some trivia relating the weather: ");
		System.out.println();
		System.out.println("Weather is the state of the atmosphere, describing for example the degree to which it is hot or cold, wet or dry, calm or stormy, clear or cloudy.");
		System.out.println("Most weather phenomena occur in the lowest level of the atmosphere, the troposphere, just below the stratosphere.");
		System.out.println("Weather refers to day-to-day temperature and precipitation activity, whereas climate is the term for the averaging of atmospheric conditions over longer periods of time.");
		System.out.println();
		System.out.println("Would you like me to continue?");
		String input = sc.nextLine();
		if(responsePositive(input))
		{
			System.out.println("When used without qualification, 'weather' is generally understood to mean the weather of Earth.");
			System.out.println("Weather is driven by air pressure, temperature and moisture differences between one place and another.");
			System.out.println("These differences can occur due to the sun's angle at any particular spot, which varies with latitude.");
			System.out.println("The strong temperature contrast between polar and tropical air gives rise to the largest scale atmospheric circulations:");
			System.out.println("the Hadley Cell, the Ferrel Cell, the Polar Cell, and the jet stream.");
			System.out.println();
			System.out.println("Would you like me to continue?");
			input = sc.nextLine();
			if(responsePositive(input))
			{
				System.out.println("On Earth, the common weather phenomena include wind, cloud, rain, snow, fog and dust storms.");
				System.out.println("Less common events include natural disasters such as tornadoes, hurricanes, typhoons and ice storms.");
				System.out.println("Almost all familiar weather phenomena occur in the troposphere (the lower part of the atmosphere).");
				System.out.println("Weather does occur in the stratosphere and can affect weather lower down in the troposphere, but the exact mechanisms are poorly understood.");
				System.out.println();
				System.out.println("Would you like me to continue?");
				input = sc.nextLine();
				if(responsePositive(input))
				{
					System.out.println("Weather occurs primarily due to air pressure, temperature and moisture differences between one place to another.");
					System.out.println("These differences can occur due to the sun angle at any particular spot, which varies by latitude from the tropics.");
					System.out.println("In other words, the farther from the tropics one lies, the lower the sun angle is, which causes those locations to be cooler due the spread of the sunlight over a greater surface.");
					System.out.println(" The strong temperature contrast between polar and tropical air gives rise to the large scale atmospheric circulation cells and the jet stream.");
					System.out.println("Weather systems in the mid-latitudes, such as extratropical cyclones, are caused by instabilities of the jet stream flow.");
					System.out.println("Weather systems in the tropics, such as monsoons or organized thunderstorm systems, are caused by different processes.");
					System.out.println();
					System.out.println("Alright, that's all for now");
					chat();
				}
				else
				{
					System.out.println("Alright, I'll give it a rest.");
					chat();
				}
			}
			else
			{
				System.out.println("Alright, I'll give it a rest.");
				chat();
			}
		}
		else
		{
			System.out.println("Alright, I'll give it a rest.");
			chat();
		}
	}
	public static void thunderTrivia() throws JAXBException, IOException{
		System.out.println("Here's some trivia relating to thunder: ");
		System.out.println();
		System.out.println("Thunder is the sound caused by lightning.");
		System.out.println("Depending on the distance and nature of the lightning, it can range from a sharp, loud crack to a long, low rumble (brontide).");
		System.out.println("The sudden increase in pressure and temperature from lightning produces rapid expansion of the air surrounding and within a bolt of lightning.");
		System.out.println("In turn, this expansion of air creates a sonic shock wave, similar to a sonic boom, often referred to as a 'thunderclap' or 'peal of thunder'.");
		System.out.println();
		System.out.println("Would you like me to continue?");
		String input = sc.nextLine();
		if(responsePositive(input))
		{
			System.out.println("The cause of thunder has been the subject of centuries of speculation and scientific inquiry.");
			System.out.println("The first recorded theory is attributed to the Greek philosopher Aristotle in the fourth century BC,");
			System.out.println("and an early speculation was that it was caused by the collision of clouds.");
			System.out.println("Subsequently, numerous other theories were proposed. By the mid-19th century, the accepted theory was that lightning produced a vacuum.");
			System.out.println();
			System.out.println("Would you like me to continue?");
			input = sc.nextLine();
			if(responsePositive(input))
			{
				System.out.println("In the 20th century a consensus evolved that thunder must begin with a shock wave in the air due to the sudden thermal expansion");
				System.out.println(" of the plasma in the lightning channel.");
				System.out.println("The temperature inside the lightning channel, measured by spectral analysis, varies during its 50 microsecond existence,");
				System.out.println("rising sharply from an initial temperature of about 20,000 K to about 30,000 K, then dropping away gradually to about 10,000 K.");
				System.out.println();
				System.out.println("Alright, that's all for now");
				chat();
			}
			else
			{
				System.out.println("Alright, I'll give it a rest.");
				chat();
			}
		}
		else
		{
			System.out.println("Alright, I'll give it a rest.");
			chat();
		}
		
	}


}

