package org.phonedetector.struct;

public class Caller {
    private String id = null;
    private String tid = null;

    /**
     * @param tid 스레드 id
     * @param id 사용자 id
     */
    public Caller(String tid, String id) {
        this.tid = tid;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getTid() {
        return this.tid;
    }
    
}
