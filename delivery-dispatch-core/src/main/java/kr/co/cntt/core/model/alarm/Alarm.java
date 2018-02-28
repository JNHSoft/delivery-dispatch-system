package kr.co.cntt.core.model.alarm;

import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class Alarm extends Common implements Dto {

    private static final long serialVersionUID = -7257042237844655752L;
    private String adminId;
    private String alarmType;
    private String oriFileName;
    private String fileName;
    private String fileSize;
}
