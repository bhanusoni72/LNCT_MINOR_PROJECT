package com.night;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

public class GmailApi {
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    /*private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        //InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        FileInputStream in=new FileInputStream(new File(CREDENTIALS_FILE_PATH ));
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }*/

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
    	 NetHttpTransport HTTP_TRANSPORT = null;
         try {
             HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
         } catch (GeneralSecurityException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }

         GoogleCredential credentials = new GoogleCredential.Builder()
                 .setClientSecrets("894943576430-c1v6r907e9i2rsg1i538r5p2q9g4d8vu.apps.googleusercontent.com", "KaRY3M1rwQB8kG0RJ7VgxdbF")
                 .setJsonFactory(JSON_FACTORY).setTransport(HTTP_TRANSPORT).build()
                 .setRefreshToken("1//0g5AFoPYC4k69CgYIARAAGBASNwF-L9Ir6fGRch760Xg3UWt9rs4t2Ee4Jso-YHIIThqqOIWXKbBh1Zfbry3o-tSd05DmI9RausE")
                 .setAccessToken("ya29.a0AfH6SMDk7zdMrpwqJriY644YqGjhd8GwOJP2RkYfiRY3_jfdvXoDGdBZLZqhSo7AufoFl8w7JnkVJUL20FgY_aSPN6hMbMRIVrKPjdHEJkETOl1GCrfWlZkK946RiCfVQ96eW3rSv1jkKPAPzK5sbIr8HTKtymIEKoI");

        
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                .setApplicationName(APPLICATION_NAME)
                .build();
        
        
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the email: ");
        String e=sc.next();
        System.out.println("Enter the subject of the email: ");
        sc.nextLine();
        String su=sc.nextLine();
        System.out.println("Enter the message you want to send: ");
        String bo=sc.nextLine();
        
        
        MimeMessage mime=null;
		try {
			mime = createEmail(e,"me",su,bo);
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Message message=null;
        try {
			message=createMessageWithEmail(mime);
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        message=service.users().messages().send("me",message).execute();
        
        System.out.println("Message id is: "+message.getId());
        System.out.println("Message content: "+message.toPrettyString());
        
        // Print the labels in the user's account.
        /*String user = "me";
        ListLabelsResponse listResponse = service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.isEmpty()) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }*/
    }

	public static MimeMessage createEmail(String to,String from,String subject,String bodyText)throws MessagingException 
    {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		MimeMessage email = new MimeMessage(session);
		
		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO,new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		return email;
    }
    public static Message createMessageWithEmail(MimeMessage emailContent)throws MessagingException, IOException 
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
