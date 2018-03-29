package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.chat.Chat;

import java.util.List;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> ChatMapper.java </p>
 * <p> Admin 관련 </p>
 *
 * @author Aiden
 * @see DeliveryDispatchMapper
 */
@DeliveryDispatchMapper
public interface ChatMapper {

    /**
     * 채팅방 존재 확인
     *
     * @param chat
     * @return
     */
    public Chat selectChatUserChatRoomRel(Chat chat);

    /**
     * <p> 채팅방 생성
     *
     * @param chat
     * @return
     */
    public int insertChatRoom(Chat chat);

    /**
     * <p> 수신자 채팅방 REL 생성
     *
     * @param chat
     * @return
     */
    public int insertChatRoomRelRecv(Chat chat);

    /**
     * <p> 송신자 채팅방 REL 생성
     *
     * @param chat
     * @return
     */
    public int insertChatRoomRelTran(Chat chat);

    /**
     * <p> 마지막 메시지 갱신
     *
     * @param chat
     * @return
     */
    public int updateChatRoomLastMessage(Chat chat);

    /**
     * <p> 채팅 보내기
     *
     * @param chat
     * @return
     */
    public int insertChat(Chat chat);

    /**
     * <p> 채팅 읽기
     *
     * @param chat
     * @return
     */
    public List<Chat> selectChat(Chat chat);

    /**
     * <p> 상점 채팅 읽기
     *
     * @param chat
     * @return
     */
    public List<Chat> selectStoreChat(Chat chat);

    /**
     * <p> 채팅방 목록
     *
     * @param chat
     * @return
     */
    public List<Chat> selectChatRoom(Chat chat);

}
