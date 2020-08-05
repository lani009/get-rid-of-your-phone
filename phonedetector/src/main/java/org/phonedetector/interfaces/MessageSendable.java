package org.phonedetector.interfaces;

import java.util.List;

public interface MessageSendable {
    
    /**
     * 등록된 사용자 모두에게 메시지 전달
     * @param text
     */
    public void sendMessageAll(String text);

    /**
     * 사용자에게 메시지 전달
     * @param text 보낼 메시지
     * @param id 아이디
     */
    public void sendMessage(String text, long id);

    /**
     * 사용자에게 메시지 전달
     * @param text 보낼 메시지
     * @param id 아이디
     */
    public void sendMessage(String text, String id);

    /**
     *  단체 사용자에게 메시지 전송
     * @param text 보낼 메시지
     * @param idList 단체 사용자
     */
    public void sendMessage(String text, List<String> idList);
}