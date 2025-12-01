package websocket;

import com.google.gson.Gson;

import exceptions.ResponseException;
import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

public class WebSocketFacade extends Endpoint {

    private final Session session;
    private final MessageHandler messageHandler;
    private final Gson gson = new Gson();

    public WebSocketFacade(String url, MessageHandler messageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new jakarta.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    messageHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        //Endpoint requires this method, but PetShop says I don't have to do anything
    }

    public void connect(UserGameCommand command) throws ResponseException{
        try {
            if (session == null || !session.isOpen()) {
                throw new ResponseException(
                        ResponseException.Code.ServerError,
                        "Websocket session is closed."
                );
            }
            String json = gson.toJson(command);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
               throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void send() {

    }

    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (IOException ignored) {}
    }
//    public void enterPetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.ENTER, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }
//
//    public void leavePetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }

}
