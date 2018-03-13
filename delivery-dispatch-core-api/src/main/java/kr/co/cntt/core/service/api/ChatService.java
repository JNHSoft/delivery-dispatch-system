package kr.co.cntt.core.service.api;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.chat.Chat;

import java.util.List;

public interface ChatService {

    /**
     * <p> postChat
     *
     * @param chat
     * @return
     */
    public int postChat(Chat chat) throws AppTrException;

    /**
     * <p> getChat
     *
     * @param chat
     * @return
     */
    public List<Chat> getChat(Chat chat) throws AppTrException;

    /**
     * <p> getChatRoom
     *
     * @param chat
     * @return
     * @throws AppTrException
     */
    public List<Chat> getChatRoom(Chat chat) throws AppTrException;

}
