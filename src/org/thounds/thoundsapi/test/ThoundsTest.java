package org.thounds.thoundsapi.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;
import org.thounds.thoundsapi.connector.ThoundsConnector;
import org.thounds.thoundsapi.connector.ThoundsDigestConnector;
import org.thounds.thoundsapi.connector.ThoundsOAuthConnector;
import org.thounds.thoundsapi.connector.ThoundsOAuthParameterExcepion;

public class ThoundsTest {
	public static void main(String[] args)
	throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception, ThoundsOAuthParameterExcepion, IOException {
		System.out.println("==== Thounds API Test ====");
		
		System.out.println("== Digest connector ==");
		testDigestConnector();
		
		System.out.println("== OAuth connector ==");
		String token = "nWehRhPnIF75g3nTr9G2";
		String tokenSecret = "QjC5jayh5Bz5gWdZnT5WpKu1QThzin842d9p6nF7";
		String callback = "http://localhost/";
		testOauthConnector(token, tokenSecret, callback);
		
		System.out.println("== OAuth connector with access token ==");
		String accessToken = "JaUCYGd47fBOAo6RWrzG";
		String accessTokenSecret = "00ury7L99SBGPahBx0pN2VtfbtTsH6V5mN3YX6s7";
		testOauthConnectorWithAccessToken(token, tokenSecret, callback, accessToken, accessTokenSecret);
	}
	
	private static void testDigestConnector()
	throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception, IOException {
		String username = readLine("Enter your email:");
		String password = readLine("Enter your password:");
        
		ThoundsDigestConnector con = new ThoundsDigestConnector(username , password);
		
		connectedAs(con);
	}
	
	private static void testOauthConnector(String token, String tokenSecret, String callback)
	throws ThoundsConnectionException, ThoundsOAuthParameterExcepion, IOException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		ThoundsOAuthConnector con = new ThoundsOAuthConnector(token, tokenSecret, callback);
		
		System.out.println("Fetching request token from Thounds...");

		String authUrl = con.getAuthorizeUrl();

        System.out.println("Request token: " + con.getToken());
        System.out.println("Token secret: " + con.getTokenSecret());

        System.out.println("Now visit: " + authUrl + "\n... and grant this app authorization");
        
        String verifier = readLine("Enter the VERIFIER code and hit ENTER when you're done (leave blank to skip):");

        if (verifier.isEmpty()) {
        	System.out.println("Skipped...");
        }
        else {
	        System.out.println("Fetching access token from Thounds...");
	
	        con.retrieveAccessToken(verifier);
	
	        System.out.println("Access token: " + con.getToken());
	        System.out.println("Token secret: " + con.getTokenSecret());
	        
	        connectedAs(con);
        }
	}
	
	private static void testOauthConnectorWithAccessToken(String token, String tokenSecret, String callback, String accessToken, String accessTokenSecret)
	throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		ThoundsOAuthConnector con = new ThoundsOAuthConnector(token, tokenSecret, callback);
		
		con.setAccessToken(accessToken, accessTokenSecret);
		
		connectedAs(con);
	}
	
	private static void connectedAs(ThoundsConnector con)
	throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		Thounds.setConnector(con);
		
		System.out.println("You're logged in as " + Thounds.loadHome().getUser().getName());
	}
	
	private static String readLine(String prompt) throws IOException {
		System.out.println(prompt);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
	}
}
