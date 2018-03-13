package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.ChatMapper;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("chatService")
public class ChatServiceImpl extends ServiceSupport implements ChatService {

    @Autowired
    private RedisService redisService;

    /**
     * Chat DAO
     */
    private ChatMapper chatMapper;

    /**
     * @param chatMapper CHAT D A O

     */
    @Autowired
    public ChatServiceImpl(ChatMapper chatMapper) { this.chatMapper = chatMapper; }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int postChat(Chat chat) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            chat.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            chat.setRole("ROLE_RIDER");
        }

        Chat resultSelectChatRoomRel = chatMapper.selectChatUserChatRoomRel(chat);

        if (resultSelectChatRoomRel == null) {
            int resultInsertChatRoom = chatMapper.insertChatRoom(chat);
            if (resultInsertChatRoom != 0) {
                chatMapper.insertChatRoomRelRecv(chat);
                chatMapper.insertChatRoomRelTran(chat);
            }
        } else {
            chat.setChatRoomId(resultSelectChatRoomRel.getChatRoomId());
        }

        if (chat.getMessage().length() > 64) {
            chat.setLastMessage(chat.getMessage().substring(0, 64));
        } else {
            chat.setLastMessage(chat.getMessage());
        }

        chatMapper.updateChatRoomLastMessage(chat);
        int S_Chat = chatMapper.insertChat(chat);

        if (S_Chat == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00030), ErrorCodeEnum.E00030.name());
        } else {
            redisService.setPublisher("chat_send", "recv_chat_user_id:"+chat.getChatUserId());
        }

        return S_Chat;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public List<Chat> getChat(Chat chat) throws AppTrException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            chat.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            chat.setRole("ROLE_RIDER");
        }

        List<Chat> S_Chat = chatMapper.selectChat(chat);

        if (S_Chat.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00031), ErrorCodeEnum.E00031.name());
        }

        return S_Chat;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public List<Chat> getChatRoom(Chat chat) throws AppTrException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            chat.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            chat.setRole("ROLE_RIDER");
        }

        List<Chat> S_Chatroom = chatMapper.selectChatRoom(chat);

        if (S_Chatroom.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00032), ErrorCodeEnum.E00032.name());
        }

        return S_Chatroom;
    }

}