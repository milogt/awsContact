package edu.uchicago.tiang.emailer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Message;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper objectMapper= new ObjectMapper();
    public final String emailTo = "tiang@uchicago.edu";

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        Contact contact = null;
        try {
            contact = objectMapper.readValue(input.getBody(), Contact.class);
        } catch (JsonProcessingException e) {
            return response
                    .withBody(String.format("{%s}", e.getMessage()))
                    .withStatusCode(400);
        }

        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1).build();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subject", contact.getSubject());
        jsonObject.put("body", contact.getBody());
        jsonObject.put("emailFrom", contact.getEmailFrom());

        List<String> recipients = new ArrayList<>();
        recipients.add(contact.getEmailFrom());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(emailTo))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withText(new Content().withCharset("UTF-8").withData(contact.getBody())))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(contact.getSubject())))
                .withSource(emailTo)
                .withReplyToAddresses(recipients);
        try {
            client.sendEmail(request);
        } catch (Exception exception) {
//            System.out.println("The email was not sent. Error message: " + exception.getMessage());
            jsonObject.put("error", "The email was not sent. Error message: " + exception.getMessage());
        }

        return response
                .withStatusCode(200)
                .withBody(jsonObject.toString());

    }

}
