package kr.zany.sample.netty4.server.common.data;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * <b>Common Result Object Value Object.</b>
 * <p>공통 결과 객체</p>
 *
 * <p>Copyright ⓒ 2015 kt corp. All rights reserved.</p>
 *
 * @author Lee Sang-Hyun (zanylove@gmail.com)
 * @since 2015. 10. 01.
 */
@Data
@Builder
public class CommonResultVo implements Serializable {

    @Getter(AccessLevel.NONE)
    private static final long serialVersionUID = 4605967981464637972L;
    
    private int resultCode;
    private String resultMsg;
    private String detailMsg;
}
