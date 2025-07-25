package sesac.bookmanager.notice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponse {
    private Integer noticeId;

    private NoticeType type;
    private LocalDateTime createdAt;
    private String title;
    private String content;
    private Integer views;
    private Integer adminId;

    public String getTypeLabel() {
        return type.getLabel();
    }

    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getNoticeId(),
                notice.getType(),
                notice.getCreatedAt(),
                notice.getTitle(),
                notice.getContent(),
                notice.getViews(),
                notice.getAdmin().getId()
        );
    }
}
