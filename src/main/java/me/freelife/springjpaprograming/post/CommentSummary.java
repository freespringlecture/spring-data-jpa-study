package me.freelife.springjpaprograming.post;

public class CommentSummary {

    private String comment;
    private int up;
    private int down;

    public CommentSummary(String comment, int up, int down) {
        this.comment = comment;
        this.up = up;
        this.down = down;
    }

    public String getComment() {
        return comment;
    }

    public int getUp() {
        return up;
    }

    public int getDown() {
        return down;
    }

    public String getVotes() {
        return this.up + " " + this.down;
    }

    // 인터페이스 구현체
    /*
    String getComment();
    int getUp();
    int getDown();

//    @Value("#{target.up + ' ' + target.down}")
//    String getVotes();

    default String getVotes() {
        return getUp() + " " + getDown();
    }
    */
}
